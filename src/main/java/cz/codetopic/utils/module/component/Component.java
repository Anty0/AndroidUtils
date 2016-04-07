package cz.codetopic.utils.module.component;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by anty on 13.3.16.
 *
 * @author anty
 */
public class Component {

    private final ComponentName mComponentName;
    private Context mContext = null;

    public Component(ComponentName name) {
        mComponentName = name;
    }

    final void init(Context context) {
        mContext = context;
        onCreate();
    }

    public Context getContext() {
        return mContext;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public void setEnabled(int componentEnabledState) {
        getContext().getPackageManager().setComponentEnabledSetting(getComponentName(),
                componentEnabledState, PackageManager.DONT_KILL_APP);
    }

    protected void onCreate() {

    }

    protected void onUpdate() {

    }

}
