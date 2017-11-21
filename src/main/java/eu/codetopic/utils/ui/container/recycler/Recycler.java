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

package eu.codetopic.utils.ui.container.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
                    isUseSwipeRefresh(), isUseFloatingActionButton());
        }
    }

    public static final class RecyclerManagerImpl extends RecyclerManager<RecyclerManagerImpl> {

        protected RecyclerManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                      boolean useSwipeRefresh, boolean useFloatingActionButton) {

            super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        }

        @Override
        protected RecyclerManagerImpl self() {
            return this;
        }
    }

}
