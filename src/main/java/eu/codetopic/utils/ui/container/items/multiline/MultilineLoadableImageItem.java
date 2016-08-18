package eu.codetopic.utils.ui.container.items.multiline;

import android.content.Context;
import android.graphics.Bitmap;

import eu.codetopic.utils.callback.ActionCallback;

public interface MultilineLoadableImageItem extends MultilineItem {

    void loadImage(Context context, int position, ActionCallback<Bitmap> callback);
}
