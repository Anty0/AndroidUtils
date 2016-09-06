package eu.codetopic.utils.debug.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import eu.codetopic.utils.R;
import eu.codetopic.utils.timing.TimedComponentsManager;
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper;

/**
 * Created by anty on 5.9.16.
 *
 * @author anty
 */
public class TimedComponentsManagerDebugItem extends CustomItem {

    private static final String LOG_TAG = "TimedComponentsManagerDebugItem";

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        final TextView resultText = (TextView) holder.itemView.findViewById(R.id.resultText);
        holder.itemView.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultText.setVisibility(View.VISIBLE);
                resultText.setText(TimedComponentsManager.isInitialized()
                        ? TimedComponentsManager.getInstance().toString()
                        : "TimedComponentsManager is not  initialized");
            }
        });
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return R.layout.debug_item_timed_components;
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return CardViewWrapper.WRAPPER;
    }
}
