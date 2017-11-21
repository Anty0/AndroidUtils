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

package eu.codetopic.utils.ui.view.holder.loading;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;

import eu.codetopic.utils.thread.progress.ProgressBarReporter;
import eu.codetopic.utils.thread.progress.ProgressReporter;

public abstract class ProgressLoadingVH extends LoadingVHImpl {

    private static final String LOG_TAG = "ProgressLoadingVH";

    private ProgressReporter progressReporter = null;

    public ProgressReporter getProgressReporter() {
        if (progressReporter == null)
            progressReporter = generateProgressReporter();

        return progressReporter;
    }

    protected ProgressReporter generateProgressReporter() {
        View loading = getLoadingView();
        return new ProgressBarReporter(loading instanceof ProgressBar
                ? (ProgressBar) loading : null);
    }

    @UiThread
    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof ProgressBarReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        View loading = getLoadingView();
        ((ProgressBarReporter) progressReporter).setProgressBar(loading instanceof ProgressBar
                ? (ProgressBar) loading : null);
    }

    @UiThread
    @Override
    protected void onUpdateView(@Nullable View view) {
        super.onUpdateView(view);
        if (progressReporter != null)
            updateProgressReporter(progressReporter);
    }

}
