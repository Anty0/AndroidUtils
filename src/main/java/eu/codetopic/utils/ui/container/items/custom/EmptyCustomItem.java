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
import android.view.View;

import eu.codetopic.utils.R;

public class EmptyCustomItem extends CustomItem {

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        holder.itemView.setVisibility(View.GONE);
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return R.layout.item_empty;
    }
}
