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

package eu.codetopic.utils.ui.activity.modular

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

open class SimpleActivityCallBackModule : ActivityCallBackModule() {

    companion object {

        private const val LOG_TAG = "SimpleActivityCallBackModule"
    }

    override fun onCreate(savedInstanceState: Bundle?) {}

    override fun onNewIntent(intent: Intent) {}

    override fun onSetContentView(@LayoutRes layoutResID: Int,
                                  callBack: ActivityCallBackModule.SetContentViewCallBack) {
        callBack.pass()
    }

    override fun onSetContentView(view: View,
                                  callBack: ActivityCallBackModule.SetContentViewCallBack) {
        callBack.pass()
    }

    override fun onSetContentView(view: View, params: ViewGroup.LayoutParams,
                                  callBack: ActivityCallBackModule.SetContentViewCallBack) {
        callBack.pass()
    }

    override fun onSetSupportActionBar(toolbar: Toolbar?): Toolbar? = toolbar

    override fun onPostSetSupportActionBar(toolbar: Toolbar?) {}

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {}

    override fun onStart() {}

    override fun onResume() {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean = false

    override fun onTitleChanged(title: CharSequence, color: Int) {}

    override fun onConfigurationChanged(newConfig: Configuration) {}

    override fun onBackPressed() {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean = false

    override fun onAttachFragment(fragment: Fragment) {}

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onDestroy() {}

    override fun onRestart() {}
}
