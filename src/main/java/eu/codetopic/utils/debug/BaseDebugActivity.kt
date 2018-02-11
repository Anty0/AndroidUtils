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

package eu.codetopic.utils.debug

import android.os.Bundle
import android.support.annotation.CallSuper

import eu.codetopic.utils.debug.items.ConnectivityDebugItem
import eu.codetopic.utils.debug.items.IssuesDebugItem
import eu.codetopic.utils.debug.items.LoggingDebugItem
import eu.codetopic.utils.debug.items.notify.NotifyManagerDebugItem
import eu.codetopic.utils.ui.activity.modular.module.BackButtonModule
import eu.codetopic.utils.ui.activity.modular.module.ToolbarModule
import eu.codetopic.utils.ui.activity.modular.ModularActivity
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.recycler.Recycler

abstract class BaseDebugActivity : ModularActivity(ToolbarModule(), BackButtonModule()) {

    companion object {

        private const val LOG_TAG = "BaseDebugActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Recycler.inflate().on(this)
                .setAdapter(mutableListOf<CustomItem>().apply {
                    prepareDebugItems(this)
                })
    }

    @CallSuper
    protected open fun prepareDebugItems(items: MutableList<CustomItem>) {
        items.add(ConnectivityDebugItem())
        items.add(NotifyManagerDebugItem())
        items.add(IssuesDebugItem())
        items.add(LoggingDebugItem())
        // add here Utils debug items
    }

}
