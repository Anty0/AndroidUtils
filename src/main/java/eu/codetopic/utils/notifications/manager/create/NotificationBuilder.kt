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

package eu.codetopic.utils.notifications.manager.create

import android.app.Notification
import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.utils.bundle.BundleSerializer
import eu.codetopic.utils.notifications.manager.NotifyClassifier
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.data.CommonNotifyId
import eu.codetopic.utils.notifications.manager.data.CommonPersistentNotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Serializable
class NotificationBuilder(val groupId: String, val channelId: String) {

    companion object {

        private const val LOG_TAG = "NotificationBuilder"

        @Suppress("NOTHING_TO_INLINE")
        inline fun create(groupId: String, channelId: String): NotificationBuilder =
                NotificationBuilder(groupId, channelId)

        @Suppress("NOTHING_TO_INLINE")
        inline fun create(groupId: String, channelId: String, data: Bundle): NotificationBuilder =
                NotificationBuilder(groupId, channelId).apply { this.data = data }

        inline fun create(groupId: String, channelId: String,
                          init: NotificationBuilder.() -> Unit): NotificationBuilder =
                NotificationBuilder(groupId, channelId).apply(init)

        @MainThread
        fun NotificationBuilder.build(context: Context, hasTag: Boolean): Pair<NotifyId, Notification> =
                NotifyManager.build(context, this, hasTag)

        @MainThread
        fun NotificationBuilder.show(context: Context): NotifyId =
                NotifyManager.notify(context, this)

        @MainThread
        fun NotificationBuilder.requestShow(context: Context, optimise: Boolean = true) =
                NotifyManager.requestNotify(context, this, optimise)

        @MainThread
        suspend fun NotificationBuilder.requestSuspendShow(context: Context,
                                                           optimise: Boolean = true): NotifyId =
                NotifyManager.requestSuspendNotify(context, this, optimise)
    }

    var timeWhen: Long = System.currentTimeMillis()

    var persistent: Boolean = false

    var refreshable: Boolean = false

    @Serializable(with = BundleSerializer::class)
    var data: Bundle = Bundle.EMPTY

    operator fun component1() = groupId
    operator fun component2() = channelId
    operator fun component3() = timeWhen
    operator fun component4() = persistent
    operator fun component5() = refreshable
    operator fun component6() = data
}
