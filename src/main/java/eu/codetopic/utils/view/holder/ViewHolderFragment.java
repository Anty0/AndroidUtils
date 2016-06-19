package eu.codetopic.utils.view.holder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Log;

public abstract class ViewHolderFragment extends Fragment {

    private static final String LOG_TAG = "ViewHolderFragment";

    private final ViewHolder[] mHolders;

    @SafeVarargs
    public ViewHolderFragment(@NonNull Class<? extends ViewHolder>... holdersClasses) {
        mHolders = new ViewHolder[holdersClasses.length];
        for (int i = 0, len = holdersClasses.length; i < len; i++) {
            try {
                mHolders[i] = holdersClasses[i].newInstance();
            } catch (Exception e) {
                mHolders[i] = null;
                Log.e(LOG_TAG, "<init> can't create instance of " + holdersClasses[i].getName(), e);
            }
        }
    }

    public int getHoldersCount() {
        return mHolders.length;
    }

    public ViewHolder[] getHolders() {
        return mHolders;
    }

    public ViewHolder getHolder(int position) {
        return mHolders[position];
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable final Bundle savedInstanceState) {
        ViewHolder.ViewCreator viewCreator = new ViewHolder.ViewCreator() {
            @Nullable
            @Override
            public View createView(Context context, ViewGroup parent) {
                return onCreateContentView(LayoutInflater.from(context), parent, savedInstanceState);
            }
        };
        for (final ViewHolder holder : mHolders) {
            final ViewHolder.ViewCreator childViewCreator = viewCreator;
            viewCreator = new ViewHolder.ViewCreator() {
                @Nullable
                @Override
                public View createView(Context context, ViewGroup parent) {
                    return holder.updateView(getContext(), parent, childViewCreator, false);
                }
            };
        }
        return viewCreator.createView(getContext(), container);
    }

    @Nullable
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return null;
    }
}
