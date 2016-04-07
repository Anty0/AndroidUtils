package cz.codetopic.utils.database.holder;

import android.support.annotation.WorkerThread;

import java.sql.SQLException;
import java.util.HashMap;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.Objects;
import cz.codetopic.utils.database.DatabaseBase;
import cz.codetopic.utils.database.DependencyDatabaseObject;
import cz.codetopic.utils.reflect.field.FieldsSearch;
import cz.codetopic.utils.reflect.field.FoundField;
import cz.codetopic.utils.reflect.field.SimpleFieldsFilter;

/**
 * Created by anty on 4.4.16.
 *
 * @author anty
 */
public final class HoldableDatabaseObjectUtils {

    private static final String LOG_TAG = "HoldableDatabaseObjectUtils";

    private static final HashMap<Class, FoundField> cache = new HashMap<>();

    public static FoundField[] getAllHolderFieldsFor(DatabaseBase database) {
        Class[] dataClasses = database.getDataClasses();
        FoundField[] result = new FoundField[dataClasses.length];
        for (int i = 0, len = result.length; i < len; i++) {
            result[i] = getHolderFieldsOf(dataClasses[i]);
        }
        return result;
    }

    public static FoundField getHolderFieldsOf(final Class clazz) {
        FoundField result = cache.get(clazz);
        if (result == null) {
            result = FieldsSearch.getAllFields(new SimpleFieldsFilter(clazz)
                    .addCalssesToFind(DatabaseObjectHolder.class, DatabaseObjectHolder[].class)
                    .addAnnotationsToDeepSearch(ScanForHolders.class));
            Log.d(LOG_TAG, "getHolderFieldsOf for " + clazz.getName() + ":\n" + result.hierarchyToString());

                    /*FieldsSearch.getAllFields(new FieldsFilter() {
                @Override
                public Class<?> getStartClass() {
                    return clazz;
                }

                @Override
                public boolean addField(@Nullable Field field, @NonNull Class<?> fieldType) {
                    return field != null && field.getAnnotation(DatabaseField.class) != null
                            && (DatabaseObjectHolder.class.isAssignableFrom(fieldType)
                            || DatabaseObjectHolder[].class.isAssignableFrom(fieldType));
                }

                @Override
                public boolean searchFieldsInFieldClass(@Nullable Field field, @NonNull Class<?> fieldType) {
                    return field == null || field.getAnnotation(ScanForHolders.class) != null;
                }
            });*/
            cache.put(clazz, result);
        }
        return result;
    }

    @WorkerThread
    public static boolean isRequired(DatabaseBase database, DependencyDatabaseObject holdableObject,
                                     Class<? extends DatabaseObjectHolder> holderClass) throws SQLException {

        Long id = holdableObject.getId();
        if (id == null) return false;

        for (Class dataClass : database.getDataClasses()) {
            FoundField foundFieldBase = getHolderFieldsOf(dataClass);
            for (Object obj : database.getDao(dataClass)) {
                for (FoundField foundField : foundFieldBase) {
                    if (!holderClass.isAssignableFrom(foundField.getGenericFieldType()))
                        continue;

                    try {
                        Object fieldObject = foundField.getFieldObjectFrom(obj);
                        if (check(id, fieldObject.getClass().isArray()
                                ? (DatabaseObjectHolder[]) fieldObject
                                : new DatabaseObjectHolder[]{(DatabaseObjectHolder) fieldObject}))
                            return true;
                    } catch (Throwable t) {
                        Log.e(LOG_TAG, "Can't check field " + foundField + " please make it accessible", t);
                    }
                }
            }
        }
        return false;
    }

    private static boolean check(Long id, DatabaseObjectHolder... holders) {
        for (DatabaseObjectHolder holder : holders) {
            if (Objects.equals(holder.getObjectId(), id))
                return true;
        }
        return false;
    }

}
