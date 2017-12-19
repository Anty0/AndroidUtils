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

package eu.codetopic.utils.notifications.manager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.notifications.manager.Notifications
import eu.codetopic.utils.notifications.manager.NotificationsManager
import eu.codetopic.utils.notifications.manager.data.NotificationId
import eu.codetopic.java.utils.JavaExtensions.kSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list

/**
 * @author anty
 */
class RequestCancelAllIdsReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RequestCancelAllIdsReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val EXTRA_IDS_LIST = "$NAME.IDS_LIST"

        internal fun getStartIntent(context: Context, ids: List<NotificationId>): Intent =
                Intent(context, RequestCancelAllIdsReceiver::class.java)
                        .putExtra(EXTRA_IDS_LIST,
                                JSON.stringify(kSerializer<NotificationId>().list, ids))
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotificationsManager.assertInitialized()

            val ids = intent.getStringExtra(EXTRA_IDS_LIST)?.let {
                JSON.parse(kSerializer<NotificationId>().list, it)
            } ?: throw IllegalArgumentException("No notifications ids list received by intent")

            Notifications.cancelAll(context, ids)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
            return
        }
    }
}