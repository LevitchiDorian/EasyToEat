package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Location.
 */
@Entity
@Table(name = "location")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @NotNull
    @Size(max = 100)
    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @NotNull
    @Size(max = 20)
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @NotNull
    @Size(max = 100)
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Min(value = 15)
    @Max(value = 480)
    @Column(name = "reservation_duration_override")
    private Integer reservationDurationOverride;

    @Min(value = 1)
    @Max(value = 365)
    @Column(name = "max_advance_booking_days_override")
    private Integer maxAdvanceBookingDaysOverride;

    @Min(value = 0)
    @Max(value = 72)
    @Column(name = "cancellation_deadline_override")
    private Integer cancellationDeadlineOverride;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "location" }, allowSetters = true)
    private Set<LocationHours> hours = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tables", "location" }, allowSetters = true)
    private Set<DiningRoom> rooms = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "brand", "location" }, allowSetters = true)
    private Set<Promotion> localPromotions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "menuItem", "location" }, allowSetters = true)
    private Set<LocationMenuItemOverride> menuOverrides = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "locations", "categories", "promotions" }, allowSetters = true)
    private Brand brand;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Location id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Location name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public Location address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public Location city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return this.phone;
    }

    public Location phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public Location email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Location latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Location longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getReservationDurationOverride() {
        return this.reservationDurationOverride;
    }

    public Location reservationDurationOverride(Integer reservationDurationOverride) {
        this.setReservationDurationOverride(reservationDurationOverride);
        return this;
    }

    public void setReservationDurationOverride(Integer reservationDurationOverride) {
        this.reservationDurationOverride = reservationDurationOverride;
    }

    public Integer getMaxAdvanceBookingDaysOverride() {
        return this.maxAdvanceBookingDaysOverride;
    }

    public Location maxAdvanceBookingDaysOverride(Integer maxAdvanceBookingDaysOverride) {
        this.setMaxAdvanceBookingDaysOverride(maxAdvanceBookingDaysOverride);
        return this;
    }

    public void setMaxAdvanceBookingDaysOverride(Integer maxAdvanceBookingDaysOverride) {
        this.maxAdvanceBookingDaysOverride = maxAdvanceBookingDaysOverride;
    }

    public Integer getCancellationDeadlineOverride() {
        return this.cancellationDeadlineOverride;
    }

    public Location cancellationDeadlineOverride(Integer cancellationDeadlineOverride) {
        this.setCancellationDeadlineOverride(cancellationDeadlineOverride);
        return this;
    }

    public void setCancellationDeadlineOverride(Integer cancellationDeadlineOverride) {
        this.cancellationDeadlineOverride = cancellationDeadlineOverride;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Location isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Location createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<LocationHours> getHours() {
        return this.hours;
    }

    public void setHours(Set<LocationHours> locationHours) {
        if (this.hours != null) {
            this.hours.forEach(i -> i.setLocation(null));
        }
        if (locationHours != null) {
            locationHours.forEach(i -> i.setLocation(this));
        }
        this.hours = locationHours;
    }

    public Location hours(Set<LocationHours> locationHours) {
        this.setHours(locationHours);
        return this;
    }

    public Location addHours(LocationHours locationHours) {
        this.hours.add(locationHours);
        locationHours.setLocation(this);
        return this;
    }

    public Location removeHours(LocationHours locationHours) {
        this.hours.remove(locationHours);
        locationHours.setLocation(null);
        return this;
    }

    public Set<DiningRoom> getRooms() {
        return this.rooms;
    }

    public void setRooms(Set<DiningRoom> diningRooms) {
        if (this.rooms != null) {
            this.rooms.forEach(i -> i.setLocation(null));
        }
        if (diningRooms != null) {
            diningRooms.forEach(i -> i.setLocation(this));
        }
        this.rooms = diningRooms;
    }

    public Location rooms(Set<DiningRoom> diningRooms) {
        this.setRooms(diningRooms);
        return this;
    }

    public Location addRooms(DiningRoom diningRoom) {
        this.rooms.add(diningRoom);
        diningRoom.setLocation(this);
        return this;
    }

    public Location removeRooms(DiningRoom diningRoom) {
        this.rooms.remove(diningRoom);
        diningRoom.setLocation(null);
        return this;
    }

    public Set<Promotion> getLocalPromotions() {
        return this.localPromotions;
    }

    public void setLocalPromotions(Set<Promotion> promotions) {
        if (this.localPromotions != null) {
            this.localPromotions.forEach(i -> i.setLocation(null));
        }
        if (promotions != null) {
            promotions.forEach(i -> i.setLocation(this));
        }
        this.localPromotions = promotions;
    }

    public Location localPromotions(Set<Promotion> promotions) {
        this.setLocalPromotions(promotions);
        return this;
    }

    public Location addLocalPromotions(Promotion promotion) {
        this.localPromotions.add(promotion);
        promotion.setLocation(this);
        return this;
    }

    public Location removeLocalPromotions(Promotion promotion) {
        this.localPromotions.remove(promotion);
        promotion.setLocation(null);
        return this;
    }

    public Set<LocationMenuItemOverride> getMenuOverrides() {
        return this.menuOverrides;
    }

    public void setMenuOverrides(Set<LocationMenuItemOverride> locationMenuItemOverrides) {
        if (this.menuOverrides != null) {
            this.menuOverrides.forEach(i -> i.setLocation(null));
        }
        if (locationMenuItemOverrides != null) {
            locationMenuItemOverrides.forEach(i -> i.setLocation(this));
        }
        this.menuOverrides = locationMenuItemOverrides;
    }

    public Location menuOverrides(Set<LocationMenuItemOverride> locationMenuItemOverrides) {
        this.setMenuOverrides(locationMenuItemOverrides);
        return this;
    }

    public Location addMenuOverrides(LocationMenuItemOverride locationMenuItemOverride) {
        this.menuOverrides.add(locationMenuItemOverride);
        locationMenuItemOverride.setLocation(this);
        return this;
    }

    public Location removeMenuOverrides(LocationMenuItemOverride locationMenuItemOverride) {
        this.menuOverrides.remove(locationMenuItemOverride);
        locationMenuItemOverride.setLocation(null);
        return this;
    }

    public Brand getBrand() {
        return this.brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Location brand(Brand brand) {
        this.setBrand(brand);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        return getId() != null && getId().equals(((Location) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", reservationDurationOverride=" + getReservationDurationOverride() +
            ", maxAdvanceBookingDaysOverride=" + getMaxAdvanceBookingDaysOverride() +
            ", cancellationDeadlineOverride=" + getCancellationDeadlineOverride() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
