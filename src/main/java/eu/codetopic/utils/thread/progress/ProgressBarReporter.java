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
