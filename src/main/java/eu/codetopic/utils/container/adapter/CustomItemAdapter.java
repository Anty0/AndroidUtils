package eu.codetopic.utils.container.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;
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
        return new CustomItemViewHolder(LayoutInflater.from(getContext())
                .inflate(R.layout.frame_wrapper_base, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomItemViewHolder holder, int position) {
        ViewGroup parent = (ViewGroup) holder.itemView;
        View view = CustomItemUtils.apply(getItem(position))
                .withPosition(position)
                .withClickSupport(getBase() instanceof RecyclerView.Adapter)
                .on(parent.getContext(), parent, parent.getChildCount() == 1
                        ? parent.getChildAt(0) : null);
        parent.removeAllViews();
        parent.addView(view);
        Utils.copyLayoutParamsSizesToView(parent, view.getLayoutParams());
    }

    @Override
    public int getItemViewLayoutId(int position) {
        return getItem(position).getLayoutResId(mContext, position);
    }

    protected static class CustomItemViewHolder extends RecyclerView.ViewHolder {

        public CustomItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
