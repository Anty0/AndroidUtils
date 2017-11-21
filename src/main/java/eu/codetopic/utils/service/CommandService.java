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
