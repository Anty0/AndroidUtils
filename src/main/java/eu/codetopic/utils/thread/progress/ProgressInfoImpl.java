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

public class ProgressInfoImpl implements ProgressInfo {

    private final int maxProgress, progress;
    private final boolean showingProgress, intermediate;

    public ProgressInfoImpl(boolean showingProgress, int maxProgress, int progress, boolean intermediate) {
        this.showingProgress = showingProgress;
        this.maxProgress = maxProgress;
        this.progress = progress;
        this.intermediate = intermediate;
    }

    @Override
    public boolean isShowingProgress() {
        return showingProgress;
    }

    @Override
    public boolean isIntermediate() {
        return intermediate;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public int getProgress() {
        return progress;
    }
}
