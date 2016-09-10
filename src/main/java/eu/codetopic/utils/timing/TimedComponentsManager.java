package eu.codetopic.utils.timing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;

import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.Objects;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.timing.info.TimCompInfo;

public class TimedComponentsManager {

    static final String ACTION_TIMED_EXECUTE = "eu.codetopic.utils.timing.TimedComponentsManager.ACTION_TIMED_EXECUTE";
    static final String ACTION_FORCED_EXECUTE = "eu.codetopic.utils.timing.TimedComponentsManager.ACTION_FORCED_EXECUTE";

    private static final String LOG_TAG = "TimedComponentsManager";
    private static TimedComponentsManager INSTANCE = null;

    private final Context mContext;
    private final Object mComponentsInfoMapLock = new Object();
    private final HashMap<Class<?>, TimCompInfo> mComponentsInfoMap;
    private NetworkManager.NetworkType mRequiredNetwork;

    private TimedComponentsManager(Context context, @NonNull NetworkManager.NetworkType requiredNetwork,
                                   Class<?>[] components) {

        mContext = context;
        mRequiredNetwork = requiredNetwork;

        mComponentsInfoMap = new HashMap<>(components.length);
        Log.d(LOG_TAG, "<init> initializing for: " + java.util.Arrays.toString(components));
        synchronized (mComponentsInfoMapLock) {
            for (Class<?> component : components) {
                try {
                    mComponentsInfoMap.put(component, TimCompInfo.createInfoFor(mContext, component));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "<init> - problem detected during initialisation of "
                            + component.getName(), t);
                }
            }
        }
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context         application context
     * @param timedComponents Classes of timed components to use in TimedComponentsManager
     */
    public static void initialize(Context context, Class<?>... timedComponents) {
        initialize(context, NetworkManager.NetworkType.ANY, timedComponents);
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context         application context
     * @param requiredNetwork required network to execute timed components that requires internet access
     * @param timedComponents Classes of timed components to use in TimedComponentsManager
     */
    public static void initialize(Context context, @NonNull NetworkManager.NetworkType requiredNetwork,
                                  Class<?>... timedComponents) {

        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        context = context.getApplicationContext();
        TimingData.initialize(context);

        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootConnectivityReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        INSTANCE = new TimedComponentsManager(context, requiredNetwork, timedComponents);

        if (TimingData.getter.get().isFirstLoad()) INSTANCE.reloadAll();
    }

    public static TimedComponentsManager getInstance() {
        return INSTANCE;
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    static Intent getReloadComponentIntentInternal(Context context, @NonNull TimCompInfo componentInfo) {
        return ReloadComponentReceiver.generateIntent(context, componentInfo);
    }

    public Context getContext() {
        return mContext;
    }

    @NonNull
    public NetworkManager.NetworkType getRequiredNetwork() {
        return mRequiredNetwork;
    }

    public void setRequiredNetwork(@NonNull NetworkManager.NetworkType requiredNetwork) {
        if (!Objects.equals(this.mRequiredNetwork, requiredNetwork)) {
            this.mRequiredNetwork = requiredNetwork;
            reloadAllNetwork();
        }
    }

    public void setComponentEnabled(@NonNull Class<?> componentClass, boolean enabled) {
        synchronized (mComponentsInfoMapLock) {
            setComponentEnabledInternal(getComponentInfoNonNull(componentClass), enabled);
        }
    }

    public void setComponentEnabled(@NonNull TimCompInfo componentInfo, boolean enabled) {
        synchronized (mComponentsInfoMapLock) {
            validateComponentInfo(componentInfo);
            setComponentEnabledInternal(componentInfo, enabled);
        }
    }

    void setComponentEnabledInternal(@NonNull TimCompInfo componentInfo, boolean enabled) {
        synchronized (mComponentsInfoMapLock) {
            PackageManager pm = mContext.getPackageManager();
            if (pm.getComponentEnabledSetting(componentInfo.getComponentName(mContext))
                    != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                    && enabled == componentInfo.isEnabled(mContext)) return;

            pm.setComponentEnabledSetting(componentInfo.getComponentName(mContext),
                    enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            tryReloadInternal(componentInfo);
        }
    }

    public Intent getReloadComponentIntent(@NonNull Class<?> componentClass) {
        return getReloadComponentIntentInternal(mContext, getComponentInfoNonNull(componentClass));
    }

    public Intent getReloadComponentIntent(@NonNull TimCompInfo componentInfo) {
        validateComponentInfo(componentInfo);
        return getReloadComponentIntentInternal(mContext, componentInfo);
    }

    public void reloadAllNetwork() {
        synchronized (mComponentsInfoMapLock) {
            for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                if (componentInfo.getComponentProperties().isRequiresInternetAccess())
                    tryReloadInternal(componentInfo);
        }
    }

    public void reloadAll() {
        synchronized (mComponentsInfoMapLock) {
            for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                tryReloadInternal(componentInfo);
        }
    }

    public void reloadComponentModifications(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMapLock) {
            reloadComponentModificationsInternal(getComponentInfoNonNull(componentClass));
        }
    }

    public void reloadComponentModifications(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMapLock) {
            validateComponentInfo(componentInfo);
            reloadComponentModificationsInternal(componentInfo);
        }
    }

    void reloadComponentModificationsInternal(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMapLock) {
            componentInfo.getComponentProperties().reloadModifications(mContext);
            tryReloadInternal(componentInfo);
        }
    }

