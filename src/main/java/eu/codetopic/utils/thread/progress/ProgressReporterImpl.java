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

import android.support.annotation.AnyThread;

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
        if (showingProgress && max == 100 && progress == 0 && !intermediate) return;
        showingProgress = true;
        max = 100;
        progress = 0;
        intermediate = false;
        onChange(getProgressInfo());
    }

    @Override
    public synchronized void stopShowingProgress() {
        if (!showingProgress && max == 100 && progress == 0 && intermediate) return;
        showingProgress = false;
        max = 100;
        progress = 0;
        intermediate = true;
        onChange(getProgressInfo());
    }

    @Override
    public synchronized void setMaxProgress(int max) {
        if (this.max == max && !intermediate) return;
        this.max = max;
        intermediate = false;
        update();
    }

    @Override
    public synchronized void reportProgress(int progress) {
        if (this.progress == progress && !intermediate) return;
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

    @AnyThread
    protected abstract void onChange(ProgressInfo info);

}
