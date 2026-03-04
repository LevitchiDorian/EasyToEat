package md.utm.restaurant.service.dto;

import java.io.Serializable;

/**
 * DTO representing a restaurant table with its current reservation (if any).
 */
public class FloorPlanTableDTO implements Serializable {

    private Long id;
    private String tableNumber;
    private String shape;
    private Integer minCapacity;
    private Integer maxCapacity;
    private Double positionX;
    private Double positionY;
    private Double widthPx;
    private Double heightPx;
    private Double rotation;
    private String status;
    private Boolean isActive;
    private String notes;
    private FloorPlanReservationDTO reservation;

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

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public FloorPlanReservationDTO getReservation() {
        return reservation;
    }

    public void setReservation(FloorPlanReservationDTO reservation) {
        this.reservation = reservation;
    }
}
