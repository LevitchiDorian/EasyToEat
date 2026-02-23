package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Review.
 */
@Entity
@Table(name = "review")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "food_rating")
    private Integer foodRating;

    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "service_rating")
    private Integer serviceRating;

    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "ambience_rating")
    private Integer ambienceRating;

    @Lob
    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @NotNull
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tableAssignments", "orders", "location", "client", "room" }, allowSetters = true)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    private User client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Review id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOverallRating() {
        return this.overallRating;
    }

    public Review overallRating(Integer overallRating) {
        this.setOverallRating(overallRating);
        return this;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getFoodRating() {
        return this.foodRating;
    }

    public Review foodRating(Integer foodRating) {
        this.setFoodRating(foodRating);
        return this;
    }

    public void setFoodRating(Integer foodRating) {
        this.foodRating = foodRating;
    }

    public Integer getServiceRating() {
        return this.serviceRating;
    }

    public Review serviceRating(Integer serviceRating) {
        this.setServiceRating(serviceRating);
        return this;
    }

    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }

    public Integer getAmbienceRating() {
        return this.ambienceRating;
    }

    public Review ambienceRating(Integer ambienceRating) {
        this.setAmbienceRating(ambienceRating);
        return this;
    }

    public void setAmbienceRating(Integer ambienceRating) {
        this.ambienceRating = ambienceRating;
    }

    public String getComment() {
        return this.comment;
    }

    public Review comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsApproved() {
        return this.isApproved;
    }

    public Review isApproved(Boolean isApproved) {
        this.setIsApproved(isApproved);
        return this;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Boolean getIsAnonymous() {
        return this.isAnonymous;
    }

    public Review isAnonymous(Boolean isAnonymous) {
        this.setIsAnonymous(isAnonymous);
        return this;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Review createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Review location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Review reservation(Reservation reservation) {
        this.setReservation(reservation);
        return this;
    }

    public User getClient() {
        return this.client;
    }

    public void setClient(User user) {
        this.client = user;
    }

    public Review client(User user) {
        this.setClient(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Review)) {
            return false;
        }
        return getId() != null && getId().equals(((Review) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Review{" +
            "id=" + getId() +
            ", overallRating=" + getOverallRating() +
            ", foodRating=" + getFoodRating() +
            ", serviceRating=" + getServiceRating() +
            ", ambienceRating=" + getAmbienceRating() +
            ", comment='" + getComment() + "'" +
            ", isApproved='" + getIsApproved() + "'" +
            ", isAnonymous='" + getIsAnonymous() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
