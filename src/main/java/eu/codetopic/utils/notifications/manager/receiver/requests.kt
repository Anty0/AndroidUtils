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

package eu.codetopic.utils.notifications.manager.receiver

import android.content.Context
import android.content.Intent
import android.support.annotation.MainThread
import eu.codetopic.utils.OrderedBroadcastResult
import eu.codetopic.utils.sendSuspendOrderedBroadcast

/**
 * @author anty
 */

internal const val REQUEST_RESULT_UNKNOWN = 1
internal const val REQUEST_RESULT_FAIL = 0
internal const val REQUEST_RESULT_OK = -1

internal const val REQUEST_EXTRA_THROWABLE = "EXTRA_THROWABLE"
internal const val REQUEST_EXTRA_RESULT = "EXTRA_RESULT"

private fun initialResult(): OrderedBroadcastResult =
        OrderedBroadcastResult(
                code = REQUEST_RESULT_UNKNOWN,
                data = null,
                extras = null
        )

@MainThread
internal suspend inline fun <T> sendSuspendRequest(context: Context, name: String, intent: Intent,
                                                  resultExtractor: (result: OrderedBroadcastResult) -> T): T =
        context.sendSuspendOrderedBroadcast(intent, initialResult()).let {
            when (it.code) {
                REQUEST_RESULT_OK -> resultExtractor(it)
                REQUEST_RESULT_FAIL ->
                    throw it.extras?.getSerializable(REQUEST_EXTRA_THROWABLE) as? Throwable
                            ?: RuntimeException("Unknown fail result received from $name")
                REQUEST_RESULT_UNKNOWN ->
                    // FIXME: Caused be some ASUS devices, maybe add some warning to user...
                    throw RuntimeException("Failed to process broadcast by $name")
                else -> throw RuntimeException("Unknown resultCode received from $name: ${it.code}")
            }
        }

@MainThread
internal suspend inline fun <T> sendSuspendRequestNotNull(context: Context, name: String, intent: Intent,
                                                         resultExtractor: (result: OrderedBroadcastResult) -> T?): T =
        sendSuspendRequest(context, name, intent) {
            resultExtractor(it)
                    ?: throw RuntimeException("Failed to extract result of $name")
        }