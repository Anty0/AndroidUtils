package cz.codetopic.utils.module.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by anty on 22.2.16.
 *
 * @author anty
 */
public abstract class CheckBoxSettingsProvider implements SettingsProvider {

    protected abstract CharSequence getText(Context context);

    protected abstract boolean getValue(Context context);

    protected abstract void onValueChanged(Context context, boolean newValue);

    @Override
    public View generateView(Context context, ViewGroup parent) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(getText(context));
        checkBox.setChecked(getValue(context));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onValueChanged(buttonView.getContext(), isChecked);
            }
        });
        return checkBox;
    }
}
