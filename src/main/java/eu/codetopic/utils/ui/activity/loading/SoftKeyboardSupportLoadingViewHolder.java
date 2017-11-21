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

package eu.codetopic.utils.ui.activity.loading;

import android.support.annotation.LayoutRes;

import eu.codetopic.utils.R;
import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Use SoftKeyboardSupportLoadingVH instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public class SoftKeyboardSupportLoadingViewHolder extends DefaultLoadingViewHolder {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.loading_soft_keyboard_support_base;

    private static final String LOG_TAG = "SoftKeyboardSupportLoadingViewHolder";

    @Keep
    @KeepName
    private static HolderInfo<DefaultLoadingViewHolder> getHolderInfo() {
        return new HolderInfo<>(DefaultLoadingViewHolder.class, true,
                LOADING_LAYOUT_ID, CONTENT_VIEW_ID);
    }
}
