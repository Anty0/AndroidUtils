package eu.codetopic.utils.module.settings;

import android.content.Context;
import android.view.View;

import eu.codetopic.utils.list.items.cardview.MultilineCardItem;
import eu.codetopic.utils.module.Module;

/**
 * Created by anty on 22.2.16.
 *
 * @author anty
 */
public class ModuleSettingsItem extends MultilineCardItem {

    private final Module mModule;

    ModuleSettingsItem(Module module) {
        mModule = module;
    }

    @Override
    public CharSequence getTitle(Context context, int position) {
        return mModule.hasSettings() ? mModule.getSettingsName() : "";
    }

    @Override
    public CharSequence getText(Context context, int position) {
        return null;
    }

    @Override
    public void onClick(Context context, View v, int itemPosition) {
        if (mModule.hasSettings())
            ModuleSettingsActivity.startModuleSettingsActivity(context,
                    mModule.getSettingsName(), mModule.getSettings());
    }

    @Override
    public boolean onLongClick(Context context, View v, int itemPosition) {
        return false;
    }
}
