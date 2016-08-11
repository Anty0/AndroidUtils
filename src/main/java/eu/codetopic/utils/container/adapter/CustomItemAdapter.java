package eu.codetopic.utils.container.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.Collection;

import eu.codetopic.utils.container.items.custom.CustomItem;

public class CustomItemAdapter<T extends CustomItem> extends
        ArrayEditAdapter<T, UniversalAdapter.ViewHolder> {

    private static final String LOG_TAG = "CustomItemAdapter";

    private final Context mContext;

    public CustomItemAdapter(Context context) {
        super();
        mContext = context;
    }

    public CustomItemAdapter(Context context, Collection<? extends T> data) {
        super(data);
        mContext = context;
    }

    @SafeVarargs
    public CustomItemAdapter(Context context, T... data) {
        super(data);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CustomItem.createViewHolder(getContext(), parent, viewType).forUniversalAdapter();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getItem(position).bindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getLayoutResId(getContext());
    }
}
