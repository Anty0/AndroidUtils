package eu.codetopic.utils.ui.container.items.custom;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.view.ViewUtils;

public class CardViewWrapper extends LayoutItemWrapper {

    public static final CustomItemWrapper[] WRAPPER = {new CardViewWrapper()};
    private static final String LOG_TAG = "CardViewWrapper";

    public CardViewWrapper(@NonNull CustomItemWrapper... wrappers) {
        super(R.layout.card_view_base, R.id.card_view, wrappers);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        ViewGroup content = (ViewGroup) holder.itemView.findViewById(getContentViewId(holder.context));
        if (content.getChildCount() == 1)
            ViewUtils.copyLayoutParamsToViewParents(content.getChildAt(0), holder.itemView);
    }
}
