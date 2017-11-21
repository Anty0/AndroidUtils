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

package eu.codetopic.utils.thread.progress;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.thread.JobUtils;

public class ProgressBarReporter extends ProgressReporterImpl {

    private static final String LOG_TAG = "ProgressBarReporter";

    private WeakReference<ProgressBar> progressBarRef;

    public ProgressBarReporter() {
        setProgressBar((ProgressBar) null);
    }

    public ProgressBarReporter(@Nullable ProgressBar progressBar) {
        setProgressBar(progressBar);
    }

    public ProgressBarReporter(@NonNull WeakReference<ProgressBar> progressBarRef) {
        setProgressBar(progressBarRef);
    }

    public void setProgressBar(@Nullable ProgressBar progressBar) {
        setProgressBar(new WeakReference<>(progressBar));
        update();
    }

    public void setProgressBar(@NonNull WeakReference<ProgressBar> progressBarRef) {
        this.progressBarRef = progressBarRef;
        update();
    }

    @Override
    protected void onChange(final ProgressInfo info) {
        JobUtils.postOnViewThread(progressBarRef.get(), new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = progressBarRef.get();
                if (progressBar != null) {
                    progressBar.setMax(info.getMaxProgress());
                    progressBar.setProgress(info.getProgress());
                    progressBar.setIndeterminate(info.isIntermediate());
                }
            }
        });
    }
}
