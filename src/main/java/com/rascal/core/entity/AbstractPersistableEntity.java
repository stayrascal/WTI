package com.rascal.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

public abstract class AbstractPersistableEntity<ID extends Serializable> extends AbstractPojo implements Persistable<ID> {

    private static final long serialVersionUID = 7750025843953658844L;

    @JsonIgnore
    public boolean isNew() {
        return getId() == null;
    }
}