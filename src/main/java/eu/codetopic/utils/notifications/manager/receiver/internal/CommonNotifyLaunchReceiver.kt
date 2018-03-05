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

package eu.codetopic.utils.notifications.manager.receiver.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.bundle.BundleSerializer
import eu.codetopic.utils.getKSerializableExtra
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.notifications.manager.NotifyBase
import eu.codetopic.utils.notifications.manager.data.CommonNotifyId
import eu.codetopic.utils.notifications.manager.data.channel
import eu.codetopic.utils.notifications.manager.data.group
import eu.codetopic.utils.putKSerializableExtra

/**
 * @author anty
 */
class CommonNotifyLaunchReceiver : BroadcastReceiver() {

    companion object {
        private const val LOG_TAG = "CommonNotifyLaunchReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager.receiver.$LOG_TAG"
        private const val NAME_REQUEST_CODE_TYPE = "$NAME.LAST_REQUEST_CODE"
        private const val EXTRA_NOTIFY_ID = "$NAME.NOTIFY_ID"
        private const val EXTRA_NOTIFY_DATA = "$NAME.NOTIFY_DATA"
        private const val EXTRA_AUTO_CANCEL = "$NAME.AUTO_CANCEL"

        internal val REQUEST_CODE_TYPE = Identifiers.Type(NAME_REQUEST_CODE_TYPE)

        internal fun getIntent(context: Context, notifyId: CommonNotifyId, data: Bundle,
                               autoCancel: Boolean): Intent =
                Intent(context, CommonNotifyLaunchReceiver::class.java)
                        .putKSerializableExtra(EXTRA_NOTIFY_ID, notifyId)
                        .putKSerializableExtra(EXTRA_NOTIFY_DATA, data, BundleSerializer)
                        .putExtra(EXTRA_AUTO_CANCEL, autoCancel)
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyBase.assertInitialized(context)

            val notifyId = intent.getKSerializableExtra<CommonNotifyId>(EXTRA_NOTIFY_ID)
                    ?: throw IllegalArgumentException("No notification id received by intent")
            val data = intent.getKSerializableExtra(EXTRA_NOTIFY_DATA, BundleSerializer)
                    ?: throw IllegalArgumentException("No notification data received by intent")
            val autoCancel = intent.getBooleanExtra(EXTRA_AUTO_CANCEL, false)

            val group = notifyId.group
            val channel = notifyId.channel

            channel.handleContentIntent(context, group, notifyId, data)

            if (autoCancel) {
                context.sendBroadcast(
                        CommonNotifyDeleteReceiver.getIntent(context, notifyId, data)
                )
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
        }
    }
}