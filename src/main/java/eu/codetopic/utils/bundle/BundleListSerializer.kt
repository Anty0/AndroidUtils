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
import kotlinx.serialization.*

/**
 * @author anty
 */
object BundleListSerializer : KSerializer<List<Bundle>> {
    private val serializer = BundleSerializer.list

    override fun save(output: KOutput, obj: List<Bundle>) = serializer.save(output, obj)

    override fun load(input: KInput): List<Bundle> = serializer.load(input)

    override fun update(input: KInput, old: List<Bundle>): List<Bundle> = serializer.update(input, old)

    override val serialClassDesc: KSerialClassDesc
        get() = serializer.serialClassDesc
}