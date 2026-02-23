package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.WaitingList} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WaitingListDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate requestedDate;

    @NotNull
    @Size(max = 5)
    private String requestedTime;

    @NotNull
    @Min(value = 1)
    private Integer partySize;

    @Size(max = 500)
    private String notes;

    @NotNull
    private Boolean isNotified;

    private Instant expiresAt;

    @NotNull
    private Instant createdAt;

    private LocationDTO location;

    private UserDTO client;

    private DiningRoomDTO room;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }

    public Integer getPartySize() {
        return partySize;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsNotified() {
        return isNotified;
    }

    public void setIsNotified(Boolean isNotified) {
        this.isNotified = isNotified;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public UserDTO getClient() {
        return client;
    }

    public void setClient(UserDTO client) {
        this.client = client;
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
        if (!(o instanceof WaitingListDTO)) {
            return false;
        }

        WaitingListDTO waitingListDTO = (WaitingListDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, waitingListDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WaitingListDTO{" +
            "id=" + getId() +
            ", requestedDate='" + getRequestedDate() + "'" +
            ", requestedTime='" + getRequestedTime() + "'" +
            ", partySize=" + getPartySize() +
            ", notes='" + getNotes() + "'" +
            ", isNotified='" + getIsNotified() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", location=" + getLocation() +
            ", client=" + getClient() +
            ", room=" + getRoom() +
            "}";
    }
}
