package cz.codetopic.utils.module.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by anty on 22.2.16.
 *
 * @author anty
 */
public abstract class ButtonSettingsProvider implements SettingsProvider {

    protected abstract CharSequence getText(Context context);

    protected abstract void onClick(Context context);

    @Override
    public View generateView(Context context, ViewGroup parent) {
        Button button = new Button(context);
        button.setText(getText(context));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonSettingsProvider.this.onClick(v.getContext());
            }
        });
        return null;
    }
}
