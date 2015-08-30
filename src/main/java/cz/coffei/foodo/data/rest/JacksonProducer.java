package cz.coffei.foodo.data.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.coffei.foodo.data.rest.conversions.LocalTimeDeserializer;
import cz.coffei.foodo.data.rest.conversions.LocalTimeSerializer;
import cz.coffei.foodo.data.rest.conversions.TimestampSerializer;


import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.sql.Timestamp;
import java.time.LocalTime;

/**
 * Created by jtrantin on 10.8.15.
 */
@Provider
@Produces( MediaType.APPLICATION_JSON )
public class JacksonProducer implements ContextResolver<ObjectMapper> {
    private final ObjectMapper json;

    public JacksonProducer() {
        this.json = new ObjectMapper();
        SimpleModule module = new SimpleModule("Serializers module");
        module.addSerializer(LocalTime.class, new LocalTimeSerializer());
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        module.addSerializer(Timestamp.class, new TimestampSerializer());
        this.json.registerModule(module);
    }

    @Override
    public ObjectMapper getContext( Class<?> type ) {
        return json;
    }
}
