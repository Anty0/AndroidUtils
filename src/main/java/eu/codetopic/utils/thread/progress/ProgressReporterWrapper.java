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

package eu.codetopic.utils.thread.progress;

public class ProgressReporterWrapper implements ProgressReporter {

    private final ProgressReporter mBase;

    public ProgressReporterWrapper(ProgressReporter base) {
        mBase = base;
    }

    @Override
    public void startShowingProgress() {
        mBase.startShowingProgress();
    }

    @Override
    public void stopShowingProgress() {
        mBase.stopShowingProgress();
    }

    @Override
    public void setIntermediate(boolean intermediate) {
        mBase.setIntermediate(intermediate);
    }

    @Override
    public void setMaxProgress(int max) {
        mBase.setMaxProgress(max);
    }

    @Override
    public void reportProgress(int progress) {
        mBase.reportProgress(progress);
    }

    @Override
    public void stepProgress(int step) {
        mBase.stepProgress(step);
    }

    @Override
    public ProgressInfo getProgressInfo() {
        return mBase.getProgressInfo();
    }
}
