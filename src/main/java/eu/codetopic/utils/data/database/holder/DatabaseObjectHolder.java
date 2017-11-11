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

package eu.codetopic.utils.data.database.holder;

import android.support.annotation.Keep;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;

import eu.codetopic.utils.data.database.DependencyDao;
import eu.codetopic.utils.data.database.DependencyDatabaseObject;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public abstract class DatabaseObjectHolder<T extends DependencyDatabaseObject> implements Serializable {

    private Long objectId = null;

    /**
     * @hide
     */
    @Keep
    public DatabaseObjectHolder() {
    }

    public DatabaseObjectHolder(Long objectId) {
        this.objectId = objectId;
    }

    public DatabaseObjectHolder(T obj) {
        this(obj.getId());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(objectId);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        objectId = (Long) in.readObject();
    }

    public Long getObjectId() {
        return objectId;
    }

    @WorkerThread
    public T getObject() throws SQLException {
        //noinspection unchecked
        return ((DependencyDao<T>) getDaoGetter().get())
                .queryForIdWithTemp(getObjectId());
    }

    public abstract DatabaseDaoGetter<T, Long> getDaoGetter();

}
