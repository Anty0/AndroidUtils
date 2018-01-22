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

import android.app.Activity
import android.app.NotificationManager
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.AnyRes
import android.support.annotation.StringRes
import android.util.SparseArray
import eu.codetopic.utils.data.getter.DataGetter
import eu.codetopic.utils.ui.container.adapter.ArrayEditAdapter
import android.os.Parcel
import android.util.Base64
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import eu.codetopic.java.utils.log.Log
import kotlinx.io.ByteArrayInputStream
import kotlinx.io.ByteArrayOutputStream
import kotlinx.serialization.internal.readToByteBuffer
import java.io.BufferedOutputStream
import java.util.zip.GZIPOutputStream
import java.nio.file.Files.size
import java.util.zip.GZIPInputStream


object AndroidExtensions {

    private const val LOG_TAG = "AndroidExtensions"

    fun SharedPreferences.edit(vararg changes: Pair<String, Any>) = edit{ put(*changes) }

    fun SharedPreferences.Editor.put(vararg changes: Pair<String, Any>): SharedPreferences.Editor {
        changes.forEach {
            val (key, value) = it
            when(value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    putStringSet(key, value as? Set<String>
                            ?:
                            throw IllegalArgumentException(
                                    "Unsupported Set content type: ${value.javaClass}"))
                }
                else -> throw IllegalArgumentException("Unsupported value type: ${value.javaClass}")
            }
        }
        return this
    }

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

    //////////////////////////////////////
    //////REGION - CONTEXT////////////////
    //////////////////////////////////////

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

    fun Context.getIconics(icon: IIcon): IconicsDrawable =
            IconicsDrawable(this, icon).colorDefault(this)

    val Context.notificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val Context.wifiManager
        get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val Context.baseActivity: Activity?
        get() = this as? Activity ?: (this as? ContextWrapper)?.baseContext?.baseActivity

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

    //////////////////////////////////////
    //////REGION - ANDROID_ICONICS////////
    //////////////////////////////////////

    fun IconicsDrawable.colorDefault(context: Context): IconicsDrawable =
            color(AndroidUtils.getColorFromAttr(context, R.attr.colorDrawable, Color.BLACK))


    // TODO: move functions from AndroidUtils, that can be implemented as extensions here and implement them as extensions
}