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

import android.accounts.AccountManager
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.*
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.content.res.TypedArray
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
import android.support.annotation.PluralsRes
import android.support.annotation.RequiresApi
import android.util.Base64
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import eu.codetopic.java.utils.log.Log
import kotlinx.io.ByteArrayInputStream
import kotlinx.io.ByteArrayOutputStream
import kotlinx.serialization.KSerialLoader
import kotlinx.serialization.KSerialSaver
import kotlinx.serialization.json.JSON
import java.io.BufferedOutputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.GZIPInputStream
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * @author anty
 */

private const val LOG_TAG = "extensions"

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

inline fun receiver(crossinline block: BroadcastReceiver.(context: Context, intent: Intent?) -> Unit): BroadcastReceiver {
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

fun Context.getFormattedQuantityText(@PluralsRes stringId: Int, quantity: Int,
                                     vararg args: Any): CharSequence {
    return AndroidUtils.getFormattedText(resources.getQuantityString(stringId, quantity), *args)
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

val Context.baseActivity: Activity?
    get() = internalGetBaseActivity()

private tailrec fun Context.internalGetBaseActivity(): Activity? =
        this as? Activity ?: (this as? ContextWrapper)?.baseContext?.internalGetBaseActivity()

suspend fun Context.sendSuspendOrderedBroadcast(intent: Intent,
                                                initialResult: OrderedBroadcastResult = OrderedBroadcastResult(),
                                                receiverPermission: String? = null) =
        suspendCoroutine<OrderedBroadcastResult> { cont ->
            sendOrderedBroadcast(
                    intent, receiverPermission,
                    receiver { _, _ ->
                        cont.resume(OrderedBroadcastResult(
                                code = resultCode,
                                data = resultData,
                                extras = getResultExtras(false)
                        ))
                    }, null,
                    initialResult.code, initialResult.data, initialResult.extras
            )
        }

data class OrderedBroadcastResult(val code: Int = 0, val data: String? = null,
                                  val extras: Bundle? = null)

//////////////////////////////////////
//////REGION - PACKAGE_MANAGER////////
//////////////////////////////////////

fun PackageManager.isAppInstalled(packageName: String): Boolean =
        getInstalledApplications(GET_META_DATA)
                .firstOrNull { it.packageName == packageName } != null

/////////////////////////////////////////
//////REGION - ATTRIBUTES////////////////
/////////////////////////////////////////

inline fun <R> TypedArray.use(block: (TypedArray) -> R): R =
        try { block(this) } finally { recycle() }

//////////////////////////////////////
//////REGION - BUNDLES////////////////
//////////////////////////////////////

fun Bundle.serializeWithGZIP(): String {
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

fun deserializeBundleWithGZIP(bundleStr: String): Bundle {
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

fun Bundle.serialize(): String {
    Parcel.obtain().use { parcel ->
        parcel.writeBundle(this)
        return Base64.encodeToString(parcel.marshall(), 0)
    }
}

fun deserializeBundle(bundleStr: String): Bundle {
    Parcel.obtain().use { parcel ->
        Base64.decode(bundleStr, 0).let {
            parcel.unmarshall(it, 0, it.size)
        }
        parcel.setDataPosition(0)
        return parcel.readBundle(UtilsBase::class.java.classLoader)
    }
}

inline fun <reified T: Any> Bundle.putKSerializable(key: String, value: T?,
                                                    json: JSON = JSON.plain) =
        putString(key, value?. let { json.stringify(value) })

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Bundle.putKSerializable(key: String, value: T, saver: KSerialSaver<T>,
                                       json: JSON = JSON.plain) =
        putString(key, json.stringify(saver, value))

inline fun <reified T: Any> Bundle.getKSerializable(key: String,
                                                    json: JSON = JSON.plain): T? =
        getString(key)?.let { json.parse(it) }

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Bundle.getKSerializable(key: String, loader: KSerialLoader<T>,
                                       json: JSON = JSON.plain): T? =
        getString(key)?.let { json.parse(loader, it) }

//////////////////////////////////////
//////REGION - Intent/////////////////
//////////////////////////////////////

inline fun <reified T: Any> Intent.putKSerializableExtra(name: String, value: T?,
                                                         json: JSON = JSON.plain): Intent =
        putExtra(name, value?.let { json.stringify(value) })

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Intent.putKSerializableExtra(name: String, value: T, saver: KSerialSaver<T>,
                                            json: JSON = JSON.plain): Intent =
        putExtra(name, json.stringify(saver, value))

inline fun <reified T: Any> Intent.getKSerializableExtra(name: String,
                                                         json: JSON = JSON.plain): T? =
        getStringExtra(name)?.let { json.parse(it) }

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Intent.getKSerializableExtra(name: String, loader: KSerialLoader<T>,
                                            json: JSON = JSON.plain): T? =
        getStringExtra(name)?.let { json.parse(loader, it) }

@Suppress("NOTHING_TO_INLINE")
inline fun Array<Intent>.asPendingActivities(context: Context, requestCode: Int,
                                             flags: Int = 0): PendingIntent =
        PendingIntent.getActivities(context, requestCode, this, flags)

@Suppress("NOTHING_TO_INLINE")
inline fun Collection<Intent>.asPendingActivities(context: Context, requestCode: Int,
                                             flags: Int = 0): PendingIntent =
        PendingIntent.getActivities(context, requestCode, this.toTypedArray(), flags)

@Suppress("NOTHING_TO_INLINE")
inline fun Intent.asPendingActivity(context: Context, requestCode: Int,
                                    flags: Int = 0): PendingIntent =
        PendingIntent.getActivity(context, requestCode, this, flags)

@Suppress("NOTHING_TO_INLINE")
inline fun Intent.asPendingBroadcast(context: Context, requestCode: Int,
                                    flags: Int = 0): PendingIntent =
        PendingIntent.getBroadcast(context, requestCode, this, flags)

@Suppress("NOTHING_TO_INLINE")
@RequiresApi(Build.VERSION_CODES.O)
inline fun Intent.asPendingForegroundService(context: Context, requestCode: Int,
                                    flags: Int = 0): PendingIntent =
        PendingIntent.getForegroundService(context, requestCode, this, flags)

@Suppress("NOTHING_TO_INLINE")
inline fun Intent.asPendingService(context: Context, requestCode: Int,
                                   flags: Int = 0): PendingIntent =
        PendingIntent.getService(context, requestCode, this, flags)

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