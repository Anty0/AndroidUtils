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

package eu.codetopic.utils.notifications.manager.data

import android.os.Bundle
import kotlinx.serialization.Serializable

import eu.codetopic.utils.AndroidExtensions.deserializeBundle
import eu.codetopic.utils.AndroidExtensions.serialize
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Serializable
class NotificationInfo internal constructor(val bundleStr: String) {

    internal constructor(data: Bundle) : this(data.serialize())

    @Transient
    val data: Bundle
        get() = deserializeBundle(bundleStr)

    override fun toString(): String = "NotificationInfo(data='$data')"
}