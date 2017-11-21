/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.context;

import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

@Deprecated
@SuppressWarnings("deprecation")
public class ActivityDestroyReporterModule extends SimpleActivityCallBackModule implements ActivityDestroyReporter {

    private static final String LOG_TAG = "ActivityDestroyReporterModule";

    private final DestroyReporterHelper helper = new DestroyReporterHelper();

    public synchronized void registerListener(ActivityDestroyListener listener) {
        helper.registerListener(listener);
    }

    public synchronized void unregisterListener(ActivityDestroyListener listener) {
        helper.unregisterListener(listener);
    }

    @Override
    protected synchronized void onDestroy() {
        helper.reportDestroy();
        super.onDestroy();
    }
}
