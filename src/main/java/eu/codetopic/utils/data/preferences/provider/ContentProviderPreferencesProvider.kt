package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences

class ContentProviderPreferencesProvider(context: Context, private val authority: String)
    : SharedPreferencesProvider<ContentProviderSharedPreferences> {

    private val preferences = createPreferences(context.applicationContext)

    private fun createPreferences(context: Context): ContentProviderSharedPreferences =
            ContentProviderSharedPreferences.getInstance(context, authority)

    override fun getName(): String? = "ContentProviderPreferences.$authority.${preferences.name}"

    override fun getSharedPreferences(): ContentProviderSharedPreferences = preferences

    override fun toString(): String = "ContentProviderPreferencesProvider(preferences=$preferences)"
}