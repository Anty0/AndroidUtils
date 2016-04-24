package eu.codetopic.utils.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.exceptions.InvalidModuleDataFileNameException;
import eu.codetopic.utils.module.data.DebugProviderData;
import eu.codetopic.utils.module.data.ModuleData;
import eu.codetopic.utils.module.data.ModuleDataManager;
import eu.codetopic.utils.module.getter.DataGetter;

/**
 * Created by anty on 15.2.16.
 *
 * @author anty
 */
public final class ModulesManager {

    private static final String LOG_TAG = "ModulesManager";

    private static ModulesManager mInstance = null;

    private final Context mContext;
    @StyleRes private final int mDefaultTheme;
    private final HashMap<Class<? extends Module>, Module> mModules = new HashMap<>();
    private Throwable validateResult = null;

    private ModulesManager(Configuration config) {
        mContext = config.getContext();
        mDefaultTheme = config.getDefaultTheme();
        for (Class<? extends Module> moduleClass : config.getModules())
            try {
                if (!moduleClass.isAnnotationPresent(DisableModule.class))
                    mModules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                Log.e(LOG_TAG, "<init>", e);
            }
    }

    public static void initialize(Configuration config) {// TODO: 6.3.16 use it in ApplicationBase
        if (mInstance != null)
            throw new IllegalStateException(LOG_TAG + " is already initialized");
        SecurePreferences.setLoggingEnabled(config.getDefaultDebugMode());
        mInstance = new ModulesManager(config);
        mInstance.init();

        DataGetter<? extends DebugProviderData> debugDataGetter = config.getDebugDataGetter();
        boolean debugMode = debugDataGetter != null ? debugDataGetter
                .get().isDebugMode() : config.getDefaultDebugMode();
        SecurePreferences.setLoggingEnabled(debugMode);
        if (debugMode) mInstance.validate();
    }

    public static ModulesManager getInstance() {
        return mInstance;
    }

    public static <M extends Module> M findModule(Class<M> moduleClass) {
        return mInstance == null ? null : mInstance.getModule(moduleClass);
    }

    public void validate() {
        try {
            List<String> moduleDataFileNames = new ArrayList<>();
            for (Module module : getModules()) {
                module.validate();

                ModuleDataManager dataManager = module.getDataManager();
                if (dataManager != null) {
                    for (ModuleData moduleData : dataManager.get()) {
                        String fileName = moduleData.getFileName();
                        if (moduleDataFileNames.contains(fileName))
                            throw new InvalidModuleDataFileNameException(module.getName()
                                    + " is used more then once in modules");
                        moduleDataFileNames.add(fileName);
                    }
                }
            }
        } catch (Throwable t) {
            Log.e(LOG_TAG, "validate", t);
            validateResult = t;
        }
    }

    private void init() {
        for (Module module : getModules())
            module.attach(this);
    }

    public Context getContext() {
        return mContext;
    }

    @StyleRes
    public int getDefaultTheme() {
        return mDefaultTheme;
    }

    public Throwable getValidateResult() {
        return validateResult;
    }

    public <M extends Module> M getModule(Class<M> moduleClass) {
        //noinspection unchecked
        return (M) mModules.get(moduleClass);
    }

    public Collection<Module> getModules() {
        return mModules.values();
    }

    public final static class Configuration {

        private final Context context;
        private final List<Class<? extends Module>> modules = new ArrayList<>();
        @StyleRes private int defaultTheme;
        private boolean defaultDebugMode = true;
        private DataGetter<? extends DebugProviderData> debugDataGetter = null;

        public Configuration(@NonNull Context context) {
            this.context = context;
            defaultTheme = context.getApplicationInfo().theme;
        }

        public Context getContext() {
            return context;
        }

        public int getDefaultTheme() {
            return defaultTheme;
        }

        public Configuration setDefaultTheme(@StyleRes int defaultTheme) {
            this.defaultTheme = defaultTheme;
            return this;
        }

        public DataGetter<? extends DebugProviderData> getDebugDataGetter() {
            return debugDataGetter;
        }

        public Configuration setDebugDataGetter(DataGetter<? extends DebugProviderData> debugDataGetter) {
            this.debugDataGetter = debugDataGetter;
            return this;
        }

        public boolean getDefaultDebugMode() {
            return defaultDebugMode;
        }

        public Configuration setDefaultDebugMode(boolean enabled) {
            this.defaultDebugMode = enabled;
            return this;
        }

        @SafeVarargs
        public final Configuration addModules(Class<? extends Module>... modules) {
            //for (Class<? extends Module> module : modules)
            //    this.modules.add(0, module);
            Collections.addAll(this.modules, modules);
            return this;
        }

        public Configuration addModules(Collection<Class<? extends Module>> modules) {
            //for (Class<? extends Module> module : modules)
            //    this.modules.add(0, module);
            this.modules.addAll(modules);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Class<? extends Module>[] getModules() {
            return modules.toArray(new Class[modules.size()]);
        }
    }
}
