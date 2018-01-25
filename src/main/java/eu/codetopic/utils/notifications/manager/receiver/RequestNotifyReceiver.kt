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
import android.os.Bundle
import eu.codetopic.java.utils.JavaExtensions.alsoIf
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.notifications.manager.Notifications
import eu.codetopic.utils.notifications.manager.NotificationsManager

/**
 * @author anty
 */
class RequestNotifyReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RequestNotifyReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val EXTRA_GROUP_ID = "$NAME.GROUP_ID"
        private const val EXTRA_CHANNEL_ID = "$NAME.CHANNEL_ID"
        private const val EXTRA_DATA_BUNDLE = "$NAME.DATA_BUNDLE"
        private const val EXTRA_WHEN_TIME = "$NAME.WHEN_TIME"

        internal fun getStartIntent(context: Context, groupId: String,
                                    channelId: String, data: Bundle,
                                    whenTime: Long = System.currentTimeMillis()): Intent =
                Intent(context, RequestNotifyReceiver::class.java)
                        .putExtra(EXTRA_GROUP_ID, groupId)
                        .putExtra(EXTRA_CHANNEL_ID, channelId)
                        .putExtra(EXTRA_DATA_BUNDLE, data)
                        .putExtra(EXTRA_WHEN_TIME, whenTime)
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotificationsManager.assertInitialized()

            val groupId = intent.getStringExtra(EXTRA_GROUP_ID)
                    ?: throw IllegalArgumentException("No group id received by intent")
            val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
                    ?: throw IllegalArgumentException("No channel id received by intent")
            val data = intent.getBundleExtra(EXTRA_DATA_BUNDLE)
                    ?: throw IllegalArgumentException("No data bundle received by intent")
            val whenTime = intent.getLongExtra(EXTRA_WHEN_TIME, -1)
                    .alsoIf({ it == -1L }) {
                        throw IllegalArgumentException("No when time received by intent")
                    }

            Notifications.notify(context, groupId, channelId, data, whenTime)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
        }
    }
}