    public boolean tryReload(@NonNull Class<?> componentClass) {
        try {
            reload(componentClass);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryReload - problem detected while reloading timed component: "
                    + componentClass.getName(), e);
            return false;
        }
    }

    public boolean tryReload(@NonNull TimCompInfo componentInfo) {
        try {
            reload(componentInfo);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryReload - problem detected while reloading timed component: "
                    + componentInfo.getComponentClass().getName(), e);
            return false;
        }
    }

    boolean tryReloadInternal(@NonNull TimCompInfo componentInfo) {
        try {
            reloadInternal(componentInfo);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryReload - problem detected while reloading timed component: "
                    + componentInfo.getComponentClass().getName(), e);
            return false;
        }
    }

    public void reload(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMapLock) {
            reloadInternal(getComponentInfoNonNull(componentClass));
        }
    }

    public void reload(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMapLock) {
            validateComponentInfo(componentInfo);
            reloadInternal(componentInfo);
        }
    }

    void reloadInternal(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMapLock) {
            new ComponentLoader(mContext, mRequiredNetwork, componentInfo).reload();
        }
    }

    public Object getTimedComponentsLock() {
        return mComponentsInfoMapLock;
    }

    public Collection<TimCompInfo> getAllTimedComponentInfo() {
        synchronized (mComponentsInfoMapLock) {
            return mComponentsInfoMap.values();
        }
    }

    @Nullable
    public TimCompInfo getComponentInfo(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMapLock) {
            return mComponentsInfoMap.get(componentClass);
        }
    }

    @NonNull
    public TimCompInfo getComponentInfoNonNull(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMapLock) {
            TimCompInfo componentInfo = mComponentsInfoMap.get(componentClass);
            if (componentInfo == null)
                throw new NullPointerException(componentClass.getName() + " no found");
            return componentInfo;
        }
    }

    public TimCompInfo validateComponentInfo(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMapLock) {
            if (!mComponentsInfoMap.containsValue(componentInfo))
                throw new IllegalArgumentException("Can't reload componentInfo that is not initialized in "
                        + LOG_TAG + ".initialize(). componentInfo=" + componentInfo);
            return componentInfo;
        }
    }

    public void forceExecute(@NonNull Class<?> componentClass) {
        forceExecute(componentClass, null);
    }

    public void forceExecute(@NonNull Class<?> componentClass, @Nullable Bundle extras) {
        forceExecuteInternal(getComponentInfoNonNull(componentClass), extras);
    }

    public void forceExecute(@NonNull TimCompInfo componentInfo) {
        forceExecute(componentInfo, null);
    }

    public void forceExecute(@NonNull TimCompInfo componentInfo, @Nullable Bundle extras) {
        validateComponentInfo(componentInfo);
        forceExecuteInternal(componentInfo, extras);
    }

    void forceExecuteInternal(@NonNull TimCompInfo componentInfo, @Nullable Bundle extras) {
        mContext.sendBroadcast(TimedComponentExecutor.generateIntent(mContext,
                ACTION_FORCED_EXECUTE, componentInfo.getComponentClass(), extras));
    }

    @Override
    public String toString() {
        return "TimedComponentsManager{" +
                "mComponentsInfoMap=" + mComponentsInfoMap +
                ", mRequiredNetwork=" + mRequiredNetwork +
                '}';
    }
}
