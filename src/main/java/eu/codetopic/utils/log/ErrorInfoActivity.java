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

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.log.base.LogLine;
import eu.codetopic.utils.log.base.Priority;

public class ErrorInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ErrorInfoActivity";
    private static final String EXTRA_LOG_LINE = "EXTRA_LOG_LINE";

    public static void start(Context context, @NonNull LogLine logLine) {
        context.startActivity(new Intent(context, ErrorInfoActivity.class)
                .putExtra(EXTRA_LOG_LINE, logLine));
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
        icon.setImageDrawable(Utils.getActivityIcon(this, getComponentName()));
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
