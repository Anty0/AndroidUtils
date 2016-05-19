package eu.codetopic.utils.container.items.multiline;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 * Created by anty on 18.6.15.
 *
 * @author anty
 */
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
