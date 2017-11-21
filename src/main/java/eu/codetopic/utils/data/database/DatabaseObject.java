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
