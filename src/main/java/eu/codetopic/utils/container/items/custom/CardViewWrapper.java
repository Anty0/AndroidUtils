package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;

/**
 * Created by anty on 23.5.16.
 *
 * @author anty
 */
public class CardViewWrapper extends CustomItemWrapper {

    private static final String LOG_TAG = "CardViewWrapper";

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        ViewGroup content = (ViewGroup) holder.itemView.findViewById(getContentViewId(holder.context));
        if (content.getChildCount() == 1)
            Utils.copyLayoutParamsSizesToView(content, content.getChildAt(0));
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return R.layout.card_view_base;
    }

    @Override
    protected int getContentViewId(Context context) {
        return R.id.card_view;
    }
}
