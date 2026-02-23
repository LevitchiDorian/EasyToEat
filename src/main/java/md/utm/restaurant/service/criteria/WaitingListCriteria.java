package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.WaitingList} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.WaitingListResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /waiting-lists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WaitingListCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter requestedDate;

    private StringFilter requestedTime;

    private IntegerFilter partySize;

    private StringFilter notes;

    private BooleanFilter isNotified;

    private InstantFilter expiresAt;

    private InstantFilter createdAt;

    private LongFilter locationId;

    private LongFilter clientId;

    private LongFilter roomId;

    private Boolean distinct;

    public WaitingListCriteria() {}

    public WaitingListCriteria(WaitingListCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.requestedDate = other.optionalRequestedDate().map(LocalDateFilter::copy).orElse(null);
        this.requestedTime = other.optionalRequestedTime().map(StringFilter::copy).orElse(null);
        this.partySize = other.optionalPartySize().map(IntegerFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.isNotified = other.optionalIsNotified().map(BooleanFilter::copy).orElse(null);
        this.expiresAt = other.optionalExpiresAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.roomId = other.optionalRoomId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WaitingListCriteria copy() {
        return new WaitingListCriteria(this);
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

    public LocalDateFilter getRequestedDate() {
        return requestedDate;
    }

    public Optional<LocalDateFilter> optionalRequestedDate() {
        return Optional.ofNullable(requestedDate);
    }

    public LocalDateFilter requestedDate() {
        if (requestedDate == null) {
            setRequestedDate(new LocalDateFilter());
        }
        return requestedDate;
    }

    public void setRequestedDate(LocalDateFilter requestedDate) {
        this.requestedDate = requestedDate;
    }

    public StringFilter getRequestedTime() {
        return requestedTime;
    }

    public Optional<StringFilter> optionalRequestedTime() {
        return Optional.ofNullable(requestedTime);
    }

    public StringFilter requestedTime() {
        if (requestedTime == null) {
            setRequestedTime(new StringFilter());
        }
        return requestedTime;
    }

    public void setRequestedTime(StringFilter requestedTime) {
        this.requestedTime = requestedTime;
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

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public BooleanFilter getIsNotified() {
        return isNotified;
    }

    public Optional<BooleanFilter> optionalIsNotified() {
        return Optional.ofNullable(isNotified);
    }

    public BooleanFilter isNotified() {
        if (isNotified == null) {
            setIsNotified(new BooleanFilter());
        }
        return isNotified;
    }

    public void setIsNotified(BooleanFilter isNotified) {
        this.isNotified = isNotified;
    }

    public InstantFilter getExpiresAt() {
        return expiresAt;
    }

    public Optional<InstantFilter> optionalExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public InstantFilter expiresAt() {
        if (expiresAt == null) {
            setExpiresAt(new InstantFilter());
        }
        return expiresAt;
    }

    public void setExpiresAt(InstantFilter expiresAt) {
        this.expiresAt = expiresAt;
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
        final WaitingListCriteria that = (WaitingListCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(requestedDate, that.requestedDate) &&
            Objects.equals(requestedTime, that.requestedTime) &&
            Objects.equals(partySize, that.partySize) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(isNotified, that.isNotified) &&
            Objects.equals(expiresAt, that.expiresAt) &&
            Objects.equals(createdAt, that.createdAt) &&
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
            requestedDate,
            requestedTime,
            partySize,
            notes,
            isNotified,
            expiresAt,
            createdAt,
            locationId,
            clientId,
            roomId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WaitingListCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRequestedDate().map(f -> "requestedDate=" + f + ", ").orElse("") +
            optionalRequestedTime().map(f -> "requestedTime=" + f + ", ").orElse("") +
            optionalPartySize().map(f -> "partySize=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalIsNotified().map(f -> "isNotified=" + f + ", ").orElse("") +
            optionalExpiresAt().map(f -> "expiresAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalRoomId().map(f -> "roomId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
