package eu.codetopic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;

public final class NetworkManager {

    private static final String LOG_TAG = "NetworkManager";

    private static Context mContext;

    private NetworkManager() {
    }

    public static void init(Context context) {
        if (mContext != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mContext = context.getApplicationContext();
    }

    public static NetworkInfo getInfo() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    public static boolean isConnected(NetworkType type) {
        if (Build.VERSION.SDK_INT >= 23) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isDeviceIdleMode()) return false;
        }

        NetworkInfo info = getInfo();
        if (info != null && info.isConnected()) {
            if (type == NetworkType.ANY) {
                return true;
            } else if (type == NetworkType.MOBILE) {
                return info.getType() == ConnectivityManager.TYPE_MOBILE;
            } else if (type == NetworkType.WIFI) {
                return info.getType() == ConnectivityManager.TYPE_WIFI;
            }
        }
        return false;
    }

    public enum NetworkType {
        ANY, WIFI, MOBILE
    }
}
