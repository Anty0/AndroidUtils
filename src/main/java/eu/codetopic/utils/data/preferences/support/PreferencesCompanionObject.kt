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

import android.content.Context
import eu.codetopic.utils.data.getter.DataGetter
import eu.codetopic.utils.data.preferences.IPreferencesData

/**
 * Warning: This code targets speed, not design.
 * @author anty
 */
open class PreferencesCompanionObject<T : IPreferencesData>(private val log_tag: String,
                                                            initializer: (Context) -> T,
                                                            getterInitializer: () -> DataGetter<T>) :
        IPreferencesCompanionObject<T> {

    private var initializer: ((Context) -> T)? = initializer
    @Volatile private var _instance: T? = null

    final override val getter: DataGetter<T> by lazy(getterInitializer)

    final override val instance: T get() = value

    final override val value: T
        get() {
            val v1 = _instance
            if (v1 != null) return v1

            return synchronized(this) { // Let's wait for possible still running initialization.
                _instance ?: throw IllegalStateException("$log_tag is not initialized")
            }
        }

    final override fun initialize(context: Context) {
        synchronized(this) {
            if (_instance != null) throw IllegalStateException("$log_tag is still initialized")
            else {
                val preferencesInstance = initializer!!(context.applicationContext)
                preferencesInstance.init()
                _instance = preferencesInstance
                initializer = null
            }
        }
    }

    final override fun isInitialized(): Boolean = _instance != null

    final override fun toString(): String =
            if (isInitialized()) value.toString() else "Lazy value of $log_tag not initialized yet."
}