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

package eu.codetopic.utils.ui.activity.loading;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import eu.codetopic.utils.thread.progress.ProgressBarReporter;
import eu.codetopic.utils.thread.progress.ProgressReporter;

/**
 * Use ProgressLoadingVH instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingViewHolderWithProgress extends LoadingViewHolderImpl {

    private static final String LOG_TAG = "LoadingViewHolderWithProgress";

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

    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof ProgressBarReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        View loading = getLoadingView();
        ((ProgressBarReporter) progressReporter).setProgressBar(loading instanceof ProgressBar
                ? (ProgressBar) loading : null);
    }

    @Override
    protected void onUpdateMainView(@Nullable View newMainView) {
        super.onUpdateMainView(newMainView);
        if (progressReporter != null)
            updateProgressReporter(progressReporter);
    }
}
