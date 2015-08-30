package cz.coffei.foodo.data.entities;

import cz.coffei.foodo.data.enums.BusinessHoursType;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.time.LocalTime;

/**
 * Created by jtrantin on 8.8.15.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "type"))
public class BusinessHours {

    @GeneratedValue
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BusinessHoursType type;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessHoursType getType() {
        return type;
    }

    public void setType(BusinessHoursType type) {
        this.type = type;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime start) {
        this.startTime = start;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime end) {
        this.endTime = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusinessHours that = (BusinessHours) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != that.type) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        return !(endTime != null ? !endTime.equals(that.endTime) : that.endTime != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BusinessHours{" +
                "id=" + id +
                ", type=" + type +
                ", start=" + startTime +
                ", end=" + endTime +
                '}';
    }
}
