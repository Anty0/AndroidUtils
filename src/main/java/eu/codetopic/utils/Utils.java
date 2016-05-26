package eu.codetopic.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.WorkerThread;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import eu.codetopic.utils.data.database.DatabaseObject;
import eu.codetopic.utils.thread.JobUtils;

public class Utils {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private static final String LOG_TAG = "Utils";
    private static final int VIEW_TAG_KEY_TAGS_HASH_MAP = R.id.view_tag_key_tags_hash_map;

    public static void setPaddingInDip(View view, int horizontal, int vertical) {
        setPaddingInDip(view, horizontal, vertical, horizontal, vertical);
    }

    public static void setPaddingInDip(View view, int left, int top, int right, int bottom) {
        Context context = view.getContext();
        view.setPadding((int) convertDpToPx(context, left), (int) convertDpToPx(context, top),
                (int) convertDpToPx(context, right), (int) convertDpToPx(context, bottom));
    }

    @CheckResult
    public static float convertDpToPx(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    @CheckResult
    public static float convertPxToDp(Context context, int px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi
                / DisplayMetrics.DENSITY_DEFAULT);
    }

    @CheckResult
    public static String substring(@NonNull String base, @Nullable String start,
                                   @Nullable String end) {

        int startIndex = start != null ? base.indexOf(start) : -1;
        if (startIndex != -1) startIndex += start.length();
        int endIndex = end != null ? base.indexOf(end) : -1;
        return base.substring(startIndex != -1 ? startIndex : 0,
                endIndex != -1 ? endIndex : base.length());
    }

    @CheckResult
    public static CharSequence getFormattedText(Context context, @StringRes int stringId,
                                                Object... args) {
        return getFormattedText(context.getString(stringId), args);
    }

    @CheckResult
    public static CharSequence getFormattedText(String text, Object... args) {
        return Html.fromHtml(String.format(text, args));
    }

    @CheckResult
    public static byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @CheckResult
    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @CheckResult
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

    @CheckResult
    public static CharSequence getTimeFromLong(Context context, long time) {
        if (time < 0) return context.getText(R.string.text_no_time);
        return getFormattedText(context, R.string.text_time_format, TimeUnit.MILLISECONDS.toMinutes(time), // TODO: 26.3.16 use plural string
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MILLISECONDS.toMinutes(time) * 60);
    }

    @CheckResult
    public static String locationToString(Context context, double latitude, double longitude) {
        return Double.isNaN(latitude) || Double.isNaN(longitude) ? context.getString(R.string.location_unknown) :
                Location.convert(latitude, Location.FORMAT_DEGREES) + ", " +
                        Location.convert(longitude, Location.FORMAT_DEGREES);
    }

    @CheckResult
    public static <T extends DatabaseObject> int findIndexById(Long id, List<T> databaseObjects) {
        for (int i = 0, size = databaseObjects.size(); i < size; i++) {
            if (Objects.equals(id, databaseObjects.get(i).getId()))
                return i;
        }
        return -1;
    }

    @CheckResult
    public static <T extends DatabaseObject> int findIndexById(Long id, T[] databaseObjects) {
        for (int i = 0, size = databaseObjects.length; i < size; i++) {
            if (Objects.equals(id, databaseObjects[i].getId()))
                return i;
        }
        return -1;
    }

