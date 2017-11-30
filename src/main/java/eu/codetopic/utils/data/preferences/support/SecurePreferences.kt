/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.preferences.support

import android.content.SharedPreferences
import android.util.Base64
import com.tozny.crypto.android.AesCbcWithIntegrity
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.AndroidExtensions.edit
import java.security.MessageDigest

/**
 * Wrapper class for Android's [SharedPreferences] interface, which adds a
 * layer of encryption to the persistent storage and retrieval of sensitive
 * key-value pairs of primitive data types.
 *
 *
 * This class provides important - but nevertheless imperfect - protection
 * against simple attacks by casual snoopers. It is crucial to remember that
 * even encrypted data may still be susceptible to attacks, especially on rooted devices
 *
 *
 * Recommended to use with user password, in which case the key will be derived from the password and not stored in the file.
 */
class SecurePreferences<out SP : SharedPreferences>(
        private val preferencesProvider: ISharedPreferencesProvider<SP>,
        secretKeys: AesCbcWithIntegrity.SecretKeys,
        private val id: String = DEFAULT_ID) : SharedPreferences {

    companion object {

        private const val LOG_TAG = "SecurePreferences"

        private const val DEFAULT_ITERATION_COUNT = 10000
        private const val SALT = "Yeah! Come on!"

        const val DEFAULT_ID = "default"

        /**
         * The Pref keys must be same each time so we're using a hash to obscure the stored value
         *
         * @param key
         * @return SHA-256 Hash of the preference key
         */
        private fun hashKey(key: String): String? {
            return try {
                Base64.encodeToString(MessageDigest.getInstance("SHA-256").apply {
                    key.toByteArray(charset("UTF-8")).let {
                        update(it, 0, it.size)
                    }
                }.digest(), AesCbcWithIntegrity.BASE64_FLAGS)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Problem generating hash", e); null
            }
        }

        private fun passwordToKeys(password: String,
                                   iterationCount: Int = DEFAULT_ITERATION_COUNT):
                AesCbcWithIntegrity.SecretKeys {
            return AesCbcWithIntegrity
                    .generateKeyFromPassword(password, SALT.toByteArray(), iterationCount)
                    //use the password to generate the key
                    ?: throw NullPointerException("Problem generating Key From Password")
        }
    }

    private var currentKeys: AesCbcWithIntegrity.SecretKeys? = secretKeys
    private val keys: AesCbcWithIntegrity.SecretKeys get() = currentKeys
            ?: throw NullPointerException("No keys available")

    private val preferences: SharedPreferences get() = preferencesProvider.preferences

    /**
     * @param iterationCount The iteration count for the keys generation
     */
    constructor(preferencesProvider: ISharedPreferencesProvider<SP>,
                password: String, iterationCount: Int = DEFAULT_ITERATION_COUNT)
            : this(preferencesProvider, passwordToKeys(password, iterationCount))

    /**
     * nulls in memory keys
     */
    @Synchronized
    fun destroyKeys() {
        currentKeys = null
    }

    private fun formatKey(key: String): String {
        return "Encrypted{$id}-${hashKey(key)}"
    }

    private fun isMyEncryptedKey(key: String): Boolean {
        return key.startsWith("Encrypted{$id}-")
    }

    private fun encrypt(clearText: String?): String? {
        return try {
            AesCbcWithIntegrity.encrypt(clearText
                    .takeUnless { it.isNullOrEmpty() }
                    ?: return clearText, keys).toString()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "encrypt(clearText=$clearText)", e); null
        }
    }

    /**
     * @param cipherText text to decrypt
     * @return decrypted plain text, unless decryption fails, in which case null
     */
    private fun decrypt(cipherText: String?): String? {
        return try {
            AesCbcWithIntegrity.decryptString(
                    AesCbcWithIntegrity.CipherTextIvMac(cipherText
                            .takeUnless { it.isNullOrEmpty() }
                            ?: return cipherText),
                    keys
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "decrypt(cipherText=$cipherText)", e); null
        }
    }

    /**
     * @return map of with decrypted values (excluding the key if present)
     */
    @Synchronized
    override fun getAll(): Map<String, String> {
        return preferences.all.filter { isMyEncryptedKey(it.key) }.map {
            it.key to it.value.toString().run {
                try {
                    (decrypt(this) ?: this)
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "getAll(key=${it.key}, value=${it.value})", e); this
                }
            }
        }.toMap()
    }

    private fun getVal(key: String): String? {
        val formattedKey = formatKey(key)
        val encryptedValue = preferences.getString(formattedKey, null)
        val value = decrypt(encryptedValue)

        Log.d(LOG_TAG, "getVal(key=$key, formattedKey=$formattedKey, " +
                "encryptedValue=(isNull=${encryptedValue == null}, isEmpty=${encryptedValue.isNullOrEmpty()}), " +
                "value=(isNull=${value == null}, isEmpty=${value.isNullOrEmpty()}))")

        return value
    }

    @Synchronized
    override fun getString(key: String, defaultValue: String?): String? = getVal(key) ?: defaultValue

    @Synchronized
    override fun getStringSet(key: String, defaultValues: Set<String>?): Set<String>? {
        val formattedKey = formatKey(key)
        val encryptedValues = preferences.getStringSet(formattedKey, null)

        Log.d(LOG_TAG, "getStringSet(key=$key, formattedKey=$formattedKey)")

        return encryptedValues?.map { decrypt(it) ?: it }?.toSet() ?: defaultValues
    }

    @Synchronized
    override fun getInt(key: String, defaultValue: Int): Int = getVal(key)?.toIntOrNull() ?: defaultValue

    @Synchronized
    override fun getLong(key: String, defaultValue: Long): Long = getVal(key)?.toLongOrNull() ?: defaultValue

    @Synchronized
    override fun getFloat(key: String, defaultValue: Float): Float = getVal(key)?.toFloatOrNull() ?: defaultValue

    @Synchronized
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = getVal(key)?.toBoolean() ?: defaultValue

    @Synchronized
    override fun contains(key: String) = preferences.contains(formatKey(key))


    /**
     * Cycle through the decrypt all the current prefs to mem cache, clear, then encrypt with key generated from new password.
     * This method can be used if switching from the generated key to a key derived from user password
     *
     * Note: the pref keys will remain the same as they are SHA256 hashes.
     *
     * @param newPassword
     * @param iterationCount The iteration count for the keys generation
     */
    @Synchronized
    fun handlePasswordChange(newPassword: String, iterationCount: Int = DEFAULT_ITERATION_COUNT) {
        val newKey = passwordToKeys(newPassword, iterationCount)

        all.let {
            currentKeys = newKey
            preferences.edit {
                it.forEach {
                    putString(it.key, encrypt(it.value))
                }
            }
        }
    }

    override fun edit(): SharedPreferences.Editor = Editor()

    @Synchronized
    private fun <R> sync(block: () -> R): R = block()

    /**
     * Wrapper for Android's [android.content.SharedPreferences.Editor].
     *
     *
     * Used for modifying values in a [SecurePreferences] object. All
     * changes you make in an editor are batched, and not copied back to the
     * original [SecurePreferences] until you call [.commit] or
     * [.apply].
     */
    private inner class Editor : SharedPreferences.Editor {

        private val editor = preferences.edit()

        private fun put(key: String, value: String?): SharedPreferences.Editor {
            val formattedKey = formatKey(key)
            val encryptedValue = encrypt(value)

            Log.d(LOG_TAG, "put(key=$key, formattedKey=$formattedKey, " +
                    "value=(isNull=${value == null}, isEmpty=${value.isNullOrEmpty()}), " +
                    "encryptedValue=(isNull=${encryptedValue == null}, isEmpty=${encryptedValue.isNullOrEmpty()}))")

            editor.putString(formattedKey, encryptedValue)
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            return put(key, value)
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            val formattedKey = formatKey(key)

            Log.d(LOG_TAG, "putStringSet(key=$key, formattedKey=$formattedKey)")

            editor.putStringSet(formattedKey, values?.map { encrypt(it) }?.toSet())
            return this
        }

        override fun putInt(key: String, value: Int) = put(key, value.toString())

        override fun putLong(key: String, value: Long) = put(key, value.toString())

        override fun putFloat(key: String, value: Float) = put(key, value.toString())

        override fun putBoolean(key: String, value: Boolean) = put(key, value.toString())

        override fun remove(key: String): SharedPreferences.Editor {
            editor.remove(formatKey(key))
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            throw UnsupportedOperationException("Not supported")
        }

        override fun commit(): Boolean {
            return sync { editor.commit() }
        }

        override fun apply() {
            sync { editor.apply() }
        }
    }

    @Synchronized
    override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    @Synchronized
    override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
