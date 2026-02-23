package md.utm.restaurant.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import md.utm.restaurant.domain.enumeration.OrderStatus;

/**
 * A DTO for the {@link md.utm.restaurant.domain.RestaurantOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantOrderDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 6, max = 20)
    private String orderCode;

    @NotNull
    private OrderStatus status;

    @NotNull
    private Boolean isPreOrder;

    private Instant scheduledFor;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0")
    private BigDecimal taxAmount;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalAmount;

    @Lob
    private String specialInstructions;

    private Instant estimatedReadyTime;

    private Instant confirmedAt;

    private Instant completedAt;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private LocationDTO location;

    private UserDTO client;

    private UserDTO assignedWaiter;

    private RestaurantTableDTO table;

    private PromotionDTO promotion;

    private ReservationDTO reservation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Boolean getIsPreOrder() {
        return isPreOrder;
    }

    public void setIsPreOrder(Boolean isPreOrder) {
        this.isPreOrder = isPreOrder;
    }

    public Instant getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Instant scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Instant getEstimatedReadyTime() {
        return estimatedReadyTime;
    }

    public void setEstimatedReadyTime(Instant estimatedReadyTime) {
        this.estimatedReadyTime = estimatedReadyTime;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public UserDTO getClient() {
        return client;
    }

    public void setClient(UserDTO client) {
        this.client = client;
    }

    public UserDTO getAssignedWaiter() {
        return assignedWaiter;
    }

    public void setAssignedWaiter(UserDTO assignedWaiter) {
        this.assignedWaiter = assignedWaiter;
    }

    public RestaurantTableDTO getTable() {
        return table;
    }

    public void setTable(RestaurantTableDTO table) {
        this.table = table;
    }

    public PromotionDTO getPromotion() {
        return promotion;
    }

    public void setPromotion(PromotionDTO promotion) {
        this.promotion = promotion;
    }

    public ReservationDTO getReservation() {
        return reservation;
    }

    public void setReservation(ReservationDTO reservation) {
        this.reservation = reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantOrderDTO)) {
            return false;
        }

        RestaurantOrderDTO restaurantOrderDTO = (RestaurantOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurantOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantOrderDTO{" +
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
            ", location=" + getLocation() +
            ", client=" + getClient() +
            ", assignedWaiter=" + getAssignedWaiter() +
            ", table=" + getTable() +
            ", promotion=" + getPromotion() +
            ", reservation=" + getReservation() +
            "}";
    }
}
