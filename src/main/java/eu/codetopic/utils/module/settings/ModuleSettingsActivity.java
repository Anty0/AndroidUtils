package eu.codetopic.utils.module.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import eu.codetopic.utils.activity.BackButtonActivity;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public class ModuleSettingsActivity extends BackButtonActivity {

    private Settings mSettings;

    static void startModuleSettingsActivity(Context context, Settings settings) {
        context.startActivity(new Intent(context, ModuleSettingsActivity.class)
                .putExtra(Settings.EXTRA_SETTINGS, settings));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                finish();
                return;
            }
            savedInstanceState = intent.getExtras();
        }
        mSettings = (Settings) savedInstanceState.getSerializable(Settings.EXTRA_SETTINGS);
        if (mSettings != null) {
            setContentView(mSettings.generateView(this));
            return;
        }
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Settings.EXTRA_SETTINGS, mSettings);
        super.onSaveInstanceState(outState);
    }
}
