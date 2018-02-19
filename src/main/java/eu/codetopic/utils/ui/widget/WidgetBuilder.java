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

package eu.codetopic.utils.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import eu.codetopic.utils.*;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.R;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.ui.container.adapter.widget.CustomItemWidgetAdapter;
import eu.codetopic.utils.ui.container.adapter.widget.WidgetCustomItemsProvider;

import static eu.codetopic.utils.ExtensionsKt.getIconics;

public class WidgetBuilder {

    private static final String LOG_TAG = "WidgetBuilder";

    private final Context mContext;
    private int[] appWidgetIds = new int[0];

    private boolean hasHeader;
    @Nullable private PendingIntent headerIntent;
    @Nullable private PendingIntent refreshIntent;
    @NonNull private CharSequence headerTitle = "";
    @ColorInt private int headerBackgroundColor;

    @ColorInt private int backgroundColor;

    @Nullable private RemoteViews ownDataContent;
    @Nullable private WidgetCustomItemsProvider itemsProvider;
    @Nullable private CharSequence emptyViewText;
    @DrawableRes private int emptyViewImageSrc;

    private WidgetBuilder(Context context, int[] appWidgetIds) {
        this(context);
        this.appWidgetIds = appWidgetIds;
    }

    @TargetApi(14)
    private WidgetBuilder(Context context) {
        mContext = context;
        setupDefaults();
    }

