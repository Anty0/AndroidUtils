/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
