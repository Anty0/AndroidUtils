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

abstract class ActivityCallBackModule {

    companion object {

        private const val LOG_TAG = "ActivityCallBackModule"
    }

    lateinit var activity: ModularActivity
        private set

    internal fun init(activity: ModularActivity) {
        if (this::activity.isInitialized) throw IllegalStateException("$LOG_TAG is still initialized")
        this.activity = activity
    }

    abstract fun onCreate(savedInstanceState: Bundle?)

    abstract fun onNewIntent(intent: Intent)

    abstract fun onSetContentView(@LayoutRes layoutResID: Int,
                                  callBack: SetContentViewCallBack)

    abstract fun onSetContentView(view: View, callBack: SetContentViewCallBack)

    abstract fun onSetContentView(view: View, params: ViewGroup.LayoutParams,
                                  callBack: SetContentViewCallBack)

    abstract fun onSetSupportActionBar(toolbar: Toolbar?): Toolbar?

    abstract fun onPostSetSupportActionBar(toolbar: Toolbar?)

    abstract fun onRestoreInstanceState(savedInstanceState: Bundle)

    abstract fun onStart()

    abstract fun onResume()

    abstract fun onCreateOptionsMenu(menu: Menu): Boolean

    abstract fun onOptionsItemSelected(item: MenuItem): Boolean

    abstract fun onTitleChanged(title: CharSequence, color: Int)

    abstract fun onConfigurationChanged(newConfig: Configuration)

    abstract fun onBackPressed()

    abstract fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean

    abstract fun onAttachFragment(fragment: Fragment)

    abstract fun onSaveInstanceState(outState: Bundle)

    abstract fun onPause()

    abstract fun onStop()

    abstract fun onDestroy()

    abstract fun onRestart()

    interface SetContentViewCallBack {

        fun set(@LayoutRes layoutResID: Int)

        fun set(view: View)

        fun set(view: View, params: ViewGroup.LayoutParams)

        fun pass()

        fun addViewAttachedCallBack(callBack: () -> Unit)
    }
}
