package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import md.utm.restaurant.domain.enumeration.DayOfWeek;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LocationHours.
 */
@Entity
@Table(name = "location_hours")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationHours implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Size(max = 5)
    @Column(name = "open_time", length = 5, nullable = false)
    private String openTime;

    @NotNull
    @Size(max = 5)
    @Column(name = "close_time", length = 5, nullable = false)
    private String closeTime;

    @NotNull
    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    @Size(max = 255)
    @Column(name = "special_note", length = 255)
    private String specialNote;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LocationHours id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    public LocationHours dayOfWeek(DayOfWeek dayOfWeek) {
        this.setDayOfWeek(dayOfWeek);
        return this;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getOpenTime() {
        return this.openTime;
    }

    public LocationHours openTime(String openTime) {
        this.setOpenTime(openTime);
        return this;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return this.closeTime;
    }

    public LocationHours closeTime(String closeTime) {
        this.setCloseTime(closeTime);
        return this;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public Boolean getIsClosed() {
        return this.isClosed;
    }

    public LocationHours isClosed(Boolean isClosed) {
        this.setIsClosed(isClosed);
        return this;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getSpecialNote() {
        return this.specialNote;
    }

    public LocationHours specialNote(String specialNote) {
        this.setSpecialNote(specialNote);
        return this;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationHours location(Location location) {
        this.setLocation(location);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationHours)) {
            return false;
        }
        return getId() != null && getId().equals(((LocationHours) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationHours{" +
            "id=" + getId() +
            ", dayOfWeek='" + getDayOfWeek() + "'" +
            ", openTime='" + getOpenTime() + "'" +
            ", closeTime='" + getCloseTime() + "'" +
            ", isClosed='" + getIsClosed() + "'" +
            ", specialNote='" + getSpecialNote() + "'" +
            "}";
    }
}
