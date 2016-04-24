package eu.codetopic.utils.list.items.cardview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import eu.codetopic.utils.list.items.multiline.MultilineResourceLayoutItem;

/**
 * Created by anty on 22.2.16.
 *
 * @author anty
 */
public class TextMultilineCardItem extends MultilineCardItem implements MultilineResourceLayoutItem {

    private CharSequence title = "", text = null;
    private int layoutId = -1;
    private View.OnClickListener onClick = null;
    private Object tag;

    public TextMultilineCardItem() {
    }

    public TextMultilineCardItem(CharSequence title, CharSequence text) {
        this.title = title;
        this.text = text;
    }

    public TextMultilineCardItem(CharSequence title, CharSequence text, @Nullable View.OnClickListener onClick) {
        this.title = title;
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    public int getLayoutResourceId(Context context, int position) {
        return layoutId == -1 ? super.getLayoutResId(context, position) : layoutId;
    }

    @Override
    public CharSequence getTitle(Context context, int position) {
        return title;
    }

    @Override
    public CharSequence getText(Context context, int position) {
        return text;
    }

    @Override
    public void onClick(Context context, View v, int itemPosition) {
        if (onClick != null) onClick.onClick(v);
    }

    public CharSequence getTitle() {
        return title;
    }

    public TextMultilineCardItem setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public CharSequence getText() {
        return text;
    }

    public TextMultilineCardItem setText(CharSequence text) {
        this.text = text;
        return this;
    }

    public TextMultilineCardItem setOnClick(View.OnClickListener onClick) {
        this.onClick = onClick;
        return this;
    }

    public TextMultilineCardItem callOnClick() {
        onClick(null, null, NO_POSITION);
        return this;
    }

    public int getLayoutResourceId() {
        return getLayoutResourceId(null, NO_POSITION);
    }

    public TextMultilineCardItem setLayoutResId(int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public TextMultilineCardItem setTag(Object tag) {
        this.tag = tag;
        return this;
    }
}
