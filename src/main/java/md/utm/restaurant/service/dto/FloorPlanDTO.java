package md.utm.restaurant.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Root DTO for the staff floor plan view.
 * Contains location info plus all dining rooms with table statuses and reservations.
 */
public class FloorPlanDTO implements Serializable {

    private Long locationId;
    private String locationName;
    private String locationAddress;
    private String date;
    private List<FloorPlanRoomDTO> rooms;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<FloorPlanRoomDTO> getRooms() {
        return rooms;
    }

    public void setRooms(List<FloorPlanRoomDTO> rooms) {
        this.rooms = rooms;
    }
}
