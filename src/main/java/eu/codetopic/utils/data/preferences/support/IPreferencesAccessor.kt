package eu.codetopic.utils.data.preferences.support

import android.content.SharedPreferences

interface IPreferencesAccessor<out SP : SharedPreferences> {

    val name: String?

    val preferences: SP

    fun edit(block: SharedPreferences.Editor.() -> Unit)
}