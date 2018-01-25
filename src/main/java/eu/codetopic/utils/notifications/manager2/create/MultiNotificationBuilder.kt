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
import eu.codetopic.utils.bundle.SerializableBundleWrapper
import eu.codetopic.utils.bundle.SerializableBundleWrapper.Companion.asSerializable
import eu.codetopic.utils.notifications.manager2.Notifier
import eu.codetopic.utils.notifications.manager2.NotifyClassifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.data.CommonNotifyId
import eu.codetopic.utils.notifications.manager2.data.CommonPersistentNotifyId
import eu.codetopic.utils.notifications.manager2.data.NotifyId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Serializable
class MultiNotificationBuilder(val groupId: String, val channelId: String) {

    companion object {

        private const val LOG_TAG = "MultiNotificationBuilder"

        inline fun notifications(groupId: String, channelId: String,
                                 init: MultiNotificationBuilder.() -> Unit): MultiNotificationBuilder =
                MultiNotificationBuilder(groupId, channelId).apply(init)

        @MainThread
        fun MultiNotificationBuilder.showAll(context: Context): Map<NotifyId, Bundle> =
                Notifier.notifyAll(context, this)

        @MainThread
        fun MultiNotificationBuilder.requestShowAll(context: Context, optimise: Boolean = true) =
                NotifyManager.requestNotifyAll(context, this, optimise)

        internal fun MultiNotificationBuilder.build(context: Context): Map<NotifyId, Bundle> {
            val (groupId, channelId, timeWhen, persistent, refreshable, data) = this
            val group = NotifyClassifier.findGroup(groupId)
            val channel = NotifyClassifier.findChannel(channelId)

            return data.map {
                val notifyId = channel.nextId(context, group, it)

                val id = if (persistent)
                    CommonPersistentNotifyId(groupId, channelId, notifyId, timeWhen, refreshable)
                else CommonNotifyId(groupId, channelId, notifyId, timeWhen)

                return@map id to it
            }.toMap()
        }
    }

    var timeWhen: Long = System.currentTimeMillis()

    var persistent: Boolean = false

    var refreshable: Boolean = false

    private var serializedData: List<SerializableBundleWrapper> = emptyList()

    @Transient
    val data: List<Bundle>
        get() = serializedData.map { it.bundle }

    fun addData(vararg data: Bundle) {
        serializedData.toMutableList().addAll(
                data.map { it.asSerializable() }
        )
    }

    fun addData(data: Collection<Bundle>) {
        serializedData.toMutableList().addAll(
                data.map { it.asSerializable() }
        )
    }

    operator fun component1() = groupId
    operator fun component2() = channelId
    operator fun component3() = timeWhen
    operator fun component4() = persistent
    operator fun component5() = refreshable
    operator fun component6() = data
}