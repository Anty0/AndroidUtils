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
