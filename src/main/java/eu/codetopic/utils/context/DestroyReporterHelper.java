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

package eu.codetopic.utils.context;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@SuppressWarnings("deprecation")
public class DestroyReporterHelper implements ActivityDestroyReporter {

    private static final String LOG_TAG = "DestroyReporterHelper";

    private final List<ActivityDestroyListener> mListeners = new ArrayList<>();

    private boolean destroyed = false;

    public synchronized void registerListener(ActivityDestroyListener listener) {
        if (destroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mListeners.add(listener);
    }

    public synchronized void unregisterListener(ActivityDestroyListener listener) {
        if (destroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mListeners.remove(listener);
    }

    public synchronized void reportDestroy() {
        for (ActivityDestroyListener listener : mListeners)
            listener.onDestroy();
        destroyed = true;
        mListeners.clear();
    }

}
