package eu.codetopic.utils.list.items.multiline;

import android.content.Context;
import android.graphics.Bitmap;

import eu.codetopic.utils.callback.ActionCallback;

/**
 * Created by anty on 9.4.16.
 *
 * @author anty
 */
public interface MultilineLoadableImageItem extends MultilineItem {

    void loadImage(Context context, int position, ActionCallback<Bitmap> callback);
}
