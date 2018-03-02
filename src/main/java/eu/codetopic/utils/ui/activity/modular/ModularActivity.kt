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
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import eu.codetopic.java.utils.debug.DebugMode

import java.util.ArrayList
import java.util.LinkedHashMap

import eu.codetopic.java.utils.log.Log

abstract class ModularActivity @JvmOverloads constructor(vararg modules: ActivityCallBackModule = emptyArray()) : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "ModularActivity"
    }

    private val modulesMap = LinkedHashMap<Class<out ActivityCallBackModule>, ActivityCallBackModule>()

    init {
        if (DebugMode.isEnabled) Log.v(LOG_TAG, "<init>(modules=$modules)")

        modules.forEach {
            try {
                modulesMap[it.javaClass] = it.apply { init(this@ModularActivity) }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "<init>(modules=$modules) -> (module=$it)", e)
            }
        }
    }

    private inline fun <T> Iterable<T>.tryForEach(methodName: String, action: (T) -> Unit) {
        forEach {
            try {
                action(it)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "$methodName()", e)
            }
        }
    }

    private inline fun modulesForEach(methodName: String, action: (ActivityCallBackModule) -> Unit) {
        modulesMap.values.tryForEach(methodName, action)
    }

    fun getModules(): Collection<ActivityCallBackModule> = modulesMap.values

    fun hasModule(moduleClass: Class<out ActivityCallBackModule>): Boolean =
            moduleClass in modulesMap

    @Suppress("UNCHECKED_CAST")
    fun <T : ActivityCallBackModule> findModule(moduleClass: Class<T>): T =
            modulesMap[moduleClass] as T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modulesForEach("onCreate") {
            it.onCreate(savedInstanceState)
        }
    }

    override fun onNewIntent(intent: Intent) {
        modulesForEach("onNewIntent") {
            it.onNewIntent(intent)
        }
        super.onNewIntent(intent)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        try {
            SetContentViewCallBackImpl(layoutResID).apply {
                modulesMap.values.forEach { callNext(it) }
            }.apply()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "setContentView()", e)
            super.setContentView(layoutResID)
        }

    }

    override fun setContentView(view: View) {
        try {
            SetContentViewCallBackImpl(view).apply {
                modulesMap.values.forEach { callNext(it) }
            }.apply()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "setContentView()", e)
            super.setContentView(view)
        }

    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        try {
            SetContentViewCallBackImpl(view, params).apply {
                modulesMap.values.forEach { callNext(it) }
            }.apply()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "setContentView()", e)
            super.setContentView(view, params)
        }

    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        throw UnsupportedOperationException("Unsupported by $LOG_TAG")
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        var modifiedToolbar = toolbar
        modulesForEach("setSupportActionBar") {
            modifiedToolbar = it.onSetSupportActionBar(modifiedToolbar)
        }

        super.setSupportActionBar(modifiedToolbar)

        modulesForEach("setSupportActionBar") {
            it.onPostSetSupportActionBar(modifiedToolbar)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        modulesForEach("onRestoreInstanceState") {
            it.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()
        modulesForEach("onStart") { it.onStart() }
    }

    override fun onResume() {
        super.onResume()
        modulesForEach("onResume") { it.onResume() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var result = false
        modulesForEach("onCreateOptionsMenu") {
            result = result or it.onCreateOptionsMenu(menu)
        }
        return super.onCreateOptionsMenu(menu) || result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var result = false
        run forEach@ {
            modulesForEach("onOptionsItemSelected") {
                result = it.onOptionsItemSelected(item)
                if (result) return@forEach
            }
        }
        return result || super.onOptionsItemSelected(item)
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        modulesForEach("onTitleChanged") {
            it.onTitleChanged(title, color)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        modulesForEach("onConfigurationChanged") {
            it.onConfigurationChanged(newConfig)
        }
    }

    override fun onBackPressed() {
        modulesForEach("onBackPressed") { it.onBackPressed() }
        super.onBackPressed()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var result = false
        run forEach@ {
            modulesForEach("onKeyDown") {
                result = it.onKeyDown(keyCode, event)
                if (result) return@forEach
            }
        }
        return result || super.onKeyDown(keyCode, event)
    }

    override fun onAttachFragment(fragment: Fragment) {
        modulesForEach("onAttachFragment") {
            it.onAttachFragment(fragment)
        }
        super.onAttachFragment(fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        modulesForEach("onSaveInstanceState") {
            it.onSaveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        modulesForEach("onPause") { it.onPause() }
        super.onPause()
    }

    override fun onStop() {
        modulesForEach("onStop") { it.onStop() }
        super.onStop()
    }

    override fun onDestroy() {
        modulesForEach("onDestroy") { it.onDestroy() }
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        modulesForEach("onRestart") { it.onRestart() }
    }

    private inner class SetContentViewCallBackImpl : ActivityCallBackModule.SetContentViewCallBack {

        private val completedCallBacks = ArrayList<() -> Unit>()
        private val completedCallBacksTmp = ArrayList<() -> Unit>()
        private var used = false

        private var layoutResID: Int? = null
        private var view: View? = null
        private var params: ViewGroup.LayoutParams? = null

        internal constructor(@LayoutRes layoutResID: Int) { set(layoutResID) }

        internal constructor(view: View) { set(view) }

        internal constructor(view: View, params: ViewGroup.LayoutParams) { set(view, params) }

        fun callNext(module: ActivityCallBackModule) {
            val layoutResID = layoutResID
            val view = view
            val params = params

            used = false

            completedCallBacks.addAll(0, completedCallBacksTmp)
            completedCallBacksTmp.clear()

            if (layoutResID != null) {
                module.onSetContentView(layoutResID, this)
            } else if (view != null) {
                if (params != null) {
                    module.onSetContentView(view, params, this)
                } else {
                    module.onSetContentView(view, this)
                }
            } else {
                throw IllegalStateException("Nothing to call")
            }

            if (!used) throw IllegalStateException("Module didn't call any method, " +
                    "so we don't know now, what should be done...")
        }

        fun apply() {
            val layoutResID = layoutResID
            val view = view
            val params = params

            completedCallBacks.addAll(0, completedCallBacksTmp)
            completedCallBacksTmp.clear()

            if (layoutResID != null) {
                super@ModularActivity.setContentView(layoutResID)
            } else if (view != null) {
                if (params != null) {
                    super@ModularActivity.setContentView(view, params)
                } else {
                    super@ModularActivity.setContentView(view)
                }
            } else {
                throw IllegalStateException("Nothing to call")
            }

            for (callBack in completedCallBacks)
                callBack()
            completedCallBacks.clear()

            resetValues()
        }

        private fun resetValues() {
            layoutResID = null
            view = null
            params = null
        }

        private fun reset(resetValues: Boolean = true) {
            if (used) throw IllegalStateException("You can call set() and pass() method only once")
            if (resetValues) resetValues()
            used = true
        }

        override fun set(@LayoutRes layoutResID: Int) {
            reset()
            this.layoutResID = layoutResID
        }

        override fun set(view: View) {
            reset()
            this.view = view
        }

        override operator fun set(view: View, params: ViewGroup.LayoutParams) {
            reset()
            this.view = view
            this.params = params
        }

        override fun pass() {
            reset(false)
        }

        override fun addViewAttachedCallBack(callBack: () -> Unit) {
            completedCallBacksTmp.add(callBack)
        }

    }
}
