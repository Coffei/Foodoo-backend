package cz.coffei.foodo.data.entities;

import cz.coffei.foodo.data.enums.OrderStatus;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by jtrantin on 25.7.15.
 */
@Table(name = "order_entity")
@Entity
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    private String customerName;

    @Email // can be null
    private String customeremail;

    private boolean takeaway;

    @NotNull
    @Min(value = 0)
    private Integer totalPrice;

    @NotNull
    private Timestamp created;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderItem> orderItems;

    private LocalTime targetTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomeremail() {
        return customeremail;
    }

    public void setCustomeremail(String customeremail) {
        this.customeremail = customeremail;
    }

    public boolean isTakeaway() {
        return takeaway;
    }

    public void setTakeaway(boolean takeaway) {
        this.takeaway = takeaway;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * @return timestamp representing a point of creation. The time is in UTC!
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Sets the point of creation. The timestamp is representing UTC time!
     * @param created
     */
    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalTime getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(LocalTime targetTime) {
        this.targetTime = targetTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (takeaway != order.takeaway) return false;
        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (customerName != null ? !customerName.equals(order.customerName) : order.customerName != null) return false;
        if (customeremail != null ? !customeremail.equals(order.customeremail) : order.customeremail != null)
            return false;
        if (totalPrice != null ? !totalPrice.equals(order.totalPrice) : order.totalPrice != null) return false;
        if (created != null ? !created.equals(order.created) : order.created != null) return false;
        if (status != order.status) return false;
        if (orderItems != null ? !orderItems.equals(order.orderItems) : order.orderItems != null) return false;
        return !(targetTime != null ? !targetTime.equals(order.targetTime) : order.targetTime != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        result = 31 * result + (customeremail != null ? customeremail.hashCode() : 0);
        result = 31 * result + (takeaway ? 1 : 0);
        result = 31 * result + (totalPrice != null ? totalPrice.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (orderItems != null ? orderItems.hashCode() : 0);
        result = 31 * result + (targetTime != null ? targetTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customeremail='" + customeremail + '\'' +
                ", takeaway=" + takeaway +
                ", totalPrice=" + totalPrice +
                ", created=" + created +
                ", status=" + status +
                ", orderItems=" + orderItems +
                ", targetTime=" + targetTime +
                '}';
    }
}
