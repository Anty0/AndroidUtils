package eu.codetopic.utils.database;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

import eu.codetopic.utils.Objects;

public abstract class DatabaseObject implements Serializable {

    @DatabaseField(generatedId = true)
    private Long id = null;

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || o instanceof DatabaseObject && Objects.equals(getClass(),
                o.getClass()) && Objects.equals(getId(), ((DatabaseObject) o).getId());
    }
}
