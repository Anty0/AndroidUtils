package cz.codetopic.utils.module.data;

import android.content.Context;

import cz.codetopic.utils.database.DatabaseBase;

/**
 * Created by anty on 16.2.16.
 *
 * @author anty
 */
public abstract class ModuleDatabase extends DatabaseBase {

    protected ModuleDatabase(Context context, String databaseName, int databaseVersion, Class... dataClasses) {
        super(context, databaseName, null, databaseVersion, dataClasses);
    }

}
