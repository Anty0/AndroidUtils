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
