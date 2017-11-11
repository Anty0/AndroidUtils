package eu.codetopic.utils.data.preferences.support

import android.content.SharedPreferences
import eu.codetopic.utils.PrefNames

abstract class VersionedContentProviderPreferences<out SP : SharedPreferences>(
        authority: String, private val saveVersion: Int) :
        ContentProviderPreferences<SP>(authority) {

    private fun checkUpgrade() {
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

    override fun onCreate(): Boolean {
        super.onCreate() || return false
        checkUpgrade()
        return true
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