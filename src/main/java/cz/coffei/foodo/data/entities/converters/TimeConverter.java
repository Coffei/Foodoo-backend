package cz.coffei.foodo.data.entities.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

/**
 * Created by jtrantin on 9.8.15.
 */
@Converter(autoApply = true)
public class TimeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime attribute) {
        if(attribute == null) return null;

        return Time.valueOf(attribute);
    }

    @Override
    public LocalTime convertToEntityAttribute(Time dbData) {
        if(dbData==null) return null;

        return dbData.toLocalTime();
    }
}
