package cz.codetopic.utils.module.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cz.codetopic.utils.R;
import cz.codetopic.utils.activity.BackButtonActivity;
import cz.codetopic.utils.list.items.cardview.MultilineCardItem;
import cz.codetopic.utils.list.items.cardview.TextMultilineCardItem;
import cz.codetopic.utils.list.recyclerView.RecyclerInflater;
import cz.codetopic.utils.module.Module;
import cz.codetopic.utils.module.ModulesManager;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public class SettingsActivity extends BackButtonActivity {

    private static Class mAboutActivityClass = null;

    public static void setAboutActivity(@Nullable Class activityClass) {// TODO: 25.3.16 use it in ApplicationBase
        mAboutActivityClass = activityClass;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<MultilineCardItem> cardItems = new ArrayList<>();
        for (Module module : ModulesManager.getInstance().getModules())
            if (module.hasSettings()) cardItems.add(new ModuleSettingsItem(module));

        final Class aboutActivity = mAboutActivityClass;
        if (aboutActivity != null) {
            cardItems.add(new TextMultilineCardItem(getText(R.string.text_settings_about), null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this, aboutActivity));
                }
            }));
        }

        RecyclerInflater.inflate(this).useSwipeRefresh(false).inflate()
                .setAdapter(cardItems.toArray(new MultilineCardItem[cardItems.size()]));
    }
}
