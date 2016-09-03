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

public class ErrorInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ErrorInfoActivity";
    private static final String EXTRA_ERROR_INFO = "EXTRA_ERROR_INFO";

    public static void start(Context context, @NonNull String errorInfo) {
        context.startActivity(new Intent(context, ErrorInfoActivity.class)
                .putExtra(EXTRA_ERROR_INFO, errorInfo));
    }

    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String errorInfo = getIntent().getStringExtra(EXTRA_ERROR_INFO);
        if (errorInfo == null) {
            finish();
            return;
        }

        setContentView(R.layout.abc_alert_dialog_material);
        findViewById(R.id.customPanel).setVisibility(View.GONE);
        findViewById(R.id.buttonPanel).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.alertTitle)).setText(getTitle());
        ((ImageView) findViewById(android.R.id.icon))
                .setImageDrawable(Utils.getActivityIcon(this, getComponentName()));
        ((TextView) findViewById(android.R.id.message)).setText(errorInfo);
    }
}
