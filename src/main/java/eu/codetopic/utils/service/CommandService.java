package eu.codetopic.utils.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.support.annotation.Nullable;

public abstract class CommandService<B extends CommandService.CommandBinder> extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //noinspection unchecked
        ServiceCommander.connect(this, (Class<? extends CommandService<B>>) getClass());
    }

    @Override
    public void onDestroy() {
        ServiceCommander.disconnect(getClass());
        super.onDestroy();
    }

    @Nullable
    @Override
    public abstract B onBind(Intent intent);

    public static abstract class CommandBinder extends Binder {

        public abstract boolean isUnneeded();
    }
}
