package eu.codetopic.utils.container.adapter;

import android.content.Context;
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
        return new ViewHolder(LayoutInflater.from(getContext())
                .inflate(R.layout.frame_wrapper_base, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewGroup parent = (ViewGroup) holder.itemView;
        View view = CustomItemUtils.apply(getItem(position))
                .withPosition(position)
                .withClickSupport(getBase() instanceof RecyclerView.Adapter)
                .on(parent.getContext(), parent, parent.getChildCount() == 1
                        ? parent.getChildAt(0) : null);
        parent.removeAllViews();
        parent.addView(view);
        Utils.copyLayoutParamsSizesToView(parent, view);
    }

    @Override
    public int getItemViewType(int position) {
        CustomItem item = getItem(position);
        return (item.getLayoutResId(mContext, position) * 2)
                + (CustomItemUtils.usesCardView(item) ? 1 : 0);
    }
}
