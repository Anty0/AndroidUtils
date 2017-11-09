package eu.codetopic.utils.data.preferences.support

import android.content.SharedPreferences

class NoApplyPreferencesEditor(private val editor : SharedPreferences.Editor,
                               private val throwableMessage: String = "Changes in this editor cannot be saved from this context!")
    : SharedPreferences.Editor by editor {

    override fun commit(): Boolean {
        throw UnsupportedOperationException(throwableMessage)
    }

    override fun apply() {
        throw UnsupportedOperationException(throwableMessage)
    }
}