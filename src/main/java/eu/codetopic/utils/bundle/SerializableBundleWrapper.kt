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

package eu.codetopic.utils.bundle

import android.os.Bundle
import eu.codetopic.utils.deserializeBundle
import eu.codetopic.utils.serialize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Suppress("DEPRECATION")
@Deprecated("Use BundleSerializer instead")
@Serializable
class SerializableBundleWrapper(private val bundleStr: String) {

    companion object {

        fun Bundle.asSerializable(): SerializableBundleWrapper =
                SerializableBundleWrapper(this)
    }

    constructor(bundle: Bundle) : this(bundle.serialize())

    @Transient
    private val bundleBase: Bundle
        get() = deserializeBundle(bundleStr)

    @Transient
    val bundle: Bundle get() = Bundle(bundleBase)

    override fun toString(): String = "SerializableBundleWrapper(bundle='$bundleBase')"
}