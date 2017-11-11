/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
