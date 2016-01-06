package com.rascal.core.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Persistable;

import java.io.IOException;
import java.io.Serializable;

public class EntityIdSerializer extends JsonSerializer<Persistable<? extends Serializable>> {

    @Override
    public void serialize(Persistable<? extends Serializable> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        if (value != null) {
            jgen.writeStartObject();
            jgen.writeFieldName("id");
            jgen.writeString(ObjectUtils.toString(value.getId()));
            jgen.writeEndObject();
        }
    }
}
