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

package eu.codetopic.utils.notifications.manager

import android.app.Notification
import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.util.NotifyChannel
import eu.codetopic.utils.notifications.manager.util.NotifyGroup

/**
 * @author anty
 */

// NotifyChannel

fun NotifyChannel.combinedIdFor(group: NotifyGroup): String =
        NotifyChannel.combinedId(group.id, this.id)

fun NotifyChannel.combinedIds(): List<String> = NotifyChannel.combinedIds(id)

fun NotifyChannel.combinedIdsMap(): Map<String, String> = NotifyChannel.combinedIdsMap(id)

// MultiNotificationBuilder

@MainThread
fun MultiNotificationBuilder.showAll(context: Context, optimise: Boolean = true) =
        NotifyManager.notifyAll(context, this, optimise)

@MainThread
suspend fun MultiNotificationBuilder.sShowAll(context: Context,
                                              optimise: Boolean = true): Map<out NotifyId, Bundle> =
        NotifyManager.sNotifyAll(context, this, optimise)

// NotificationBuilder

@MainThread
fun NotificationBuilder.build(context: Context, hasTag: Boolean): Pair<NotifyId, Notification> =
        NotifyManager.build(context, this, hasTag)

@MainThread
fun NotificationBuilder.buildOrNull(context: Context,
                                    hasTag: Boolean): Pair<NotifyId, Notification>? =
        NotifyManager.buildOrNull(context, this, hasTag)

@MainThread
fun NotificationBuilder.show(context: Context, optimise: Boolean = true) =
        NotifyManager.notify(context, this, optimise)

@MainThread
suspend fun NotificationBuilder.sShow(context: Context, optimise: Boolean = true): NotifyId? =
        NotifyManager.sNotify(context, this, optimise)