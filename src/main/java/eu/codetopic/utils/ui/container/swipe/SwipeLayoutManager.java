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

package eu.codetopic.utils.ui.container.swipe;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import eu.codetopic.utils.*;
import eu.codetopic.utils.thread.LooperUtils;
import kotlin.Unit;

import static eu.codetopic.utils.ExtensionsKt.getIconics;

public abstract class SwipeLayoutManager<T extends SwipeLayoutManager<T>> {

    private static final String LOG_TAG = "SwipeLayoutManager";

    private final Context mContext;
    private final View mMainView;
    private final SwipeRefreshLayout mSwipeRefreshLayout;
    private final FloatingActionButton mFAB;

    protected SwipeLayoutManager(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                 boolean useSwipeRefresh, boolean useFloatingActionButton) {
        mContext = mainView.getContext();
        mMainView = mainView;
        mFAB = mainView.findViewById(R.id.floatingActionButton);
        mFAB.setImageDrawable(getIconics(getContext(),
                GoogleMaterial.Icon.gmd_add).actionBar());
        mSwipeRefreshLayout = mainView.findViewById(R.id.swipe_refresh_layout);

        if (swipeSchemeColors != null)
            mSwipeRefreshLayout.setColorSchemeColors(swipeSchemeColors);
        mSwipeRefreshLayout.setEnabled(useSwipeRefresh);

        mFAB.setVisibility(useFloatingActionButton ? View.VISIBLE : View.GONE);
    }

    protected abstract T self();

    public Context getContext() {
        return mContext;
    }

    public synchronized View getBaseView() {
        return mMainView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public synchronized T setOnRefreshListener(final SwipeRefreshLayout.OnRefreshListener listener) {
        LooperUtils.runOnMainThread(() -> {
            getSwipeRefreshLayout().setOnRefreshListener(listener);
            return Unit.INSTANCE;
        });
        return self();
    }

    public synchronized T setOnRefreshListener(final OnSwipeRefreshListener listener) {
        return setOnRefreshListener(() -> listener.onRefresh(getSwipeRefreshLayout()));
    }

    public synchronized T setRefreshing(final boolean refreshing) {
        LooperUtils.runOnMainThread(() -> {
            getSwipeRefreshLayout().setRefreshing(refreshing);
            return Unit.INSTANCE;
        });
        return self();
    }

    public FloatingActionButton getFloatingActionButton() {
        return mFAB;
    }

    public synchronized T setFabClickListener(final View.OnClickListener listener) {
        LooperUtils.runOnMainThread(() -> {
            getFloatingActionButton().setOnClickListener(listener);
            return Unit.INSTANCE;
        });
        return self();
    }

    public synchronized T setEmptyImage(@Nullable final Drawable image) {
        LooperUtils.runOnMainThread(() -> {
            ImageView imageView = getBaseView().findViewById(R.id.empty_image);
            imageView.setImageDrawable(image);
            imageView.setVisibility(image != null ? View.VISIBLE : View.GONE);
            return Unit.INSTANCE;
        });
        return self();
    }

    public synchronized T setEmptyImage(@NonNull final Bitmap image) {
        LooperUtils.runOnMainThread(() -> {
            ImageView imageView = getBaseView().findViewById(R.id.empty_image);
            imageView.setImageBitmap(image);
            imageView.setVisibility(View.VISIBLE);
            return Unit.INSTANCE;
        });
        return self();
    }

    @TargetApi(23)
    public synchronized T setEmptyImage(@Nullable final Icon image) {
        LooperUtils.runOnMainThread(() -> {
            ImageView imageView = getBaseView().findViewById(R.id.empty_image);
            imageView.setImageIcon(image);
            imageView.setVisibility(image != null ? View.VISIBLE : View.GONE);
            return Unit.INSTANCE;
        });
        return self();
    }


    public synchronized T setEmptyImage(@DrawableRes final int imageResId) {
        LooperUtils.runOnMainThread(() -> {
            ImageView imageView = getBaseView().findViewById(R.id.empty_image);
            imageView.setImageResource(imageResId);
            imageView.setVisibility(View.VISIBLE);
            return Unit.INSTANCE;
        });
        return self();
    }

    public synchronized T setEmptyText(@StringRes int textRes) {
        return setEmptyText(getContext().getText(textRes));
    }

    public synchronized T setEmptyText(@Nullable final CharSequence text) {
        LooperUtils.runOnMainThread(() -> {
            TextView textView = getBaseView().findViewById(R.id.empty_text);
            textView.setText(text);
            return Unit.INSTANCE;
        });
        return self();
    }

    public synchronized T setSmallEmptyText(@StringRes int textRes) {
        return setSmallEmptyText(getContext().getText(textRes));
    }

    public synchronized T setSmallEmptyText(@Nullable final CharSequence text) {
        LooperUtils.runOnMainThread(() -> {
            TextView textView = getBaseView().findViewById(R.id.empty_text_small);
            textView.setText(text);
            textView.setVisibility(text != null ? View.VISIBLE : View.GONE);
            return Unit.INSTANCE;
        });
        return self();
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnSwipeRefreshListener {
        void onRefresh(SwipeRefreshLayout view);
    }
}
