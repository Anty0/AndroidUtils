package eu.codetopic.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.WorkerThread;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import eu.codetopic.utils.database.DatabaseObject;
import eu.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 17.10.15.
 *
 * @author anty
 */
public class Utils {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private static final String LOG_TAG = "Utils";

    public static void setPadding(View view, int horizontal, int vertical) {
        setPadding(view, horizontal, vertical, horizontal, vertical);
    }

    public static void setPadding(View view, int left, int top, int right, int bottom) {
        Context context = view.getContext();
        view.setPadding((int) toDP(context, left), (int) toDP(context, top),
                (int) toDP(context, right), (int) toDP(context, bottom));
    }

    public static float toDP(Context context, int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size, context.getResources().getDisplayMetrics());
    }

    /**
     * Returns text between start and end in base
     *
     * @param base  string to cut from
     * @param start text before text to return
     * @param end   text after text to return
     * @return text between start and end
     */
    public static String substring(@NonNull String base, @Nullable String start,
                                   @Nullable String end) {

        int startIndex = start != null ? base.indexOf(start) : -1;
        if (startIndex != -1) startIndex += start.length();
        int endIndex = end != null ? base.indexOf(end) : -1;
        return base.substring(startIndex != -1 ? startIndex : 0,
                endIndex != -1 ? endIndex : base.length());
    }

    public static CharSequence getFormattedText(Context context, @StringRes int stringId,
                                                Object... args) {
        return getFormattedText(context.getString(stringId), args);
    }

    public static CharSequence getFormattedText(String text, Object... args) {
        return Html.fromHtml(String.format(text, args));
    }

    public static byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap cropBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width >= height)
            //noinspection SuspiciousNameCombination
            return Bitmap.createBitmap(bitmap, width / 2 - height / 2, 0, height, height);
        else
            //noinspection SuspiciousNameCombination
            return Bitmap.createBitmap(bitmap, 0, height / 2 - width / 2, width, width);
    }

    @TargetApi(11)
    public static <T> void setupSpinner(final Spinner spinner, List<T> list) {
        final ArrayAdapter<T> adapter = new ArrayAdapter<>(
                spinner.getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(list);
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                spinner.setAdapter(adapter);
            }
        });
    }

    public static CharSequence getTimeFromLong(Context context, long time) {
        if (time < 0) return context.getText(R.string.text_no_time);
        return getFormattedText(context, R.string.text_time_format, TimeUnit.MILLISECONDS.toMinutes(time), // TODO: 26.3.16 use plural string
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MILLISECONDS.toMinutes(time) * 60);
    }

    public static String locationToString(Context context, double latitude, double longitude) {
        return Double.isNaN(latitude) || Double.isNaN(longitude) ? context.getString(R.string.location_unknown) :
                Location.convert(latitude, Location.FORMAT_DEGREES) + ", " +
                        Location.convert(longitude, Location.FORMAT_DEGREES);
    }

    public static <T extends DatabaseObject> int findIndexById(Long id, List<T> databaseObjects) {
        for (int i = 0, size = databaseObjects.size(); i < size; i++) {
            if (Objects.equals(id, databaseObjects.get(i).getId()))
                return i;
        }
        return -1;
    }

    public static <T extends DatabaseObject> int findIndexById(Long id, T[] databaseObjects) {
        for (int i = 0, size = databaseObjects.length; i < size; i++) {
            if (Objects.equals(id, databaseObjects[i].getId()))
                return i;
        }
        return -1;
    }

    @ColorInt
    public static int makeColorDarker(@ColorInt int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int makeColorTransparent(@ColorInt int color, int factor) {
        return Color.argb(
                Color.alpha(color) - factor,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    @ColorInt
    public static int getColorFromAttr(Context themedContext, @AttrRes int attr, @ColorInt int defValue) {
        TypedArray a = null;
        try {
            a = themedContext.obtainStyledAttributes(new int[]{attr});
            return a.getColor(0, defValue);
        } finally {
            if (a != null) a.recycle();
        }
    }

    @AnyRes
    public static int getResIdFromAttr(Context themedContext, @AttrRes int attr, @AnyRes int defValue) {
        TypedArray a = null;
        try {
            a = themedContext.obtainStyledAttributes(new int[]{attr});
            return a.getResourceId(0, defValue);
        } finally {
            if (a != null) a.recycle();
        }
    }

    public static CharSequence getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager());
    }

    public static CharSequence getApplicationName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        return pm.getApplicationInfo(packageName, 0).loadLabel(pm);
    }

    public static Drawable getActivityIcon(Context context, ComponentName component) {
        PackageManager pm = context.getPackageManager();
        return pm.resolveActivity(new Intent().setComponent(component), 0).loadIcon(pm);
    }

    @WorkerThread
    public static String getLocationName(Context context, double lat, double lon) {
        try {
            String res = Jsoup.connect("http://maps.googleapis.com/maps/api/geocode/json")
                    .method(Connection.Method.GET)
                    .data("latlng", lat + "," + lon, "sensor", "true")
                    .ignoreContentType(true)
                    .execute().body();

            if (res != null && !res.equalsIgnoreCase("")) {
                JSONArray jArray = new JSONObject(res).getJSONArray("results");
                if (jArray.length() > 0) {
                    String result = jArray.getJSONObject(0).getString("formatted_address").trim();
                    if (result.endsWith(","))
                        result = result.substring(0, result.length() - 1);
                    return result;
                }
            }
        } catch (IOException | JSONException e) {
            Log.d(LOG_TAG, "getLocationName", e);
        }
        return context.getString(R.string.location_none);
    }

    public static <T extends View> List<T> findViewsByClass(Class<T> clazz, Activity activity) {
        return findViewsByClass(clazz, activity.getWindow().getDecorView());
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> List<T> findViewsByClass(Class<T> clazz, View view) {
        List<T> views = new ArrayList<>();
        if (Objects.equals(view.getClass(), clazz)) views.add((T) view);
        if (!(view instanceof ViewGroup)) return views;
        for (int i = 0, count = ((ViewGroup) view).getChildCount(); i < count; i++) {
            views.addAll(findViewsByClass(clazz, ((ViewGroup) view).getChildAt(i)));
        }
        return views;
    }

    public static String drawViewHierarchyToString(View view, boolean showId) {
        StringBuilder builder = new StringBuilder();
        drawViewHierarchyToString(builder, view, showId, 1);
        return builder.toString();
    }

    private static void drawViewHierarchyToString(StringBuilder builder, View view, boolean showId, int depth) {
        for (int i = 0; i < depth; i++) builder.append("-");
        if (showId) builder.append(fillToMaxLen(view.getId())).append("|");
        builder.append(view.getClass().getSimpleName()).append("|")
                .append(view.getClass().getName()).append("\n");
        if (view instanceof ViewGroup) {
            for (int i = 0; i < depth; i++) builder.append("-");
            builder.append("\\\n");

            for (int i = 0, count = ((ViewGroup) view).getChildCount(); i < count; i++)
                drawViewHierarchyToString(builder, ((ViewGroup) view).getChildAt(i), showId, depth + 1);
        }
    }

    public static String fillToMaxLen(int toFill) {
        return fillToLen(Integer.toString(toFill),
                Integer.toString(Integer.MAX_VALUE).length());
    }

    public static String fillToLen(CharSequence toFill, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = toFill.length(); i < len; i++)
            builder.append(" ");
        builder.append(toFill);
        return builder.toString();
    }

    public static String addBeforeEveryLine(String toEdit, String toAdd) {
        return toAdd + toEdit.replace("\n", "\n" + toAdd);
    }
}
