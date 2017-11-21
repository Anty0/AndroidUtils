/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

    override final val getter: DataGetter<T> by lazy(getterInitializer)

    override final val instance: T get() = value

    override final val value: T
        get() {
            val v1 = _instance
            if (v1 != null) return v1

            return synchronized(this) { // Let's wait for possible still running initialization.
                _instance ?: throw IllegalStateException("$log_tag is not initialized")
            }
        }

    override final fun initialize(context: Context) {
        synchronized(this) {
            if (_instance != null) throw IllegalStateException("$log_tag is still initialized")
            else {
                val preferencesInstance = initializer!!(context)
                preferencesInstance.init()
                _instance = preferencesInstance
                initializer = null
            }
        }
    }

    override final fun isInitialized(): Boolean = _instance != null

    override final fun toString(): String =
            if (isInitialized()) value.toString() else "Lazy value of $log_tag not initialized yet."
}