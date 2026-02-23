package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WaitingList.
 */
@Entity
@Table(name = "waiting_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WaitingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @NotNull
    @Size(max = 5)
    @Column(name = "requested_time", length = 5, nullable = false)
    private String requestedTime;

    @NotNull
    @Min(value = 1)
    @Column(name = "party_size", nullable = false)
    private Integer partySize;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @NotNull
    @Column(name = "is_notified", nullable = false)
    private Boolean isNotified;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tables", "location" }, allowSetters = true)
    private DiningRoom room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WaitingList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRequestedDate() {
        return this.requestedDate;
    }

    public WaitingList requestedDate(LocalDate requestedDate) {
        this.setRequestedDate(requestedDate);
        return this;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getRequestedTime() {
        return this.requestedTime;
    }

    public WaitingList requestedTime(String requestedTime) {
        this.setRequestedTime(requestedTime);
        return this;
    }

    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }

    public Integer getPartySize() {
        return this.partySize;
    }

    public WaitingList partySize(Integer partySize) {
        this.setPartySize(partySize);
        return this;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public String getNotes() {
        return this.notes;
    }

    public WaitingList notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsNotified() {
        return this.isNotified;
    }

    public WaitingList isNotified(Boolean isNotified) {
        this.setIsNotified(isNotified);
        return this;
    }

    public void setIsNotified(Boolean isNotified) {
        this.isNotified = isNotified;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public WaitingList expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public WaitingList createdAt(Instant createdAt) {
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

    public WaitingList location(Location location) {
        this.setLocation(location);
        return this;
    }

    public User getClient() {
        return this.client;
    }

    public void setClient(User user) {
        this.client = user;
    }

    public WaitingList client(User user) {
        this.setClient(user);
        return this;
    }

    public DiningRoom getRoom() {
        return this.room;
    }

    public void setRoom(DiningRoom diningRoom) {
        this.room = diningRoom;
    }

    public WaitingList room(DiningRoom diningRoom) {
        this.setRoom(diningRoom);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WaitingList)) {
            return false;
        }
        return getId() != null && getId().equals(((WaitingList) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WaitingList{" +
            "id=" + getId() +
            ", requestedDate='" + getRequestedDate() + "'" +
            ", requestedTime='" + getRequestedTime() + "'" +
            ", partySize=" + getPartySize() +
            ", notes='" + getNotes() + "'" +
            ", isNotified='" + getIsNotified() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
