package cz.coffei.foodo.data.rest.conversions;



import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by jtrantin on 10.8.15.
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
    @Override
    public void serialize(LocalTime localTime, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(localTime!=null) {
            String timeString = localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            jgen.writeString(timeString);
        } else {
            jgen.writeNull();
        }
    }
}
