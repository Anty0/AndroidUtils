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

package eu.codetopic.utils.ui.view.holder.loading;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.LooperUtils;
import eu.codetopic.utils.thread.progress.ProgressInfo;
import eu.codetopic.utils.thread.progress.ProgressReporter;
import eu.codetopic.utils.thread.progress.ProgressReporterImpl;
import kotlin.Unit;

public class DefaultLoadingVH extends ProgressLoadingVH {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.view_holder_loading;
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
            LooperUtils.postOnViewThread(loadingViewRef.get(), () -> {
                View loadingView = loadingViewRef.get();
                if (loadingView != null) {
                    View circleProgress = loadingView.findViewById(CIRCLE_LOADING_VIEW_ID);
                    ProgressBar horizontalProgress = loadingView
                            .findViewById(HORIZONTAL_LOADING_VIEW_ID);

                    circleProgress.setVisibility(info.isShowingProgress() ? View.GONE : View.VISIBLE);
                    horizontalProgress.setVisibility(info.isShowingProgress() ? View.VISIBLE : View.GONE);
                    horizontalProgress.setMax(info.getMaxProgress());
                    horizontalProgress.setProgress(info.getProgress());
                    horizontalProgress.setIndeterminate(info.isIntermediate());
                }
                return Unit.INSTANCE;
            });
        }
    }
}
