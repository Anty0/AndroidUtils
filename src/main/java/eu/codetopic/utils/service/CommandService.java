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
