package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.domain.enumeration.ReservationStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.Reservation} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.ReservationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reservations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReservationStatus
     */
    public static class ReservationStatusFilter extends Filter<ReservationStatus> {

        public ReservationStatusFilter() {}

        public ReservationStatusFilter(ReservationStatusFilter filter) {
            super(filter);
        }

        @Override
        public ReservationStatusFilter copy() {
            return new ReservationStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reservationCode;

    private LocalDateFilter reservationDate;

    private StringFilter startTime;

    private StringFilter endTime;

    private IntegerFilter partySize;

    private ReservationStatusFilter status;

    private InstantFilter reminderSentAt;

    private InstantFilter confirmedAt;

    private InstantFilter cancelledAt;

    private StringFilter cancellationReason;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter tableAssignmentsId;

    private LongFilter ordersId;

    private LongFilter locationId;

    private LongFilter clientId;

    private LongFilter roomId;

    private Boolean distinct;

    public ReservationCriteria() {}

    public ReservationCriteria(ReservationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reservationCode = other.optionalReservationCode().map(StringFilter::copy).orElse(null);
        this.reservationDate = other.optionalReservationDate().map(LocalDateFilter::copy).orElse(null);
        this.startTime = other.optionalStartTime().map(StringFilter::copy).orElse(null);
        this.endTime = other.optionalEndTime().map(StringFilter::copy).orElse(null);
        this.partySize = other.optionalPartySize().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ReservationStatusFilter::copy).orElse(null);
        this.reminderSentAt = other.optionalReminderSentAt().map(InstantFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.cancelledAt = other.optionalCancelledAt().map(InstantFilter::copy).orElse(null);
        this.cancellationReason = other.optionalCancellationReason().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.tableAssignmentsId = other.optionalTableAssignmentsId().map(LongFilter::copy).orElse(null);
        this.ordersId = other.optionalOrdersId().map(LongFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.roomId = other.optionalRoomId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReservationCriteria copy() {
        return new ReservationCriteria(this);
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

    public StringFilter getReservationCode() {
        return reservationCode;
    }

    public Optional<StringFilter> optionalReservationCode() {
        return Optional.ofNullable(reservationCode);
    }

    public StringFilter reservationCode() {
        if (reservationCode == null) {
            setReservationCode(new StringFilter());
        }
        return reservationCode;
    }

    public void setReservationCode(StringFilter reservationCode) {
        this.reservationCode = reservationCode;
    }

    public LocalDateFilter getReservationDate() {
        return reservationDate;
    }

    public Optional<LocalDateFilter> optionalReservationDate() {
        return Optional.ofNullable(reservationDate);
    }

    public LocalDateFilter reservationDate() {
        if (reservationDate == null) {
            setReservationDate(new LocalDateFilter());
        }
        return reservationDate;
    }

    public void setReservationDate(LocalDateFilter reservationDate) {
        this.reservationDate = reservationDate;
    }

    public StringFilter getStartTime() {
        return startTime;
    }

    public Optional<StringFilter> optionalStartTime() {
        return Optional.ofNullable(startTime);
    }

    public StringFilter startTime() {
        if (startTime == null) {
            setStartTime(new StringFilter());
        }
        return startTime;
    }

    public void setStartTime(StringFilter startTime) {
        this.startTime = startTime;
    }

    public StringFilter getEndTime() {
        return endTime;
    }

    public Optional<StringFilter> optionalEndTime() {
        return Optional.ofNullable(endTime);
    }

    public StringFilter endTime() {
        if (endTime == null) {
            setEndTime(new StringFilter());
        }
        return endTime;
    }

    public void setEndTime(StringFilter endTime) {
        this.endTime = endTime;
    }

    public IntegerFilter getPartySize() {
        return partySize;
    }

    public Optional<IntegerFilter> optionalPartySize() {
        return Optional.ofNullable(partySize);
    }

    public IntegerFilter partySize() {
        if (partySize == null) {
            setPartySize(new IntegerFilter());
        }
        return partySize;
    }

    public void setPartySize(IntegerFilter partySize) {
        this.partySize = partySize;
    }

    public ReservationStatusFilter getStatus() {
        return status;
    }

    public Optional<ReservationStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ReservationStatusFilter status() {
        if (status == null) {
            setStatus(new ReservationStatusFilter());
        }
        return status;
    }

    public void setStatus(ReservationStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getReminderSentAt() {
        return reminderSentAt;
    }

    public Optional<InstantFilter> optionalReminderSentAt() {
        return Optional.ofNullable(reminderSentAt);
    }

    public InstantFilter reminderSentAt() {
        if (reminderSentAt == null) {
            setReminderSentAt(new InstantFilter());
        }
        return reminderSentAt;
    }

    public void setReminderSentAt(InstantFilter reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
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

    public InstantFilter getCancelledAt() {
        return cancelledAt;
    }

    public Optional<InstantFilter> optionalCancelledAt() {
        return Optional.ofNullable(cancelledAt);
    }

    public InstantFilter cancelledAt() {
        if (cancelledAt == null) {
            setCancelledAt(new InstantFilter());
        }
        return cancelledAt;
    }

    public void setCancelledAt(InstantFilter cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public StringFilter getCancellationReason() {
        return cancellationReason;
    }

    public Optional<StringFilter> optionalCancellationReason() {
        return Optional.ofNullable(cancellationReason);
    }

    public StringFilter cancellationReason() {
        if (cancellationReason == null) {
            setCancellationReason(new StringFilter());
        }
        return cancellationReason;
    }

    public void setCancellationReason(StringFilter cancellationReason) {
        this.cancellationReason = cancellationReason;
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

    public LongFilter getTableAssignmentsId() {
        return tableAssignmentsId;
    }

    public Optional<LongFilter> optionalTableAssignmentsId() {
        return Optional.ofNullable(tableAssignmentsId);
    }

    public LongFilter tableAssignmentsId() {
        if (tableAssignmentsId == null) {
            setTableAssignmentsId(new LongFilter());
        }
        return tableAssignmentsId;
    }

    public void setTableAssignmentsId(LongFilter tableAssignmentsId) {
        this.tableAssignmentsId = tableAssignmentsId;
    }

    public LongFilter getOrdersId() {
        return ordersId;
    }

    public Optional<LongFilter> optionalOrdersId() {
        return Optional.ofNullable(ordersId);
    }

    public LongFilter ordersId() {
        if (ordersId == null) {
            setOrdersId(new LongFilter());
        }
        return ordersId;
    }

    public void setOrdersId(LongFilter ordersId) {
        this.ordersId = ordersId;
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

    public LongFilter getRoomId() {
        return roomId;
    }

    public Optional<LongFilter> optionalRoomId() {
        return Optional.ofNullable(roomId);
    }

    public LongFilter roomId() {
        if (roomId == null) {
            setRoomId(new LongFilter());
        }
        return roomId;
    }

    public void setRoomId(LongFilter roomId) {
        this.roomId = roomId;
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
        final ReservationCriteria that = (ReservationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reservationCode, that.reservationCode) &&
            Objects.equals(reservationDate, that.reservationDate) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(partySize, that.partySize) &&
            Objects.equals(status, that.status) &&
            Objects.equals(reminderSentAt, that.reminderSentAt) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(cancelledAt, that.cancelledAt) &&
            Objects.equals(cancellationReason, that.cancellationReason) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(tableAssignmentsId, that.tableAssignmentsId) &&
            Objects.equals(ordersId, that.ordersId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(roomId, that.roomId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reservationCode,
            reservationDate,
            startTime,
            endTime,
            partySize,
            status,
            reminderSentAt,
            confirmedAt,
            cancelledAt,
            cancellationReason,
            createdAt,
            updatedAt,
            tableAssignmentsId,
            ordersId,
            locationId,
            clientId,
            roomId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReservationCode().map(f -> "reservationCode=" + f + ", ").orElse("") +
            optionalReservationDate().map(f -> "reservationDate=" + f + ", ").orElse("") +
            optionalStartTime().map(f -> "startTime=" + f + ", ").orElse("") +
            optionalEndTime().map(f -> "endTime=" + f + ", ").orElse("") +
            optionalPartySize().map(f -> "partySize=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalReminderSentAt().map(f -> "reminderSentAt=" + f + ", ").orElse("") +
            optionalConfirmedAt().map(f -> "confirmedAt=" + f + ", ").orElse("") +
            optionalCancelledAt().map(f -> "cancelledAt=" + f + ", ").orElse("") +
            optionalCancellationReason().map(f -> "cancellationReason=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalTableAssignmentsId().map(f -> "tableAssignmentsId=" + f + ", ").orElse("") +
            optionalOrdersId().map(f -> "ordersId=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalRoomId().map(f -> "roomId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
