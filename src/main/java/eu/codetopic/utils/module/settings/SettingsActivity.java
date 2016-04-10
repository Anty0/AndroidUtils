package eu.codetopic.utils.module.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.BackButtonActivity;
import eu.codetopic.utils.list.items.cardview.MultilineCardItem;
import eu.codetopic.utils.list.items.cardview.TextMultilineCardItem;
import eu.codetopic.utils.list.recyclerView.Recycler;
import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.ModulesManager;

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

        Recycler.apply().withoutSwipeToRefresh().on(this)
                .setAdapter(cardItems.toArray(new MultilineCardItem[cardItems.size()]));
    }
}
