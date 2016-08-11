package com.codetopic.utils.thread.job.database;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;

public interface DatabaseWork<T, ID> {

    @WorkerThread
    void run(Dao<T, ID> dao) throws Throwable;
}
