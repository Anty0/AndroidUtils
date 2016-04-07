package cz.codetopic.utils.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.avast.android.dialogs.core.BaseDialogBuilder;
import com.avast.android.dialogs.core.BaseDialogFragment;

import java.io.Serializable;

/**
 * This is Serializable data transfer implementation for BaseDialogFragment
 *
 * @author anty
 */
public abstract class BaseDataDialogFragment extends BaseDialogFragment {

    public final static String ARG_SERIALIZABLE_DATA = "serializable_data";

    protected Serializable getData() {
        return getArguments().getSerializable(ARG_SERIALIZABLE_DATA);
    }

    public abstract static class BaseDataDialogBuilder<T extends BaseDataDialogBuilder<T>> extends BaseDialogBuilder<T> {

        private Serializable mData;

        public BaseDataDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends BaseDataDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        public T setData(Serializable data) {
            mData = data;
            return self();
        }

        @Override
        protected Bundle prepareArguments() {
            Bundle args = new Bundle();
            args.putSerializable(ARG_SERIALIZABLE_DATA, mData);
            return args;
        }
    }
}
