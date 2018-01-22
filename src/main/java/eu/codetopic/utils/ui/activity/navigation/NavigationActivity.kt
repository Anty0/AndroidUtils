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

package eu.codetopic.utils.ui.activity.navigation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.design.internal.NavigationMenuPresenter
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.mikepenz.google_material_typeface_library.GoogleMaterial

import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.AndroidExtensions.getIconics
import eu.codetopic.utils.AndroidUtils
import eu.codetopic.utils.R
import eu.codetopic.utils.ui.activity.fragment.BaseFragmentActivity
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_navigation_header.*
import kotlinx.android.synthetic.main.activity_navigation_header.view.*
import kotlinx.android.synthetic.main.activity_navigation_base.*
import kotlinx.android.synthetic.main.activity_module_toolbar.*

@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
abstract class NavigationActivity : BaseFragmentActivity() {

    companion object {

        private const val LOG_TAG = "NavigationActivity"

        private const val KEY_SWITCHING_ACCOUNTS =
                "eu.codetopic.utils.ui.activity.navigation.NavigationActivity.SWITCHING_ACCOUNTS"
    }

    @ContainerOptions(CacheImplementation.SPARSE_ARRAY)
    internal inner class HeaderViews : LayoutContainer {
        override val containerView: LinearLayout =
                this@NavigationActivity.navigationView.getHeaderView(0).boxHeader
    }

    private lateinit var header: HeaderViews

    private lateinit var drawerToggle: ActionBarDrawerToggle

    var enableSwitchingAccounts = false
        set(enable) {
            field = enable
            invalidateNavigationMenu()
        }
    private var switchingAccounts = false
    var isSwitchingAccounts: Boolean
        get() = switchingAccounts
        set(switchingAccounts) {
            this.switchingAccounts = switchingAccounts
            invalidateNavigationMenu()
        }

    var enableActiveAccountEditButton: Boolean
        get() = header.butAccountEdit.visibility == View.VISIBLE
        set(enabled) {
            header.butAccountEdit.visibility =
                    if (enabled) View.VISIBLE
                    else View.GONE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            switchingAccounts = it.getBoolean(KEY_SWITCHING_ACCOUNTS, false)
        }

        setContentView(R.layout.activity_navigation_base)
        header = HeaderViews()

        header.boxAccountsSwitch.setOnClickListener { isSwitchingAccounts = !isSwitchingAccounts }
        header.butAccountEdit.apply {
            setImageDrawable(
                    getIconics(GoogleMaterial.Icon.gmd_edit)
                            .actionBar()
            )
            setOnClickListener { onEditAccountButtonClick(it) }
        }

        setNavigationViewAppIconDrawable(AndroidUtils.getActivityIcon(this, componentName))
        setHeaderBackgroundColor(AndroidUtils.getColorFromAttr(this,
                R.attr.colorPrimaryDark, Color.BLACK))

        header.txtAppName.text = AndroidUtils.getAppLabel(this)

        setSupportActionBar(toolbar)

        drawerToggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
                .also {
                    drawer.addDrawerListener(it)
                    it.syncState()
                }

        navigationView.setNavigationItemSelectedListener listener@ {
            drawer.closeDrawer(GravityCompat.START)

            if (enableSwitchingAccounts && switchingAccounts) {
                isSwitchingAccounts = false
                return@listener onAccountNavigationItemSelected(it)
            }

            return@listener onNavigationItemSelected(it)
        }
        invalidateNavigationMenu()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    fun setNavigationViewAppIconBitmap(bm: Bitmap) =
            header.imgAppIcon.setImageBitmap(bm)

    fun setNavigationViewAppIconDrawable(drawable: Drawable?) =
            header.imgAppIcon.setImageDrawable(drawable)

    fun setNavigationViewAppIconResource(@DrawableRes resId: Int) =
            header.imgAppIcon.setImageResource(resId)

    fun setHeaderBackground(drawable: Drawable) =
            ViewCompat.setBackground(header.boxHeader, drawable)

    fun setHeaderBackgroundColor(@ColorInt color: Int) =
            header.boxHeader.setBackgroundColor(color)

    fun setHeaderBackgroundResource(@DrawableRes resId: Int) =
            header.boxHeader.setBackgroundResource(resId)

    fun setNavigationTitle(title: CharSequence) {
        header.txtAppName.text = title
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(KEY_SWITCHING_ACCOUNTS, switchingAccounts)
    }

    override fun onBackPressed() {
        drawer.takeIf { it.isDrawerOpen(GravityCompat.START) }?.let {
            it.closeDrawer(GravityCompat.START)
            return
        }

        // If class of current fragment differs from main fragment class,
        // replace current fragment with main fragment.
        (currentFragment to mainFragmentClass)
                .takeIf { it.first?.javaClass != it.second }
                ?.run {
                    // If main fragment class is not null, replace current fragment
                    // with main fragment, if it's null remove current fragment
                    second?.apply { replaceFragment(this) }
                            ?: removeCurrentFragment()
                    return
                }

        super.onBackPressed()
    }

