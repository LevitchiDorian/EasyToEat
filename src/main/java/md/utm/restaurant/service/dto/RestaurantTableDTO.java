package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import md.utm.restaurant.domain.enumeration.TableShape;
import md.utm.restaurant.domain.enumeration.TableStatus;

/**
 * A DTO for the {@link md.utm.restaurant.domain.RestaurantTable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantTableDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 20)
    private String tableNumber;

    @NotNull
    private TableShape shape;

    @NotNull
    @Min(value = 1)
    private Integer minCapacity;

    @NotNull
    @Min(value = 1)
    private Integer maxCapacity;

    private Double positionX;

    private Double positionY;

    private Double widthPx;

    private Double heightPx;

    private Double rotation;

    @NotNull
    private TableStatus status;

    @NotNull
    private Boolean isActive;

    @Size(max = 500)
    private String notes;

    @NotNull
    private DiningRoomDTO room;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public TableShape getShape() {
        return shape;
    }

    public void setShape(TableShape shape) {
        this.shape = shape;
    }

    public Integer getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public Double getWidthPx() {
        return widthPx;
    }

    public void setWidthPx(Double widthPx) {
        this.widthPx = widthPx;
    }

    public Double getHeightPx() {
        return heightPx;
    }

    public void setHeightPx(Double heightPx) {
        this.heightPx = heightPx;
    }

    public Double getRotation() {
        return rotation;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DiningRoomDTO getRoom() {
        return room;
    }

    public void setRoom(DiningRoomDTO room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantTableDTO)) {
            return false;
        }

        RestaurantTableDTO restaurantTableDTO = (RestaurantTableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurantTableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantTableDTO{" +
            "id=" + getId() +
            ", tableNumber='" + getTableNumber() + "'" +
            ", shape='" + getShape() + "'" +
            ", minCapacity=" + getMinCapacity() +
            ", maxCapacity=" + getMaxCapacity() +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", widthPx=" + getWidthPx() +
            ", heightPx=" + getHeightPx() +
            ", rotation=" + getRotation() +
            ", status='" + getStatus() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", notes='" + getNotes() + "'" +
            ", room=" + getRoom() +
            "}";
    }
}
