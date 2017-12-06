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

package eu.codetopic.utils.ui.activity.modular.module

import android.support.annotation.LayoutRes
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup

import eu.codetopic.utils.R
import eu.codetopic.utils.ui.activity.modular.ActivityCallBackModule
import eu.codetopic.utils.ui.activity.modular.ActivityCallBackModule.SetContentViewCallBack
import eu.codetopic.utils.ui.activity.modular.ModularActivity
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule

class ToolbarModule : SimpleActivityCallBackModule() {

    companion object {

        private const val LOG_TAG = "ToolbarModule"
    }

    override fun onSetContentView(@LayoutRes layoutResID: Int, callBack: SetContentViewCallBack) {
        callBack.set(R.layout.toolbar_base)
        callBack.addViewAttachedCallBack {
            activity.let {
                it.layoutInflater.inflate(layoutResID, it.findViewById(R.id.base_content))
            }
        }
        setupCallback(callBack)
    }

    override fun onSetContentView(view: View, callBack: SetContentViewCallBack) {
        callBack.set(R.layout.toolbar_base)
        callBack.addViewAttachedCallBack {
            activity.findViewById<ViewGroup>(R.id.base_content).addView(view)
        }
        setupCallback(callBack)
    }

    override fun onSetContentView(view: View, params: ViewGroup.LayoutParams, callBack: SetContentViewCallBack) {
        callBack.set(R.layout.toolbar_base)
        callBack.addViewAttachedCallBack {
            (activity.findViewById<View>(R.id.base_content) as ViewGroup).addView(view, params)
        }
        setupCallback(callBack)
    }

    private fun setupCallback(callBack: SetContentViewCallBack) {
        callBack.addViewAttachedCallBack({
            activity.let { it.setSupportActionBar(it.findViewById(R.id.toolbar)) }
        })
    }
}
