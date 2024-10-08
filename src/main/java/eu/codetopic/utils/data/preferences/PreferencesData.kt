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

package eu.codetopic.utils.data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.support.annotation.CallSuper
import eu.codetopic.utils.broadcast.LocalBroadcast
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.edit

abstract class PreferencesData<out SP : SharedPreferences> (
        context: Context, private val preferencesProvider: ISharedPreferencesProvider<SP>) :
        IPreferencesData {

    companion object {

        private const val LOG_TAG = "PreferencesData"

        const val EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY"

        private fun getBroadcastActionChanged(data: PreferencesData<*>): String =
                "eu.codetopic.utils.data.preferences.PreferencesData" +
                        ".PREFERENCES_CHANGED.${data.name ?: "default"}"
    }

    private val preferenceChangeListener = OnSharedPreferenceChangeListener { _, key -> onChanged(key) }

    protected val context: Context = context.applicationContext

    protected val accessProvider: ISharedPreferencesProvider<SP>
            by lazy { PreferencesProvider() }

    final override var isCreated = false
        private set

    final override var isDestroyed = false
        private set

    final override val broadcastActionChanged: String
        get() = getBroadcastActionChanged(this)

    final override val name: String?
        get() = preferencesProvider.name

    protected val preferences: SP
        get() {
            if (!isCreated) throw IllegalStateException("$LOG_TAG is not initialized")
            if (isDestroyed) throw IllegalStateException("$LOG_TAG is still destroyed")
            return preferencesProvider.preferences
        }

    private fun generateIntentActionChanged(changedKey: String?): Intent {
        return Intent(this.broadcastActionChanged)
                .putExtra(EXTRA_CHANGED_DATA_KEY, changedKey)
                .putExtras(getAdditionalDataChangedExtras(changedKey))
    }

    protected open fun getAdditionalDataChangedExtras(changedKey: String?): Bundle {
        return Bundle()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Migrate to Kotlin and use new edit(block) instead.")
    @SuppressLint("CommitPrefEdits")
    protected fun edit(): SharedPreferences.Editor = preferences.edit()

    protected inline fun edit(block: SharedPreferences.Editor.() -> Unit) = preferences.edit(block)

    @Synchronized
    final override fun init() {
        if (isCreated) throw IllegalStateException("$LOG_TAG is still initialized")
        if (isDestroyed) throw IllegalStateException("$LOG_TAG is destroyed")
        isCreated = true
        try {
            onCreate()
        } catch (t: Throwable) {
            try {
                destroy()
            } catch (_: Throwable) {}
            throw t
        }

    }

    @CallSuper
    @Synchronized
    protected open fun onCreate() {
        //Log.v(LOG_TAG, "onCreate(name=$name)")
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    @CallSuper
    @Synchronized
    protected open fun onChanged(key: String?) {
        //Log.v(LOG_TAG, "onChanged(name=$name, key=$key)")
        LocalBroadcast.sendBroadcast(generateIntentActionChanged(key))
    }

    @CallSuper
    @Synchronized
    protected open fun onDestroy() {
        //Log.v(LOG_TAG, "onDestroy(name=$name)")
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    @Synchronized
    final override fun destroy() {
        if (!isCreated) throw IllegalStateException("$LOG_TAG is not initialized")
        if (isDestroyed) throw IllegalStateException("$LOG_TAG is still destroyed")
        onDestroy()
        isDestroyed = true
        isCreated = false
    }

    protected open fun finalize() {
        if (isCreated && !isDestroyed) destroy()
    }

    override fun toString(): String {
        return "PreferencesData(preferencesProvider=$preferencesProvider, " +
                "isCreated=$isCreated, isDestroyed=$isDestroyed)"
    }

    protected inner class PreferencesProvider : ISharedPreferencesProvider<SP> {

        override val name: String?
            get() = this@PreferencesData.name

        override val preferences: SP
            get() = this@PreferencesData.preferences

    }
}
