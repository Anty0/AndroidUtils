package eu.codetopic.utils.debug.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import eu.codetopic.java.utils.log.Logger;
import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper;

public class LoggingDebugItem extends CustomItem {

    private static final String LOG_TAG = "LoggingDebugItem";

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        final TextView resultText = (TextView) holder.itemView.findViewById(R.id.resultText);
        holder.itemView.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultText.setVisibility(View.VISIBLE);
                resultText.setText(Logger.getLogLinesCache());
            }
        });
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return R.layout.debug_item_logging;
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return CardViewWrapper.WRAPPER;
    }
}
