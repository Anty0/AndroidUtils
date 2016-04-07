package cz.codetopic.utils.list.items.multiline;

import android.content.Context;
import android.support.annotation.DrawableRes;

public class TextMultilineResourceImageItem extends TextMultilineItem implements MultilineResourceImageItem {

    private int mImageResourceId;

    public TextMultilineResourceImageItem() {
        super();
    }

    public TextMultilineResourceImageItem(CharSequence title, CharSequence text,
                                          @DrawableRes int imageResourceId) {
        super(title, text);
        mImageResourceId = imageResourceId;
    }

    public TextMultilineResourceImageItem(CharSequence title, CharSequence text,
                                          @DrawableRes int imageResourceId, boolean usePadding) {
        super(title, text, usePadding);
        mImageResourceId = imageResourceId;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.mImageResourceId = imageResourceId;
    }

    @Override
    public int getImageResourceId(Context context, int position) {
        return mImageResourceId;
    }
}