    public static Intent getUpdateIntent(Context context, Class<? extends AppWidgetProvider> widgetClass) {
        return new Intent(context.getApplicationContext(), widgetClass)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                        getAllWidgetIds(context, widgetClass));
    }

    public static int[] getAllWidgetIds(Context context, Class<? extends AppWidgetProvider> widgetClass) {
        return AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, widgetClass));
    }

    public static void notifyItemsChanged(Context context, Bundle intentData,
                                          Class<? extends AppWidgetProvider> widgetClass) {
        if (Build.VERSION.SDK_INT >= 11) {
            notifyItemsChanged(context, widgetClass);
            return;
        }
        context.sendBroadcast(new Intent(context.getApplicationContext(), widgetClass)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                        getAllWidgetIds(context, widgetClass))
                .putExtras(intentData));
    }

    @TargetApi(11)
    public static void notifyItemsChanged(Context context, Class<? extends AppWidgetProvider> widgetClass) {
        notifyItemsChanged(context, getAllWidgetIds(context, widgetClass));
    }

    @TargetApi(11)
    public static void notifyItemsChanged(Context context, int[] appWidgetIds) {
        AppWidgetManager.getInstance(context)
                .notifyAppWidgetViewDataChanged(appWidgetIds, R.id.content_list_view);
    }

    public static WidgetBuilder build(Context context) {
        return new WidgetBuilder(context);
    }

    @SuppressLint("PrivateResource")
    private void setupDefaults() {
        hasHeader = true;
        headerIntent = null;
        refreshIntent = null;
        headerTitle = AndroidUtils.getAppLabel(mContext);
        headerBackgroundColor = AndroidUtils.getColorFromAttr(mContext, R.attr.colorPrimary,
                ContextCompat.getColor(mContext, R.color.primary_material_dark));

        backgroundColor = AndroidUtils.makeColorDarker(AndroidUtils.getColorFromAttr(mContext, R.attr.colorPrimaryDark,
                ContextCompat.getColor(mContext, R.color.primary_dark_material_dark)), 0.5f);

        itemsProvider = null;
        emptyViewText = null;
        emptyViewImageSrc = 0;
    }

    public WidgetBuilder setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        return this;
    }

    public WidgetBuilder setHeaderIntent(@NonNull IntentType type, @NonNull Intent intent) {
        return setHeaderIntent(type.toPendingIntent(mContext, intent));
    }

    public WidgetBuilder setHeaderIntent(@Nullable PendingIntent headerIntent) {
        this.headerIntent = headerIntent;
        return this;
    }

    public WidgetBuilder setRefreshIntent(@NonNull IntentType type, @NonNull Intent intent) {
        return setRefreshIntent(type.toPendingIntent(mContext, intent));
    }

    public WidgetBuilder setRefreshIntent(@Nullable PendingIntent refreshIntent) {
        this.refreshIntent = refreshIntent;
        return this;
    }

    public WidgetBuilder setHeaderTitle(@StringRes int headerTitleRes) {
        return setHeaderTitle(mContext.getText(headerTitleRes));
    }

    public WidgetBuilder setHeaderTitle(@NonNull CharSequence headerTitle) {
        this.headerTitle = headerTitle;
        return this;
    }

    public WidgetBuilder setHeaderBackgroundColor(@ColorInt int headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
        return this;
    }

    public WidgetBuilder setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public WidgetBuilder setOwnDataContent(@Nullable RemoteViews ownDataContent) {
        this.ownDataContent = ownDataContent;
        return this;
    }

    public WidgetBuilder setItemsProvider(@Nullable WidgetCustomItemsProvider itemsProvider) {
        this.itemsProvider = itemsProvider;
        return this;
    }

    public WidgetBuilder setEmptyViewText(@StringRes int emptyViewTextRes) {
        return setEmptyViewText(mContext.getText(emptyViewTextRes));
    }

    public WidgetBuilder setEmptyViewText(@Nullable CharSequence emptyViewText) {
        this.emptyViewText = emptyViewText;
        return this;
    }

    public WidgetBuilder setEmptyViewImageSrc(@DrawableRes int emptyViewImageSrc) {
        this.emptyViewImageSrc = emptyViewImageSrc;
        return this;
    }

    public WidgetApplicator apply() {
        return apply(ContentType.FULL);
    }

    public WidgetApplicator apply(@NonNull ContentType type) {
        return new WidgetApplicator(mContext, get(type));
    }

    public RemoteViews get() {
        return get(ContentType.FULL);
    }

    public RemoteViews get(@NonNull ContentType type) {
        //only-content
        if (type == ContentType.DATA)
            return getDataView(null);

        //base
        RemoteViews baseView = new RemoteViews(mContext.getPackageName(), R.layout.widget_base_layout);

        baseView.setTextViewText(R.id.text_view_title, headerTitle);
        baseView.setInt(R.id.relative_layout_widget_main,
                "setBackgroundColor", headerBackgroundColor);
        baseView.setViewVisibility(R.id.relative_layout_widget_main,
                hasHeader ? View.VISIBLE : View.GONE);

        baseView.setInt(R.id.content_frame_layout, "setBackgroundColor",
                AndroidUtils.makeColorTransparent(backgroundColor, 30));

        baseView.setImageViewBitmap(
                R.id.image_button_refresh,
                getIconics(mContext,
                        GoogleMaterial.Icon.gmd_refresh).actionBar().toBitmap()
        );

        baseView.removeAllViews(R.id.content_frame_layout);

        //loading
        if (type == ContentType.FULL_LOADING) {
            baseView.addView(R.id.content_frame_layout, new RemoteViews(mContext
                    .getPackageName(), R.layout.widget_content_loading));
            return baseView;
        }

        //clicks
        baseView.setOnClickPendingIntent(R.id.image_button_refresh, refreshIntent);
        baseView.setOnClickPendingIntent(R.id.relative_layout_widget_main, headerIntent);

        //content
        baseView.addView(R.id.content_frame_layout, getDataView(baseView));

        return baseView;
    }

    private RemoteViews getDataView(@Nullable RemoteViews baseView) {
        if (ownDataContent != null) return ownDataContent;
        RemoteViews dataView;

        if (baseView != null) {
            dataView = new RemoteViews(mContext.getPackageName(), R.layout.widget_content_new);
            dataView.setEmptyView(R.id.content_list_view, R.id.empty_view);

            Intent serviceIntent = CustomItemWidgetAdapter.getServiceIntent(mContext, itemsProvider);
            baseView.setRemoteAdapter(R.id.content_list_view, serviceIntent);
        } else {
            dataView = new RemoteViews(mContext.getPackageName(), R.layout.base_empty_view);
        }

        if (emptyViewImageSrc != 0) {
            dataView.setImageViewResource(R.id.empty_image, emptyViewImageSrc);
            dataView.setViewVisibility(R.id.empty_image, View.VISIBLE);
        }
        if (emptyViewText != null) dataView.setTextViewText(R.id.empty_text, emptyViewText);
        return dataView;
    }

    public enum ContentType {
        FULL_LOADING, FULL, DATA
    }

    public enum IntentType {
        BROADCAST, SERVICE, ACTIVITY;

        public PendingIntent toPendingIntent(Context context, Intent intent) {
            int requestCode = Identifiers.Companion.next(Identifiers.Companion.getTYPE_REQUEST_CODE());
            switch (this) {
                case BROADCAST:
                    return PendingIntent.getBroadcast(context, requestCode,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT);
                case SERVICE:
                    return PendingIntent.getService(context, requestCode,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT);
                case ACTIVITY:
                    return PendingIntent.getActivity(context, requestCode,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
            return null;
        }
    }

    public static class WidgetApplicator {

        private final Context mContext;
        private final RemoteViews mRemoteViews;

        private WidgetApplicator(Context context, RemoteViews toApply) {
            mContext = context;
            mRemoteViews = toApply;
        }

        public void on(int[] appWidgetIds) {
            on(AppWidgetManager.getInstance(mContext), appWidgetIds);
        }

        public void on(AppWidgetManager manager, int[] appWidgetIds) {
            manager.updateAppWidget(appWidgetIds, mRemoteViews);
        }

        public void on(Class<? extends AppWidgetProvider> widgetClass) {
            on(AppWidgetManager.getInstance(mContext), widgetClass);
        }

        public void on(AppWidgetManager manager, Class<? extends AppWidgetProvider> widgetClass) {
            manager.updateAppWidget(new ComponentName(mContext, widgetClass), mRemoteViews);
        }
    }

}
