package cz.coffei.foodo.data.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Created by jtrantin on 25.7.15.
 */
@Entity
public class IngredientGroup {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Min(value = 0)
    private Integer price;

    private boolean required;

    private boolean allowMore;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "group")
    private List<Ingredient> ingredients;

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isAllowMore() {
        return allowMore;
    }

    public void setAllowMore(boolean allowMore) {
        this.allowMore = allowMore;
    }

    @XmlTransient
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IngredientGroup that = (IngredientGroup) o;

        if (required != that.required) return false;
        if (allowMore != that.allowMore) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(price != null ? !price.equals(that.price) : that.price != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (allowMore ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IngredientGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", required=" + required +
                ", allowMore=" + allowMore +
                ", ingredients=" + ingredients +
                '}';
    }
}
