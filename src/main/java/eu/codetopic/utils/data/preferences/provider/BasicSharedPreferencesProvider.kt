package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class BasicSharedPreferencesProvider(context: Context, private val fileName: String? = null,
                                     preferencesOperatingMode: Int = Context.MODE_PRIVATE) :
        ISharedPreferencesProvider<SharedPreferences> {

    private val preferences = createPreferences(context.applicationContext, preferencesOperatingMode)

    val preferencesOperatingMode = if (fileName == null) Context.MODE_PRIVATE else preferencesOperatingMode

    private fun createPreferences(context: Context, preferencesOperatingMode: Int): SharedPreferences {
        return if (fileName == null) PreferenceManager.getDefaultSharedPreferences(context)
        else context.getSharedPreferences(fileName, preferencesOperatingMode)
    }

    override fun getName(): String? = "BasicSharedPreferences.$1%s".format(fileName ?: "default")

    override fun getSharedPreferences(): SharedPreferences = preferences

    override fun toString(): String =
            "BasicSharedPreferencesProvider(fileName=$fileName, preferences=$preferences," +
                    " preferencesOperatingMode=$preferencesOperatingMode)"
}