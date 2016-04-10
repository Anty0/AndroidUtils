package eu.codetopic.utils.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.exceptions.InvalidModuleDataFileNameException;
import eu.codetopic.utils.module.data.DebugProviderData;
import eu.codetopic.utils.module.data.ModuleData;
import eu.codetopic.utils.module.data.ModuleDataGetter;
import eu.codetopic.utils.module.data.ModuleDataManager;

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
                mModules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                Log.e(LOG_TAG, "<init>", e);
            }
    }

    public static void initialize(Configuration config) {// TODO: 6.3.16 use it in ApplicationBase
        if (mInstance != null)
            throw new IllegalStateException(LOG_TAG + " is already initialized");
        mInstance = new ModulesManager(config);
        mInstance.init();
        ModuleDataGetter<?, ? extends DebugProviderData> debugDataGetter = config.getDebugDataGetter();
        if (debugDataGetter == null || debugDataGetter.get().isDebugMode())
            mInstance.validate();
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
        private ModuleDataGetter<?, ? extends DebugProviderData> debugDataGetter = null;

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

        public ModuleDataGetter<?, ? extends DebugProviderData> getDebugDataGetter() {
            return debugDataGetter;
        }

        public Configuration setDebugDataGetter(ModuleDataGetter<?, ? extends DebugProviderData> debugDataGetter) {
            this.debugDataGetter = debugDataGetter;
            return this;
        }

        @SafeVarargs
        public final Configuration addModules(Class<? extends Module>... modules) {
            Collections.addAll(this.modules, modules);
            return this;
        }

        public Configuration addModules(Collection<Class<? extends Module>> modules) {
            this.modules.addAll(modules);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Class<? extends Module>[] getModules() {
            return modules.toArray(new Class[modules.size()]);
        }
    }
}
