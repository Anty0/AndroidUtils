package cz.codetopic.utils.module.settings;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.io.Serializable;
import java.util.ArrayList;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public class Settings extends ArrayList<SettingsProvider> implements Serializable {

    public static final String EXTRA_SETTINGS = Settings.class.getName() + ".EXTRA_SETTINGS";
    private static final String LOG_TAG = "Settings";

    View generateView(Context context) {
        try {
            Resources res = context.getResources();
            int marginVertical = res.getDimensionPixelSize(R.dimen.activity_vertical_margin);
            int marginHorizontal = res.getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            int padding = res.getDimensionPixelSize(R.dimen.settings_items_padding);

            ScrollView scrollView = new ScrollView(context);
            FrameLayout parent = new FrameLayout(context);
            scrollView.addView(parent);
            ((ScrollView.LayoutParams) parent.getLayoutParams()).setMargins(marginHorizontal,
                    marginVertical, marginHorizontal, marginVertical);

            for (SettingsProvider provider : this) {
                View view = provider.generateView(context, parent);
                view.setPadding(0, 0, 0, padding);
                parent.addView(view);
            }
            return scrollView;
        } catch (Exception e) {
            Log.e(LOG_TAG, "generateView", e);
            return null;
        }
    }

}
