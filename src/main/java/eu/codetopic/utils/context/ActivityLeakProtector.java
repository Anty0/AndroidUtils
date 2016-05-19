package eu.codetopic.utils.context;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.codetopic.utils.activity.modular.ModularActivity;

/**
 * Created by anty on 15.5.16.
 *
 * @author anty
 */
public final class ActivityLeakProtector {

    private static final String LOG_TAG = "ActivityLeakProtector";

    private ActivityLeakProtector() {
    }

    public static <D> ActivityHolder<D> protect(@NonNull Context context, @Nullable D protectedObj) {
        if (context instanceof Activity)
            return protect((Activity) context, protectedObj);
        throw new IllegalArgumentException(context + " must extends Activity");
    }

    public static <D> ActivityHolder<D> protect(@NonNull Activity activity, @Nullable D protectedObj) {
        if (activity instanceof ActivityDestroyReporter)
            return protect(activity, (ActivityDestroyReporter) activity, protectedObj);
        if (activity instanceof ModularActivity)
            return protect((ModularActivity) activity, protectedObj);
        throw new IllegalArgumentException(activity + " must extends ModularActivity" +
                " (and must have ActivityDestroyReporterModule module) or ActivityDestroyReporter");
    }

    public static <D> ActivityHolder<D> protect(@NonNull ModularActivity activity, @Nullable D protectedObj) {
        if (!activity.hasModule(ActivityDestroyReporterModule.class))
            throw new IllegalArgumentException(activity + " don't using module " +
                    "ActivityDestroyReporterModule please add it to modules.");
        return protect(activity, activity.findModule(ActivityDestroyReporterModule.class), protectedObj);
    }

    public static <D> ActivityHolder<D> protect(@NonNull Activity activity,
                                                @NonNull ActivityDestroyReporter protector,
                                                @Nullable D protectedObj) {
        return new ActivityHolder<>(activity, protector, protectedObj);
    }

}
