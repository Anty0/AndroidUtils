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

package eu.codetopic.utils.notifications.manager2.receiver.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.AndroidExtensions.putKotlinSerializableExtra
import eu.codetopic.utils.AndroidExtensions.getKotlinSerializableExtra
import eu.codetopic.utils.notifications.manager2.*
import eu.codetopic.utils.notifications.manager2.data.NotifyId.Companion.group
import eu.codetopic.utils.notifications.manager2.data.NotifyId.Companion.channel
import eu.codetopic.utils.notifications.manager2.data.SummaryNotifyId
import eu.codetopic.utils.notifications.manager2.save.NotifyData
import eu.codetopic.utils.notifications.manager2.util.SummarizedNotifyChannel

/**
 * @author anty
 */
class SummaryNotifyDeleteReceiver : BroadcastReceiver() {

    companion object {
        private const val LOG_TAG = "SummaryNotifyDeleteReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager2.receiver.$LOG_TAG"
        private const val NAME_REQUEST_CODE_TYPE = "$NAME.LAST_REQUEST_CODE"
        private const val EXTRA_NOTIFY_ID = "$NAME.NOTIFY_ID"

        internal val REQUEST_CODE_TYPE = Identifiers.Type(NAME_REQUEST_CODE_TYPE)

        internal fun getIntent(context: Context, notifyId: SummaryNotifyId): Intent =
                Intent(context, SummaryNotifyDeleteReceiver::class.java)
                        .putKotlinSerializableExtra(EXTRA_NOTIFY_ID, notifyId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyManager.assertInitialized()

            val notifyId = intent.getKotlinSerializableExtra<SummaryNotifyId>(EXTRA_NOTIFY_ID)
                    ?: throw IllegalArgumentException("No notification id received by intent")

            val group = notifyId.group
            val channel = notifyId.channel as? SummarizedNotifyChannel
                    ?: throw IllegalArgumentException("Received delete request on summary " +
                            "notification with channel without summary implementation.")

            val data = NotifyData.instance.removeAll(notifyId.idGroup, notifyId.idChannel)

            Notifier.refreshSummaryOf(context, notifyId)

            channel.handleSummaryDeleteIntent(context, group, notifyId, data)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
        }
    }
}