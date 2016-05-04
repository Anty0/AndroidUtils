package eu.codetopic.utils.module.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.InputStream;

import eu.codetopic.utils.database.DatabaseBase;

/**
 * Created by anty on 16.2.16.
 *
 * @author anty
 */
public abstract class ModuleDatabase extends DatabaseBase {

    public ModuleDatabase(Context context, String databaseName,
                          int databaseVersion, Class... dataClasses) {

        super(context, databaseName, null, databaseVersion, dataClasses);
    }

    public ModuleDatabase(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                          int databaseVersion, Class... classes) {

        super(context, databaseName, factory, databaseVersion, classes);
    }

    public ModuleDatabase(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                          int databaseVersion, int configFileId, Class... classes) {

        super(context, databaseName, factory, databaseVersion, configFileId, classes);
    }

    public ModuleDatabase(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                          int databaseVersion, File configFile, Class... classes) {

        super(context, databaseName, factory, databaseVersion, configFile, classes);
    }

    public ModuleDatabase(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                          int databaseVersion, InputStream stream, Class... classes) {

        super(context, databaseName, factory, databaseVersion, stream, classes);
    }
}
