package eu.codetopic.utils

import android.content.SharedPreferences

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) =
        edit().apply { block() }.apply()