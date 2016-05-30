package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.view.View;

import eu.codetopic.utils.R;

public class EmptyCustomItem extends CustomItem {

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        holder.itemView.setVisibility(View.GONE);
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return R.layout.item_empty;
    }
}
