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
