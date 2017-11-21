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

package eu.codetopic.utils.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eu.codetopic.java.utils.log.base.LogLine;
import eu.codetopic.java.utils.log.base.Priority;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.R;

public class ErrorInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ErrorInfoActivity";
    private static final String EXTRA_LOG_LINE = "EXTRA_LOG_LINE";

    public static void start(Context context, @NonNull LogLine logLine) {
        context.startActivity(new Intent(context, ErrorInfoActivity.class)
                .putExtra(EXTRA_LOG_LINE, logLine).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogLine logLine = (LogLine) getIntent().getSerializableExtra(EXTRA_LOG_LINE);
        if (logLine == null) {
            finish();
            return;
        }

        setFinishOnTouchOutside(false);
        setTitle(Priority.ERROR.equals(logLine.getPriority())
                ? R.string.activity_label_error_info
                : R.string.activity_label_warn_info);

        setContentView(R.layout.abc_alert_dialog_material);
        findViewById(R.id.customPanel).setVisibility(View.GONE);
        findViewById(R.id.buttonPanel).setVisibility(View.GONE);
        findViewById(R.id.textSpacerNoButtons).setVisibility(View.VISIBLE);

        ImageView icon = (ImageView) findViewById(android.R.id.icon);
        icon.setImageDrawable(AndroidUtils.getActivityIcon(this, getComponentName()));
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.alertTitle)).setText(getTitle());

        TextView message = ((TextView) findViewById(android.R.id.message));
        message.setHorizontallyScrolling(true);
        message.setText(logLine.toString());
    }
}
