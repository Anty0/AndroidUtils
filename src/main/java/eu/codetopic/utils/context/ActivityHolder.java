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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Deprecated
@SuppressWarnings("deprecation")
public class ActivityHolder<D> {

    private final Context mApplicationContext;
    private Activity mActivity;
    private D mProtected;
    private Handler mThreadHandler;
    private Thread mContextThread;

    protected ActivityHolder(@NonNull Activity activity, @NonNull ActivityDestroyReporter protector, @Nullable D protectedObj) {
        mApplicationContext = activity.getApplicationContext();
        mActivity = activity;
        mProtected = protectedObj;
        initHandler(mActivity);

        protector.registerListener(new ActivityDestroyListener() {
            @Override
            public void onDestroy() {
                mActivity = null;
                mProtected = null;
                initHandler(mApplicationContext);
            }
        });
    }

    private void initHandler(Context context) {
        Looper looper = context.getMainLooper();
        mThreadHandler = new Handler(looper);
        mContextThread = looper.getThread();
    }

    @NonNull
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    @Nullable
    public Activity getActivity() {
        return mActivity;
    }

    @Nullable
    public D getProtectedObj() {
        return mProtected;
    }

    public boolean isAlive() {
        return mActivity != null;
    }

    public void postCommandIfAlive(final AliveContextCommand<D> command) {
        runOnActivityThread(new Runnable() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void run() {
                if (isAlive())
                    command.run(getApplicationContext(), getActivity(), getProtectedObj());
            }
        });
    }

    public void postCommand(final ContextCommand<D> command) {
        runOnActivityThread(new Runnable() {
            @Override
            public void run() {
                command.run(getApplicationContext(), getActivity(), getProtectedObj());
            }
        });
    }

    public void runOnActivityThread(Runnable action) {
        if (!isOnActivityThread()) {
            postOnActivityThread(action);
            return;
        }
        action.run();
    }

    public boolean isOnActivityThread() {
        return Thread.currentThread() == mContextThread;
    }

    public void postOnActivityThread(Runnable action) {
        mThreadHandler.post(action);
    }

    public interface ContextCommand<D> {

        void run(@NonNull Context context, @Nullable Activity activity, @Nullable D protectedObj);
    }

    public interface AliveContextCommand<D> {

        void run(@NonNull Context context, @NonNull Activity activity, D protectedObj);
    }
}
