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

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.view.ViewUtils;

public class CardViewWrapper extends LayoutItemWrapper {

    public static final CustomItemWrapper[] WRAPPER = {new CardViewWrapper()};
    private static final String LOG_TAG = "CardViewWrapper";

    public CardViewWrapper(@NonNull CustomItemWrapper... wrappers) {
        super(R.layout.card_view_base, R.id.card_view, wrappers);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        ViewGroup content = (ViewGroup) holder.itemView.findViewById(getContentViewId(holder.context));
        if (content.getChildCount() == 1)
            ViewUtils.copyLayoutParamsToViewParents(content.getChildAt(0), holder.itemView);
    }
}
