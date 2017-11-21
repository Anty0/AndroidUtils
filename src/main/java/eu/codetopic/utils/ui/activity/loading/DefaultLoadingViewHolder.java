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

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ProgressBar;

import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.progress.ProgressInfo;
import eu.codetopic.utils.thread.progress.ProgressReporter;
import eu.codetopic.utils.thread.progress.ProgressReporterImpl;
import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Use DefaultLoadingVH instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public class DefaultLoadingViewHolder extends LoadingViewHolderWithProgress {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.loading_base;
    @IdRes protected static final int CONTENT_VIEW_ID = R.id.base_loadable_content;
    @IdRes protected static final int LOADING_VIEW_ID = R.id.base_loading;
    @IdRes protected static final int CIRCLE_LOADING_VIEW_ID = R.id.base_loading_circle;
    @IdRes protected static final int HORIZONTAL_LOADING_VIEW_ID = R.id.base_loading_horizontal;

    private static final String LOG_TAG = "DefaultLoadingViewHolder";

    @Keep
    @KeepName
    private static HolderInfo<DefaultLoadingViewHolder> getHolderInfo() {
        return new HolderInfo<>(DefaultLoadingViewHolder.class, true,
                LOADING_LAYOUT_ID, CONTENT_VIEW_ID);
    }

    @Override
    protected int getContentViewId(Context context) {
        return CONTENT_VIEW_ID;
    }

    @Override
    protected int getLoadingViewId(Context context) {
        return LOADING_VIEW_ID;
    }

    @Override
    protected ProgressReporter generateProgressReporter() {
        return doUpdateReporter(new DualProgressReporter());
    }

    @Override
    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof DualProgressReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        doUpdateReporter((DualProgressReporter) reporter);
    }

    private DualProgressReporter doUpdateReporter(DualProgressReporter reporter) {
        reporter.loadingView = getLoadingView();
        reporter.onChange(reporter.getProgressInfo());
        return reporter;
    }

    private static class DualProgressReporter extends ProgressReporterImpl {

        View loadingView;

        @Override
        protected void onChange(final ProgressInfo info) {
            JobUtils.postOnViewThread(loadingView, new Runnable() {
                @Override
                public void run() {
                    if (loadingView != null) {
                        View circleProgress = loadingView.findViewById(CIRCLE_LOADING_VIEW_ID);
                        ProgressBar horizontalProgress = (ProgressBar) loadingView
                                .findViewById(HORIZONTAL_LOADING_VIEW_ID);

                        circleProgress.setVisibility(info.isShowingProgress() ? View.GONE : View.VISIBLE);
                        horizontalProgress.setVisibility(info.isShowingProgress() ? View.VISIBLE : View.GONE);
                        horizontalProgress.setMax(info.getMaxProgress());
                        horizontalProgress.setProgress(info.getProgress());
                        horizontalProgress.setIndeterminate(info.isIntermediate());
                    }
                }
            });
        }
    }
}
