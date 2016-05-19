package eu.codetopic.utils.container.items.multiline;

import android.content.Context;
import android.support.annotation.DrawableRes;

public class TextMultilineImageItem extends TextMultilineItem implements MultilineResourceImageItem {

    private int mImageResourceId;

    public TextMultilineImageItem() {
        super();
    }

    public TextMultilineImageItem(CharSequence title, CharSequence text,
                                  @DrawableRes int imageResourceId) {
        super(title, text);
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
