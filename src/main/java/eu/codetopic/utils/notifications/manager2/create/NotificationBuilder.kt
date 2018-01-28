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

package eu.codetopic.utils.notifications.manager2.create

import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.bundle.SerializableBundleWrapper
import eu.codetopic.utils.bundle.SerializableBundleWrapper.Companion.asSerializable
import eu.codetopic.utils.notifications.manager2.Notifier
import eu.codetopic.utils.notifications.manager2.NotifyClassifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.data.CommonNotifyId
import eu.codetopic.utils.notifications.manager2.data.CommonPersistentNotifyId
import eu.codetopic.utils.notifications.manager2.data.NotifyId
import eu.codetopic.utils.notifications.manager2.save.NotifyData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Serializable
class NotificationBuilder(val groupId: String, val channelId: String) {

    companion object {

        private const val LOG_TAG = "NotificationBuilder"

        inline fun create(groupId: String, channelId: String,
                          init: NotificationBuilder.() -> Unit): NotificationBuilder =
                NotificationBuilder(groupId, channelId).apply(init)

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

        internal fun NotificationBuilder.build(context: Context): Pair<NotifyId, Bundle> {
            val (groupId, channelId, timeWhen, persistent, refreshable, data) = this
            val group = NotifyClassifier.findGroup(groupId)
            val channel = NotifyClassifier.findChannel(channelId)
            val notifyId = channel.nextId(context, group, data)

            val id = if (persistent)
                CommonPersistentNotifyId(groupId, channelId, notifyId, timeWhen, refreshable)
            else CommonNotifyId(groupId, channelId, notifyId, timeWhen)
            return id to data
        }
    }

    var timeWhen: Long = System.currentTimeMillis()

    var persistent: Boolean = false

    var refreshable: Boolean = false

    private var serializedData: SerializableBundleWrapper? = null

    @Transient
    var data: Bundle
        get() = serializedData?.bundle ?: Bundle.EMPTY
    set(value) { serializedData = value.asSerializable() }

    operator fun component1() = groupId
    operator fun component2() = channelId
    operator fun component3() = timeWhen
    operator fun component4() = persistent
    operator fun component5() = refreshable
    operator fun component6() = data
}
