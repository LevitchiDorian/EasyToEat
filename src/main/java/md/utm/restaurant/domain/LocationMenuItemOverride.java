package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LocationMenuItemOverride.
 */
@Entity
@Table(name = "location_menu_item_override")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationMenuItemOverride implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "is_available_at_location", nullable = false)
    private Boolean isAvailableAtLocation;

    @DecimalMin(value = "0")
    @Column(name = "price_override", precision = 21, scale = 2)
    private BigDecimal priceOverride;

    @Min(value = 0)
    @Max(value = 180)
    @Column(name = "preparation_time_override")
    private Integer preparationTimeOverride;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "allergens", "options", "category" }, allowSetters = true)
    private MenuItem menuItem;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LocationMenuItemOverride id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsAvailableAtLocation() {
        return this.isAvailableAtLocation;
    }

    public LocationMenuItemOverride isAvailableAtLocation(Boolean isAvailableAtLocation) {
        this.setIsAvailableAtLocation(isAvailableAtLocation);
        return this;
    }

    public void setIsAvailableAtLocation(Boolean isAvailableAtLocation) {
        this.isAvailableAtLocation = isAvailableAtLocation;
    }

    public BigDecimal getPriceOverride() {
        return this.priceOverride;
    }

    public LocationMenuItemOverride priceOverride(BigDecimal priceOverride) {
        this.setPriceOverride(priceOverride);
        return this;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }

    public Integer getPreparationTimeOverride() {
        return this.preparationTimeOverride;
    }

    public LocationMenuItemOverride preparationTimeOverride(Integer preparationTimeOverride) {
        this.setPreparationTimeOverride(preparationTimeOverride);
        return this;
    }

    public void setPreparationTimeOverride(Integer preparationTimeOverride) {
        this.preparationTimeOverride = preparationTimeOverride;
    }

    public String getNotes() {
        return this.notes;
    }

    public LocationMenuItemOverride notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public LocationMenuItemOverride menuItem(MenuItem menuItem) {
        this.setMenuItem(menuItem);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationMenuItemOverride location(Location location) {
        this.setLocation(location);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationMenuItemOverride)) {
            return false;
        }
        return getId() != null && getId().equals(((LocationMenuItemOverride) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationMenuItemOverride{" +
            "id=" + getId() +
            ", isAvailableAtLocation='" + getIsAvailableAtLocation() + "'" +
            ", priceOverride=" + getPriceOverride() +
            ", preparationTimeOverride=" + getPreparationTimeOverride() +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
