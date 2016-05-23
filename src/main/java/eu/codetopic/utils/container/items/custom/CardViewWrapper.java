package eu.codetopic.utils.container.items.custom;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;

/**
 * Created by anty on 23.5.16.
 *
 * @author anty
 */
public class CardViewWrapper extends LayoutItemWrapper {

    private static final String LOG_TAG = "CardViewWrapper";

    public CardViewWrapper(@NonNull CustomItemWrapper... wrappers) {
        super(R.layout.card_view_base, R.id.card_view, wrappers);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        ViewGroup content = (ViewGroup) holder.itemView.findViewById(getContentViewId(holder.context));
        if (content.getChildCount() == 1)
            Utils.copyLayoutParamsSizesToView(content, content.getChildAt(0));
    }
}
