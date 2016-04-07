package cz.codetopic.utils.list.listView;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import cz.codetopic.utils.Objects;
import cz.codetopic.utils.list.items.multiline.MultilineItem;
import cz.codetopic.utils.list.items.multiline.MultilineItemUtils;
import cz.codetopic.utils.list.items.multiline.MultilineResourceLayoutItem;

/**
 * Created by anty on 18.6.15.
 *
 * @author anty
 */
public class MultilineAdapter<M extends MultilineItem> extends ArrayAdapter<M> {

    private final Context context;
    private final int layoutResourceId;

    public MultilineAdapter(Context context) {
        this(context, MultilineItem.DEFAULT_ITEM_LAYOUT_ID);
    }

    public MultilineAdapter(Context context, @LayoutRes int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    public MultilineAdapter(Context context, @LayoutRes int layoutResourceId, M[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return generateView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return generateView(position, convertView, parent);
    }

    protected View generateView(int position, View convertView, ViewGroup parent) {
        M item = getItem(position);
        Integer layoutResourceId = this.layoutResourceId;
        if (item instanceof MultilineResourceLayoutItem)
            layoutResourceId = ((MultilineResourceLayoutItem) item)
                    .getLayoutResourceId(context, position);

        if (convertView != null) {
            if (!Objects.equals(((MultilineItemUtils.ItemViewHolder) convertView.getTag())
                    .layoutResourceId, layoutResourceId))
                convertView = null;
        }

        if (convertView == null)
            return MultilineItemUtils.applyMultilineItemOnView(context, parent, item, layoutResourceId);

        MultilineItemUtils.applyMultilineItemOnView(convertView, item);
        return convertView;
    }
}