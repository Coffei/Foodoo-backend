package cz.coffei.foodo.data.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Generated;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Date;

/**
 * Created by jtrantin on 24.7.15.
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"number", "date"}))
@Entity
public class MenuItem {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Integer number;

    @NotNull
    private Date date;

    @NotNull
    @Min(value = 0)
    private Integer price;

    private String description;


    //Generated stuff
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        if (id != null ? !id.equals(menuItem.id) : menuItem.id != null) return false;
        if (name != null ? !name.equals(menuItem.name) : menuItem.name != null) return false;
        if (number != null ? !number.equals(menuItem.number) : menuItem.number != null) return false;
        if (date != null ? !date.equals(menuItem.date) : menuItem.date != null) return false;
        if (price != null ? !price.equals(menuItem.price) : menuItem.price != null) return false;
        return !(description != null ? !description.equals(menuItem.description) : menuItem.description != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", date=" + date +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
