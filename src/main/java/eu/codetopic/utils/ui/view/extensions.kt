/*
 * utils
 * Copyright (C)   2018  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.view

import android.support.annotation.CheckResult
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import eu.codetopic.utils.R
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.inputMethodManager

/**
 * @author anty
 */

/////////////////////////
//////REGION - OTHER/////
/////////////////////////

@Suppress("NOTHING_TO_INLINE")
inline fun Boolean.asViewVisibility(): Int =
        if (this) View.VISIBLE else View.GONE

fun View.hideKeyboard() {
    context.inputMethodManager.hideSoftInputFromWindow(
            windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

/////////////////////////
//////REGION - TAGS//////
/////////////////////////

private val VIEW_TAG_KEY_TAGS_HASH_MAP = R.id.view_tag_key_tags_hash_map

@Suppress("UNCHECKED_CAST")
private fun View.getTags(): MutableMap<String, Any?> =
        (getTag(VIEW_TAG_KEY_TAGS_HASH_MAP) as? MutableMap<String, Any?>)
                ?: mutableMapOf<String, Any?>().also {
                    setTag(VIEW_TAG_KEY_TAGS_HASH_MAP, it)
                }

fun View.setTag(key: String, tag: Any?) {
    getTags()[key] = tag
}

@CheckResult
fun View.getTag(key: String): Any? = getTags()[key]

@CheckResult
fun View.getTagFromChildren(key: String): Any? =
        findViewWithTagKey(key)?.getTag(key)

/**
 * Look for a child view with the given tag key. If this view has the given
 * key, return this view.
 *
 * @param tagKey tag key to search
 * @return found view or null
 */
@CheckResult
fun View.findViewWithTagKey(tagKey: String): View? = findViewWithTagKey(tagKey, null)

/**
 * Look for a child view with the given key with value tag. If this view has the given
 * key with value tag, return this view.
 *
 * @param tagKey tag key to search
 * @param tag    tag to search or null for any tag
 * @return found view or null
 */
@CheckResult
fun View.findViewWithTagKey(tagKey: String, tag: Any?): View? {
    if (if (tag == null) getTags().containsKey(tagKey) else getTag(tagKey) == tag)
        return this

    (this as? ViewGroup)?.forEachChild { it.findViewWithTagKey(tagKey, tag)?.let { return it } }
    return null
}