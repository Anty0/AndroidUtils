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

package eu.codetopic.utils.notifications.manager2.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.JavaExtensions.kSerializer
import eu.codetopic.utils.notifications.manager2.Notifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.data.NotifyId
import eu.codetopic.utils.notifications.manager2.data.NotifyId.Companion.stringify
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list

/**
 * @author anty
 */
class RqCancelAllIdsReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RqCancelAllIdsReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager2.receiver.$LOG_TAG"
        private const val EXTRA_NOTIFY_IDS_LIST = "$NAME.NOTIFY_IDS_LIST"

        internal fun getStartIntent(context: Context, notifyIds: Collection<NotifyId>): Intent {
            if (notifyIds.size == 1)
                return RqCancelReceiver.getStartIntent(context, notifyIds.first())

            return Intent(context, RqCancelAllIdsReceiver::class.java)
                    .putExtra(EXTRA_NOTIFY_IDS_LIST, JSON.stringify(
                            kSerializer<String>().list,
                            notifyIds.map { it.stringify() }
                    ))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyManager.assertInitialized()

            val notifyIds = intent.getStringExtra(EXTRA_NOTIFY_IDS_LIST)
                    ?.let { JSON.parse(kSerializer<String>().list, it).map { NotifyId.parse(it) } }
                    ?: throw IllegalArgumentException("No notifications ids list received by intent")

            Notifier.cancelAll(context, notifyIds)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
        }
    }
}