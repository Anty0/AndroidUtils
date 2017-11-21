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

import android.support.annotation.WorkerThread;

public abstract class ProgressReporterImpl implements ProgressReporter {

    private static final String LOG_TAG = "ProgressReporterImpl";

    private boolean showingProgress = false;
    private int max = 100;
    private int progress = 0;
    private boolean intermediate = true;

    public boolean isShowingProgress() {
        return showingProgress;
    }

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isIntermediate() {
        return intermediate;
    }

    @Override
    public synchronized void setIntermediate(boolean intermediate) {
        this.intermediate = intermediate;
        update();
    }

    @Override
    public synchronized void startShowingProgress() {
        showingProgress = true;
        max = 100;
        progress = 0;
        intermediate = false;
        onChange(getProgressInfo());
    }

    @Override
    public synchronized void stopShowingProgress() {
        showingProgress = false;
        max = 100;
        progress = 0;
        intermediate = true;
        onChange(getProgressInfo());
    }

    @Override
    public synchronized void setMaxProgress(int max) {
        this.max = max;
        intermediate = false;
        update();
    }

    @Override
    public synchronized void reportProgress(int progress) {
        this.progress = progress;
        intermediate = false;
        update();
    }

    @Override
    public void stepProgress(int step) {
        reportProgress(progress + step);
    }

    @Override
    public ProgressInfo getProgressInfo() {
        return new ProgressInfoImpl(showingProgress, max, progress, intermediate);
    }

    protected final synchronized void update() {
        if (!showingProgress) return;
        onChange(getProgressInfo());
    }

    @WorkerThread
    protected abstract void onChange(ProgressInfo info);

}
