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

package eu.codetopic.utils.ui.container.swipe;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class SwipeLayout {

    private static final String LOG_TAG = "SwipeLayout";

    public static SwipeLayoutInflaterImpl inflate() {
        return new SwipeLayoutInflaterImpl();
    }

    public static final class SwipeLayoutInflaterImpl extends SwipeLayoutInflater<SwipeLayoutInflaterImpl, SwipeLayoutManagerImpl> {

        private SwipeLayoutInflaterImpl() {
        }

        @Override
        protected SwipeLayoutInflaterImpl self() {
            return this;
        }

        @Override
        protected SwipeLayoutManagerImpl getManagerInstance(View view) {
            return new SwipeLayoutManagerImpl(view, getSchemeColors(),
                    isUseSwipeRefresh(), isUseFloatingActionButton());
        }
    }

    public static final class SwipeLayoutManagerImpl extends SwipeLayoutManager<SwipeLayoutManagerImpl> {

        private SwipeLayoutManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                       boolean useSwipeRefresh, boolean useFloatingActionButton) {
            super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        }

        @Override
        protected SwipeLayoutManagerImpl self() {
            return this;
        }
    }
}
