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

package eu.codetopic.utils.notifications.manager2.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Serializable
internal class CommonPersistentNotifyId(override val idGroup: String,
                                        override val idChannel: String,
                                        override val idNotify: Int,
                                        override val timeWhen: Long = System.currentTimeMillis(),
                                        override val isRefreshable: Boolean = true) : NotifyId() {

    @Transient
    override val isSummary: Boolean
        get() = false

    @Transient
    override val isPersistent: Boolean
        get() = true
}