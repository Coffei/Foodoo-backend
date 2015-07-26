package cz.coffei.foodo.data.entities;

import cz.coffei.foodo.data.enums.OrderItemType;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Created by jtrantin on 25.7.15.
 */
@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private OrderItemType type;

    @NotNull
    @Min(value = 1)
    private Integer times;

    // validation of correctness will be done manually by dao
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Ingredient> ingredients;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private MenuItem menu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItemType getType() {
        return type;
    }

    public void setType(OrderItemType type) {
        this.type = type;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @XmlTransient
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getMenu() {
        return menu;
    }

    public void setMenu(MenuItem menu) {
        this.menu = menu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        if (id != null ? !id.equals(orderItem.id) : orderItem.id != null) return false;
        if (type != orderItem.type) return false;
        if (times != null ? !times.equals(orderItem.times) : orderItem.times != null) return false;
        if (ingredients != null ? !ingredients.equals(orderItem.ingredients) : orderItem.ingredients != null)
            return false;
        return !(menu != null ? !menu.equals(orderItem.menu) : orderItem.menu != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (times != null ? times.hashCode() : 0);
        result = 31 * result + (ingredients != null ? ingredients.hashCode() : 0);
        result = 31 * result + (menu != null ? menu.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", type=" + type +
                ", times=" + times +
                ", ingredients=" + ingredients +
                ", menu=" + menu +
                '}';
    }
}
