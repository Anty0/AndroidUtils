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

package eu.codetopic.utils.data.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

import eu.codetopic.java.utils.Objects;

public abstract class DatabaseObject implements Serializable {

    @DatabaseField(generatedId = true, dataType = DataType.LONG_OBJ)
    private Long id = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (hasId()) throw new IllegalStateException("Can't set 'id': id is already set");
        this.id = id;
    }

    public boolean hasId() {
        return id != null;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || hasId()
                && o instanceof DatabaseObject
                && Objects.equals(getClass(), o.getClass())
                && Objects.equals(getId(), ((DatabaseObject) o).getId());
    }
}
