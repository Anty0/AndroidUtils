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

package eu.codetopic.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.PowerManager

object NetworkManager {

    private const val LOG_TAG = "NetworkManager"

    private lateinit var connManager: ConnectivityManager
    private lateinit var powerManager: PowerManager

    @JvmStatic
    val info: NetworkInfo? get() = connManager.activeNetworkInfo

    fun init(context: Context) {
        if (::connManager.isInitialized)
            throw IllegalStateException("$LOG_TAG is still initialized")
        context.applicationContext.run {
            connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        }
    }

    @JvmStatic
    fun isConnected(type: NetworkType): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && powerManager.isDeviceIdleMode) return false

        return info?.takeIf { it.isConnected }?.let {
            when (type) {
                NetworkType.ANY -> true
                NetworkType.MOBILE -> it.type == ConnectivityManager.TYPE_MOBILE
                NetworkType.WIFI -> it.type == ConnectivityManager.TYPE_WIFI
            }
        } ?: false
    }

    enum class NetworkType {
        ANY, WIFI, MOBILE
    }
}
