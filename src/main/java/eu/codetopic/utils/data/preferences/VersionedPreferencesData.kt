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