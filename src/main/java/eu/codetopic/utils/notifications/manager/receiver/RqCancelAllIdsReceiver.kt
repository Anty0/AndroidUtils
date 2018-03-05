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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.bundle.BundleSerializer
import eu.codetopic.utils.getKSerializableExtra
import eu.codetopic.utils.notifications.manager.Notifier
import eu.codetopic.utils.notifications.manager.NotifyBase
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyIdSerializer
import eu.codetopic.utils.putKSerializableExtra
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.map
import org.jetbrains.anko.bundleOf

/**
 * @author anty
 */
class RqCancelAllIdsReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RqCancelAllIdsReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val EXTRA_NOTIFY_IDS_LIST = "$NAME.NOTIFY_IDS_LIST"

        private val PARAM_NOTIFY_IDS_SERIALIZER = NotifyIdSerializer.list
        internal val RESULT_SERIALIZER = (NotifyIdSerializer to BundleSerializer).map

        internal fun getStartIntent(context: Context, notifyIds: Collection<NotifyId>): Intent {
            if (notifyIds.size == 1)
                return RqCancelReceiver.getStartIntent(context, notifyIds.first())

            return Intent(context, RqCancelAllIdsReceiver::class.java)
                    .putKSerializableExtra(
                            name = EXTRA_NOTIFY_IDS_LIST,
                            value = notifyIds.toList(),
                            saver = PARAM_NOTIFY_IDS_SERIALIZER
                    )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyBase.assertInitialized(context)

            val notifyIds = intent
                    .getKSerializableExtra(EXTRA_NOTIFY_IDS_LIST, PARAM_NOTIFY_IDS_SERIALIZER)
                    ?: throw IllegalArgumentException("No notifications ids list received by intent")

            val result = Notifier.cancelAll(context, notifyIds)

            if (isOrderedBroadcast) {
                setResult(REQUEST_RESULT_OK, null, bundleOf(
                        REQUEST_EXTRA_RESULT to JSON.stringify(
                                RESULT_SERIALIZER,
                                result.toMap()
                        )
                ))
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)

            if (isOrderedBroadcast) {
                setResult(REQUEST_RESULT_FAIL, null, bundleOf(
                        REQUEST_EXTRA_THROWABLE to e
                ))
            }
        }
    }
}