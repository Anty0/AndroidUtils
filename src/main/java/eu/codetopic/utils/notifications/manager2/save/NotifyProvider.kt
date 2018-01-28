/*
 * utils
 * Copyright (C)   2018  anty
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

package eu.codetopic.utils.notifications.manager2.save

import android.content.Context
import android.content.SharedPreferences
import eu.codetopic.utils.PrefNames.FILE_NAME_NOTIFY_DATA
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.data.preferences.support.VersionedContentProviderPreferences

/**
 * @author anty
 */
class NotifyProvider : VersionedContentProviderPreferences<SharedPreferences>(AUTHORITY, NotifyData.SAVE_VERSION) {

    companion object {

        const val AUTHORITY = "eu.codetopic.utils.notifications.manager.data"
    }

    override fun onPreparePreferencesProvider(): ISharedPreferencesProvider<SharedPreferences> {
        return BasicSharedPreferencesProvider(
                context = context,
                fileName = FILE_NAME_NOTIFY_DATA,
                preferencesOperatingMode = Context.MODE_PRIVATE
        )
    }

    override fun onUpgrade(editor: SharedPreferences.Editor, from: Int, to: Int) {
        NotifyData.onUpgrade(editor, from, to)
    }
}