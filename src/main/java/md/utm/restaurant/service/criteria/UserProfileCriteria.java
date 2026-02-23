package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.domain.enumeration.UserRole;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.UserProfile} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.UserProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfileCriteria implements Serializable, Criteria {

    /**
     * Class for filtering UserRole
     */
    public static class UserRoleFilter extends Filter<UserRole> {

        public UserRoleFilter() {}

        public UserRoleFilter(UserRoleFilter filter) {
            super(filter);
        }

        @Override
        public UserRoleFilter copy() {
            return new UserRoleFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phone;

    private StringFilter avatarUrl;

    private UserRoleFilter role;

    private StringFilter preferredLanguage;

    private BooleanFilter receiveEmailNotifications;

    private BooleanFilter receivePushNotifications;

    private IntegerFilter loyaltyPoints;

    private InstantFilter createdAt;

    private LongFilter userId;

    private LongFilter locationId;

    private Boolean distinct;

    public UserProfileCriteria() {}

    public UserProfileCriteria(UserProfileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.avatarUrl = other.optionalAvatarUrl().map(StringFilter::copy).orElse(null);
        this.role = other.optionalRole().map(UserRoleFilter::copy).orElse(null);
        this.preferredLanguage = other.optionalPreferredLanguage().map(StringFilter::copy).orElse(null);
        this.receiveEmailNotifications = other.optionalReceiveEmailNotifications().map(BooleanFilter::copy).orElse(null);
        this.receivePushNotifications = other.optionalReceivePushNotifications().map(BooleanFilter::copy).orElse(null);
        this.loyaltyPoints = other.optionalLoyaltyPoints().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UserProfileCriteria copy() {
        return new UserProfileCriteria(this);
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

    public StringFilter getAvatarUrl() {
        return avatarUrl;
    }

    public Optional<StringFilter> optionalAvatarUrl() {
        return Optional.ofNullable(avatarUrl);
    }

    public StringFilter avatarUrl() {
        if (avatarUrl == null) {
            setAvatarUrl(new StringFilter());
        }
        return avatarUrl;
    }

    public void setAvatarUrl(StringFilter avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRoleFilter getRole() {
        return role;
    }

    public Optional<UserRoleFilter> optionalRole() {
        return Optional.ofNullable(role);
    }

    public UserRoleFilter role() {
        if (role == null) {
            setRole(new UserRoleFilter());
        }
        return role;
    }

    public void setRole(UserRoleFilter role) {
        this.role = role;
    }

    public StringFilter getPreferredLanguage() {
        return preferredLanguage;
    }

    public Optional<StringFilter> optionalPreferredLanguage() {
        return Optional.ofNullable(preferredLanguage);
    }

    public StringFilter preferredLanguage() {
        if (preferredLanguage == null) {
            setPreferredLanguage(new StringFilter());
        }
        return preferredLanguage;
    }

    public void setPreferredLanguage(StringFilter preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public BooleanFilter getReceiveEmailNotifications() {
        return receiveEmailNotifications;
    }

    public Optional<BooleanFilter> optionalReceiveEmailNotifications() {
        return Optional.ofNullable(receiveEmailNotifications);
    }

    public BooleanFilter receiveEmailNotifications() {
        if (receiveEmailNotifications == null) {
            setReceiveEmailNotifications(new BooleanFilter());
        }
        return receiveEmailNotifications;
    }

    public void setReceiveEmailNotifications(BooleanFilter receiveEmailNotifications) {
        this.receiveEmailNotifications = receiveEmailNotifications;
    }

    public BooleanFilter getReceivePushNotifications() {
        return receivePushNotifications;
    }

    public Optional<BooleanFilter> optionalReceivePushNotifications() {
        return Optional.ofNullable(receivePushNotifications);
    }

    public BooleanFilter receivePushNotifications() {
        if (receivePushNotifications == null) {
            setReceivePushNotifications(new BooleanFilter());
        }
        return receivePushNotifications;
    }

    public void setReceivePushNotifications(BooleanFilter receivePushNotifications) {
        this.receivePushNotifications = receivePushNotifications;
    }

    public IntegerFilter getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Optional<IntegerFilter> optionalLoyaltyPoints() {
        return Optional.ofNullable(loyaltyPoints);
    }

    public IntegerFilter loyaltyPoints() {
        if (loyaltyPoints == null) {
            setLoyaltyPoints(new IntegerFilter());
        }
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(IntegerFilter loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
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

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final UserProfileCriteria that = (UserProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(avatarUrl, that.avatarUrl) &&
            Objects.equals(role, that.role) &&
            Objects.equals(preferredLanguage, that.preferredLanguage) &&
            Objects.equals(receiveEmailNotifications, that.receiveEmailNotifications) &&
            Objects.equals(receivePushNotifications, that.receivePushNotifications) &&
            Objects.equals(loyaltyPoints, that.loyaltyPoints) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            phone,
            avatarUrl,
            role,
            preferredLanguage,
            receiveEmailNotifications,
            receivePushNotifications,
            loyaltyPoints,
            createdAt,
            userId,
            locationId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalAvatarUrl().map(f -> "avatarUrl=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalPreferredLanguage().map(f -> "preferredLanguage=" + f + ", ").orElse("") +
            optionalReceiveEmailNotifications().map(f -> "receiveEmailNotifications=" + f + ", ").orElse("") +
            optionalReceivePushNotifications().map(f -> "receivePushNotifications=" + f + ", ").orElse("") +
            optionalLoyaltyPoints().map(f -> "loyaltyPoints=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
