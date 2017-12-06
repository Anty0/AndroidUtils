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

package eu.codetopic.utils.ui.view.holder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;
import kotlin.Unit;

public class ViewHolderModule<VH extends ViewHolder> extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "ViewHolderModule";

    private final VH mHolder;

    public ViewHolderModule(@NonNull Class<? extends VH> holderClass) {
        VH holder;
        try {
            holder = holderClass.newInstance();
        } catch (Exception e) {
            holder = null;
            Log.e(LOG_TAG, "<init> can't create instance of " + holderClass.getName(), e);
        }
        mHolder = holder;
    }

    public VH getHolder() {
        return mHolder;
    }

    @Override
    public void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        final ViewHolder.ViewUpdater updater = mHolder.getViewUpdater();
        if (!updater.requiresBaseLayout()) {
            throw new IllegalStateException("Can't create contentView for ViewHolder without base layout");
            //callBack.pass();
            //return;
        }

        callBack.set(updater.getBaseLayoutResId());
        callBack.addViewAttachedCallBack(() -> {
            updater.applyOnBaseLayout(getActivity(),
                    new ViewHolder.LayoutViewCreator(layoutResID));
            return Unit.INSTANCE;
        });
    }

    @Override
    public void onSetContentView(final View view, SetContentViewCallBack callBack) {
        final ViewHolder.ViewUpdater updater = mHolder.getViewUpdater();
        if (!updater.requiresBaseLayout()) {
            throw new IllegalStateException("Can't create contentView for ViewHolder without base layout");
            //callBack.pass();
            //return;
        }

        callBack.set(updater.getBaseLayoutResId());
        callBack.addViewAttachedCallBack(() -> {
            updater.applyOnBaseLayout(getActivity(), (context, parent) -> view);
            return Unit.INSTANCE;
        });
    }

    @Override
    public void onSetContentView(final View view, final ViewGroup.LayoutParams params,
                                    SetContentViewCallBack callBack) {
        throw new UnsupportedOperationException("Not supported");
    }

}
