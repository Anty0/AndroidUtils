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

package eu.codetopic.utils.ui.container.items.multiline;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import eu.codetopic.utils.R;

public interface MultilineItem {

    @LayoutRes int DEFAULT_ITEM_LAYOUT_ID = R.layout.item_multiline_image_text;
    int NO_POSITION = -1;

    @Nullable
    CharSequence getTitle(Context context, int position);

    @Nullable
    CharSequence getText(Context context, int position);

}
