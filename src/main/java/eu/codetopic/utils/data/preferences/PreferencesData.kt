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
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.content.LocalBroadcastManager

import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider

abstract class PreferencesData<out SP : SharedPreferences> (
        context: Context, private val preferencesProvider: ISharedPreferencesProvider<SP>) :
        IPreferencesData {

    companion object {

        private const val LOG_TAG = "PreferencesData"

        private const val ACTION_DATA_CHANGED_BASE = "eu.codetopic.utils.data.preferences.PreferencesData.PREFERENCES_CHANGED.$1%s"
        const val EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY"

        private fun getBroadcastActionChanged(data: PreferencesData<*>): String {
            return String.format(ACTION_DATA_CHANGED_BASE, data.name ?: "default")
        }
    }

    private val preferenceChangeListener = { _: SharedPreferences, key: String -> onChanged(key) }

    protected val context: Context = context.applicationContext

    protected val accessProvider: ISharedPreferencesProvider<SP>
            by lazy { PreferencesProvider() }

    override final var isCreated = false
        private set

    override final var isDestroyed = false
        private set

    override final val broadcastActionChanged: String
        get() = getBroadcastActionChanged(this)

    override final val name: String?
        get() = preferencesProvider.name

    protected val preferences: SP
        get() {
            if (!isCreated) throw IllegalStateException(LOG_TAG + " is not initialized")
            if (isDestroyed) throw IllegalStateException(LOG_TAG + " is still destroyed")
            return preferencesProvider.preferences
        }

    private fun generateIntentActionChanged(changedKey: String): Intent {
        return Intent(this.broadcastActionChanged)
                .putExtra(EXTRA_CHANGED_DATA_KEY, changedKey)
                .putExtras(getAdditionalDataChangedExtras(changedKey))
    }

    protected open fun getAdditionalDataChangedExtras(changedKey: String): Bundle {
        return Bundle()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Migrate to Kotlin and use new edit(block) instead.")
    @SuppressLint("CommitPrefEdits")
    protected fun edit(): SharedPreferences.Editor = preferences.edit()

    protected fun edit(block: SharedPreferences.Editor.() -> Unit) =
            preferences.edit().apply { block() }.apply()

    @Synchronized
    override final fun init() {
        if (isCreated) throw IllegalStateException(LOG_TAG + " is still initialized")
        if (isDestroyed) throw IllegalStateException(LOG_TAG + " is destroyed")
        isCreated = true
        try {
            onCreate()
        } catch (t: Throwable) {
            destroy()
            throw t
        }

    }

    @CallSuper
    @Synchronized
    protected open fun onCreate() {
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    @CallSuper
    @Synchronized
    protected open fun onChanged(key: String) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(generateIntentActionChanged(key))
    }

    @CallSuper
    @Synchronized
    protected open fun onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    @Synchronized
    override final fun destroy() {
        if (!isCreated) throw IllegalStateException(LOG_TAG + " is not initialized")
        if (isDestroyed) throw IllegalStateException(LOG_TAG + " is still destroyed")
        isDestroyed = true
        isCreated = false
        onDestroy()
    }

    protected open fun finalize() {
        if (isCreated && !isDestroyed) destroy()
    }

    override fun toString(): String {
        return "PreferencesData(preferencesProvider=$preferencesProvider, isCreated=$isCreated, isDestroyed=$isDestroyed)"
    }

    protected inner class PreferencesProvider : ISharedPreferencesProvider<SP> {

        override val name: String?
            get() = this@PreferencesData.name

        override val preferences: SP
            get() = this@PreferencesData.preferences

    }
}
