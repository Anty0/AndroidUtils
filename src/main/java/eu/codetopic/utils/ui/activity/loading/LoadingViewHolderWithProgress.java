/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
