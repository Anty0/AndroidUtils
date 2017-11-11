/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.CallSuper
import eu.codetopic.utils.PrefNames
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.data.preferences.support.NoApplyPreferencesEditor

abstract class VersionedPreferencesData<out SP : SharedPreferences>(
        context: Context, preferencesProvider: ISharedPreferencesProvider<SP>,
        private val saveVersion: Int) :
        PreferencesData<SP>(context, preferencesProvider) {

    @Synchronized private fun checkUpgrade() {
        val actualVersion = preferences.getInt(PrefNames.DATA_SAVE_VERSION, -1)
        if (actualVersion != saveVersion) {
            edit {
                val noApplyEditor = NoApplyPreferencesEditor(this@edit,
                        "Don't call methods editor.apply() or editor.commit() " +
                                "during onUpgrade() or onDowngrade(). Changes will be saved later automatically.")
                if (saveVersion > actualVersion)
                    onUpgrade(noApplyEditor, actualVersion, saveVersion)
                else onDowngrade(noApplyEditor, actualVersion, saveVersion)

                putInt(PrefNames.DATA_SAVE_VERSION, saveVersion)
            }
        }
    }

    @CallSuper
    @Synchronized
    override fun onCreate() {
        super.onCreate()
        checkUpgrade()
    }

    @Synchronized
    protected open fun onUpgrade(editor: SharedPreferences.Editor, from: Int, to: Int) {
        // Default implementation will just throw all data away.
        editor.clear()
    }

    @Synchronized
    protected open fun onDowngrade(editor: SharedPreferences.Editor, from: Int, to: Int) {
        // Default implementation should not support downgrading, so we will just throw exception.
        throw UnsupportedOperationException("Cannot downgrade version of '$name' from '$from' to '$to'")
    }
}