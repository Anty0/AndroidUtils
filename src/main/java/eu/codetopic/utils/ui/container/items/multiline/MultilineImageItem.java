package eu.codetopic.utils.ui.container.items.multiline;

import android.content.Context;
import android.graphics.Bitmap;

public interface MultilineImageItem extends MultilineItem {

    Bitmap getImageBitmap(Context context, int position);
}
