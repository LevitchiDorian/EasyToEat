package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.ReservationTable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservationTableDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant assignedAt;

    @Size(max = 255)
    private String notes;

    private RestaurantTableDTO table;

    @NotNull
    private ReservationDTO reservation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public RestaurantTableDTO getTable() {
        return table;
    }

    public void setTable(RestaurantTableDTO table) {
        this.table = table;
    }

    public ReservationDTO getReservation() {
        return reservation;
    }

    public void setReservation(ReservationDTO reservation) {
        this.reservation = reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservationTableDTO)) {
            return false;
        }

        ReservationTableDTO reservationTableDTO = (ReservationTableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reservationTableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationTableDTO{" +
            "id=" + getId() +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", notes='" + getNotes() + "'" +
            ", table=" + getTable() +
            ", reservation=" + getReservation() +
            "}";
    }
}
