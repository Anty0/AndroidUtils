/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
