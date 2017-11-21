/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.debug.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.timing.TimedComponentsManager;
import eu.codetopic.utils.timing.TimingData;
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
    @SuppressLint("SetTextI18n")
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        final TextView resultText = holder.itemView.findViewById(R.id.resultText);
        holder.itemView.findViewById(R.id.refreshButton).setOnClickListener(v -> {
            resultText.setVisibility(View.VISIBLE);
            if (TimedComponentsManager.isInitialized()) {
                String timCompInfoText = TimedComponentsManager.getInstance().toString()
                        .replace("{", "{\n")
                        .replace("}", "\n}")
                        .replace("[", "[\n")
                        .replace("]", "\n]")
                        .replace(", ", ",\n    ");

                String timCompDebugLogJson = TimingData.getter.get().getDebugLogJson();
                try {
                    JSONArray timCompDebugLogArray = new JSONArray(timCompDebugLogJson);

                    for (int i = 0, len = timCompDebugLogArray.length(); i < len; i++) {
                        JSONArray line = timCompDebugLogArray.getJSONArray(i);
                        line.put(0, new Date(line.getLong(0)).toString());
                    }

                    timCompDebugLogJson = timCompDebugLogArray.toString(4);
                } catch (JSONException e) {
                    Log.w(LOG_TAG, e);
                    timCompDebugLogJson = "LoadingFailed";
                }

                resultText.setText(timCompInfoText + "\n\nDebugLog:\n" + timCompDebugLogJson);
            } else {
                resultText.setText("TimedComponentsManager is not initialized");
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
