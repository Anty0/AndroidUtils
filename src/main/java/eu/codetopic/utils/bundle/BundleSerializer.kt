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
import eu.codetopic.utils.AndroidExtensions.deserializeBundle
import eu.codetopic.utils.AndroidExtensions.serialize
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl

/**
 * @author anty
 */
@Serializer(forClass = Bundle::class)
object
BundleSerializer : KSerializer<Bundle> {

    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("android.os.Bundle")

    override fun save(output: KOutput, obj: Bundle) = output.writeStringValue(obj.serialize())

    override fun load(input: KInput): Bundle = deserializeBundle(input.readStringValue())
}