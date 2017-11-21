/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.getter;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import eu.codetopic.utils.data.database.DatabaseBase;

public interface DatabaseDaoGetter<DT, ID> extends BaseGetter, JobManagerGetter {

    @WorkerThread
    Dao<DT, ID> get() throws SQLException;

    Class<DT> getDaoObjectClass();

    DatabaseBase getDatabase();
}
