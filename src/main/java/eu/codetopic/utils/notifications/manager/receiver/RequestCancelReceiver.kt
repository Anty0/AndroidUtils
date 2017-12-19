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
import kotlinx.serialization.json.JSON

/**
 * @author anty
 */
class RequestCancelReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RequestCancelReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val EXTRA_ID = "$NAME.ID"

        internal fun getStartIntent(context: Context, id: NotificationId): Intent =
                Intent(context, RequestCancelReceiver::class.java)
                        .putExtra(EXTRA_ID, JSON.stringify(id))
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotificationsManager.assertInitialized()

            val id = intent.getStringExtra(EXTRA_ID)?.let { JSON.parse<NotificationId>(it) }
                    ?: throw IllegalArgumentException("No notification id received by intent")

            Notifications.cancel(context, id)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
        }
    }
}