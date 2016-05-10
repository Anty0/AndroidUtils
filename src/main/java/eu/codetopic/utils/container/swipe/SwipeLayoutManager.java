package eu.codetopic.utils.container.swipe;

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

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 10.5.16.
 *
 * @author anty
 */
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
        mFAB = (FloatingActionButton) mainView.findViewById(R.id.floatingActionButton);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainView
                .findViewById(R.id.swipe_refresh_layout);

        Log.d(LOG_TAG, "<init> for " + mContext.getClass().getName());

        /*ArrayList<Module> modules = new ArrayList<>(ModulesManager.getInstance().getModules());
        Collections.sort(modules);// TODO: 9.5.16 use it to init modules colors (and use to obtaining styles attributes method in Utils)
        int[] colors = new int[0];
        for (Module module : modules) {
            TypedArray a = module.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, -1);
            if (color != -1 && !Arrays.contains(colors, color)) colors = Arrays.add(colors, color);
            a.recycle();
        }*/
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

        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                getSwipeRefreshLayout().setOnRefreshListener(listener);
            }
        });
        return self();
    }

    public synchronized T setRefreshing(final boolean refreshing) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                getSwipeRefreshLayout().setRefreshing(refreshing);
            }
        });
        return self();
    }

    public FloatingActionButton getFloatingActionButton() {
        return mFAB;
    }

    public synchronized T setFabClickListener(final View.OnClickListener listener) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                getFloatingActionButton().setOnClickListener(listener);
            }
        });
        return self();
    }

    public synchronized T setEmptyImage(final Drawable image) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) getBaseView().findViewById(R.id.empty_image)).setImageDrawable(image);
            }
        });
        return self();
    }

    public synchronized T setEmptyImage(final Bitmap image) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) getBaseView().findViewById(R.id.empty_image)).setImageBitmap(image);
            }
        });
        return self();
    }

    @TargetApi(23)
    public synchronized T setEmptyImage(final Icon image) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) getBaseView().findViewById(R.id.empty_image)).setImageIcon(image);
            }
        });
        return self();
    }


    public synchronized T setEmptyImage(@DrawableRes final int imageResId) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) getBaseView().findViewById(R.id.empty_image)).setImageResource(imageResId);
            }
        });
        return self();
    }

    public synchronized T setEmptyText(@StringRes int textRes) {
        return setEmptyText(getContext().getText(textRes));
    }

    public synchronized T setEmptyText(final CharSequence text) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) getBaseView().findViewById(R.id.empty_text)).setText(text);
            }
        });
        return self();
    }
}
