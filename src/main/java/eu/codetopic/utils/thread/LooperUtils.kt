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

package eu.codetopic.utils.thread

import android.content.Context
import android.os.Handler
import android.view.View

import eu.codetopic.java.utils.log.Log

object LooperUtils {

    private const val LOG_TAG = "LooperUtils"

    // TODO: maybe use Looper.getMainLooper to obtain main looper instead

    private var initialized = false
    lateinit var mainLooperHandler: Handler private set
    lateinit var mainThread: Thread private set

    @Synchronized
    fun initialize(context: Context) {
        if (initialized) throw IllegalStateException(LOG_TAG + " is still initialized")
        initialized = true

        val looper = context.applicationContext.mainLooper
        mainLooperHandler = Handler(looper)
        mainThread = looper.thread
    }

    @JvmStatic
    fun isOnMainThread(): Boolean = Thread.currentThread() === mainThread

    @JvmStatic
    inline fun runOnMainThread(crossinline action: () -> Unit): Boolean {
        return if (!isOnMainThread()) postOnMainThread(action)
        else { action(); true }
    }

    @JvmStatic
    inline fun postOnMainThread(crossinline action: () -> Unit): Boolean = mainLooperHandler.post { action() }

    @JvmStatic
    inline fun runOnContextThread(context: Context?, crossinline action: () -> Unit): Boolean = when {
        context == null -> runOnMainThread(action)
        !isOnContextThread(context) -> postOnContextThread(context, action)
        else -> { action(); true }
    }

    @JvmStatic
    fun isOnContextThread(context: Context?): Boolean {
        return if (context == null) isOnMainThread()
        else Thread.currentThread() === context.mainLooper.thread
    }

    @JvmStatic
    inline fun postOnContextThread(context: Context?, crossinline action: () -> Unit): Boolean =
            context != null && Handler(context.mainLooper).post { action() } || postOnMainThread(action)

    @JvmStatic
    inline fun postOnViewThread(view: View?, crossinline action: () -> Unit): Boolean =
            view != null && view.post { action() } || postOnMainThread(action)
}
