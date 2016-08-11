package eu.codetopic.utils.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.MainThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.thread.JobUtils;

// TODO: 4.4.16 find way to stop unneeded services (try use timeout and CommandBinder to make it)
public final class ServiceCommander<B extends CommandService.CommandBinder> {

    private static final String LOG_TAG = "ServiceCommander";

    private static final HashMap<Class<? extends CommandService>, ServiceCommander<?>>
            SERVICE_MANAGERS = new HashMap<>();

    private final Context mContext;
    private final Class<? extends Service> mServiceClass;
    private final Object mBinderLock = new Object();
    private final List<BinderConnection<B>> mConnections = new ArrayList<>();
    private B mBinder = null;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (mBinderLock) {
                //noinspection unchecked
                setBinder((B) service);
                if (isConnected())
                    synchronized (mConnections) {
                        for (BinderConnection<B> connection : mConnections) {
                            connection.onBinderConnected(getBinder());
                        }
                    }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            synchronized (mBinderLock) {
                if (isConnected())
                    synchronized (mConnections) {
                        for (BinderConnection<B> listener : mConnections) {
                            listener.onBinderDisconnected();
                        }
                    }
                setBinder(null);
            }
        }
    };

    private ServiceCommander(Context context, Class<? extends CommandService<B>> serviceClass) {
        mContext = context.getApplicationContext();
        mServiceClass = serviceClass;
    }

    public static <B extends CommandService.CommandBinder> ServiceCommander<B> connect
            (Context context, Class<? extends CommandService<B>> serviceClass) {
        return getInstance(context, serviceClass).connect();
    }

    public static void disconnect(Class<? extends CommandService> serviceClass) {
        ServiceCommander serviceCommander = SERVICE_MANAGERS.get(serviceClass);
        if (serviceCommander != null) serviceCommander.forceDisconnect();
    }

    /**
     * Call this method to destroy all services and free device memory.
     */
    public static void disconnectAndStopAll() {
        for (ServiceCommander manager : SERVICE_MANAGERS.values()) {
            manager.forceDisconnect();
            manager.stopService();
        }
    }

    /**
     * Call this method to destroy unneeded services and free device memory.
     */
    public static void disconnectAndKillUnneeded() {
        for (ServiceCommander manager : SERVICE_MANAGERS.values()) {
            if (manager.isConnected()) {
                if (manager.getBinder().isUnneeded()) {
                    manager.forceDisconnect();
                    manager.stopService();
                }
            } else manager.stopService();
        }
    }

    public static <B extends CommandService.CommandBinder> ServiceCommander<B> getInstance
            (Context context, Class<? extends CommandService<B>> serviceClass) {
        Log.d(LOG_TAG, "getInstance");
        //noinspection unchecked,unchecked
        ServiceCommander<B> result = (ServiceCommander<B>) SERVICE_MANAGERS.get(serviceClass);
        if (result == null) {
            Log.d(LOG_TAG, "getInstance noInstance - creating instance");
            result = new ServiceCommander<>(context, serviceClass);
            SERVICE_MANAGERS.put(serviceClass, result);
        }
        //if (!result.isConnected()) result.connect();
        return result;
    }

    public ServiceCommander<B> addBinderConnection(final BinderConnection<B> connection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mConnections) {
                    if (mConnections.contains(connection)) return;
                    synchronized (mBinderLock) {
                        mConnections.add(connection);
                        if (isConnected())
                            JobUtils.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    connection.onBinderConnected(getBinder());
                                }
                            });
                    }
                }
            }
        }).start();
        return this;
    }

    public ServiceCommander<B> removeBinderConnection(final BinderConnection<B> connection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mConnections) {
                    if (!mConnections.contains(connection)) return;
                    synchronized (mBinderLock) {
                        mConnections.remove(connection);
                        if (isConnected())
                            JobUtils.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    connection.onBinderDisconnected();
                                }
                            });
                    }
                }
            }
        }).start();
        return this;
    }

    public ServiceCommander<B> startService() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                synchronized (mServiceClass) {
                    mContext.startService(new Intent(mContext, mServiceClass));
                }
            }
        });
        return this;
    }

    public ServiceCommander<B> stopService() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                synchronized (mServiceClass) {
                    mContext.stopService(new Intent(mContext, mServiceClass));
                }
            }
        });
        return this;
    }

    public ServiceCommander<B> connect() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callConnect();
            }
        });
        return this;
    }

    @MainThread
    private void callConnect() {
        synchronized (mServiceClass) {
            if (!isConnected()) {
                Intent service = new Intent(mContext, mServiceClass);
                //mContext.startService(service);
                mContext.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    public ServiceCommander<B> disconnect() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callDisconnect();
            }
        });
        return this;
    }

    public ServiceCommander<B> disconnectAndStopIfUnneeded() {
        if (isConnected() && getBinder().isUnneeded()) {
            forceDisconnect();
            stopService();
        }
        return this;
    }

    @MainThread
    private void callDisconnect() {
        synchronized (mServiceClass) {
            if (isConnected())
                mContext.unbindService(mConnection);
        }
    }

    public ServiceCommander<B> forceDisconnect() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                synchronized (mServiceClass) {
                    try {
                        callDisconnect();
                    } catch (RuntimeException e) {
                        Log.d(LOG_TAG, "forceDisconnect", e);
                        mConnection.onServiceDisconnected(null);
                    }
                }
            }
        });
        return this;
    }

    public boolean isConnected() {
        return getBinder() != null;
    }

    public ServiceCommander<B> postCommand(final ServiceCommand<B> command) {
        synchronized (mConnections) {
            if (isConnected()) {
                command.run(getBinder());
                return this;
            }
            addBinderConnection(new BinderConnection<B>() {
                @Override
                public void onBinderConnected(B binder) {
                    command.run(binder);
                    removeBinderConnection(this);
                }

                @Override
                public void onBinderDisconnected() {

                }
            });
            connect();
        }
        return this;
    }

    public B getBinder() {
        synchronized (mBinderLock) {
            return mBinder;
        }
    }

    private ServiceCommander<B> setBinder(B binder) {
        synchronized (mBinderLock) {
            mBinder = binder;
        }
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        forceDisconnect();
    }

    public interface ServiceCommand<B extends IBinder> {

        void run(B binder);
    }

    public interface BinderConnection<B extends IBinder> {

        void onBinderConnected(B binder);

        void onBinderDisconnected();
    }
}