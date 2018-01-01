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

import android.app.NotificationManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.AnyRes
import android.support.annotation.StringRes
import android.util.SparseArray
import eu.codetopic.utils.data.getter.DataGetter
import eu.codetopic.utils.ui.container.adapter.ArrayEditAdapter
import android.os.Parcel
import android.util.Base64
import eu.codetopic.java.utils.log.Log
import kotlinx.io.ByteArrayInputStream
import kotlinx.io.ByteArrayOutputStream
import kotlinx.serialization.internal.readToByteBuffer
import java.io.BufferedOutputStream
import java.util.zip.GZIPOutputStream
import java.nio.file.Files.size
import java.util.zip.GZIPInputStream


object AndroidExtensions {

    const val LOG_TAG = "AndroidExtensions"

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

    fun intentFilter(vararg actions: Any): IntentFilter {
        return IntentFilter().apply {
            actions.forEach {
                when (it) {
                    is DataGetter<*> -> {
                        if (it.hasDataChangedBroadcastAction()) {
                            addAction(it.dataChangedBroadcastAction)
                        } else {
                            Log.e(LOG_TAG, "intentFilter() -> (action=$it) -> " +
                                    "Can't add data getter action:" +
                                    " getter hasn't got data changed broadcast action")
                        }
                    }
                    is String -> addAction(it)
                    else -> Log.e(LOG_TAG, "intentFilter() -> (action=$it) -> " +
                            "Can't add action: Unknown action type")
                }
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
    //////REGION - CONTEXT////////////////
    //////////////////////////////////////

    val Context.notificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //////////////////////////////////////
    //////REGION - BUNDLES////////////////
    //////////////////////////////////////

    fun Bundle.serialize(): String {
        Parcel.obtain().use { parcel ->
            parcel.writeBundle(this)
            ByteArrayOutputStream().use {
                GZIPOutputStream(BufferedOutputStream(it)).use {
                    it.write(parcel.marshall())
                }
                return Base64.encodeToString(it.toByteArray(), 0)
            }
        }
    }

    fun deserializeBundle(bundleStr: String): Bundle {
        Parcel.obtain().use { parcel ->
            GZIPInputStream(ByteArrayInputStream(Base64.decode(bundleStr, 0))).use {
                it.readBytes().let {
                    parcel.unmarshall(it, 0, it.size)
                }
            }
            parcel.setDataPosition(0)
            return parcel.readBundle(UtilsBase.javaClass.classLoader)
        }
    }

    //////////////////////////////////////
    //////REGION - PARCELABLE/////////////
    //////////////////////////////////////

    inline fun <T : Parcel?, R> T.use(block: (T) -> R): R {
        var exception: Throwable? = null
        try {
            return block(this)
        } catch (e: Throwable) {
            exception = e
            throw e
        } finally {
            when {
                this == null -> {}
                exception == null -> recycle()
                else ->
                    try {
                        recycle()
                    } catch (closeException: Throwable) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            exception.addSuppressed(closeException)
                        }
                    }
            }
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