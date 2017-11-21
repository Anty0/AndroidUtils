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
 * @author anty
 */
interface IPreferencesCompanionObject<T : IPreferencesData> : Lazy<T> {

    val getter: DataGetter<T>

    val instance : T

    fun initialize(context: Context)
}