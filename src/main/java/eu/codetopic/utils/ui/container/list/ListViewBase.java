/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.container.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class ListViewBase {

    private static final String LOG_TAG = "ListViewBase";

    public static ListViewInflaterImpl inflate() {
        return new ListViewInflaterImpl();
    }

    public static final class ListViewInflaterImpl extends
            ListViewInflater<ListViewInflaterImpl, ListViewManagerImpl> {

        private ListViewInflaterImpl() {
        }

        @Override
        protected ListViewInflaterImpl self() {
            return this;
        }

        @Override
        protected ListViewManagerImpl getManagerInstance(View view) {
            return new ListViewManagerImpl(view, getSchemeColors(),
                    isUseSwipeRefresh(), isUseFloatingActionButton());
        }
    }

    public static final class ListViewManagerImpl extends ListViewManager<ListViewManagerImpl> {

        protected ListViewManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                      boolean useSwipeRefresh, boolean useFloatingActionButton) {

            super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        }

        @Override
        protected ListViewManagerImpl self() {
            return this;
        }
    }

}
