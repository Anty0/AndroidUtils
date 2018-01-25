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
import eu.codetopic.utils.AndroidExtensions.putKotlinSerializableExtra
import eu.codetopic.utils.AndroidExtensions.getKotlinSerializableExtra
import eu.codetopic.utils.notifications.manager2.Notifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.create.MultiNotificationBuilder

/**
 * @author anty
 */
class RqNotifyAllReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RqNotifyAllReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager2.receiver.$LOG_TAG"
        private const val EXTRA_BUILDER = "$NAME.BUILDER"

        internal fun getStartIntent(context: Context, builder: MultiNotificationBuilder): Intent {
            return Intent(context, RqNotifyAllReceiver::class.java)
                    .putKotlinSerializableExtra(EXTRA_BUILDER, builder)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyManager.assertInitialized()

            val builder = intent.getKotlinSerializableExtra<MultiNotificationBuilder>(EXTRA_BUILDER)
                    ?: throw IllegalArgumentException("No notification builder received by intent")

            Notifier.notifyAll(context, builder)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)
        }
    }
}