package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.Review} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.ReviewResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reviews?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReviewCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter overallRating;

    private IntegerFilter foodRating;

    private IntegerFilter serviceRating;

    private IntegerFilter ambienceRating;

    private BooleanFilter isApproved;

    private BooleanFilter isAnonymous;

    private InstantFilter createdAt;

    private LongFilter locationId;

    private LongFilter reservationId;

    private LongFilter clientId;

    private Boolean distinct;

    public ReviewCriteria() {}

    public ReviewCriteria(ReviewCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.overallRating = other.optionalOverallRating().map(IntegerFilter::copy).orElse(null);
        this.foodRating = other.optionalFoodRating().map(IntegerFilter::copy).orElse(null);
        this.serviceRating = other.optionalServiceRating().map(IntegerFilter::copy).orElse(null);
        this.ambienceRating = other.optionalAmbienceRating().map(IntegerFilter::copy).orElse(null);
        this.isApproved = other.optionalIsApproved().map(BooleanFilter::copy).orElse(null);
        this.isAnonymous = other.optionalIsAnonymous().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.reservationId = other.optionalReservationId().map(LongFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReviewCriteria copy() {
        return new ReviewCriteria(this);
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

    public IntegerFilter getOverallRating() {
        return overallRating;
    }

    public Optional<IntegerFilter> optionalOverallRating() {
        return Optional.ofNullable(overallRating);
    }

    public IntegerFilter overallRating() {
        if (overallRating == null) {
            setOverallRating(new IntegerFilter());
        }
        return overallRating;
    }

    public void setOverallRating(IntegerFilter overallRating) {
        this.overallRating = overallRating;
    }

    public IntegerFilter getFoodRating() {
        return foodRating;
    }

    public Optional<IntegerFilter> optionalFoodRating() {
        return Optional.ofNullable(foodRating);
    }

    public IntegerFilter foodRating() {
        if (foodRating == null) {
            setFoodRating(new IntegerFilter());
        }
        return foodRating;
    }

    public void setFoodRating(IntegerFilter foodRating) {
        this.foodRating = foodRating;
    }

    public IntegerFilter getServiceRating() {
        return serviceRating;
    }

    public Optional<IntegerFilter> optionalServiceRating() {
        return Optional.ofNullable(serviceRating);
    }

    public IntegerFilter serviceRating() {
        if (serviceRating == null) {
            setServiceRating(new IntegerFilter());
        }
        return serviceRating;
    }

    public void setServiceRating(IntegerFilter serviceRating) {
        this.serviceRating = serviceRating;
    }

    public IntegerFilter getAmbienceRating() {
        return ambienceRating;
    }

    public Optional<IntegerFilter> optionalAmbienceRating() {
        return Optional.ofNullable(ambienceRating);
    }

    public IntegerFilter ambienceRating() {
        if (ambienceRating == null) {
            setAmbienceRating(new IntegerFilter());
        }
        return ambienceRating;
    }

    public void setAmbienceRating(IntegerFilter ambienceRating) {
        this.ambienceRating = ambienceRating;
    }

    public BooleanFilter getIsApproved() {
        return isApproved;
    }

    public Optional<BooleanFilter> optionalIsApproved() {
        return Optional.ofNullable(isApproved);
    }

    public BooleanFilter isApproved() {
        if (isApproved == null) {
            setIsApproved(new BooleanFilter());
        }
        return isApproved;
    }

    public void setIsApproved(BooleanFilter isApproved) {
        this.isApproved = isApproved;
    }

    public BooleanFilter getIsAnonymous() {
        return isAnonymous;
    }

    public Optional<BooleanFilter> optionalIsAnonymous() {
        return Optional.ofNullable(isAnonymous);
    }

    public BooleanFilter isAnonymous() {
        if (isAnonymous == null) {
            setIsAnonymous(new BooleanFilter());
        }
        return isAnonymous;
    }

    public void setIsAnonymous(BooleanFilter isAnonymous) {
        this.isAnonymous = isAnonymous;
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
        final ReviewCriteria that = (ReviewCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(overallRating, that.overallRating) &&
            Objects.equals(foodRating, that.foodRating) &&
            Objects.equals(serviceRating, that.serviceRating) &&
            Objects.equals(ambienceRating, that.ambienceRating) &&
            Objects.equals(isApproved, that.isApproved) &&
            Objects.equals(isAnonymous, that.isAnonymous) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(reservationId, that.reservationId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            overallRating,
            foodRating,
            serviceRating,
            ambienceRating,
            isApproved,
            isAnonymous,
            createdAt,
            locationId,
            reservationId,
            clientId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReviewCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOverallRating().map(f -> "overallRating=" + f + ", ").orElse("") +
            optionalFoodRating().map(f -> "foodRating=" + f + ", ").orElse("") +
            optionalServiceRating().map(f -> "serviceRating=" + f + ", ").orElse("") +
            optionalAmbienceRating().map(f -> "ambienceRating=" + f + ", ").orElse("") +
            optionalIsApproved().map(f -> "isApproved=" + f + ", ").orElse("") +
            optionalIsAnonymous().map(f -> "isAnonymous=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalReservationId().map(f -> "reservationId=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
