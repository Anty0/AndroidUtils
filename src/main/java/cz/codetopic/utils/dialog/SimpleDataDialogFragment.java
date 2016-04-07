package cz.codetopic.utils.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.View;

import com.avast.android.dialogs.core.BaseDialogFragment;

import java.util.List;

import cz.codetopic.utils.dialog.listener.INegativeButtonDataDialogListener;
import cz.codetopic.utils.dialog.listener.INeutralButtonDataDialogListener;
import cz.codetopic.utils.dialog.listener.IPositiveButtonDataDialogListener;

/**
 * This is copy of SimpleDialogFragment with Serializable data transfer implementation
 *
 * @author anty
 */
public class SimpleDataDialogFragment extends BaseDataDialogFragment {

    protected final static String ARG_MESSAGE = "message";
    protected final static String ARG_TITLE = "title";
    protected final static String ARG_POSITIVE_BUTTON = "positive_button";
    protected final static String ARG_NEGATIVE_BUTTON = "negative_button";
    protected final static String ARG_NEUTRAL_BUTTON = "neutral_button";


    public static SimpleDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new SimpleDialogBuilder(context, fragmentManager, SimpleDataDialogFragment.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Key method for extending {@link com.avast.android.dialogs.fragment.SimpleDialogFragment}.
     * Children can extend this to add more things to base builder.
     */
    @Override
    protected BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        final CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        final CharSequence message = getMessage();
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        final CharSequence positiveButtonText = getPositiveButtonText();
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (IPositiveButtonDataDialogListener listener : getPositiveButtonDialogListeners()) {
                        listener.onPositiveButtonClicked(mRequestCode, getData());
                    }
                    dismiss();
                }
            });
        }

        final CharSequence negativeButtonText = getNegativeButtonText();
        if (!TextUtils.isEmpty(negativeButtonText)) {
            builder.setNegativeButton(negativeButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (INegativeButtonDataDialogListener listener : getNegativeButtonDialogListeners()) {
                        listener.onNegativeButtonClicked(mRequestCode, getData());
                    }
                    dismiss();
                }
            });
        }

        final CharSequence neutralButtonText = getNeutralButtonText();
        if (!TextUtils.isEmpty(neutralButtonText)) {
            builder.setNeutralButton(neutralButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (INeutralButtonDataDialogListener listener : getNeutralButtonDialogListeners()) {
                        listener.onNeutralButtonClicked(mRequestCode, getData());
                    }
                    dismiss();
                }
            });
        }

        return builder;
    }

    protected CharSequence getMessage() {
        return getArguments().getCharSequence(ARG_MESSAGE);
    }

    protected CharSequence getTitle() {
        return getArguments().getCharSequence(ARG_TITLE);
    }

    protected CharSequence getPositiveButtonText() {
        return getArguments().getCharSequence(ARG_POSITIVE_BUTTON);
    }

    protected CharSequence getNegativeButtonText() {
        return getArguments().getCharSequence(ARG_NEGATIVE_BUTTON);
    }

    protected CharSequence getNeutralButtonText() {
        return getArguments().getCharSequence(ARG_NEUTRAL_BUTTON);
    }

    /**
     * Get positive button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<IPositiveButtonDataDialogListener> getPositiveButtonDialogListeners() {
        return getDialogListeners(IPositiveButtonDataDialogListener.class);
    }

    /**
     * Get negative button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<INegativeButtonDataDialogListener> getNegativeButtonDialogListeners() {
        return getDialogListeners(INegativeButtonDataDialogListener.class);
    }

    /**
     * Get neutral button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<INeutralButtonDataDialogListener> getNeutralButtonDialogListeners() {
        return getDialogListeners(INeutralButtonDataDialogListener.class);
    }


    public static class SimpleDialogBuilder extends BaseDataDialogBuilder<SimpleDialogBuilder> {

        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private CharSequence mNeutralButtonText;

        protected SimpleDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends SimpleDataDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        @Override
        protected SimpleDialogBuilder self() {
            return this;
        }

        public SimpleDialogBuilder setTitle(int titleResourceId) {
            mTitle = mContext.getString(titleResourceId);
            return this;
        }


        public SimpleDialogBuilder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public SimpleDialogBuilder setMessage(int messageResourceId) {
            mMessage = mContext.getText(messageResourceId);
            return this;
        }

        /**
         * Allow to set resource string with HTML formatting and bind %s,%i.
         * This is workaround for https://code.google.com/p/android/issues/detail?id=2923
         */
        public SimpleDialogBuilder setMessage(int resourceId, Object... formatArgs) {
            mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
            return this;
        }

        public SimpleDialogBuilder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public SimpleDialogBuilder setPositiveButtonText(int textResourceId) {
            mPositiveButtonText = mContext.getString(textResourceId);
            return this;
        }

        public SimpleDialogBuilder setPositiveButtonText(CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public SimpleDialogBuilder setNegativeButtonText(int textResourceId) {
            mNegativeButtonText = mContext.getString(textResourceId);
            return this;
        }

        public SimpleDialogBuilder setNegativeButtonText(CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public SimpleDialogBuilder setNeutralButtonText(int textResourceId) {
            mNeutralButtonText = mContext.getString(textResourceId);
            return this;
        }

        public SimpleDialogBuilder setNeutralButtonText(CharSequence text) {
            mNeutralButtonText = text;
            return this;
        }

        @Override
        protected Bundle prepareArguments() {
            Bundle args = super.prepareArguments();
            args.putCharSequence(ARG_MESSAGE, mMessage);
            args.putCharSequence(ARG_TITLE, mTitle);
            args.putCharSequence(ARG_POSITIVE_BUTTON, mPositiveButtonText);
            args.putCharSequence(ARG_NEGATIVE_BUTTON, mNegativeButtonText);
            args.putCharSequence(ARG_NEUTRAL_BUTTON, mNeutralButtonText);

            return args;
        }
    }
}