    @ColorInt
    @CheckResult
    public static int makeColorDarker(@ColorInt int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    @CheckResult
    public static int makeColorTransparent(@ColorInt int color, int factor) {
        return Color.argb(
                Color.alpha(color) - factor,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    @ColorInt
    @CheckResult
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
    @CheckResult
    public static int getResIdFromAttr(Context themedContext, @AttrRes int attr, @AnyRes int defValue) {
        TypedArray a = null;
        try {
            a = themedContext.obtainStyledAttributes(new int[]{attr});
            return a.getResourceId(0, defValue);
        } finally {
            if (a != null) a.recycle();
        }
    }

    public static int getVersionCode(Context context) {
        try {
            return getVersionCode(context, context.getPackageName());
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return -1;
    }

    public static int getVersionCode(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
    }

    @CheckResult
    public static CharSequence getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager());
    }

    @CheckResult
    public static CharSequence getApplicationName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        return pm.getApplicationInfo(packageName, 0).loadLabel(pm);
    }

    @CheckResult
    public static Drawable getActivityIcon(Context context, ComponentName component) {
        PackageManager pm = context.getPackageManager();
        return pm.resolveActivity(new Intent().setComponent(component), 0).loadIcon(pm);
    }

    @CheckResult
    public static boolean isComponentEnabled(Context context, Class<?> componentClass) {
        return isComponentEnabled(context.getPackageManager(), new ComponentName(context, componentClass));
    }

    @CheckResult
    public static boolean isComponentEnabled(PackageManager pm, ComponentName componentName) {
        switch (pm.getComponentEnabledSetting(componentName)) {
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                return true;
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
                return false;
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
            default:
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(componentName.getPackageName(),
                            PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES |
                                    PackageManager.GET_RECEIVERS | PackageManager.GET_PROVIDERS);

                    List<ComponentInfo> components = new ArrayList<>();
                    if (packageInfo.activities != null)
                        Collections.addAll(components, packageInfo.activities);
                    if (packageInfo.services != null)
                        Collections.addAll(components, packageInfo.services);
                    if (packageInfo.receivers != null)
                        Collections.addAll(components, packageInfo.receivers);
                    if (packageInfo.providers != null)
                        Collections.addAll(components, packageInfo.providers);

                    String clsName = componentName.getClassName();
                    for (ComponentInfo componentInfo : components)
                        if (clsName.equals(componentInfo.name))
                            return componentInfo.enabled && componentInfo.applicationInfo.enabled;
                } catch (Exception e) {
                    Log.d(LOG_TAG, "isComponentEnabled", e);
                }
                return false;
        }
    }

    @WorkerThread
    @CheckResult
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

