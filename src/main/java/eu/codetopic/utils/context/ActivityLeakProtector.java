/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.context;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.codetopic.utils.ui.activity.modular.ModularActivity;

@Deprecated
@SuppressWarnings("deprecation")
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
