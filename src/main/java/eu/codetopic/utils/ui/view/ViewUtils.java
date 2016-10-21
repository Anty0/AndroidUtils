package eu.codetopic.utils.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.codetopic.java.utils.JavaUtils;
import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.JobUtils;

public class ViewUtils {

    private static final String LOG_TAG = "ViewUtils";
    private static final int VIEW_TAG_KEY_TAGS_HASH_MAP = R.id.view_tag_key_tags_hash_map;

    /////////////////////////
    //////REGION - TAGS//////
    /////////////////////////

    private ViewUtils() {
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

    //////////////////////////////////
    //////REGION - LAYOUT_PARAMS//////
    //////////////////////////////////

    @CheckResult
    public static String layoutParamsSizeToString(int size) {
        switch (size) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return "wrap_content";
            case ViewGroup.LayoutParams.MATCH_PARENT:
                return "match_parent";
            default:
                return String.valueOf(size);
        }
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

    //////////////////////////
    //////REGION - UTILS//////
    //////////////////////////

    @CheckResult
    public static String visibilityToString(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                return "visible";
            case View.INVISIBLE:
                return "invisible";
            case View.GONE:
                return "gone";
            default:
                return "unknown";
        }
    }

    @CheckResult
    public static String viewIdToString(@NonNull View view) {
        return viewIdToString(view.getContext(), view.getId());
    }

    @CheckResult
    public static String viewIdToString(@NonNull Context context, @IdRes int id) {
        return viewIdToString(context.getResources(), id);
    }

    @CheckResult
    public static String viewIdToString(@NonNull Resources res, @IdRes int id) {
        if (id == View.NO_ID) return "NO_ID";
        StringBuilder out = new StringBuilder("#")
                .append(Integer.toHexString(id));

        try {
            String pkg;
            switch (id & 0xff000000) {
                case 0x7f000000:
                    pkg = "app";
                    break;
                case 0x01000000:
                    pkg = "android";
                    break;
                default:
                    pkg = res.getResourcePackageName(id);
                    break;
            }
            String typeName = res.getResourceTypeName(id);
            String entryName = res.getResourceEntryName(id);
            out.append(" ");
            out.append(pkg);
            out.append(":");
            out.append(typeName);
            out.append("/");
            out.append(entryName);
        } catch (Resources.NotFoundException e) {
            Log.d(LOG_TAG, "viewIdToString");
        }
        return out.toString();
    }

    @CheckResult
    public static <T extends View> List<T> findViewsByClass(Class<T> clazz, Activity activity) {
        return findViewsByClass(clazz, activity.getWindow().getDecorView());
    }

    @CheckResult
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

    public static View getLastViewParent(View view) {
        ViewParent parent = view.getParent();
        while (parent instanceof View) {
            view = (View) parent;
            parent = parent.getParent();
        }
        return view;
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

    public static void drawViewToImageView(final Activity activity, final View view, final ImageView imageView) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    if (imageView.getMeasuredWidth() == 0 || imageView.getMeasuredHeight() == 0) {
                        imageView.post(this);
                    } else {
                        imageView.setImageBitmap(ViewUtils.drawViewToBitmap(view,
                                imageView.getMeasuredWidth(), imageView.getMeasuredHeight()));
                    }
                }
            }
        });
    }

    @Nullable
    @CheckResult
    public static Activity getViewActivity(@NonNull View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    @CheckResult
    public static String drawViewHierarchy(View view) {
        StringBuilder sb = new StringBuilder("-->");

        //draw visibility
        sb.append(JavaUtils.fillToLen(visibilityToString(view.getVisibility()), 9)).append(" -> ");

        //draw id
        sb.append(viewIdToString(view)).append(" -> ");

        //draw sizes
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            sb.append("H: ").append(layoutParamsSizeToString(view.getLayoutParams().height))
                    .append("|").append("W: ")
                    .append(layoutParamsSizeToString(view.getLayoutParams().width))
                    .append(" -> ");
        }

        //draw view name and info
        sb.append(view.getClass().getSimpleName()).append(" -> ")
                .append(view.getClass().getName());

        //draw children
        if (view instanceof ViewGroup) {
            sb.append(" {");
            StringBuilder csb = new StringBuilder();
            for (int i = 0, count = ((ViewGroup) view).getChildCount(); i < count; i++)
                csb.append("\n").append(drawViewHierarchy(((ViewGroup) view).getChildAt(i)));
            sb.append(JavaUtils.addBeforeEveryLine(csb.toString(), "\t"));
            sb.append("\n}");
        }
        return sb.toString();
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

    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= 16) view.setBackground(background);
        else view.setBackgroundDrawable(background);
    }

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

    public static void setViewContentAsBackground(View view) {
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        view.layout(0, 0, view.getWidth(), view.getHeight());
        view.draw(c);
        setBackground(view, new BitmapDrawable(view.getContext().getResources(), b));
    }

}
