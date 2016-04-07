package cz.codetopic.utils.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.avast.android.dialogs.core.BaseDialogFragment;

import java.util.List;

import cz.codetopic.utils.dialog.listener.INegativeButtonEditDialogListener;
import cz.codetopic.utils.dialog.listener.INeutralButtonEditDialogListener;
import cz.codetopic.utils.dialog.listener.IPositiveButtonEditDialogListener;

/**
 * This is copy of SimpleDialogFragment with EditText and Serializable data transfer implementation
 *
 * @author anty
 */
public class EditDataDialogFragment extends BaseDataDialogFragment {

    protected final static String ARG_MESSAGE = "message";
    protected final static String ARG_TITLE = "title";
    protected final static String ARG_POSITIVE_BUTTON = "positive_button";
    protected final static String ARG_NEGATIVE_BUTTON = "negative_button";
    protected final static String ARG_NEUTRAL_BUTTON = "neutral_button";
    protected final static String ARG_EDIT_TEXT = "edit_text";
    protected final static String ARG_EDIT_HINT = "edit_hint";

    private EditText mEditText;

    public static EditDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new EditDialogBuilder(context, fragmentManager, EditDataDialogFragment.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

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

        mEditText = new EditText(getContext());
        mEditText.setTag(ARG_EDIT_TEXT);
        mEditText.setText(getEditText());
        mEditText.setHint(getEditHint());
        mEditText.setLayoutParams(new FrameLayout
                .LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        //Utils.setPadding(editText, 20, 0, 20, 0);
        builder.setView(mEditText);

        final CharSequence positiveButtonText = getPositiveButtonText();
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (IPositiveButtonEditDialogListener listener : getPositiveButtonDialogListeners()) {
                        listener.onPositiveButtonClicked(mRequestCode, getData(), mEditText.getText());
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
                    for (INegativeButtonEditDialogListener listener : getNegativeButtonDialogListeners()) {
                        listener.onNegativeButtonClicked(mRequestCode, getData(), mEditText.getText());
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
                    for (INeutralButtonEditDialogListener listener : getNeutralButtonDialogListeners()) {
                        listener.onNeutralButtonClicked(mRequestCode, getData(), mEditText.getText());
                    }
                    dismiss();
                }
            });
        }

        return builder;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getArguments().putCharSequence(ARG_EDIT_TEXT, mEditText.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        mEditText = null;
        super.onDestroyView();
    }

    protected CharSequence getEditText() {
        return getArguments().getCharSequence(ARG_EDIT_TEXT);
    }

    protected CharSequence getEditHint() {
        return getArguments().getCharSequence(ARG_EDIT_HINT);
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
    protected List<IPositiveButtonEditDialogListener> getPositiveButtonDialogListeners() {
        return getDialogListeners(IPositiveButtonEditDialogListener.class);
    }

    /**
     * Get negative button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<INegativeButtonEditDialogListener> getNegativeButtonDialogListeners() {
        return getDialogListeners(INegativeButtonEditDialogListener.class);
    }

    /**
     * Get neutral button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<INeutralButtonEditDialogListener> getNeutralButtonDialogListeners() {
        return getDialogListeners(INeutralButtonEditDialogListener.class);
    }


    public static class EditDialogBuilder extends BaseDataDialogBuilder<EditDialogBuilder> {

        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private CharSequence mNeutralButtonText;
        private CharSequence mEditText;
        private CharSequence mEditHint;

        protected EditDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends EditDataDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        @Override
        protected EditDialogBuilder self() {
            return this;
        }

        public EditDialogBuilder setTitle(int titleResourceId) {
            mTitle = mContext.getString(titleResourceId);
            return this;
        }


        public EditDialogBuilder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public EditDialogBuilder setMessage(int messageResourceId) {
            mMessage = mContext.getText(messageResourceId);
            return this;
        }

        /**
         * Allow to set resource string with HTML formatting and bind %s,%i.
         * This is workaround for https://code.google.com/p/android/issues/detail?id=2923
         */
        public EditDialogBuilder setMessage(int resourceId, Object... formatArgs) {
            mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
            return this;
        }

        public EditDialogBuilder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public EditDialogBuilder setTextToEdit(CharSequence text) {
            mEditText = text;
            return this;
        }

        public EditDialogBuilder setEditHint(CharSequence hint) {
            mEditHint = hint;
            return this;
        }

        public EditDialogBuilder setPositiveButtonText(int textResourceId) {
            mPositiveButtonText = mContext.getString(textResourceId);
            return this;
        }

        public EditDialogBuilder setPositiveButtonText(CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public EditDialogBuilder setNegativeButtonText(int textResourceId) {
            mNegativeButtonText = mContext.getString(textResourceId);
            return this;
        }

        public EditDialogBuilder setNegativeButtonText(CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public EditDialogBuilder setNeutralButtonText(int textResourceId) {
            mNeutralButtonText = mContext.getString(textResourceId);
            return this;
        }

        public EditDialogBuilder setNeutralButtonText(CharSequence text) {
            mNeutralButtonText = text;
            return this;
        }

        @Override
        protected Bundle prepareArguments() {
            Bundle args = super.prepareArguments();
            args.putCharSequence(EditDataDialogFragment.ARG_MESSAGE, mMessage);
            args.putCharSequence(EditDataDialogFragment.ARG_TITLE, mTitle);
            args.putCharSequence(EditDataDialogFragment.ARG_POSITIVE_BUTTON, mPositiveButtonText);
            args.putCharSequence(EditDataDialogFragment.ARG_NEGATIVE_BUTTON, mNegativeButtonText);
            args.putCharSequence(EditDataDialogFragment.ARG_NEUTRAL_BUTTON, mNeutralButtonText);
            args.putCharSequence(EditDataDialogFragment.ARG_EDIT_TEXT, mEditText);
            args.putCharSequence(EditDataDialogFragment.ARG_EDIT_HINT, mEditHint);

            return args;
        }
    }
}
