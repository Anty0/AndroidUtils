package cz.codetopic.utils.list.items.multiline;

import android.content.Context;

import cz.codetopic.utils.R;

/**
 * Created by anty on 18.6.15.
 *
 * @author anty
 */
public interface MultilineItem {

    int DEFAULT_ITEM_LAYOUT_ID = R.layout.listitem_multiline_image_text;
    int NO_POSITION = -1;

    CharSequence getTitle(Context context, int position);

    CharSequence getText(Context context, int position);

}
