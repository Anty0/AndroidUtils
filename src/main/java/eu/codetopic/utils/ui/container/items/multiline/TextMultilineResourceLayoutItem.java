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

public class TextMultilineResourceLayoutItem extends TextMultilineItem implements MultilineResourceLayoutItem {

    @LayoutRes private int layoutRes = DEFAULT_ITEM_LAYOUT_ID;

    public TextMultilineResourceLayoutItem() {
    }

    public TextMultilineResourceLayoutItem(CharSequence title, @Nullable CharSequence text, @LayoutRes int layoutRes) {
        super(title, text);
        this.layoutRes = layoutRes;
    }

    @Override
    public int getLayoutResourceId(Context context) {
        return layoutRes;
    }

    public int getLayoutResourceId() {
        return layoutRes;
    }

    public TextMultilineResourceLayoutItem setLayoutResourceId(int layoutRes) {
        this.layoutRes = layoutRes;
        return this;
    }
}
