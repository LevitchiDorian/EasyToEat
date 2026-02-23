package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.domain.enumeration.OrderItemStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OrderItem.
 */
@Entity
@Table(name = "order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status;

    @Size(max = 500)
    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @Size(max = 255)
    @Column(name = "notes", length = 255)
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orderItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "optionValue", "orderItem" }, allowSetters = true)
    private Set<OrderItemOptionSelection> optionSelections = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "allergens", "options", "category" }, allowSetters = true)
    private MenuItem menuItem;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "items", "payments", "location", "client", "assignedWaiter", "table", "promotion", "reservation" },
        allowSetters = true
    )
    private RestaurantOrder order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public OrderItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public OrderItem totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderItemStatus getStatus() {
        return this.status;
    }

    public OrderItem status(OrderItemStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public String getSpecialInstructions() {
        return this.specialInstructions;
    }

    public OrderItem specialInstructions(String specialInstructions) {
        this.setSpecialInstructions(specialInstructions);
        return this;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getNotes() {
        return this.notes;
    }

    public OrderItem notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<OrderItemOptionSelection> getOptionSelections() {
        return this.optionSelections;
    }

    public void setOptionSelections(Set<OrderItemOptionSelection> orderItemOptionSelections) {
        if (this.optionSelections != null) {
            this.optionSelections.forEach(i -> i.setOrderItem(null));
        }
        if (orderItemOptionSelections != null) {
            orderItemOptionSelections.forEach(i -> i.setOrderItem(this));
        }
        this.optionSelections = orderItemOptionSelections;
    }

    public OrderItem optionSelections(Set<OrderItemOptionSelection> orderItemOptionSelections) {
        this.setOptionSelections(orderItemOptionSelections);
        return this;
    }

    public OrderItem addOptionSelections(OrderItemOptionSelection orderItemOptionSelection) {
        this.optionSelections.add(orderItemOptionSelection);
        orderItemOptionSelection.setOrderItem(this);
        return this;
    }

    public OrderItem removeOptionSelections(OrderItemOptionSelection orderItemOptionSelection) {
        this.optionSelections.remove(orderItemOptionSelection);
        orderItemOptionSelection.setOrderItem(null);
        return this;
    }

    public MenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public OrderItem menuItem(MenuItem menuItem) {
        this.setMenuItem(menuItem);
        return this;
    }

    public RestaurantOrder getOrder() {
        return this.order;
    }

    public void setOrder(RestaurantOrder restaurantOrder) {
        this.order = restaurantOrder;
    }

    public OrderItem order(RestaurantOrder restaurantOrder) {
        this.setOrder(restaurantOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", totalPrice=" + getTotalPrice() +
            ", status='" + getStatus() + "'" +
            ", specialInstructions='" + getSpecialInstructions() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
