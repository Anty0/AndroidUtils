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

package eu.codetopic.utils.ui.view.holder.loading;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.progress.ProgressInfo;
import eu.codetopic.utils.thread.progress.ProgressReporter;
import eu.codetopic.utils.thread.progress.ProgressReporterImpl;

public class DefaultLoadingVH extends ProgressLoadingVH {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.loading_base;
    @IdRes protected static final int CONTENT_VIEW_ID = R.id.base_loadable_content;
    @IdRes protected static final int LOADING_VIEW_ID = R.id.base_loading;
    @IdRes protected static final int CIRCLE_LOADING_VIEW_ID = R.id.base_loading_circle;
    @IdRes protected static final int HORIZONTAL_LOADING_VIEW_ID = R.id.base_loading_horizontal;

    private static final String LOG_TAG = "DefaultLoadingVH";

    @NonNull
    @Override
    protected LoadingWrappingInfo getWrappingInfo() {
        return new LoadingWrappingInfo(LOADING_LAYOUT_ID, CONTENT_VIEW_ID, LOADING_VIEW_ID);
    }

    @Override
    protected DualProgressReporter generateProgressReporter() {
        return doUpdateReporter(new DualProgressReporter());
    }

    @UiThread
    @Override
    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof DualProgressReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        doUpdateReporter((DualProgressReporter) reporter);
    }

    private DualProgressReporter doUpdateReporter(DualProgressReporter reporter) {
        reporter.loadingViewRef = getLoadingViewRef();
        reporter.onChange(reporter.getProgressInfo());
        return reporter;
    }

    private static class DualProgressReporter extends ProgressReporterImpl {

        WeakReference<View> loadingViewRef;

        @Override
        protected void onChange(final ProgressInfo info) {
            JobUtils.postOnViewThread(loadingViewRef.get(), new Runnable() {
                @Override
                public void run() {
                    View loadingView = loadingViewRef.get();
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
