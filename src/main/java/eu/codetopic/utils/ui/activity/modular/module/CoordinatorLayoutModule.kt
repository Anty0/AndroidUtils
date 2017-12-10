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
import android.view.View
import android.view.ViewGroup

import eu.codetopic.utils.R
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule

/**
 * Created by anty on 10/13/17.
 *
 * @author anty
 */
class CoordinatorLayoutModule : SimpleActivityCallBackModule() {

    override fun onSetContentView(@LayoutRes layoutResID: Int, callBack: SetContentViewCallBack) {
        callBack.set(R.layout.activity_module_coordinator_layout)
        callBack.addViewAttachedCallBack {
            activity.let {
                it.layoutInflater.inflate(layoutResID, it.findViewById(R.id.base_coordinator_layout_content))
            }
        }
    }

    override fun onSetContentView(view: View, callBack: SetContentViewCallBack) {
        callBack.set(R.layout.activity_module_coordinator_layout)
        callBack.addViewAttachedCallBack {
            activity.findViewById<ViewGroup>(R.id.base_coordinator_layout_content)
                    .addView(view)
        }
    }

    override fun onSetContentView(view: View, params: ViewGroup.LayoutParams,
                                  callBack: SetContentViewCallBack) {
        callBack.set(R.layout.activity_module_coordinator_layout)
        callBack.addViewAttachedCallBack {
            activity.findViewById<ViewGroup>(R.id.base_coordinator_layout_content)
                    .addView(view, params)
        }
    }
}
