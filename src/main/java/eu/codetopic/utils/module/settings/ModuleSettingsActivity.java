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

    private static final String EXTRA_TITLE = "eu.codetopic.utils.module.settings.ModuleSettingsActivity.TITLE";

    static void startModuleSettingsActivity(Context context, CharSequence title, Settings settings) {
        context.startActivity(new Intent(context, ModuleSettingsActivity.class)
                .putExtra(EXTRA_TITLE, title).putExtra(Settings.EXTRA_SETTINGS, settings));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(Settings.EXTRA_SETTINGS)) {
            finish();
            return;
        }
        setTitle(intent.getCharSequenceExtra(EXTRA_TITLE));
        setContentView(((Settings) intent.getSerializableExtra(Settings.EXTRA_SETTINGS))
                .generateView(this));
    }
}
