package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.Location} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.LocationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /locations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter address;

    private StringFilter city;

    private StringFilter phone;

    private StringFilter email;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private IntegerFilter reservationDurationOverride;

    private IntegerFilter maxAdvanceBookingDaysOverride;

    private IntegerFilter cancellationDeadlineOverride;

    private BooleanFilter isActive;

    private InstantFilter createdAt;

    private LongFilter hoursId;

    private LongFilter roomsId;

    private LongFilter localPromotionsId;

    private LongFilter menuOverridesId;

    private LongFilter brandId;

    private Boolean distinct;

    public LocationCriteria() {}

    public LocationCriteria(LocationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(DoubleFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(DoubleFilter::copy).orElse(null);
        this.reservationDurationOverride = other.optionalReservationDurationOverride().map(IntegerFilter::copy).orElse(null);
        this.maxAdvanceBookingDaysOverride = other.optionalMaxAdvanceBookingDaysOverride().map(IntegerFilter::copy).orElse(null);
        this.cancellationDeadlineOverride = other.optionalCancellationDeadlineOverride().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.hoursId = other.optionalHoursId().map(LongFilter::copy).orElse(null);
        this.roomsId = other.optionalRoomsId().map(LongFilter::copy).orElse(null);
        this.localPromotionsId = other.optionalLocalPromotionsId().map(LongFilter::copy).orElse(null);
        this.menuOverridesId = other.optionalMenuOverridesId().map(LongFilter::copy).orElse(null);
        this.brandId = other.optionalBrandId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LocationCriteria copy() {
        return new LocationCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public Optional<DoubleFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            setLatitude(new DoubleFilter());
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public Optional<DoubleFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            setLongitude(new DoubleFilter());
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public IntegerFilter getReservationDurationOverride() {
        return reservationDurationOverride;
    }

    public Optional<IntegerFilter> optionalReservationDurationOverride() {
        return Optional.ofNullable(reservationDurationOverride);
    }

    public IntegerFilter reservationDurationOverride() {
        if (reservationDurationOverride == null) {
            setReservationDurationOverride(new IntegerFilter());
        }
        return reservationDurationOverride;
    }

    public void setReservationDurationOverride(IntegerFilter reservationDurationOverride) {
        this.reservationDurationOverride = reservationDurationOverride;
    }

    public IntegerFilter getMaxAdvanceBookingDaysOverride() {
        return maxAdvanceBookingDaysOverride;
    }

    public Optional<IntegerFilter> optionalMaxAdvanceBookingDaysOverride() {
        return Optional.ofNullable(maxAdvanceBookingDaysOverride);
    }

    public IntegerFilter maxAdvanceBookingDaysOverride() {
        if (maxAdvanceBookingDaysOverride == null) {
            setMaxAdvanceBookingDaysOverride(new IntegerFilter());
        }
        return maxAdvanceBookingDaysOverride;
    }

    public void setMaxAdvanceBookingDaysOverride(IntegerFilter maxAdvanceBookingDaysOverride) {
        this.maxAdvanceBookingDaysOverride = maxAdvanceBookingDaysOverride;
    }

    public IntegerFilter getCancellationDeadlineOverride() {
        return cancellationDeadlineOverride;
    }

    public Optional<IntegerFilter> optionalCancellationDeadlineOverride() {
        return Optional.ofNullable(cancellationDeadlineOverride);
    }

    public IntegerFilter cancellationDeadlineOverride() {
        if (cancellationDeadlineOverride == null) {
            setCancellationDeadlineOverride(new IntegerFilter());
        }
        return cancellationDeadlineOverride;
    }

    public void setCancellationDeadlineOverride(IntegerFilter cancellationDeadlineOverride) {
        this.cancellationDeadlineOverride = cancellationDeadlineOverride;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
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

    public LongFilter getHoursId() {
        return hoursId;
    }

    public Optional<LongFilter> optionalHoursId() {
        return Optional.ofNullable(hoursId);
    }

    public LongFilter hoursId() {
        if (hoursId == null) {
            setHoursId(new LongFilter());
        }
        return hoursId;
    }

    public void setHoursId(LongFilter hoursId) {
        this.hoursId = hoursId;
    }

    public LongFilter getRoomsId() {
        return roomsId;
    }

    public Optional<LongFilter> optionalRoomsId() {
        return Optional.ofNullable(roomsId);
    }

    public LongFilter roomsId() {
        if (roomsId == null) {
            setRoomsId(new LongFilter());
        }
        return roomsId;
    }

    public void setRoomsId(LongFilter roomsId) {
        this.roomsId = roomsId;
    }

    public LongFilter getLocalPromotionsId() {
        return localPromotionsId;
    }

    public Optional<LongFilter> optionalLocalPromotionsId() {
        return Optional.ofNullable(localPromotionsId);
    }

    public LongFilter localPromotionsId() {
        if (localPromotionsId == null) {
            setLocalPromotionsId(new LongFilter());
        }
        return localPromotionsId;
    }

    public void setLocalPromotionsId(LongFilter localPromotionsId) {
        this.localPromotionsId = localPromotionsId;
    }

    public LongFilter getMenuOverridesId() {
        return menuOverridesId;
    }

    public Optional<LongFilter> optionalMenuOverridesId() {
        return Optional.ofNullable(menuOverridesId);
    }

    public LongFilter menuOverridesId() {
        if (menuOverridesId == null) {
            setMenuOverridesId(new LongFilter());
        }
        return menuOverridesId;
    }

    public void setMenuOverridesId(LongFilter menuOverridesId) {
        this.menuOverridesId = menuOverridesId;
    }

    public LongFilter getBrandId() {
        return brandId;
    }

    public Optional<LongFilter> optionalBrandId() {
        return Optional.ofNullable(brandId);
    }

    public LongFilter brandId() {
        if (brandId == null) {
            setBrandId(new LongFilter());
        }
        return brandId;
    }

    public void setBrandId(LongFilter brandId) {
        this.brandId = brandId;
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
        final LocationCriteria that = (LocationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(address, that.address) &&
            Objects.equals(city, that.city) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(email, that.email) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(reservationDurationOverride, that.reservationDurationOverride) &&
            Objects.equals(maxAdvanceBookingDaysOverride, that.maxAdvanceBookingDaysOverride) &&
            Objects.equals(cancellationDeadlineOverride, that.cancellationDeadlineOverride) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(hoursId, that.hoursId) &&
            Objects.equals(roomsId, that.roomsId) &&
            Objects.equals(localPromotionsId, that.localPromotionsId) &&
            Objects.equals(menuOverridesId, that.menuOverridesId) &&
            Objects.equals(brandId, that.brandId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            address,
            city,
            phone,
            email,
            latitude,
            longitude,
            reservationDurationOverride,
            maxAdvanceBookingDaysOverride,
            cancellationDeadlineOverride,
            isActive,
            createdAt,
            hoursId,
            roomsId,
            localPromotionsId,
            menuOverridesId,
            brandId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalReservationDurationOverride().map(f -> "reservationDurationOverride=" + f + ", ").orElse("") +
            optionalMaxAdvanceBookingDaysOverride().map(f -> "maxAdvanceBookingDaysOverride=" + f + ", ").orElse("") +
            optionalCancellationDeadlineOverride().map(f -> "cancellationDeadlineOverride=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalHoursId().map(f -> "hoursId=" + f + ", ").orElse("") +
            optionalRoomsId().map(f -> "roomsId=" + f + ", ").orElse("") +
            optionalLocalPromotionsId().map(f -> "localPromotionsId=" + f + ", ").orElse("") +
            optionalMenuOverridesId().map(f -> "menuOverridesId=" + f + ", ").orElse("") +
            optionalBrandId().map(f -> "brandId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
