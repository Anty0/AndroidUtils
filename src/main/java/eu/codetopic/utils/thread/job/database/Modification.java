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

package eu.codetopic.utils.thread.job.database;

import com.j256.ormlite.dao.Dao;

import java.util.Arrays;

import eu.codetopic.utils.data.database.DependencyDao;

public enum Modification {
    CREATE, UPDATE, CREATE_OR_UPDATE, DELETE, DELETE_FROM_TEMP;

    @SafeVarargs
    public final <T, ID> DatabaseWork<T, ID> generateWork(final T... toModify) {
        switch (this) {
            case CREATE:
                return new DatabaseWork<T, ID>() {
                    @Override
                    public void run(Dao<T, ID> dao) throws Throwable {
                        dao.create(Arrays.asList(toModify));
                    }
                };
            case UPDATE:
                return new DatabaseWork<T, ID>() {
                    @Override
                    public void run(Dao<T, ID> dao) throws Throwable {
                        for (T object : toModify) dao.update(object);
                    }
                };
            case CREATE_OR_UPDATE:
                return new DatabaseWork<T, ID>() {
                    @Override
                    public void run(Dao<T, ID> dao) throws Throwable {
                        for (T object : toModify) dao.createOrUpdate(object);
                    }
                };
            case DELETE:
                return new DatabaseWork<T, ID>() {
                    @Override
                    public void run(Dao<T, ID> dao) throws Throwable {
                        dao.delete(Arrays.asList(toModify));
                    }
                };
            case DELETE_FROM_TEMP:
                return new DatabaseWork<T, ID>() {
                    @Override
                    public void run(Dao<T, ID> dao) throws Throwable {
                        //noinspection unchecked
                        ((DependencyDao) dao).deleteFromTemp(Arrays.asList(toModify));
                    }
                };
            default:
                throw new EnumConstantNotPresentException(Modification.class, name());
        }
    }
}
