package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import eu.codetopic.utils.R;

public class EmptyCustomItem extends CustomItem {

    public EmptyCustomItem(@NonNull CustomItemWrapper... wrappers) {
        super(wrappers);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        holder.itemView.setVisibility(View.GONE);
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return R.layout.item_empty;
    }
}
