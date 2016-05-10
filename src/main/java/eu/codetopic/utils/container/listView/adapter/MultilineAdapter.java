package eu.codetopic.utils.container.listView.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.MultilineItemUtils;

/**
 * Created by anty on 18.6.15.
 *
 * @author anty
 */
public class MultilineAdapter<M extends MultilineItem> extends ArrayAdapter<M> {

    private final int layoutResourceId;

    public MultilineAdapter(Context context) {
        this(context, MultilineItem.DEFAULT_ITEM_LAYOUT_ID);
    }

    public MultilineAdapter(Context context, @LayoutRes int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
    }

    public MultilineAdapter(Context context, M[] data) {
        this(context, MultilineItem.DEFAULT_ITEM_LAYOUT_ID, data);
    }

    public MultilineAdapter(Context context, @LayoutRes int layoutResourceId, M[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
    }

    public MultilineAdapter(Context context, List<M> data) {
        this(context, MultilineItem.DEFAULT_ITEM_LAYOUT_ID, data);
    }

    public MultilineAdapter(Context context, @LayoutRes int layoutResourceId, List<M> data) {
        super(context, layoutResourceId, data);
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
        return MultilineItemUtils.apply(getItem(position))
                .withDefaultLayoutResId(layoutResourceId)
                .withPosition(position)
                .on(getContext(), parent, convertView);
    }
}