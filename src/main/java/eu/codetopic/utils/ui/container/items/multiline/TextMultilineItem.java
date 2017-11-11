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
import android.support.annotation.Nullable;

public class TextMultilineItem implements MultilineItem {

    private CharSequence title = "", text = null;
    private Object tag;

    public TextMultilineItem() {

    }

    public TextMultilineItem(CharSequence title, @Nullable CharSequence text) {
        this.title = title;
        this.text = text;
    }

    @Override
    public CharSequence getTitle(Context context, int position) {
        return title;
    }

    @Override
    public CharSequence getText(Context context, int position) {
        return text;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public Object getTag() {
        return tag;
    }

    public TextMultilineItem setTag(Object tag) {
        this.tag = tag;
        return this;
    }
}
