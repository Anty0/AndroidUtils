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

package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences

class ContentProviderPreferencesProvider(context: Context, private val authority: String) :
        ISharedPreferencesProvider<ContentProviderSharedPreferences> {

    private fun createPreferences(context: Context): ContentProviderSharedPreferences =
            ContentProviderSharedPreferences.getInstance(context, authority)

    override val name: String? by lazy { "ContentProviderPreferences.{$authority}" } // ==${preferences.name}

    override val preferences: ContentProviderSharedPreferences by lazy { createPreferences(context.applicationContext) }

    override fun toString(): String =
            "ContentProviderPreferencesProvider(" +
                    "name=$name, " +
                    "authority=$authority, " +
                    "preferences=$preferences)"
}