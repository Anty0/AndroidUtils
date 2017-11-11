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

package eu.codetopic.utils.data.database.singleton;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.SingletonJobManagerGetter;

public class SingletonDatabaseDaoGetter<DT, ID> extends SingletonJobManagerGetter implements DatabaseDaoGetter<DT, ID> {

    private static final String LOG_TAG = "SingletonDatabaseDaoGetter";

    private final Class<DT> mDataClass;

    public SingletonDatabaseDaoGetter(Class<DT> dataClass) {
        mDataClass = dataClass;
    }

    @Override
    public Dao<DT, ID> get() throws SQLException {
        return getDatabase().getDao(getDaoObjectClass());
    }

    @Override
    public Class<DT> getDaoObjectClass() {
        return mDataClass;
    }

    @Override
    public DatabaseBase getDatabase() {
        return SingletonDatabase.getInstance();
    }
}
