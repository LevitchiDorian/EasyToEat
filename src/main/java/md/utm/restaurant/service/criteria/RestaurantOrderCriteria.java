package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.domain.enumeration.OrderStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.RestaurantOrder} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.RestaurantOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /restaurant-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orderCode;

    private OrderStatusFilter status;

    private BooleanFilter isPreOrder;

    private InstantFilter scheduledFor;

    private BigDecimalFilter subtotal;

    private BigDecimalFilter discountAmount;

    private BigDecimalFilter taxAmount;

    private BigDecimalFilter totalAmount;

    private InstantFilter estimatedReadyTime;

    private InstantFilter confirmedAt;

    private InstantFilter completedAt;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter itemsId;

    private LongFilter paymentsId;

    private LongFilter locationId;

    private LongFilter clientId;

    private LongFilter assignedWaiterId;

    private LongFilter tableId;

    private LongFilter promotionId;

    private LongFilter reservationId;

    private Boolean distinct;

    public RestaurantOrderCriteria() {}

    public RestaurantOrderCriteria(RestaurantOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orderCode = other.optionalOrderCode().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(OrderStatusFilter::copy).orElse(null);
        this.isPreOrder = other.optionalIsPreOrder().map(BooleanFilter::copy).orElse(null);
        this.scheduledFor = other.optionalScheduledFor().map(InstantFilter::copy).orElse(null);
        this.subtotal = other.optionalSubtotal().map(BigDecimalFilter::copy).orElse(null);
        this.discountAmount = other.optionalDiscountAmount().map(BigDecimalFilter::copy).orElse(null);
        this.taxAmount = other.optionalTaxAmount().map(BigDecimalFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.estimatedReadyTime = other.optionalEstimatedReadyTime().map(InstantFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.completedAt = other.optionalCompletedAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.paymentsId = other.optionalPaymentsId().map(LongFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.assignedWaiterId = other.optionalAssignedWaiterId().map(LongFilter::copy).orElse(null);
        this.tableId = other.optionalTableId().map(LongFilter::copy).orElse(null);
        this.promotionId = other.optionalPromotionId().map(LongFilter::copy).orElse(null);
        this.reservationId = other.optionalReservationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RestaurantOrderCriteria copy() {
        return new RestaurantOrderCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getOrderCode() {
        return orderCode;
    }

    public Optional<StringFilter> optionalOrderCode() {
        return Optional.ofNullable(orderCode);
    }

    public StringFilter orderCode() {
        if (orderCode == null) {
            setOrderCode(new StringFilter());
        }
        return orderCode;
    }

    public void setOrderCode(StringFilter orderCode) {
        this.orderCode = orderCode;
    }

    public OrderStatusFilter getStatus() {
        return status;
    }

    public Optional<OrderStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public OrderStatusFilter status() {
        if (status == null) {
            setStatus(new OrderStatusFilter());
        }
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
        this.status = status;
    }

    public BooleanFilter getIsPreOrder() {
        return isPreOrder;
    }

    public Optional<BooleanFilter> optionalIsPreOrder() {
        return Optional.ofNullable(isPreOrder);
    }

    public BooleanFilter isPreOrder() {
        if (isPreOrder == null) {
            setIsPreOrder(new BooleanFilter());
        }
        return isPreOrder;
    }

    public void setIsPreOrder(BooleanFilter isPreOrder) {
        this.isPreOrder = isPreOrder;
    }

    public InstantFilter getScheduledFor() {
        return scheduledFor;
    }

    public Optional<InstantFilter> optionalScheduledFor() {
        return Optional.ofNullable(scheduledFor);
    }

    public InstantFilter scheduledFor() {
        if (scheduledFor == null) {
            setScheduledFor(new InstantFilter());
        }
        return scheduledFor;
    }

    public void setScheduledFor(InstantFilter scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public BigDecimalFilter getSubtotal() {
        return subtotal;
    }

    public Optional<BigDecimalFilter> optionalSubtotal() {
        return Optional.ofNullable(subtotal);
    }

    public BigDecimalFilter subtotal() {
        if (subtotal == null) {
            setSubtotal(new BigDecimalFilter());
        }
        return subtotal;
    }

    public void setSubtotal(BigDecimalFilter subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public Optional<BigDecimalFilter> optionalDiscountAmount() {
        return Optional.ofNullable(discountAmount);
    }

    public BigDecimalFilter discountAmount() {
        if (discountAmount == null) {
            setDiscountAmount(new BigDecimalFilter());
        }
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimalFilter getTaxAmount() {
        return taxAmount;
    }

    public Optional<BigDecimalFilter> optionalTaxAmount() {
        return Optional.ofNullable(taxAmount);
    }

    public BigDecimalFilter taxAmount() {
        if (taxAmount == null) {
            setTaxAmount(new BigDecimalFilter());
        }
        return taxAmount;
    }

    public void setTaxAmount(BigDecimalFilter taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InstantFilter getEstimatedReadyTime() {
        return estimatedReadyTime;
    }

    public Optional<InstantFilter> optionalEstimatedReadyTime() {
        return Optional.ofNullable(estimatedReadyTime);
    }

    public InstantFilter estimatedReadyTime() {
        if (estimatedReadyTime == null) {
            setEstimatedReadyTime(new InstantFilter());
        }
        return estimatedReadyTime;
    }

    public void setEstimatedReadyTime(InstantFilter estimatedReadyTime) {
        this.estimatedReadyTime = estimatedReadyTime;
    }

    public InstantFilter getConfirmedAt() {
        return confirmedAt;
    }

    public Optional<InstantFilter> optionalConfirmedAt() {
        return Optional.ofNullable(confirmedAt);
    }

    public InstantFilter confirmedAt() {
        if (confirmedAt == null) {
            setConfirmedAt(new InstantFilter());
        }
        return confirmedAt;
    }

    public void setConfirmedAt(InstantFilter confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public InstantFilter getCompletedAt() {
        return completedAt;
    }

    public Optional<InstantFilter> optionalCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public InstantFilter completedAt() {
        if (completedAt == null) {
            setCompletedAt(new InstantFilter());
        }
        return completedAt;
    }

    public void setCompletedAt(InstantFilter completedAt) {
        this.completedAt = completedAt;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getItemsId() {
        return itemsId;
    }

    public Optional<LongFilter> optionalItemsId() {
        return Optional.ofNullable(itemsId);
    }

    public LongFilter itemsId() {
        if (itemsId == null) {
            setItemsId(new LongFilter());
        }
        return itemsId;
    }

    public void setItemsId(LongFilter itemsId) {
        this.itemsId = itemsId;
    }

    public LongFilter getPaymentsId() {
        return paymentsId;
    }

    public Optional<LongFilter> optionalPaymentsId() {
        return Optional.ofNullable(paymentsId);
    }

    public LongFilter paymentsId() {
        if (paymentsId == null) {
            setPaymentsId(new LongFilter());
        }
        return paymentsId;
    }

    public void setPaymentsId(LongFilter paymentsId) {
        this.paymentsId = paymentsId;
    }

    public LongFilter getLocationId() {
        return locationId;
    }

    public Optional<LongFilter> optionalLocationId() {
        return Optional.ofNullable(locationId);
    }

    public LongFilter locationId() {
        if (locationId == null) {
            setLocationId(new LongFilter());
        }
        return locationId;
    }

    public void setLocationId(LongFilter locationId) {
        this.locationId = locationId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public Optional<LongFilter> optionalClientId() {
        return Optional.ofNullable(clientId);
    }

    public LongFilter clientId() {
        if (clientId == null) {
            setClientId(new LongFilter());
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public LongFilter getAssignedWaiterId() {
        return assignedWaiterId;
    }

    public Optional<LongFilter> optionalAssignedWaiterId() {
        return Optional.ofNullable(assignedWaiterId);
    }

    public LongFilter assignedWaiterId() {
        if (assignedWaiterId == null) {
            setAssignedWaiterId(new LongFilter());
        }
        return assignedWaiterId;
    }

    public void setAssignedWaiterId(LongFilter assignedWaiterId) {
        this.assignedWaiterId = assignedWaiterId;
    }

    public LongFilter getTableId() {
        return tableId;
    }

    public Optional<LongFilter> optionalTableId() {
        return Optional.ofNullable(tableId);
    }

    public LongFilter tableId() {
        if (tableId == null) {
            setTableId(new LongFilter());
        }
        return tableId;
    }

    public void setTableId(LongFilter tableId) {
        this.tableId = tableId;
    }

    public LongFilter getPromotionId() {
        return promotionId;
    }

    public Optional<LongFilter> optionalPromotionId() {
        return Optional.ofNullable(promotionId);
    }

    public LongFilter promotionId() {
        if (promotionId == null) {
            setPromotionId(new LongFilter());
        }
        return promotionId;
    }

    public void setPromotionId(LongFilter promotionId) {
        this.promotionId = promotionId;
    }

    public LongFilter getReservationId() {
        return reservationId;
    }

    public Optional<LongFilter> optionalReservationId() {
        return Optional.ofNullable(reservationId);
    }

    public LongFilter reservationId() {
        if (reservationId == null) {
            setReservationId(new LongFilter());
        }
        return reservationId;
    }

    public void setReservationId(LongFilter reservationId) {
        this.reservationId = reservationId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RestaurantOrderCriteria that = (RestaurantOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderCode, that.orderCode) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isPreOrder, that.isPreOrder) &&
            Objects.equals(scheduledFor, that.scheduledFor) &&
            Objects.equals(subtotal, that.subtotal) &&
            Objects.equals(discountAmount, that.discountAmount) &&
            Objects.equals(taxAmount, that.taxAmount) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(estimatedReadyTime, that.estimatedReadyTime) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(completedAt, that.completedAt) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(itemsId, that.itemsId) &&
            Objects.equals(paymentsId, that.paymentsId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(assignedWaiterId, that.assignedWaiterId) &&
            Objects.equals(tableId, that.tableId) &&
            Objects.equals(promotionId, that.promotionId) &&
            Objects.equals(reservationId, that.reservationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            orderCode,
            status,
            isPreOrder,
            scheduledFor,
            subtotal,
            discountAmount,
            taxAmount,
            totalAmount,
            estimatedReadyTime,
            confirmedAt,
            completedAt,
            createdAt,
            updatedAt,
            itemsId,
            paymentsId,
            locationId,
            clientId,
            assignedWaiterId,
            tableId,
            promotionId,
            reservationId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrderCode().map(f -> "orderCode=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsPreOrder().map(f -> "isPreOrder=" + f + ", ").orElse("") +
            optionalScheduledFor().map(f -> "scheduledFor=" + f + ", ").orElse("") +
            optionalSubtotal().map(f -> "subtotal=" + f + ", ").orElse("") +
            optionalDiscountAmount().map(f -> "discountAmount=" + f + ", ").orElse("") +
            optionalTaxAmount().map(f -> "taxAmount=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalEstimatedReadyTime().map(f -> "estimatedReadyTime=" + f + ", ").orElse("") +
            optionalConfirmedAt().map(f -> "confirmedAt=" + f + ", ").orElse("") +
            optionalCompletedAt().map(f -> "completedAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
            optionalPaymentsId().map(f -> "paymentsId=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalAssignedWaiterId().map(f -> "assignedWaiterId=" + f + ", ").orElse("") +
            optionalTableId().map(f -> "tableId=" + f + ", ").orElse("") +
            optionalPromotionId().map(f -> "promotionId=" + f + ", ").orElse("") +
            optionalReservationId().map(f -> "reservationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
