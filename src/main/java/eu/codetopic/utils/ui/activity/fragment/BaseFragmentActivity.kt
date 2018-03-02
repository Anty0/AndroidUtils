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

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.ContextThemeWrapper
import android.view.View

import eu.codetopic.java.utils.letIf
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.AndroidUtils
import eu.codetopic.utils.use
import eu.codetopic.utils.R
import eu.codetopic.utils.ui.view.hideKeyboard
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.synthetic.main.activity_module_toolbar.*
import kotlinx.android.synthetic.main.activity_navigation_base.*

@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
abstract class BaseFragmentActivity : AppCompatActivity() {// TODO: 12.5.16 rework fragment replacing

    companion object {

        private const val LOG_TAG = "BaseFragmentActivity"

        const val FRAGMENT_TAG_CURRENT = "BaseFragmentActivity.CURRENT_FRAGMENT"

        @IdRes val LAYOUT_ID_CONTAINER = R.id.base_content
    }

    val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_CURRENT)

    protected abstract val mainFragmentClass: Class<out Fragment>?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getDefaultTitle()

        if (savedInstanceState == null) replaceFragment(onCreateMainFragment())
    }

    override fun onContentChanged() {
        super.onContentChanged()
        updateProviderFragment()
    }

    override fun onStart() {
        super.onStart()
        updateProviderFragment()
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

        currentFocus?.hideKeyboard()

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

    protected open fun onCreateMainFragment(): Fragment? = try {
        mainFragmentClass?.newInstance()
    } catch (e: Exception) {
        Log.e(LOG_TAG, "onCreateMainFragment() -> Failed to create main fragment", e)
        null
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (FRAGMENT_TAG_CURRENT == fragment.tag)
            applyProviderFragment(fragment)

        //System.runFinalization();
        //System.gc();
    }

    protected open fun getDefaultTitle(): CharSequence = AndroidUtils.getAppLabel(this)

    fun updateProviderFragment() = applyProviderFragment(currentFragment)

    @SuppressLint("InlinedApi")
    private fun applyProviderFragment(fragment: Fragment?) {
        val targetTitle = if (fragment is TitleProvider) fragment.title else getDefaultTitle()

        val themedContext =
                if (fragment is ThemeProvider)
                    ContextThemeWrapper(layoutInflater.context, fragment.themeId)
                else this

        val colorsAttrs = intArrayOf(
                R.attr.colorPrimary,
                R.attr.titleTextColor,
                R.attr.subtitleTextColor,
                R.attr.colorPrimaryDark
        ).letIf({ Build.VERSION.SDK_INT >= 21 }) {
            it + intArrayOf(
                    android.R.attr.navigationBarColor,
                    android.R.attr.statusBarColor
            )
        }

        val colors = themedContext.obtainStyledAttributes(colorsAttrs)
                .use { tArray ->
                    (0 until colorsAttrs.size).map {
                        tArray.getColor(it, -1)
                                .takeIf { it != -1 }
                    }
                }

        title = targetTitle

        if (Build.VERSION.SDK_INT >= 21) {
            run taskDescription@ {
                val primaryColor = colors[colorsAttrs.indexOf(R.attr.colorPrimary)]
                        ?: return@taskDescription
                /*val icon =
                        if (fragment is IconProvider) fragment.icon
                        else AndroidUtils.drawableToBitmap(
                                AndroidUtils.getActivityIcon(themedContext, componentName)
                        )*/
                //val title = targetTitle.toString()

                //setTaskDescription(ActivityManager.TaskDescription(title, icon, primaryColor))
                setTaskDescription(ActivityManager.TaskDescription(null, null, primaryColor))
            }

            colors[colorsAttrs.indexOf(android.R.attr.navigationBarColor)]
                    ?.let { window.navigationBarColor = it }

            colors[colorsAttrs.indexOf(android.R.attr.statusBarColor)]
                    ?.let { window.statusBarColor = it }
        }

        toolbar?.apply {
            colors[colorsAttrs.indexOf(R.attr.colorPrimary)]
                    ?.let { setBackgroundColor(it) }

            colors[colorsAttrs.indexOf(R.attr.titleTextColor)]
                    ?.let { setTitleTextColor(it) }

            colors[colorsAttrs.indexOf(R.attr.subtitleTextColor)]
                    ?.let { setSubtitleTextColor(it) }
        }

        navigationView?.apply {
            getHeaderView(0).findViewById<View>(R.id.boxHeader)?.apply {
                colors[colorsAttrs.indexOf(R.attr.colorPrimaryDark)]
                        ?.let { setBackgroundColor(it) }
            }
        }

    }
}