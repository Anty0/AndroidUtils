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

package eu.codetopic.utils.data.preferences.support

import android.content.SharedPreferences

class NoApplyPreferencesEditor(private val editor : SharedPreferences.Editor,
                               private val throwableMessage: String = "Changes in this editor cannot be saved from this context!")
    : SharedPreferences.Editor by editor {

    override fun commit(): Boolean {
        throw UnsupportedOperationException(throwableMessage)
    }

    override fun apply() {
        throw UnsupportedOperationException(throwableMessage)
    }
}