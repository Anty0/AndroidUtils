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

package eu.codetopic.utils.ui.container.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class Recycler {

    private static final String LOG_TAG = "Recycler";

    public static RecyclerInflaterImpl inflate() {
        return new RecyclerInflaterImpl();
    }

    public static final class RecyclerInflaterImpl extends
            RecyclerInflater<RecyclerInflaterImpl, RecyclerManagerImpl> {

        private RecyclerInflaterImpl() {
        }

        @Override
        protected RecyclerInflaterImpl self() {
            return this;
        }

        @Override
        protected RecyclerManagerImpl getManagerInstance(View view) {
            return new RecyclerManagerImpl(view, getSchemeColors(),
                    isUseSwipeRefresh(), isUseFloatingActionButton(),
                    getLayoutManager(), isUseItemDivider());
        }
    }

    public static final class RecyclerManagerImpl extends RecyclerManager<RecyclerManagerImpl> {

        private RecyclerManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                    boolean useSwipeRefresh, boolean useFloatingActionButton,
                                    RecyclerView.LayoutManager layoutManager,
                                    boolean useItemDivider) {

            super(mainView, swipeSchemeColors,
                    useSwipeRefresh, useFloatingActionButton,
                    layoutManager, useItemDivider);
        }

        @Override
        protected RecyclerManagerImpl self() {
            return this;
        }
    }

}
