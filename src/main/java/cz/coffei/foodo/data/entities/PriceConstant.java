package cz.coffei.foodo.data.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by jtrantin on 26.7.15.
 */
@Entity
public class PriceConstant {

    public PriceConstant() {
    }

    public PriceConstant(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @NotNull
    @NotEmpty
    @Id
    private String name;

    @NotNull
    private Integer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceConstant that = (PriceConstant) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PriceConstant{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
