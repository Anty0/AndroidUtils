package eu.codetopic.utils.container.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import eu.codetopic.utils.container.items.custom.CustomItem;
import eu.codetopic.utils.container.items.custom.CustomItemUtils;

public class CustomItemAdapter<T extends CustomItem> extends
        ArrayEditAdapter<T, CustomItemAdapter.CustomItemViewHolder> {

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
    public CustomItemViewHolder onCreateViewHolder(ViewGroup parent, @LayoutRes int viewLayoutId) {
        return new CustomItemAdapter.CustomItemViewHolder(CustomItemUtils
                .apply(null).on(parent.getContext(), parent));
    }

    @Override
    public void onBindViewHolder(CustomItemViewHolder holder, int position) {
        CustomItemUtils.apply(getItem(position))
                .withPosition(position)
                .withoutClickSupport()// FIXME: 19.5.16 fix this after solve clicking problem
                //.withClickSupport(getBase() instanceof RecyclerView.Adapter)
                .on(holder.itemView.getContext(), null, holder.itemView);
    }

    @Override
    public int getItemViewLayoutId(int position) {
        return getItem(position).getLayoutResId(mContext, position);
    }

    @Override
    public Editor<T, ? extends CustomItemAdapter<T>> edit() {
        return new Editor<>(this);
    }

    protected static class CustomItemViewHolder extends RecyclerView.ViewHolder {

        public CustomItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
