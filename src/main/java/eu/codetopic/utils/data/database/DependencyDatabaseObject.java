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

package eu.codetopic.utils.data.database;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

public abstract class DependencyDatabaseObject extends DatabaseObject {

    @DatabaseField
    private boolean deleted = false;

    public boolean isDeleted() {
        return deleted;
    }

    public void delete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    @WorkerThread
    public abstract boolean isRequired() throws SQLException;// TODO: 5.4.16 Use HoldableDatabaseObjectUtils to implement this method

}
