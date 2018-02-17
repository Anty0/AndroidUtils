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
import eu.codetopic.utils.AndroidExtensions.putKSerializableExtra
import eu.codetopic.utils.AndroidExtensions.getKSerializableExtra
import eu.codetopic.utils.notifications.manager.Notifier
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyIdSerializer
import org.jetbrains.anko.bundleOf

/**
 * @author anty
 */
class RqCancelReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RqCancelReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val EXTRA_NOTIFY_ID = "$NAME.NOTIFY_ID"

        internal fun getStartIntent(context: Context, notifyId: NotifyId): Intent =
                Intent(context, RqCancelReceiver::class.java)
                        .putKSerializableExtra(EXTRA_NOTIFY_ID, notifyId, NotifyIdSerializer)
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyManager.assertInitialized(context)

            val id = intent.getKSerializableExtra(EXTRA_NOTIFY_ID, NotifyIdSerializer)
                    ?: throw IllegalArgumentException("No notification id received by intent")

            val result = Notifier.cancel(context, id)

            if (isOrderedBroadcast) {
                setResult(NotifyManager.REQUEST_RESULT_OK, null, bundleOf(
                        NotifyManager.REQUEST_EXTRA_RESULT to result
                ))
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)

            if (isOrderedBroadcast) {
                setResult(NotifyManager.REQUEST_RESULT_FAIL, null, bundleOf(
                        NotifyManager.REQUEST_EXTRA_THROWABLE to e
                ))
            }
        }
    }
}