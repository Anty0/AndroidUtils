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

package eu.codetopic.utils.ui.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public class LayoutItemWrapper extends CustomItemWrapper {

    private static final String LOG_TAG = "LayoutItemWrapper";

    @LayoutRes private final int layoutRes;
    @IdRes private final int contentViewId;
    private final CustomItemWrapper[] wrappers;

    public LayoutItemWrapper(@LayoutRes int layoutRes, @IdRes int contentViewId,
                             @NonNull CustomItemWrapper... wrappers) {
        this.layoutRes = layoutRes;
        this.contentViewId = contentViewId;
        this.wrappers = wrappers;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return layoutRes;
    }

    @Override
    protected int getContentViewId(Context context) {
        return contentViewId;
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return wrappers;
    }
}
