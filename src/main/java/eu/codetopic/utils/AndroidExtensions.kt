/*
 * utils
 * Copyright (C)   2017  anty
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

package eu.codetopic.utils

import android.content.*
import android.net.Uri
import android.support.annotation.AnyRes
import android.support.annotation.StringRes
import android.util.SparseArray
import eu.codetopic.utils.data.getter.DataGetter
import eu.codetopic.utils.ui.container.adapter.ArrayEditAdapter

object AndroidExtensions {

    inline fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) =
            edit().apply { block() }.apply()

    inline fun <T : Any> ArrayEditAdapter<T, *>.edit(block: ArrayEditAdapter.Editor<T>.() -> Unit) =
            edit().apply { block() }.apply()

    inline fun broadcast(crossinline block: BroadcastReceiver.(context: Context, intent: Intent?) -> Unit): BroadcastReceiver {
        return object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) = block(context, intent)
        }
    }

    fun intentFilter(vararg actions: String): IntentFilter {
        return IntentFilter().apply { actions.forEach { addAction(it) } }
    }

    fun intentFilter(vararg getters: DataGetter<*>): IntentFilter {
        return IntentFilter().apply {
            getters.forEach {
                if (it.hasDataChangedBroadcastAction()) addAction(it.dataChangedBroadcastAction)
            }
        }
    }

    fun Context.getFormattedText(@StringRes stringId: Int, vararg args: Any): CharSequence {
        return AndroidUtils.getFormattedText(getString(stringId), *args)
    }

    fun Context.getResourceUri(@AnyRes resource: Int): Uri {
        return with (resources) {
            Uri.parse("""${ContentResolver.SCHEME_ANDROID_RESOURCE}://
                |${getResourcePackageName(resource)}/
                |${getResourceTypeName(resource)}/
                |${getResourceEntryName(resource)}""".trimMargin())
        }
    }

    //////////////////////////////////////
    //////REGION - SPARSE_ARRAY///////////
    //////////////////////////////////////

    inline fun <E> SparseArray<E>.getOrPut(key: Int, defaultValue: () -> E): E =
            get(key) ?: defaultValue().also { put(key, it) }

    // TODO: add more SparseArray extension functions

    // TODO: move functions from AndroidUtils, that can be implemented as extensions here and implement them as extensions
}