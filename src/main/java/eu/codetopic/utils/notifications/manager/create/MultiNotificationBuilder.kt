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

import android.os.Bundle
import eu.codetopic.utils.bundle.BundleListSerializer
import kotlinx.serialization.Serializable

/**
 * @author anty
 */
@Serializable
class MultiNotificationBuilder(val groupId: String, val channelId: String) {

    companion object {

        private const val LOG_TAG = "MultiNotificationBuilder"

        @Suppress("NOTHING_TO_INLINE")
        inline fun create(groupId: String, channelId: String, vararg data: Bundle): MultiNotificationBuilder =
                MultiNotificationBuilder(groupId, channelId)
                        .apply { this.data = data.asList() }

        inline fun create(groupId: String, channelId: String,
                          init: MultiNotificationBuilder.() -> Unit): MultiNotificationBuilder =
                MultiNotificationBuilder(groupId, channelId).apply(init)
    }

    var timeWhen: Long = System.currentTimeMillis()

    var persistent: Boolean = false

    var refreshable: Boolean = false

    @Serializable(with = BundleListSerializer::class)
    var data: List<Bundle> = emptyList()

    operator fun component1() = groupId
    operator fun component2() = channelId
    operator fun component3() = timeWhen
    operator fun component4() = persistent
    operator fun component5() = refreshable
    operator fun component6() = data
}