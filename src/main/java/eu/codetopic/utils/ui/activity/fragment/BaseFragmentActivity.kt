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

package eu.codetopic.utils.ui.activity.fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ContextThemeWrapper
import android.view.View

import eu.codetopic.java.utils.Objects
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.AndroidUtils
import eu.codetopic.utils.R

abstract class BaseFragmentActivity : AppCompatActivity() {// TODO: 12.5.16 rework fragment replacing

    companion object {

        private const val LOG_TAG = "BaseFragmentActivity"

        const val FRAGMENT_TAG_CURRENT = "BaseFragmentActivity.CURRENT_FRAGMENT"

        @IdRes val LAYOUT_ID_CONTAINER = R.id.base_content
    }

    val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_CURRENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getDefaultTitle()

        if (savedInstanceState == null) replaceFragment(onCreateMainFragment())
    }

    override fun onContentChanged() {
        super.onContentChanged()
        updateTheme()
    }

    override fun onStart() {
        super.onStart()

        // Let's fix problem with theme of navigation menu after screen rotation.
        updateTheme()
    }

    private fun <T : Fragment> initFragment(fragmentClass: Class<out T>, bundle: Bundle? = null): T? = try {
        fragmentClass.newInstance().apply {
            bundle?.let { arguments = bundle }
        }
    } catch (e: Exception) { Log.e(LOG_TAG, "initFragment", e); null }

    fun removeCurrentFragment() {
        replaceFragment(null as Fragment?)
    }

    @JvmOverloads
    fun <T : Fragment> replaceFragment(fragmentClass: Class<out T>, bundle: Bundle? = null): T? =
            replaceFragment(initFragment(fragmentClass, bundle))

    @JvmOverloads
    fun <T : Fragment> replaceFragment(ft: FragmentTransaction,
                                       fragmentClass: Class<out T>, bundle: Bundle? = null): T? =
            replaceFragment(ft, initFragment(fragmentClass, bundle))

    fun <T : Fragment> replaceFragment(fragment: T?): T? {
        val ft = supportFragmentManager.beginTransaction()
        return try {
            try {
                return replaceFragment(ft, fragment)
            } finally {
                ft.commit()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "replaceFragment($fragment) -> Failed to replace fragment", e); null
        }
    }

    fun <T : Fragment> replaceFragment(ft: FragmentTransaction, fragment: T?): T? {
        onBeforeReplaceFragment(ft, fragment)

        val currFragment = currentFragment

        return fragment?.apply {
            takeIf {
                currFragment == null
                        || it.javaClass != currFragment.javaClass
                        || !AndroidUtils.equalBundles(it.arguments, currFragment.arguments)
            }?.let { ft.replace(LAYOUT_ID_CONTAINER, it, FRAGMENT_TAG_CURRENT) }
        } ?: run {
            currFragment?.let {
                ft.remove(currFragment)
                title = getDefaultTitle()
            }
            null
        }
    }

    protected open fun onBeforeReplaceFragment(ft: FragmentTransaction, fragment: Fragment?) {}

    protected abstract fun onCreateMainFragment(): Fragment?

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (FRAGMENT_TAG_CURRENT == fragment.tag) {
            applyFragmentTitle(fragment)
            applyFragmentTheme(fragment)
        }

        //System.runFinalization();
        //System.gc();
    }

    protected open fun getDefaultTitle(): CharSequence = AndroidUtils.getAppLabel(this)

    fun updateTitle() = applyFragmentTitle(currentFragment)

    private fun applyFragmentTitle(fragment: Fragment?) {
        title = if (fragment is TitleProvider) fragment.title else getDefaultTitle()
    }

    fun updateTheme() = applyFragmentTheme(currentFragment)

    private fun applyFragmentTheme(fragment: Fragment?) {
        if (fragment !is ThemeProvider) return
        val themeId = fragment.themeId
        val themedContext = ContextThemeWrapper(layoutInflater.context, themeId)

        findViewById<Toolbar>(R.id.toolbar)?.apply {
            AndroidUtils.getColorFromAttr(themedContext, R.attr.colorPrimary, -1)
                    .takeIf { it != -1 }?.let { setBackgroundColor(it) }

            AndroidUtils.getColorFromAttr(themedContext, R.attr.titleTextColor, -1)
                    .takeIf { it != -1 }?.let { setTitleTextColor(it) }

            AndroidUtils.getColorFromAttr(themedContext, R.attr.subtitleTextColor, -1)
                    .takeIf { it != -1 }?.let { setSubtitleTextColor(it) }
        }

        if (Build.VERSION.SDK_INT >= 21) {
            AndroidUtils.getColorFromAttr(themedContext, android.R.attr.navigationBarColor, -1)
                    .takeIf { it != -1 }?.let { window.navigationBarColor = it }

            AndroidUtils.getColorFromAttr(themedContext, android.R.attr.statusBarColor, -1)
                    .takeIf { it != -1 }?.let { window.statusBarColor = it }
        }

        findViewById<NavigationView>(R.id.nav_view)?.apply {
            getHeaderView(0).findViewById<View>(R.id.navigationHeader)?.apply {
                AndroidUtils.getColorFromAttr(themedContext, R.attr.colorPrimaryDark, -1)
                        .takeIf { it != -1 }?.let { setBackgroundColor(it) }
            }
        }
    }
}