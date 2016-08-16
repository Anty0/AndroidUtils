package eu.codetopic.utils.data.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

import eu.codetopic.utils.Objects;

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
