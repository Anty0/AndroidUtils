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

package eu.codetopic.utils.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat

import eu.codetopic.utils.ids.Identifiers

class PostNotificationCanceler : BroadcastReceiver() {

    companion object {

        private const val EXTRA_NOTIFICATION_ID = "eu.codetopic.utils.notifications.PostNotificationCanceler.NOTIFICATION_ID"

        fun postNotificationCancel(context: Context, notificationId: Int, waitTime: Long) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                    .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + waitTime,
                            PendingIntent.getBroadcast(context, Identifiers.next(Identifiers.TYPE_REQUEST_CODE),
                                    Intent(context, PostNotificationCanceler::class.java)
                                            .putExtra(EXTRA_NOTIFICATION_ID, notificationId),
                                    PendingIntent.FLAG_CANCEL_CURRENT))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) return
        NotificationManagerCompat.from(context)
                .cancel(intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1))
    }
}
