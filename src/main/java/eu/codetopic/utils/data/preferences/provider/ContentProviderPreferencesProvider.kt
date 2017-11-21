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
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences

class ContentProviderPreferencesProvider(context: Context, private val authority: String) :
        ISharedPreferencesProvider<ContentProviderSharedPreferences> {

    private fun createPreferences(context: Context): ContentProviderSharedPreferences =
            ContentProviderSharedPreferences.getInstance(context, authority)

    override val name: String? by lazy { "ContentProviderPreferences.$authority.${preferences.name}" }

    override val preferences: ContentProviderSharedPreferences by lazy { createPreferences(context.applicationContext) }

    override fun toString(): String = "ContentProviderPreferencesProvider(preferences=$preferences)"
}