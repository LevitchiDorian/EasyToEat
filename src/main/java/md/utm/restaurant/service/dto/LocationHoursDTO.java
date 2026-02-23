package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import md.utm.restaurant.domain.enumeration.DayOfWeek;

/**
 * A DTO for the {@link md.utm.restaurant.domain.LocationHours} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationHoursDTO implements Serializable {

    private Long id;

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    @Size(max = 5)
    private String openTime;

    @NotNull
    @Size(max = 5)
    private String closeTime;

    @NotNull
    private Boolean isClosed;

    @Size(max = 255)
    private String specialNote;

    @NotNull
    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
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
        if (!(o instanceof LocationHoursDTO)) {
            return false;
        }

        LocationHoursDTO locationHoursDTO = (LocationHoursDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locationHoursDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationHoursDTO{" +
            "id=" + getId() +
            ", dayOfWeek='" + getDayOfWeek() + "'" +
            ", openTime='" + getOpenTime() + "'" +
            ", closeTime='" + getCloseTime() + "'" +
            ", isClosed='" + getIsClosed() + "'" +
            ", specialNote='" + getSpecialNote() + "'" +
            ", location=" + getLocation() +
            "}";
    }
}
