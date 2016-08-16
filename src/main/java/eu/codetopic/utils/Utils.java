package eu.codetopic.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import android.text.Spanned;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import eu.codetopic.utils.data.database.DatabaseObject;
import eu.codetopic.utils.log.Log;

public final class Utils {

    private static final String LOG_TAG = "Utils";

    private Utils() {
    }

    //////////////////////////////////////
    //////REGION - TEXTS_AND_STRINGS//////
    //////////////////////////////////////

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
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= 24) return Html.fromHtml(source, 0);
        //noinspection deprecation
        return Html.fromHtml(source);
    }

    @CheckResult
    public static CharSequence getFormattedText(Context context, @StringRes int stringId,
                                                Object... args) {
        return getFormattedText(context.getString(stringId), args);
    }

    @CheckResult
    public static CharSequence getFormattedText(String text, Object... args) {
        return fromHtml(String.format(text, args));
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
    public static double parseDouble(String string) {
        try {
            return Double.parseDouble(string.replace(",", ".").replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public static Uri getResourceUri(@NonNull Context context, @AnyRes int resource) {
        Resources resources = context.getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(resource) + "/"
                + resources.getResourceTypeName(resource) + "/"
                + resources.getResourceEntryName(resource));
    }

    ////////////////////////////
    //////REGION - BITMAPS//////
    ////////////////////////////

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

    /////////////////////////////
    //////REGION - DATABASE//////
    /////////////////////////////

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

    ///////////////////////////
    //////REGION - COLORS//////
    ///////////////////////////

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

    ///////////////////////////////
    //////REGION - ATTRIBUTES//////
    ///////////////////////////////

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

    public static int[] getSystemStyleableInts(String styleableName) {
        try {
            Field field = Class.forName("com.android.internal.R$styleable")
                    .getDeclaredField(styleableName);
            field.setAccessible(true);
            return (int[]) field.get(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getSystemStyleableInts -> can't access internal styleable " + styleableName, e);
            return new int[0];
        }
    }

    public static int getSystemStyleableInt(String styleableName) {
        try {
            Field field = Class.forName("com.android.internal.R$styleable")
                    .getDeclaredField(styleableName);
            field.setAccessible(true);
            return (int) field.get(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getSystemStyleableInts -> can't access internal styleable " + styleableName, e);
            return 0;
        }
    }

    /////////////////////////////
    //////REGION - APP_INFO//////
    /////////////////////////////

    @CheckResult
    public static int getApplicationVersionCode(Context context) {
        try {
            return getApplicationVersionCode(context, context.getPackageName());
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return -1;
    }

    @CheckResult
    public static int getApplicationVersionCode(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
    }

    @CheckResult
    public static String getApplicationVersionName(Context context) {
        try {
            return getApplicationVersionName(context, context.getPackageName());
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "unknown";
    }

    @CheckResult
    public static String getApplicationVersionName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
    }

    @CheckResult
    public static CharSequence getApplicationLabel(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager());
    }

    @CheckResult
    public static CharSequence getApplicationLabel(Context context, String packageName) throws PackageManager.NameNotFoundException {
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

    /////////////////////////////
    //////REGION - LOCALE////////
    /////////////////////////////

    public static void setLocale(Context context, Locale locale) {
        setLocale(context.getResources(), locale);
    }

    public static void setLocale(Resources res, Locale locale) {
        Configuration conf = res.getConfiguration();
        setLocale(conf, locale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    @SuppressWarnings("deprecation")
    public static void setLocale(Configuration conf, Locale locale) {
        if (Build.VERSION.SDK_INT >= 24) conf.setLocale(locale);
        else conf.locale = locale;
    }

    /////////////////////////////
    //////REGION - LOCATION//////
    /////////////////////////////

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

    ////////////////////////////
    //////REGION - BUNDLES//////
    ////////////////////////////

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

    ////////////////////////////
    //////REGION - SERIALIZABLE/
    ////////////////////////////

    @CheckResult
    public static Object fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    @CheckResult
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
