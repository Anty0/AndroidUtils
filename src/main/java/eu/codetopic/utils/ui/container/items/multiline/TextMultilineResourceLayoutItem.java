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
