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
import eu.codetopic.java.utils.letIfNull
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.notifications.manager.NotifyManager
import org.jetbrains.anko.bundleOf

/**
 * @author anty
 */
class RqSetEnabledReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RqSetEnabledReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val EXTRA_GROUP_ID = "$NAME.GROUP_ID"
        private const val EXTRA_CHANNEL_ID = "$NAME.CHANNEL_ID"
        private const val EXTRA_ENABLE = "$NAME.CHANNEL_ID"

        internal fun getStartIntent(context: Context, groupId: String?,
                                    channelId: String, enable: Boolean): Intent =
                Intent(context, RqSetEnabledReceiver::class.java)
                        .putExtra(EXTRA_GROUP_ID, groupId)
                        .putExtra(EXTRA_CHANNEL_ID, channelId)
                        .putExtra(EXTRA_ENABLE, enable)
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyManager.assertInitialized(context)

            val groupId = intent.takeIf { it.hasExtra(EXTRA_GROUP_ID) }
                    .letIfNull { throw IllegalArgumentException("No group id received by intent") }
                    .getStringExtra(EXTRA_GROUP_ID)
            val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
                    ?: throw IllegalArgumentException("No channel id received by intent")
            val enable = intent.takeIf { it.hasExtra(EXTRA_ENABLE) }
                    ?.getBooleanExtra(EXTRA_ENABLE, true)
                    ?: throw IllegalArgumentException("No target enable state received by intent")

            NotifyManager.setChannelEnabled(context, groupId, channelId, enable)

            if (isOrderedBroadcast) {
                setResult(NotifyManager.REQUEST_RESULT_OK, null, null)
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