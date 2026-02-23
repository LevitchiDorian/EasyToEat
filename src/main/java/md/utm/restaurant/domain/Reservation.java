package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.domain.enumeration.ReservationStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reservation.
 */
@Entity
@Table(name = "reservation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 6, max = 20)
    @Column(name = "reservation_code", length = 20, nullable = false, unique = true)
    private String reservationCode;

    @NotNull
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @NotNull
    @Size(max = 5)
    @Column(name = "start_time", length = 5, nullable = false)
    private String startTime;

    @NotNull
    @Size(max = 5)
    @Column(name = "end_time", length = 5, nullable = false)
    private String endTime;

    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    @Column(name = "party_size", nullable = false)
    private Integer partySize;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Lob
    @Column(name = "special_requests")
    private String specialRequests;

    @Lob
    @Column(name = "internal_notes")
    private String internalNotes;

    @Column(name = "reminder_sent_at")
    private Instant reminderSentAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Size(max = 500)
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "table", "reservation" }, allowSetters = true)
    private Set<ReservationTable> tableAssignments = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "items", "payments", "location", "client", "assignedWaiter", "table", "promotion", "reservation" },
        allowSetters = true
    )
    private Set<RestaurantOrder> orders = new HashSet<>();

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

    public Reservation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationCode() {
        return this.reservationCode;
    }

    public Reservation reservationCode(String reservationCode) {
        this.setReservationCode(reservationCode);
        return this;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public LocalDate getReservationDate() {
        return this.reservationDate;
    }

    public Reservation reservationDate(LocalDate reservationDate) {
        this.setReservationDate(reservationDate);
        return this;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public Reservation startTime(String startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public Reservation endTime(String endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPartySize() {
        return this.partySize;
    }

    public Reservation partySize(Integer partySize) {
        this.setPartySize(partySize);
        return this;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public ReservationStatus getStatus() {
        return this.status;
    }

    public Reservation status(ReservationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getSpecialRequests() {
        return this.specialRequests;
    }

    public Reservation specialRequests(String specialRequests) {
        this.setSpecialRequests(specialRequests);
        return this;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getInternalNotes() {
        return this.internalNotes;
    }

    public Reservation internalNotes(String internalNotes) {
        this.setInternalNotes(internalNotes);
        return this;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public Instant getReminderSentAt() {
        return this.reminderSentAt;
    }

    public Reservation reminderSentAt(Instant reminderSentAt) {
        this.setReminderSentAt(reminderSentAt);
        return this;
    }

    public void setReminderSentAt(Instant reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public Reservation confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getCancelledAt() {
        return this.cancelledAt;
    }

    public Reservation cancelledAt(Instant cancelledAt) {
        this.setCancelledAt(cancelledAt);
        return this;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return this.cancellationReason;
    }

    public Reservation cancellationReason(String cancellationReason) {
        this.setCancellationReason(cancellationReason);
        return this;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Reservation createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Reservation updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ReservationTable> getTableAssignments() {
        return this.tableAssignments;
    }

    public void setTableAssignments(Set<ReservationTable> reservationTables) {
        if (this.tableAssignments != null) {
            this.tableAssignments.forEach(i -> i.setReservation(null));
        }
        if (reservationTables != null) {
            reservationTables.forEach(i -> i.setReservation(this));
        }
        this.tableAssignments = reservationTables;
    }

    public Reservation tableAssignments(Set<ReservationTable> reservationTables) {
        this.setTableAssignments(reservationTables);
        return this;
    }

    public Reservation addTableAssignments(ReservationTable reservationTable) {
        this.tableAssignments.add(reservationTable);
        reservationTable.setReservation(this);
        return this;
    }

    public Reservation removeTableAssignments(ReservationTable reservationTable) {
        this.tableAssignments.remove(reservationTable);
        reservationTable.setReservation(null);
        return this;
    }

    public Set<RestaurantOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<RestaurantOrder> restaurantOrders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setReservation(null));
        }
        if (restaurantOrders != null) {
            restaurantOrders.forEach(i -> i.setReservation(this));
        }
        this.orders = restaurantOrders;
    }

    public Reservation orders(Set<RestaurantOrder> restaurantOrders) {
        this.setOrders(restaurantOrders);
        return this;
    }

    public Reservation addOrders(RestaurantOrder restaurantOrder) {
        this.orders.add(restaurantOrder);
        restaurantOrder.setReservation(this);
        return this;
    }

    public Reservation removeOrders(RestaurantOrder restaurantOrder) {
        this.orders.remove(restaurantOrder);
        restaurantOrder.setReservation(null);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Reservation location(Location location) {
        this.setLocation(location);
        return this;
    }

    public User getClient() {
        return this.client;
    }

    public void setClient(User user) {
        this.client = user;
    }

    public Reservation client(User user) {
        this.setClient(user);
        return this;
    }

    public DiningRoom getRoom() {
        return this.room;
    }

    public void setRoom(DiningRoom diningRoom) {
        this.room = diningRoom;
    }

    public Reservation room(DiningRoom diningRoom) {
        this.setRoom(diningRoom);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        return getId() != null && getId().equals(((Reservation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + getId() +
            ", reservationCode='" + getReservationCode() + "'" +
            ", reservationDate='" + getReservationDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", partySize=" + getPartySize() +
            ", status='" + getStatus() + "'" +
            ", specialRequests='" + getSpecialRequests() + "'" +
            ", internalNotes='" + getInternalNotes() + "'" +
            ", reminderSentAt='" + getReminderSentAt() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", cancelledAt='" + getCancelledAt() + "'" +
            ", cancellationReason='" + getCancellationReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
