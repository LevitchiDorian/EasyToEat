package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.Brand} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.BrandResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /brands?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BrandCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter logoUrl;

    private StringFilter coverImageUrl;

    private StringFilter primaryColor;

    private StringFilter secondaryColor;

    private StringFilter website;

    private StringFilter contactEmail;

    private StringFilter contactPhone;

    private IntegerFilter defaultReservationDuration;

    private IntegerFilter maxAdvanceBookingDays;

    private IntegerFilter cancellationDeadlineHours;

    private BooleanFilter isActive;

    private InstantFilter createdAt;

    private LongFilter locationsId;

    private LongFilter categoriesId;

    private LongFilter promotionsId;

    private Boolean distinct;

    public BrandCriteria() {}

    public BrandCriteria(BrandCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.logoUrl = other.optionalLogoUrl().map(StringFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.primaryColor = other.optionalPrimaryColor().map(StringFilter::copy).orElse(null);
        this.secondaryColor = other.optionalSecondaryColor().map(StringFilter::copy).orElse(null);
        this.website = other.optionalWebsite().map(StringFilter::copy).orElse(null);
        this.contactEmail = other.optionalContactEmail().map(StringFilter::copy).orElse(null);
        this.contactPhone = other.optionalContactPhone().map(StringFilter::copy).orElse(null);
        this.defaultReservationDuration = other.optionalDefaultReservationDuration().map(IntegerFilter::copy).orElse(null);
        this.maxAdvanceBookingDays = other.optionalMaxAdvanceBookingDays().map(IntegerFilter::copy).orElse(null);
        this.cancellationDeadlineHours = other.optionalCancellationDeadlineHours().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.locationsId = other.optionalLocationsId().map(LongFilter::copy).orElse(null);
        this.categoriesId = other.optionalCategoriesId().map(LongFilter::copy).orElse(null);
        this.promotionsId = other.optionalPromotionsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BrandCriteria copy() {
        return new BrandCriteria(this);
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

    public StringFilter getLogoUrl() {
        return logoUrl;
    }

    public Optional<StringFilter> optionalLogoUrl() {
        return Optional.ofNullable(logoUrl);
    }

    public StringFilter logoUrl() {
        if (logoUrl == null) {
            setLogoUrl(new StringFilter());
        }
        return logoUrl;
    }

    public void setLogoUrl(StringFilter logoUrl) {
        this.logoUrl = logoUrl;
    }

    public StringFilter getCoverImageUrl() {
        return coverImageUrl;
    }

    public Optional<StringFilter> optionalCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public StringFilter coverImageUrl() {
        if (coverImageUrl == null) {
            setCoverImageUrl(new StringFilter());
        }
        return coverImageUrl;
    }

    public void setCoverImageUrl(StringFilter coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public StringFilter getPrimaryColor() {
        return primaryColor;
    }

    public Optional<StringFilter> optionalPrimaryColor() {
        return Optional.ofNullable(primaryColor);
    }

    public StringFilter primaryColor() {
        if (primaryColor == null) {
            setPrimaryColor(new StringFilter());
        }
        return primaryColor;
    }

    public void setPrimaryColor(StringFilter primaryColor) {
        this.primaryColor = primaryColor;
    }

    public StringFilter getSecondaryColor() {
        return secondaryColor;
    }

    public Optional<StringFilter> optionalSecondaryColor() {
        return Optional.ofNullable(secondaryColor);
    }

    public StringFilter secondaryColor() {
        if (secondaryColor == null) {
            setSecondaryColor(new StringFilter());
        }
        return secondaryColor;
    }

    public void setSecondaryColor(StringFilter secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public StringFilter getWebsite() {
        return website;
    }

    public Optional<StringFilter> optionalWebsite() {
        return Optional.ofNullable(website);
    }

    public StringFilter website() {
        if (website == null) {
            setWebsite(new StringFilter());
        }
        return website;
    }

    public void setWebsite(StringFilter website) {
        this.website = website;
    }

    public StringFilter getContactEmail() {
        return contactEmail;
    }

    public Optional<StringFilter> optionalContactEmail() {
        return Optional.ofNullable(contactEmail);
    }

    public StringFilter contactEmail() {
        if (contactEmail == null) {
            setContactEmail(new StringFilter());
        }
        return contactEmail;
    }

    public void setContactEmail(StringFilter contactEmail) {
        this.contactEmail = contactEmail;
    }

    public StringFilter getContactPhone() {
        return contactPhone;
    }

    public Optional<StringFilter> optionalContactPhone() {
        return Optional.ofNullable(contactPhone);
    }

    public StringFilter contactPhone() {
        if (contactPhone == null) {
            setContactPhone(new StringFilter());
        }
        return contactPhone;
    }

    public void setContactPhone(StringFilter contactPhone) {
        this.contactPhone = contactPhone;
    }

    public IntegerFilter getDefaultReservationDuration() {
        return defaultReservationDuration;
    }

    public Optional<IntegerFilter> optionalDefaultReservationDuration() {
        return Optional.ofNullable(defaultReservationDuration);
    }

    public IntegerFilter defaultReservationDuration() {
        if (defaultReservationDuration == null) {
            setDefaultReservationDuration(new IntegerFilter());
        }
        return defaultReservationDuration;
    }

    public void setDefaultReservationDuration(IntegerFilter defaultReservationDuration) {
        this.defaultReservationDuration = defaultReservationDuration;
    }

    public IntegerFilter getMaxAdvanceBookingDays() {
        return maxAdvanceBookingDays;
    }

    public Optional<IntegerFilter> optionalMaxAdvanceBookingDays() {
        return Optional.ofNullable(maxAdvanceBookingDays);
    }

    public IntegerFilter maxAdvanceBookingDays() {
        if (maxAdvanceBookingDays == null) {
            setMaxAdvanceBookingDays(new IntegerFilter());
        }
        return maxAdvanceBookingDays;
    }

    public void setMaxAdvanceBookingDays(IntegerFilter maxAdvanceBookingDays) {
        this.maxAdvanceBookingDays = maxAdvanceBookingDays;
    }

    public IntegerFilter getCancellationDeadlineHours() {
        return cancellationDeadlineHours;
    }

    public Optional<IntegerFilter> optionalCancellationDeadlineHours() {
        return Optional.ofNullable(cancellationDeadlineHours);
    }

    public IntegerFilter cancellationDeadlineHours() {
        if (cancellationDeadlineHours == null) {
            setCancellationDeadlineHours(new IntegerFilter());
        }
        return cancellationDeadlineHours;
    }

    public void setCancellationDeadlineHours(IntegerFilter cancellationDeadlineHours) {
        this.cancellationDeadlineHours = cancellationDeadlineHours;
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

    public LongFilter getLocationsId() {
        return locationsId;
    }

    public Optional<LongFilter> optionalLocationsId() {
        return Optional.ofNullable(locationsId);
    }

    public LongFilter locationsId() {
        if (locationsId == null) {
            setLocationsId(new LongFilter());
        }
        return locationsId;
    }

    public void setLocationsId(LongFilter locationsId) {
        this.locationsId = locationsId;
    }

    public LongFilter getCategoriesId() {
        return categoriesId;
    }

    public Optional<LongFilter> optionalCategoriesId() {
        return Optional.ofNullable(categoriesId);
    }

    public LongFilter categoriesId() {
        if (categoriesId == null) {
            setCategoriesId(new LongFilter());
        }
        return categoriesId;
    }

    public void setCategoriesId(LongFilter categoriesId) {
        this.categoriesId = categoriesId;
    }

    public LongFilter getPromotionsId() {
        return promotionsId;
    }

    public Optional<LongFilter> optionalPromotionsId() {
        return Optional.ofNullable(promotionsId);
    }

    public LongFilter promotionsId() {
        if (promotionsId == null) {
            setPromotionsId(new LongFilter());
        }
        return promotionsId;
    }

    public void setPromotionsId(LongFilter promotionsId) {
        this.promotionsId = promotionsId;
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
        final BrandCriteria that = (BrandCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(logoUrl, that.logoUrl) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(primaryColor, that.primaryColor) &&
            Objects.equals(secondaryColor, that.secondaryColor) &&
            Objects.equals(website, that.website) &&
            Objects.equals(contactEmail, that.contactEmail) &&
            Objects.equals(contactPhone, that.contactPhone) &&
            Objects.equals(defaultReservationDuration, that.defaultReservationDuration) &&
            Objects.equals(maxAdvanceBookingDays, that.maxAdvanceBookingDays) &&
            Objects.equals(cancellationDeadlineHours, that.cancellationDeadlineHours) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(locationsId, that.locationsId) &&
            Objects.equals(categoriesId, that.categoriesId) &&
            Objects.equals(promotionsId, that.promotionsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            logoUrl,
            coverImageUrl,
            primaryColor,
            secondaryColor,
            website,
            contactEmail,
            contactPhone,
            defaultReservationDuration,
            maxAdvanceBookingDays,
            cancellationDeadlineHours,
            isActive,
            createdAt,
            locationsId,
            categoriesId,
            promotionsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BrandCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalLogoUrl().map(f -> "logoUrl=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalPrimaryColor().map(f -> "primaryColor=" + f + ", ").orElse("") +
            optionalSecondaryColor().map(f -> "secondaryColor=" + f + ", ").orElse("") +
            optionalWebsite().map(f -> "website=" + f + ", ").orElse("") +
            optionalContactEmail().map(f -> "contactEmail=" + f + ", ").orElse("") +
            optionalContactPhone().map(f -> "contactPhone=" + f + ", ").orElse("") +
            optionalDefaultReservationDuration().map(f -> "defaultReservationDuration=" + f + ", ").orElse("") +
            optionalMaxAdvanceBookingDays().map(f -> "maxAdvanceBookingDays=" + f + ", ").orElse("") +
            optionalCancellationDeadlineHours().map(f -> "cancellationDeadlineHours=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalLocationsId().map(f -> "locationsId=" + f + ", ").orElse("") +
            optionalCategoriesId().map(f -> "categoriesId=" + f + ", ").orElse("") +
            optionalPromotionsId().map(f -> "promotionsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
