package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.DiningRoom} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.DiningRoomResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /dining-rooms?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiningRoomCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private IntegerFilter floor;

    private IntegerFilter capacity;

    private BooleanFilter isActive;

    private StringFilter floorPlanUrl;

    private DoubleFilter widthPx;

    private DoubleFilter heightPx;

    private LongFilter tablesId;

    private LongFilter locationId;

    private Boolean distinct;

    public DiningRoomCriteria() {}

    public DiningRoomCriteria(DiningRoomCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.floor = other.optionalFloor().map(IntegerFilter::copy).orElse(null);
        this.capacity = other.optionalCapacity().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.floorPlanUrl = other.optionalFloorPlanUrl().map(StringFilter::copy).orElse(null);
        this.widthPx = other.optionalWidthPx().map(DoubleFilter::copy).orElse(null);
        this.heightPx = other.optionalHeightPx().map(DoubleFilter::copy).orElse(null);
        this.tablesId = other.optionalTablesId().map(LongFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DiningRoomCriteria copy() {
        return new DiningRoomCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getFloor() {
        return floor;
    }

    public Optional<IntegerFilter> optionalFloor() {
        return Optional.ofNullable(floor);
    }

    public IntegerFilter floor() {
        if (floor == null) {
            setFloor(new IntegerFilter());
        }
        return floor;
    }

    public void setFloor(IntegerFilter floor) {
        this.floor = floor;
    }

    public IntegerFilter getCapacity() {
        return capacity;
    }

    public Optional<IntegerFilter> optionalCapacity() {
        return Optional.ofNullable(capacity);
    }

    public IntegerFilter capacity() {
        if (capacity == null) {
            setCapacity(new IntegerFilter());
        }
        return capacity;
    }

    public void setCapacity(IntegerFilter capacity) {
        this.capacity = capacity;
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

    public StringFilter getFloorPlanUrl() {
        return floorPlanUrl;
    }

    public Optional<StringFilter> optionalFloorPlanUrl() {
        return Optional.ofNullable(floorPlanUrl);
    }

    public StringFilter floorPlanUrl() {
        if (floorPlanUrl == null) {
            setFloorPlanUrl(new StringFilter());
        }
        return floorPlanUrl;
    }

    public void setFloorPlanUrl(StringFilter floorPlanUrl) {
        this.floorPlanUrl = floorPlanUrl;
    }

    public DoubleFilter getWidthPx() {
        return widthPx;
    }

    public Optional<DoubleFilter> optionalWidthPx() {
        return Optional.ofNullable(widthPx);
    }

    public DoubleFilter widthPx() {
        if (widthPx == null) {
            setWidthPx(new DoubleFilter());
        }
        return widthPx;
    }

    public void setWidthPx(DoubleFilter widthPx) {
        this.widthPx = widthPx;
    }

    public DoubleFilter getHeightPx() {
        return heightPx;
    }

    public Optional<DoubleFilter> optionalHeightPx() {
        return Optional.ofNullable(heightPx);
    }

    public DoubleFilter heightPx() {
        if (heightPx == null) {
            setHeightPx(new DoubleFilter());
        }
        return heightPx;
    }

    public void setHeightPx(DoubleFilter heightPx) {
        this.heightPx = heightPx;
    }

    public LongFilter getTablesId() {
        return tablesId;
    }

    public Optional<LongFilter> optionalTablesId() {
        return Optional.ofNullable(tablesId);
    }

    public LongFilter tablesId() {
        if (tablesId == null) {
            setTablesId(new LongFilter());
        }
        return tablesId;
    }

    public void setTablesId(LongFilter tablesId) {
        this.tablesId = tablesId;
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
        final DiningRoomCriteria that = (DiningRoomCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(floor, that.floor) &&
            Objects.equals(capacity, that.capacity) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(floorPlanUrl, that.floorPlanUrl) &&
            Objects.equals(widthPx, that.widthPx) &&
            Objects.equals(heightPx, that.heightPx) &&
            Objects.equals(tablesId, that.tablesId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            description,
            floor,
            capacity,
            isActive,
            floorPlanUrl,
            widthPx,
            heightPx,
            tablesId,
            locationId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiningRoomCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalFloor().map(f -> "floor=" + f + ", ").orElse("") +
            optionalCapacity().map(f -> "capacity=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalFloorPlanUrl().map(f -> "floorPlanUrl=" + f + ", ").orElse("") +
            optionalWidthPx().map(f -> "widthPx=" + f + ", ").orElse("") +
            optionalHeightPx().map(f -> "heightPx=" + f + ", ").orElse("") +
            optionalTablesId().map(f -> "tablesId=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
