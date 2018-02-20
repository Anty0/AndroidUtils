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

package eu.codetopic.utils.notifications.manager.util

import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import eu.codetopic.utils.notifications.manager.data.NotifyId

/**
 * @author anty
 */
abstract class SummarizedNotifyChannel(id: String, checkForIdOverrides: Boolean,
                                       defaultEnabled: Boolean = true) :
        NotifyChannel(id, checkForIdOverrides, defaultEnabled) {

    abstract fun createSummaryNotification(context: Context, group: NotifyGroup, notifyId: NotifyId,
                                           data: Map<out NotifyId, Bundle>): NotificationCompat.Builder

    open fun handleSummaryContentIntent(context: Context, group: NotifyGroup, notifyId: NotifyId,
                                        data: Map<out NotifyId, Bundle>) {}

    open fun handleSummaryDeleteIntent(context: Context, group: NotifyGroup, notifyId: NotifyId,
                                       data: Map<out NotifyId, Bundle>) {}
}