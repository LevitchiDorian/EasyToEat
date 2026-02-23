package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.DiningRoom} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiningRoomDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private Integer floor;

    @NotNull
    @Min(value = 1)
    private Integer capacity;

    @NotNull
    private Boolean isActive;

    @Size(max = 500)
    private String floorPlanUrl;

    private Double widthPx;

    private Double heightPx;

    @NotNull
    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getFloorPlanUrl() {
        return floorPlanUrl;
    }

    public void setFloorPlanUrl(String floorPlanUrl) {
        this.floorPlanUrl = floorPlanUrl;
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

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiningRoomDTO)) {
            return false;
        }

        DiningRoomDTO diningRoomDTO = (DiningRoomDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, diningRoomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiningRoomDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", floor=" + getFloor() +
            ", capacity=" + getCapacity() +
            ", isActive='" + getIsActive() + "'" +
            ", floorPlanUrl='" + getFloorPlanUrl() + "'" +
            ", widthPx=" + getWidthPx() +
            ", heightPx=" + getHeightPx() +
            ", location=" + getLocation() +
            "}";
    }
}
