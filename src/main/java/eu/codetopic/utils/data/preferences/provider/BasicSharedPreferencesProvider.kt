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

package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class BasicSharedPreferencesProvider(context: Context, private val fileName: String? = null,
                                     preferencesOperatingMode: Int = Context.MODE_PRIVATE) :
        ISharedPreferencesProvider<SharedPreferences> {

    val preferencesOperatingMode = if (fileName == null) Context.MODE_PRIVATE else preferencesOperatingMode

    private fun createPreferences(context: Context, preferencesOperatingMode: Int): SharedPreferences {
        return if (fileName == null) PreferenceManager.getDefaultSharedPreferences(context)
        else context.getSharedPreferences(fileName, preferencesOperatingMode)
    }

    override val name: String? by lazy { "BasicSharedPreferences.${fileName ?: "default"}" }

    override val preferences: SharedPreferences by lazy { createPreferences(context.applicationContext, preferencesOperatingMode) }

    override fun toString(): String =
            "BasicSharedPreferencesProvider(fileName=$fileName, preferences=$preferences," +
                    " preferencesOperatingMode=$preferencesOperatingMode)"
}