    @SuppressLint("RestrictedApi")
    fun invalidateNavigationMenu() {
        if (enableSwitchingAccounts) {
            header.txtAccountName.text = onUpdateActiveAccountName()
            header.imgAccountsSwitchArrow.setImageDrawable(
                    getIconics(
                            if (switchingAccounts) GoogleMaterial.Icon.gmd_arrow_drop_up
                            else GoogleMaterial.Icon.gmd_arrow_drop_down
                    ).actionBar()
            )

            header.boxAppName.visibility = View.GONE
            header.boxAccountsSwitch.visibility = View.VISIBLE
        } else {
            header.boxAccountsSwitch.visibility = View.GONE
            header.boxAppName.visibility = View.VISIBLE
        }

        // Extract menu presenter from navigation view. This allows us to control navigation menu updating.
        val presenter: NavigationMenuPresenter? = try {
            NavigationView::class.java.getDeclaredField("mPresenter")?.apply {
                isAccessible = true
            }?.let {
                // Yeah, you are right, next line is really big and dirty HACK.
                it.get(navigationView) as NavigationMenuPresenter?
            }
        } catch (e: Exception) {
            Log.w(LOG_TAG, "invalidateNavigationMenu() -> " +
                    "Cannot extract menu presenter form navigation view: " +
                    "Failed to get menu presenter field from navigation view instance -> " +
                    "Falling back to updating navigation menu after every change", e)
            null
        }

        try {
            presenter?.setUpdateSuspended(true)

            navigationView.menu.apply {
                clear()

                if (enableSwitchingAccounts && switchingAccounts) {
                    onCreateAccountNavigationMenu(this)
                    // setupMenuItemsClickListeners(this);
                } else {
                    onCreateNavigationMenu(this)
                    // setupMenuItemsClickListeners(this);
                }
            }

            resetNavigationView(currentFragment)
        } finally {
            presenter?.run {
                setUpdateSuspended(false)
                updateMenuView(false)
            }
        }
    }

    private fun resetNavigationView(currentFragment: Fragment?) {
        if (enableSwitchingAccounts && switchingAccounts) {
            onUpdateSelectedAccountNavigationMenuItem(currentFragment, navigationView.menu)
            header.txtAccountName.text = onUpdateActiveAccountName()
        } else {
            onUpdateSelectedNavigationMenuItem(currentFragment, navigationView.menu)
        }
    }

    protected open fun onCreateNavigationMenu(menu: Menu): Boolean = false

    protected open fun onCreateAccountNavigationMenu(menu: Menu): Boolean = false

    /**
     * Override this method to enable navigation menu items selecting
     *
     * @param currentFragment current fragment
     * @param menu            navigation menu
     * @return true if checked state of any item was changed
     */
    protected open fun onUpdateSelectedNavigationMenuItem(currentFragment: Fragment?, menu: Menu): Boolean {
        if (currentFragment != null) {
            Log.e(LOG_TAG, "onUpdateSelectedNavigationMenuItem(currentFragment=$currentFragment, menu=$currentFragment) -> " +
                    "Cannot detect selected item for ${currentFragment.javaClass}, override method " +
                    "onUpdateSelectedNavigationMenuItem() and implement your own selected item detection.")
            return false
        }


        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        return true
    }

    protected open fun onUpdateSelectedAccountNavigationMenuItem(currentFragment: Fragment?, menu: Menu): Boolean {
        Log.e(LOG_TAG, "onUpdateSelectedAccountNavigationMenuItem(currentFragment=$currentFragment, menu=$currentFragment) -> " +
                "Cannot update selected account item, override method onUpdateSelectedAccountNavigationMenuItem() " +
                "and implement your own selected item detection.")
        return false
    }

    open fun onNavigationItemSelected(item: MenuItem): Boolean = false

    open fun onAccountNavigationItemSelected(item: MenuItem): Boolean = false

    protected open fun onUpdateActiveAccountName(): CharSequence {
        Log.e(LOG_TAG, "onUpdateActiveAccountName() -> Cannot get active account name, " +
                "override method onUpdateActiveAccountName() and implement active account name detecting.")
        return ""
    }

    protected open fun onEditAccountButtonClick(v: View): Boolean = false

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        // Check if navigation view is available and if fragment is current fragment and if so, reset navigation view.
        if (navigationView != null && BaseFragmentActivity.FRAGMENT_TAG_CURRENT == fragment.tag)
            resetNavigationView(fragment)
    }

    @SuppressLint("PrivateResource")
    override fun onBeforeReplaceFragment(ft: FragmentTransaction, fragment: Fragment?) {
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
    }
}