    @CheckResult
    public static Bitmap drawViewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.measure(width, height);
        view.layout(0, 0, width, height);
        view.buildDrawingCache();
        view.draw(new Canvas(bitmap));
        view.destroyDrawingCache();
        return bitmap;
    }

    @CheckResult
    public static <T extends View> List<T> findViewsByClass(Class<T> clazz, Activity activity) {
        return findViewsByClass(clazz, activity.getWindow().getDecorView());
    }

    @SuppressWarnings("unchecked")
    @CheckResult
    public static <T extends View> List<T> findViewsByClass(Class<T> clazz, View view) {
        List<T> views = new ArrayList<>();
        if (Objects.equals(view.getClass(), clazz)) views.add((T) view);
        if (!(view instanceof ViewGroup)) return views;
        for (int i = 0, count = ((ViewGroup) view).getChildCount(); i < count; i++) {
            views.addAll(findViewsByClass(clazz, ((ViewGroup) view).getChildAt(i)));
        }
        return views;
    }

    @CheckResult
    public static String drawViewHierarchy(View view, boolean showId, boolean showSizes) {
        StringBuilder sb = new StringBuilder("-");
        if (showId) sb.append(fillToMaxLen(view.getId())).append(" -> ");
        if (showSizes) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null) {
                sb.append("H: ").append(layoutParamsSizeToString(view.getLayoutParams().height))
                        .append("|").append("W: ")
                        .append(layoutParamsSizeToString(view.getLayoutParams().width))
                        .append(" -> ");
            }
        }
        sb.append(view.getClass().getSimpleName()).append(" -> ")
                .append(view.getClass().getName());
        if (view instanceof ViewGroup) {
            sb.append(" {");
            StringBuilder csb = new StringBuilder();
            for (int i = 0, count = ((ViewGroup) view).getChildCount(); i < count; i++)
                csb.append("\n").append(drawViewHierarchy(((ViewGroup) view)
                        .getChildAt(i), showId, showSizes));
            sb.append(Utils.addBeforeEveryLine(csb.toString(), "    "));
            sb.append("\n}");
        }
        return sb.toString();
    }

    @CheckResult
    public static String layoutParamsSizeToString(int size) {
        if (size == ViewGroup.LayoutParams.WRAP_CONTENT) return "wrap_content";
        if (size == ViewGroup.LayoutParams.MATCH_PARENT) return "match_parent";
        return String.valueOf(size);
    }

    public static void copyLayoutParamsToViewParents(@NonNull View view, @Nullable View maxParent) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        while (true) {
            ViewParent parent = view.getParent();
            if (!(parent instanceof View)) break;
            view = (View) parent;
            copyLayoutParamsSizesToView(view, params);
            if (parent.equals(maxParent)) break;
        }
    }

    public static ViewGroup.LayoutParams copyLayoutParamsSizesToView(@NonNull View target, @NonNull View source) {
        return copyLayoutParamsSizesToView(target, source.getLayoutParams());
    }

    public static ViewGroup.LayoutParams copyLayoutParamsSizesToView(
            @NonNull View view, @NonNull ViewGroup.LayoutParams params) {

        ViewGroup.LayoutParams oldParams = view.getLayoutParams();
        if (oldParams == null) {
            oldParams = new ViewGroup.LayoutParams(params.width, params.height);
            view.setLayoutParams(oldParams);
        } else {
            oldParams.height = params.height;
            oldParams.width = params.width;
        }
        return oldParams;
    }

    private static Map<String, Object> getTags(View view) {
        //noinspection unchecked
        Map<String, Object> tags = (Map<String, Object>)
                view.getTag(VIEW_TAG_KEY_TAGS_HASH_MAP);
        if (tags == null) {
            tags = new HashMap<>();
            view.setTag(VIEW_TAG_KEY_TAGS_HASH_MAP, tags);
        }
        return tags;
    }

    public static void setViewTag(View view, String key, Object tag) {
        getTags(view).put(key, tag);
    }

    @CheckResult
    public static Object getViewTag(View view, String key) {
        return getTags(view).get(key);
    }

    @CheckResult
    public static Object getViewTagFromChildren(@NonNull View view, String key) {
        View result = findViewWithTag(view, key);
        return result == null ? null : getViewTag(result, key);
    }

    /**
     * Look for a child view with the given tag key. If this view has the given
     * key, return this view.
     *
     * @param view   starting view
     * @param tagKey tag key to search
     * @return found view or null
     */
    @CheckResult
    public static View findViewWithTag(@NonNull View view, String tagKey) {
        return findViewWithTag(view, tagKey, null);
    }

    /**
     * Look for a child view with the given key with value tag. If this view has the given
     * key with value tag, return this view.
     *
     * @param view   starting view
     * @param tagKey tag key to search
     * @param tag    tag to search or null for any tag
     * @return found view or null
     */
    @CheckResult
    public static View findViewWithTag(@NonNull View view, String tagKey, @Nullable Object tag) {
        if (tag == null ? getTags(view).containsKey(tagKey)
                : Objects.equals(getViewTag(view, tagKey), tag))
            return view;

        if (view instanceof ViewGroup) {
            for (int i = 0, len = ((ViewGroup) view).getChildCount(); i < len; i++) {
                View result = findViewWithTag(((ViewGroup) view).getChildAt(i), tagKey, tag);
                if (result != null) return result;
            }
        }
        return null;
    }

    @CheckResult
    public static String fillToMaxLen(int toFill) {
        return fillToLen(Integer.toString(toFill),
                Integer.toString(Integer.MAX_VALUE).length());
    }

    @CheckResult
    public static String fillToLen(CharSequence toFill, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = toFill.length(); i < len; i++)
            builder.append(" ");
        builder.append(toFill);
        return builder.toString();
    }

    @CheckResult
    public static String addBeforeEveryLine(String toEdit, String toAdd) {
        return toAdd + toEdit.replace("\n", "\n" + toAdd);
    }

    @CheckResult
    public static boolean equalBundles(@Nullable Bundle first, @Nullable Bundle second) {
        if (first == null || second == null) return first == second;
        if (first.size() != second.size()) return false;

        for (String key : first.keySet()) {
            Object firstValue = first.get(key);
            Object secondValue = second.get(key);

            if (firstValue instanceof Bundle && secondValue instanceof Bundle &&
                    !equalBundles((Bundle) firstValue, (Bundle) secondValue)) {
                return false;
            }

            if (!Objects.equals(firstValue, secondValue)
                    || (secondValue == null && !second.containsKey(key))) {
                return false;
            }
        }

        return true;
    }
}
