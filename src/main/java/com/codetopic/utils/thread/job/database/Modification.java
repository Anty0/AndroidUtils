package com.codetopic.utils.thread.job.database;

import com.codetopic.utils.data.database.DependencyDao;
import com.j256.ormlite.dao.Dao;

import java.util.Arrays;

public enum Modification {
    CREATE, UPDATE, CREATE_OR_UPDATE, DELETE, DELETE_FROM_TEMP;

    @SafeVarargs
    public final <T, ID> DatabaseWork<T, ID> generateWork(final T... toModify) {
        switch (this) {
            case CREATE:
                return new DatabaseWork<T, ID>() {
                    @Override
                    public void run(Dao<T, ID> dao) throws Throwable {
                        for (T object : toModify) dao.create(object);
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
                throw new EnumConstantNotPresentException(Modification.class, this.name());
        }
    }
}
