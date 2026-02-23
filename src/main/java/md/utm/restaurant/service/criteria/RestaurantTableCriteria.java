package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.domain.enumeration.TableShape;
import md.utm.restaurant.domain.enumeration.TableStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.RestaurantTable} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.RestaurantTableResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /restaurant-tables?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantTableCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TableShape
     */
    public static class TableShapeFilter extends Filter<TableShape> {

        public TableShapeFilter() {}

        public TableShapeFilter(TableShapeFilter filter) {
            super(filter);
        }

        @Override
        public TableShapeFilter copy() {
            return new TableShapeFilter(this);
        }
    }

    /**
     * Class for filtering TableStatus
     */
    public static class TableStatusFilter extends Filter<TableStatus> {

        public TableStatusFilter() {}

        public TableStatusFilter(TableStatusFilter filter) {
            super(filter);
        }

        @Override
        public TableStatusFilter copy() {
            return new TableStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter tableNumber;

    private TableShapeFilter shape;

    private IntegerFilter minCapacity;

    private IntegerFilter maxCapacity;

    private DoubleFilter positionX;

    private DoubleFilter positionY;

    private DoubleFilter widthPx;

    private DoubleFilter heightPx;

    private DoubleFilter rotation;

    private TableStatusFilter status;

    private BooleanFilter isActive;

    private StringFilter notes;

    private LongFilter roomId;

    private Boolean distinct;

    public RestaurantTableCriteria() {}

    public RestaurantTableCriteria(RestaurantTableCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.tableNumber = other.optionalTableNumber().map(StringFilter::copy).orElse(null);
        this.shape = other.optionalShape().map(TableShapeFilter::copy).orElse(null);
        this.minCapacity = other.optionalMinCapacity().map(IntegerFilter::copy).orElse(null);
        this.maxCapacity = other.optionalMaxCapacity().map(IntegerFilter::copy).orElse(null);
        this.positionX = other.optionalPositionX().map(DoubleFilter::copy).orElse(null);
        this.positionY = other.optionalPositionY().map(DoubleFilter::copy).orElse(null);
        this.widthPx = other.optionalWidthPx().map(DoubleFilter::copy).orElse(null);
        this.heightPx = other.optionalHeightPx().map(DoubleFilter::copy).orElse(null);
        this.rotation = other.optionalRotation().map(DoubleFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TableStatusFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.roomId = other.optionalRoomId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RestaurantTableCriteria copy() {
        return new RestaurantTableCriteria(this);
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

    public StringFilter getTableNumber() {
        return tableNumber;
    }

    public Optional<StringFilter> optionalTableNumber() {
        return Optional.ofNullable(tableNumber);
    }

    public StringFilter tableNumber() {
        if (tableNumber == null) {
            setTableNumber(new StringFilter());
        }
        return tableNumber;
    }

    public void setTableNumber(StringFilter tableNumber) {
        this.tableNumber = tableNumber;
    }

    public TableShapeFilter getShape() {
        return shape;
    }

    public Optional<TableShapeFilter> optionalShape() {
        return Optional.ofNullable(shape);
    }

    public TableShapeFilter shape() {
        if (shape == null) {
            setShape(new TableShapeFilter());
        }
        return shape;
    }

    public void setShape(TableShapeFilter shape) {
        this.shape = shape;
    }

    public IntegerFilter getMinCapacity() {
        return minCapacity;
    }

    public Optional<IntegerFilter> optionalMinCapacity() {
        return Optional.ofNullable(minCapacity);
    }

    public IntegerFilter minCapacity() {
        if (minCapacity == null) {
            setMinCapacity(new IntegerFilter());
        }
        return minCapacity;
    }

    public void setMinCapacity(IntegerFilter minCapacity) {
        this.minCapacity = minCapacity;
    }

    public IntegerFilter getMaxCapacity() {
        return maxCapacity;
    }

    public Optional<IntegerFilter> optionalMaxCapacity() {
        return Optional.ofNullable(maxCapacity);
    }

    public IntegerFilter maxCapacity() {
        if (maxCapacity == null) {
            setMaxCapacity(new IntegerFilter());
        }
        return maxCapacity;
    }

    public void setMaxCapacity(IntegerFilter maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public DoubleFilter getPositionX() {
        return positionX;
    }

    public Optional<DoubleFilter> optionalPositionX() {
        return Optional.ofNullable(positionX);
    }

    public DoubleFilter positionX() {
        if (positionX == null) {
            setPositionX(new DoubleFilter());
        }
        return positionX;
    }

    public void setPositionX(DoubleFilter positionX) {
        this.positionX = positionX;
    }

    public DoubleFilter getPositionY() {
        return positionY;
    }

    public Optional<DoubleFilter> optionalPositionY() {
        return Optional.ofNullable(positionY);
    }

    public DoubleFilter positionY() {
        if (positionY == null) {
            setPositionY(new DoubleFilter());
        }
        return positionY;
    }

    public void setPositionY(DoubleFilter positionY) {
        this.positionY = positionY;
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

    public DoubleFilter getRotation() {
        return rotation;
    }

    public Optional<DoubleFilter> optionalRotation() {
        return Optional.ofNullable(rotation);
    }

    public DoubleFilter rotation() {
        if (rotation == null) {
            setRotation(new DoubleFilter());
        }
        return rotation;
    }

    public void setRotation(DoubleFilter rotation) {
        this.rotation = rotation;
    }

    public TableStatusFilter getStatus() {
        return status;
    }

    public Optional<TableStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TableStatusFilter status() {
        if (status == null) {
            setStatus(new TableStatusFilter());
        }
        return status;
    }

    public void setStatus(TableStatusFilter status) {
        this.status = status;
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
        final RestaurantTableCriteria that = (RestaurantTableCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tableNumber, that.tableNumber) &&
            Objects.equals(shape, that.shape) &&
            Objects.equals(minCapacity, that.minCapacity) &&
            Objects.equals(maxCapacity, that.maxCapacity) &&
            Objects.equals(positionX, that.positionX) &&
            Objects.equals(positionY, that.positionY) &&
            Objects.equals(widthPx, that.widthPx) &&
            Objects.equals(heightPx, that.heightPx) &&
            Objects.equals(rotation, that.rotation) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(roomId, that.roomId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tableNumber,
            shape,
            minCapacity,
            maxCapacity,
            positionX,
            positionY,
            widthPx,
            heightPx,
            rotation,
            status,
            isActive,
            notes,
            roomId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantTableCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTableNumber().map(f -> "tableNumber=" + f + ", ").orElse("") +
            optionalShape().map(f -> "shape=" + f + ", ").orElse("") +
            optionalMinCapacity().map(f -> "minCapacity=" + f + ", ").orElse("") +
            optionalMaxCapacity().map(f -> "maxCapacity=" + f + ", ").orElse("") +
            optionalPositionX().map(f -> "positionX=" + f + ", ").orElse("") +
            optionalPositionY().map(f -> "positionY=" + f + ", ").orElse("") +
            optionalWidthPx().map(f -> "widthPx=" + f + ", ").orElse("") +
            optionalHeightPx().map(f -> "heightPx=" + f + ", ").orElse("") +
            optionalRotation().map(f -> "rotation=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalRoomId().map(f -> "roomId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
