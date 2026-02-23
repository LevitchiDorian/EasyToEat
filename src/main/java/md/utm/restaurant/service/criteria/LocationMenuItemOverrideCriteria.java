package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.LocationMenuItemOverride} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.LocationMenuItemOverrideResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /location-menu-item-overrides?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationMenuItemOverrideCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter isAvailableAtLocation;

    private BigDecimalFilter priceOverride;

    private IntegerFilter preparationTimeOverride;

    private StringFilter notes;

    private LongFilter menuItemId;

    private LongFilter locationId;

    private Boolean distinct;

    public LocationMenuItemOverrideCriteria() {}

    public LocationMenuItemOverrideCriteria(LocationMenuItemOverrideCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.isAvailableAtLocation = other.optionalIsAvailableAtLocation().map(BooleanFilter::copy).orElse(null);
        this.priceOverride = other.optionalPriceOverride().map(BigDecimalFilter::copy).orElse(null);
        this.preparationTimeOverride = other.optionalPreparationTimeOverride().map(IntegerFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.menuItemId = other.optionalMenuItemId().map(LongFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LocationMenuItemOverrideCriteria copy() {
        return new LocationMenuItemOverrideCriteria(this);
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

    public BooleanFilter getIsAvailableAtLocation() {
        return isAvailableAtLocation;
    }

    public Optional<BooleanFilter> optionalIsAvailableAtLocation() {
        return Optional.ofNullable(isAvailableAtLocation);
    }

    public BooleanFilter isAvailableAtLocation() {
        if (isAvailableAtLocation == null) {
            setIsAvailableAtLocation(new BooleanFilter());
        }
        return isAvailableAtLocation;
    }

    public void setIsAvailableAtLocation(BooleanFilter isAvailableAtLocation) {
        this.isAvailableAtLocation = isAvailableAtLocation;
    }

    public BigDecimalFilter getPriceOverride() {
        return priceOverride;
    }

    public Optional<BigDecimalFilter> optionalPriceOverride() {
        return Optional.ofNullable(priceOverride);
    }

    public BigDecimalFilter priceOverride() {
        if (priceOverride == null) {
            setPriceOverride(new BigDecimalFilter());
        }
        return priceOverride;
    }

    public void setPriceOverride(BigDecimalFilter priceOverride) {
        this.priceOverride = priceOverride;
    }

    public IntegerFilter getPreparationTimeOverride() {
        return preparationTimeOverride;
    }

    public Optional<IntegerFilter> optionalPreparationTimeOverride() {
        return Optional.ofNullable(preparationTimeOverride);
    }

    public IntegerFilter preparationTimeOverride() {
        if (preparationTimeOverride == null) {
            setPreparationTimeOverride(new IntegerFilter());
        }
        return preparationTimeOverride;
    }

    public void setPreparationTimeOverride(IntegerFilter preparationTimeOverride) {
        this.preparationTimeOverride = preparationTimeOverride;
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

    public LongFilter getMenuItemId() {
        return menuItemId;
    }

    public Optional<LongFilter> optionalMenuItemId() {
        return Optional.ofNullable(menuItemId);
    }

    public LongFilter menuItemId() {
        if (menuItemId == null) {
            setMenuItemId(new LongFilter());
        }
        return menuItemId;
    }

    public void setMenuItemId(LongFilter menuItemId) {
        this.menuItemId = menuItemId;
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
        final LocationMenuItemOverrideCriteria that = (LocationMenuItemOverrideCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isAvailableAtLocation, that.isAvailableAtLocation) &&
            Objects.equals(priceOverride, that.priceOverride) &&
            Objects.equals(preparationTimeOverride, that.preparationTimeOverride) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(menuItemId, that.menuItemId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isAvailableAtLocation, priceOverride, preparationTimeOverride, notes, menuItemId, locationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationMenuItemOverrideCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsAvailableAtLocation().map(f -> "isAvailableAtLocation=" + f + ", ").orElse("") +
            optionalPriceOverride().map(f -> "priceOverride=" + f + ", ").orElse("") +
            optionalPreparationTimeOverride().map(f -> "preparationTimeOverride=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalMenuItemId().map(f -> "menuItemId=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
