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

package eu.codetopic.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.NotificationManager;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.service.notification.StatusBarNotification;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;

public final class AndroidUtils {

    private static final String LOG_TAG = "AndroidUtils";

    private AndroidUtils() {
    }

    //////////////////////////////////////
    //////REGION - TEXTS_AND_STRINGS//////
    //////////////////////////////////////

    @CheckResult
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= 24) return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        //noinspection deprecation
        return Html.fromHtml(source);
    }

    /**
     * Use extensions.getFormattedText() instead
     */
    @Deprecated
    @CheckResult
    public static CharSequence getFormattedText(Context context, @StringRes int stringId,
                                                Object... args) {
        return getFormattedText(context.getString(stringId), args);
    }

    @CheckResult
    public static CharSequence getFormattedText(String text, Object... args) {
        return fromHtml(String.format(text, args));
    }

    /**
     * Use extensions.getResourceUri() instead
     */
    @Deprecated
    @CheckResult
    public static Uri getResourceUri(@NonNull Context context, @AnyRes int resource) {
        Resources resources = context.getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(resource) + "/"
                + resources.getResourceTypeName(resource) + "/"
                + resources.getResourceEntryName(resource));
    }

    //////////////////////////////////////
    //////REGION - BITMAPS////////////////
    //////////////////////////////////////

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
    public static Bitmap cropBitmapToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width >= height)
            //noinspection SuspiciousNameCombination
            return Bitmap.createBitmap(bitmap, width / 2 - height / 2, 0, height, height);
        else
            //noinspection SuspiciousNameCombination
            return Bitmap.createBitmap(bitmap, 0, height / 2 - width / 2, width, width);
    }

    @CheckResult
    public static Bitmap drawableToBitmap(Drawable drawable) {
        final Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /////////////////////////////////////
    //////REGION - COLORS////////////////
    /////////////////////////////////////

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

    /////////////////////////////////////////
    //////REGION - ATTRIBUTES////////////////
    /////////////////////////////////////////

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
            @SuppressLint("PrivateApi")
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
            @SuppressLint("PrivateApi")
            Field field = Class.forName("com.android.internal.R$styleable")
                    .getDeclaredField(styleableName);
            field.setAccessible(true);
            return (int) field.get(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getSystemStyleableInts -> can't access internal styleable " + styleableName, e);
            return 0;
        }
    }

    ///////////////////////////////////////
    //////REGION - APP_INFO////////////////
    ///////////////////////////////////////

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
    public static CharSequence getAppLabel(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager());
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
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER:
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED:
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
                    Log.w(LOG_TAG, "isComponentEnabled", e);
                }
                return false;
        }
    }

    ///////////////////////////////////////
    //////REGION - LOCALE//////////////////
    ///////////////////////////////////////

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

    ///////////////////////////////////////
    //////REGION - LOCATION////////////////
    ///////////////////////////////////////

    /*@WorkerThread
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
    }*/

    //////////////////////////////////////
    //////REGION - BUNDLES////////////////
    //////////////////////////////////////

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

            //noinspection deprecation
            if (!Objects.equals(firstValue, secondValue)
                    || (secondValue == null && !second.containsKey(key))) {
                return false;
            }
        }

        return true;
    }

    //////////////////////////////////////
    //////REGION - INTENTS////////////////
    //////////////////////////////////////

    public static boolean openUri(@NonNull Context context, @NonNull String uri, @StringRes int failMessage) {
        return openUri(context, uri, context.getText(failMessage));
    }

    public static boolean openUri(@NonNull Context context, @NonNull String uri, @NonNull CharSequence failMessage) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            return true;
        } catch (Exception e) {
            Log.w(LOG_TAG, "openUri(uri=" + uri + ") -> Failed to open uri", e);
            Toast.makeText(context, failMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //////////////////////////////////////
    //////REGION - SERIALIZABLE///////////
    //////////////////////////////////////

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

    //////////////////////////////////////
    //////REGION - NOTIFICATIONS//////////
    //////////////////////////////////////

    @TargetApi(23)
    public static int cancelNotificationsByTag(Context context, @NonNull String tag) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return 0;
        StatusBarNotification[] notifications = manager.getActiveNotifications();
        if (notifications == null) return 0;

        int canceled = 0;
        for (StatusBarNotification notification : notifications) {
            String nTag = notification.getTag();
            if (tag.equals(nTag)) {
                manager.cancel(nTag, notification.getId());
                canceled++;
            }
        }
        return canceled;
    }

    //////////////////////////////////////
    //////REGION - PROCESSES//////////////
    //////////////////////////////////////

    @Nullable
    @CheckResult
    public static String getCurrentProcessName(Context context) {
        FileInputStream input = null;
        try {
            input = new FileInputStream("/proc/self/cmdline");
            final InputStreamReader in = new InputStreamReader(input);

            final StringWriter sw = new StringWriter();

            char[] buffer = new char[1024 * 4];
            int n;
            while ((n = in.read(buffer)) != -1) {
                sw.write(buffer, 0, n);
            }

            return sw.toString().trim();
        } catch (IOException e) {
            Log.w(LOG_TAG, "getCurrentProcessName()" +
                    " -> Failed to obtain process name using primary strategy" +
                    " -> Using fallback strategy");

            final ActivityManager actManager = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            if (actManager == null) {
                Log.e(LOG_TAG, "getCurrentProcessName()" +
                        " -> Failed to obtain process name using fallback strategy" +
                        " -> ActivityManager is null");
                return null;
            }

            final List<ActivityManager.RunningAppProcessInfo> processesInfo =
                    actManager.getRunningAppProcesses();
            if (processesInfo == null) {
                Log.e(LOG_TAG, "getCurrentProcessName()" +
                        " -> Failed to obtain process name using fallback strategy" +
                        " -> List of RunningAppProcessInfo is null");
                return null;
            }

            final int pid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo processInfo : processesInfo) {
                if (processInfo.pid != pid) continue;
                return processInfo.processName;
            }


            Log.e(LOG_TAG, "getCurrentProcessName()" +
                    " -> Failed to obtain process name using fallback strategy" +
                    " -> Current process not found");
            return null;
        } finally {
            try {
                if (input != null) input.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    //////////////////////////////////////
    //////REGION - STORAGE////////////////
    //////////////////////////////////////

    @CheckResult
    public static boolean isStorageWritable() {
        //noinspection deprecation
        return Objects.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState());
    }
}
