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
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.java.utils.*;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.thread.LooperUtils;
import kotlin.Unit;

import static eu.codetopic.java.utils.Anchor.RIGHT;
import static eu.codetopic.java.utils.ExtensionsKt.addBeforeEveryLine;
import static eu.codetopic.java.utils.ExtensionsKt.fillToLen;

public class ViewUtils { // TODO: rework to kotlin (and move to ViewExtensions)

    private static final String LOG_TAG = "ViewUtils";

    private ViewUtils() {
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
        //noinspection deprecation
        if (Objects.equals(view.getClass(), clazz)) views.add((T) view);
        if (!(view instanceof ViewGroup)) return views;
        for (int i = 0, count = ((ViewGroup) view).getChildCount(); i < count; i++) {
            views.addAll(findViewsByClass(clazz, ((ViewGroup) view).getChildAt(i)));
        }
        return views;
    }

    @CheckResult
    public static View getLastViewParent(View view) {
        ViewParent parent = view.getParent();
        while (parent instanceof View) {
            view = (View) parent;
            parent = parent.getParent();
        }
        return view;
    }

    @CheckResult
    public static Bitmap drawViewToBitmap(View view, boolean useDrawingCache) {
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        if (useDrawingCache) view.buildDrawingCache();
        view.draw(new Canvas(bitmap));
        if (useDrawingCache) view.destroyDrawingCache();
        return bitmap;
    }

    @CheckResult
    public static Bitmap drawViewToBitmap(View view, int width, int height, boolean useDrawingCache) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
        if (useDrawingCache) view.buildDrawingCache();
        view.draw(new Canvas(bitmap));
        if (useDrawingCache) view.destroyDrawingCache();
        return bitmap;
    }

    public static void drawViewToImageView(final Activity activity, final View view, final ImageView imageView, final boolean useDrawingCache) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    if (imageView.getMeasuredWidth() == 0 || imageView.getMeasuredHeight() == 0) {
                        imageView.post(this);
                    } else {
                        imageView.setImageBitmap(ViewUtils.drawViewToBitmap(view,
                                imageView.getMeasuredWidth(), imageView.getMeasuredHeight(),
                                useDrawingCache));
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
        sb.append(fillToLen(visibilityToString(view.getVisibility()), 9, RIGHT)).append(" -> ");

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
            sb.append(addBeforeEveryLine(csb.toString(), "\t"));
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
        LooperUtils.runOnMainThread(() -> {
            spinner.setAdapter(adapter);
            return Unit.INSTANCE;
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
