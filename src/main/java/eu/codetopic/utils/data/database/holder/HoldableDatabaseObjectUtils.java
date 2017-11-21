/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.data.database.holder;

import android.support.annotation.WorkerThread;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.SQLException;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.java.utils.reflect.field.FieldsSearch;
import eu.codetopic.java.utils.reflect.field.FoundField;
import eu.codetopic.java.utils.reflect.field.SimpleFieldsFilter;
import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.database.DependencyDatabaseObject;

public final class HoldableDatabaseObjectUtils {

    private static final String LOG_TAG = "HoldableDatabaseObjectUtils";

    private static final Cache<Class, FoundField> cache = CacheBuilder
            .newBuilder()/*.softValues()*/.build();

    public static FoundField[] getAllHolderFieldsFor(DatabaseBase database) {
        Class[] dataClasses = database.getDataClasses();
        FoundField[] result = new FoundField[dataClasses.length];
        for (int i = 0, len = result.length; i < len; i++) {
            result[i] = getHolderFieldsOf(dataClasses[i]);
        }
        return result;
    }

    public synchronized static FoundField getHolderFieldsOf(final Class clazz) {
        FoundField result = cache.getIfPresent(clazz);
        if (result == null) {
            result = FieldsSearch.getFields(new SimpleFieldsFilter(clazz)
                    .addClassesToFind(DatabaseObjectHolder.class, DatabaseObjectHolder[].class)
                    .addAnnotationsToDeepSearch(ScanForHolders.class));

            if (Log.isInDebugMode())
                Log.d(LOG_TAG, "getHolderFieldsOf for " + clazz.getName() + ":\n"
                        + result.hierarchyToString());

                    /*FieldsSearch.getFields(new FieldsFilter() {
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
                        if (fieldObject == null) continue;
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
