package md.utm.restaurant.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.Review} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReviewDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer overallRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer foodRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer serviceRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer ambienceRating;

    @Lob
    private String comment;

    @NotNull
    private Boolean isApproved;

    @NotNull
    private Boolean isAnonymous;

    @NotNull
    private Instant createdAt;

    private LocationDTO location;

    private ReservationDTO reservation;

    private UserDTO client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(Integer foodRating) {
        this.foodRating = foodRating;
    }

    public Integer getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }

    public Integer getAmbienceRating() {
        return ambienceRating;
    }

    public void setAmbienceRating(Integer ambienceRating) {
        this.ambienceRating = ambienceRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
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

    public ReservationDTO getReservation() {
        return reservation;
    }

    public void setReservation(ReservationDTO reservation) {
        this.reservation = reservation;
    }

    public UserDTO getClient() {
        return client;
    }

    public void setClient(UserDTO client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReviewDTO)) {
            return false;
        }

        ReviewDTO reviewDTO = (ReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReviewDTO{" +
            "id=" + getId() +
            ", overallRating=" + getOverallRating() +
            ", foodRating=" + getFoodRating() +
            ", serviceRating=" + getServiceRating() +
            ", ambienceRating=" + getAmbienceRating() +
            ", comment='" + getComment() + "'" +
            ", isApproved='" + getIsApproved() + "'" +
            ", isAnonymous='" + getIsAnonymous() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", location=" + getLocation() +
            ", reservation=" + getReservation() +
            ", client=" + getClient() +
            "}";
    }
}
