package eu.codetopic.utils.data.preferences.provider

import android.content.SharedPreferences

/**
 * Created by anty on 11/8/17.
 * @author anty
 */
interface SharedPreferencesProvider<out SP : SharedPreferences> {

    fun getName(): String?

    fun getSharedPreferences(): SP
}