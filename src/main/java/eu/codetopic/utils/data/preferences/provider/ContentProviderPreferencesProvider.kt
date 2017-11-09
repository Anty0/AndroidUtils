package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences

/**
 * Created by anty on 11/8/17.
 * @author anty
 */
class ContentProviderPreferencesProvider<T : ContentProviderPreferences>(context: Context,
                                                                         private val clazz: Class<T>)
    : SharedPreferencesProvider<ContentProviderSharedPreferences> {

    private val preferences = createPreferences(context.applicationContext)

    private fun createPreferences(context: Context): ContentProviderSharedPreferences {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): String? {
        return "ContentProviderPreferences.$1%s".format(clazz.name)
    }

    override fun getSharedPreferences(): ContentProviderSharedPreferences {
        return preferences
    }

    override fun toString(): String {
        return "ContentProviderPreferencesProvider(clazz=$clazz, preferences=$preferences)"
    }
}