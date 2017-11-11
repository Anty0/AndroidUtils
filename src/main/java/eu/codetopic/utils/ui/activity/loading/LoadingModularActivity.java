/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.activity.loading;

import eu.codetopic.java.utils.ArrayTools;
import eu.codetopic.utils.ui.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;

/**
 * Use LoadingModularActivity instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingModularActivity extends ModularActivity {

    private static final String LOG_TAG = "LoadingModularActivity";

    public LoadingModularActivity(ActivityCallBackModule... modules) {
        this(new LoadingModule(), modules);
    }

    public LoadingModularActivity(Class<? extends LoadingViewHolder> loadingViewHolderClass,
                                  ActivityCallBackModule... modules) {
        this(new LoadingModule(loadingViewHolderClass), modules);
    }

    private LoadingModularActivity(LoadingModule loadingModule,
                                   ActivityCallBackModule... additionalModules) {
        super(ArrayTools.add(additionalModules, 0, loadingModule));
    }

    public LoadingViewHolder getLoadingViewHolder() {
        return findModule(LoadingModule.class).getLoadingViewHolder();
    }

    public LoadingViewHolder.HolderInfo<?> getLoadingHolderInfo() {
        return findModule(LoadingModule.class).getLoadingHolderInfo();
    }
}
