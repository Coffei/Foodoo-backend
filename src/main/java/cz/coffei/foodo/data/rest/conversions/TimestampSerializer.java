package cz.coffei.foodo.data.rest.conversions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * Created by jtrantin on 24.8.15.
 */
public class TimestampSerializer extends JsonSerializer<Timestamp> {
    @Override
    public void serialize(Timestamp timestamp, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(timestamp == null) jgen.writeNull();

        String dateString = timestamp.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        jgen.writeString(dateString);
    }
}
