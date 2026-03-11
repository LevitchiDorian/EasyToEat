package md.utm.restaurant.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO representing a dining room with its tables for the staff floor plan view.
 */
public class FloorPlanRoomDTO implements Serializable {

    private Long id;
    private String name;
    private Integer floor;
    private Double widthPx;
    private Double heightPx;
    private List<FloorPlanTableDTO> tables;

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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
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

    private String decorationsJson;

    public String getDecorationsJson() {
        return decorationsJson;
    }

    public void setDecorationsJson(String decorationsJson) {
        this.decorationsJson = decorationsJson;
    }

    public List<FloorPlanTableDTO> getTables() {
        return tables;
    }

    public void setTables(List<FloorPlanTableDTO> tables) {
        this.tables = tables;
    }
}
