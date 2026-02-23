package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.domain.enumeration.OrderStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RestaurantOrder.
 */
@Entity
@Table(name = "restaurant_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 6, max = 20)
    @Column(name = "order_code", length = 20, nullable = false, unique = true)
    private String orderCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotNull
    @Column(name = "is_pre_order", nullable = false)
    private Boolean isPreOrder;

    @Column(name = "scheduled_for")
    private Instant scheduledFor;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "subtotal", precision = 21, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @DecimalMin(value = "0")
    @Column(name = "tax_amount", precision = 21, scale = 2)
    private BigDecimal taxAmount;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Lob
    @Column(name = "special_instructions")
    private String specialInstructions;

    @Column(name = "estimated_ready_time")
    private Instant estimatedReadyTime;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "optionSelections", "menuItem", "order" }, allowSetters = true)
    private Set<OrderItem> items = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "processedBy", "order" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedWaiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "room" }, allowSetters = true)
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "brand", "location" }, allowSetters = true)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tableAssignments", "orders", "location", "client", "room" }, allowSetters = true)
    private Reservation reservation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RestaurantOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public RestaurantOrder orderCode(String orderCode) {
        this.setOrderCode(orderCode);
        return this;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public RestaurantOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Boolean getIsPreOrder() {
        return this.isPreOrder;
    }

    public RestaurantOrder isPreOrder(Boolean isPreOrder) {
        this.setIsPreOrder(isPreOrder);
        return this;
    }

    public void setIsPreOrder(Boolean isPreOrder) {
        this.isPreOrder = isPreOrder;
    }

    public Instant getScheduledFor() {
        return this.scheduledFor;
    }

    public RestaurantOrder scheduledFor(Instant scheduledFor) {
        this.setScheduledFor(scheduledFor);
        return this;
    }

    public void setScheduledFor(Instant scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public RestaurantOrder subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public RestaurantOrder discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return this.taxAmount;
    }

    public RestaurantOrder taxAmount(BigDecimal taxAmount) {
        this.setTaxAmount(taxAmount);
        return this;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public RestaurantOrder totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSpecialInstructions() {
        return this.specialInstructions;
    }

    public RestaurantOrder specialInstructions(String specialInstructions) {
        this.setSpecialInstructions(specialInstructions);
        return this;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Instant getEstimatedReadyTime() {
        return this.estimatedReadyTime;
    }

    public RestaurantOrder estimatedReadyTime(Instant estimatedReadyTime) {
        this.setEstimatedReadyTime(estimatedReadyTime);
        return this;
    }

    public void setEstimatedReadyTime(Instant estimatedReadyTime) {
        this.estimatedReadyTime = estimatedReadyTime;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public RestaurantOrder confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public RestaurantOrder completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public RestaurantOrder createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public RestaurantOrder updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<OrderItem> getItems() {
        return this.items;
    }

    public void setItems(Set<OrderItem> orderItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setOrder(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setOrder(this));
        }
        this.items = orderItems;
    }

    public RestaurantOrder items(Set<OrderItem> orderItems) {
        this.setItems(orderItems);
        return this;
    }

    public RestaurantOrder addItems(OrderItem orderItem) {
        this.items.add(orderItem);
        orderItem.setOrder(this);
        return this;
    }

    public RestaurantOrder removeItems(OrderItem orderItem) {
        this.items.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setOrder(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setOrder(this));
        }
        this.payments = payments;
    }

    public RestaurantOrder payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public RestaurantOrder addPayments(Payment payment) {
        this.payments.add(payment);
        payment.setOrder(this);
        return this;
    }

    public RestaurantOrder removePayments(Payment payment) {
        this.payments.remove(payment);
        payment.setOrder(null);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public RestaurantOrder location(Location location) {
        this.setLocation(location);
        return this;
    }

    public User getClient() {
        return this.client;
    }

    public void setClient(User user) {
        this.client = user;
    }

    public RestaurantOrder client(User user) {
        this.setClient(user);
        return this;
    }

    public User getAssignedWaiter() {
        return this.assignedWaiter;
    }

    public void setAssignedWaiter(User user) {
        this.assignedWaiter = user;
    }

    public RestaurantOrder assignedWaiter(User user) {
        this.setAssignedWaiter(user);
        return this;
    }

    public RestaurantTable getTable() {
        return this.table;
    }

    public void setTable(RestaurantTable restaurantTable) {
        this.table = restaurantTable;
    }

    public RestaurantOrder table(RestaurantTable restaurantTable) {
        this.setTable(restaurantTable);
        return this;
    }

    public Promotion getPromotion() {
        return this.promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public RestaurantOrder promotion(Promotion promotion) {
        this.setPromotion(promotion);
        return this;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public RestaurantOrder reservation(Reservation reservation) {
        this.setReservation(reservation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((RestaurantOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantOrder{" +
            "id=" + getId() +
            ", orderCode='" + getOrderCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", isPreOrder='" + getIsPreOrder() + "'" +
            ", scheduledFor='" + getScheduledFor() + "'" +
            ", subtotal=" + getSubtotal() +
            ", discountAmount=" + getDiscountAmount() +
            ", taxAmount=" + getTaxAmount() +
            ", totalAmount=" + getTotalAmount() +
            ", specialInstructions='" + getSpecialInstructions() + "'" +
            ", estimatedReadyTime='" + getEstimatedReadyTime() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
