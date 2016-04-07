package cz.codetopic.utils.module.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public interface SettingsProvider extends Serializable {

    View generateView(Context context, ViewGroup parent);
}
