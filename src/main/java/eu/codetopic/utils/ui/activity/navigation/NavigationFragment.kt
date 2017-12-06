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

import android.support.v4.app.Fragment
import android.util.Log
import eu.codetopic.utils.ui.view.holder.loading.LoadingFragment
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH

abstract class NavigationFragment : LoadingFragment {

    companion object {

        private const val LOG_TAG = "NavigationFragment"
    }

    protected open val navigationActivity: NavigationActivity?
        get() = activity.takeIf { it is NavigationActivity? } as NavigationActivity?

    constructor()

    constructor(loadingViewHolderClass: Class<out LoadingVH>) : super(loadingViewHolderClass)

    protected open fun <T : Fragment> switchFragment(clazz: Class<out T>): T? {
        navigationActivity?.apply { return replaceFragment(clazz) } ?:
                Log.e(LOG_TAG, "switchFragment(clazz=$clazz) -> " +
                        "Failed to switch fragment: No NavigationActivity available.")
        return null
    }

    protected open fun removeSelf() =
            navigationActivity?.apply { removeCurrentFragment() } ?:
                    Log.e(LOG_TAG, "removeSelf() -> " +
                            "Failed to remove fragment: No NavigationActivity available.")

}
