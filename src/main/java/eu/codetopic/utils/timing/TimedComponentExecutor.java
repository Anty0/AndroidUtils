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

package eu.codetopic.utils.timing;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.BuildConfig;

@MainThread
public final class TimedComponentExecutor extends BroadcastReceiver {

    private static final String LOG_TAG = "TimedComponentExecutor";

    private static final String EXTRA_TIMED_COMPONENT_CLASS_NAME =
            "eu.codetopic.utils.timing.TimedComponentExecutor.TIMED_COMPONENT_CLASS_NAME";
    private static final String EXTRA_EXECUTE_EXTRAS =
            "eu.codetopic.utils.timing.TimedComponentExecutor.EXECUTE_EXTRAS";

    static Intent generateIntent(Context context, String callTypeAction,
                                 @NonNull Class<?> componentClass,
                                 @Nullable Bundle executeExtras) {

        return new Intent(context, TimedComponentExecutor.class).setAction(callTypeAction)
                .putExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME, componentClass.getName())// fixes api 24 class passing trough PendingIntent
                .putExtra(EXTRA_EXECUTE_EXTRAS, executeExtras);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getBundleExtra(EXTRA_EXECUTE_EXTRAS);
        if (extras == null) extras = new Bundle();

        Class<?> componentClass;
        try {
            componentClass = Class.forName(intent.getStringExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME));
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "onReceive: can't find requested class to execute", e);
            return;
        }
        if (componentClass == null) {
            Log.e(LOG_TAG, "onReceive", new NullPointerException("Received request" +
                    " to execute component without component class"));
            return;
        }

        try {
            if (!TimedComponentsManager.isInitialized()) {
                throw new IllegalStateException("can't check if Component is active," +
                        " TimedComponentsManager is not initialized");
            } else {
                if (!TimedComponentsManager.getInstance().getComponentInfoNonNull(componentClass).isActive()) {
                    Log.w(LOG_TAG, "onReceive", new IllegalStateException("Received ComponentExecute" +
                            " request in situation, when component is not active. ComponentInfo: " +
                            TimedComponentsManager.getInstance().getComponentInfo(componentClass)));
                    TimedComponentsManager.getInstance().tryReload(componentClass);
                    return;
                }
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "onReceive", e);
        }

        TimingData timingData = TimingData.getter.get();
        timingData.setLastExecuteTime(componentClass, System.currentTimeMillis());

        if (BuildConfig.DEBUG) {
            timingData.addDebugLogLine(String.format(
                    "Received execute request of component %1$s with intent action %2$s",
                    componentClass.getName(), intent.getAction()));
        }

        try {
            Intent targetIntent = new Intent(context, componentClass)
                    .setAction(intent.getAction()).putExtras(extras);

            if (BroadcastReceiver.class.isAssignableFrom(componentClass)) {
                context.sendBroadcast(targetIntent);
            } else if (Service.class.isAssignableFrom(componentClass)) {
                context.startService(targetIntent);
            } else if (Activity.class.isAssignableFrom(componentClass)) {
                targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(targetIntent);
            } else {
                throw new ClassCastException("Unknown component class: " + componentClass.getName());
            }
            Log.d(LOG_TAG, "onReceive: " + componentClass.getName() + " was executed at "
                    + new Date(TimingData.getter.get().getLastExecuteTime(componentClass)));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't execute component: " + componentClass.getName(), e);
        }

        if (TimedComponentsManager.ACTION_FORCED_EXECUTE.equals(intent.getAction()))
            TimedComponentsManager.getInstance().reload(componentClass);
    }
}
