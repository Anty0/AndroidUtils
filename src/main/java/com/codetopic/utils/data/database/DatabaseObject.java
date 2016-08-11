package com.codetopic.utils.data.database;

import com.codetopic.utils.Objects;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public abstract class DatabaseObject implements Serializable {

    @DatabaseField(generatedId = true)
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
        return super.equals(o) || getId() != null
                && o instanceof DatabaseObject
                && Objects.equals(getClass(), o.getClass())
                && Objects.equals(getId(), ((DatabaseObject) o).getId());
    }
}
