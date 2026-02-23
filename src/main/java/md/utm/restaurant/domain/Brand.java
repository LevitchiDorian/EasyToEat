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
 * A Brand.
 */
@Entity
@Table(name = "brand")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Brand implements Serializable {

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

    @Lob
    @Column(name = "description")
    private String description;

    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Size(max = 500)
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Size(max = 7)
    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    @Size(max = 7)
    @Column(name = "secondary_color", length = 7)
    private String secondaryColor;

    @Size(max = 255)
    @Column(name = "website", length = 255)
    private String website;

    @NotNull
    @Size(max = 100)
    @Column(name = "contact_email", length = 100, nullable = false)
    private String contactEmail;

    @NotNull
    @Size(max = 20)
    @Column(name = "contact_phone", length = 20, nullable = false)
    private String contactPhone;

    @NotNull
    @Min(value = 15)
    @Max(value = 480)
    @Column(name = "default_reservation_duration", nullable = false)
    private Integer defaultReservationDuration;

    @NotNull
    @Min(value = 1)
    @Max(value = 365)
    @Column(name = "max_advance_booking_days", nullable = false)
    private Integer maxAdvanceBookingDays;

    @NotNull
    @Min(value = 0)
    @Max(value = 72)
    @Column(name = "cancellation_deadline_hours", nullable = false)
    private Integer cancellationDeadlineHours;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Set<Location> locations = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "items", "parent", "brand" }, allowSetters = true)
    private Set<MenuCategory> categories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "brand", "location" }, allowSetters = true)
    private Set<Promotion> promotions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Brand id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Brand name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Brand description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public Brand logoUrl(String logoUrl) {
        this.setLogoUrl(logoUrl);
        return this;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public Brand coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getPrimaryColor() {
        return this.primaryColor;
    }

    public Brand primaryColor(String primaryColor) {
        this.setPrimaryColor(primaryColor);
        return this;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return this.secondaryColor;
    }

    public Brand secondaryColor(String secondaryColor) {
        this.setSecondaryColor(secondaryColor);
        return this;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getWebsite() {
        return this.website;
    }

    public Brand website(String website) {
        this.setWebsite(website);
        return this;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public Brand contactEmail(String contactEmail) {
        this.setContactEmail(contactEmail);
        return this;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public Brand contactPhone(String contactPhone) {
        this.setContactPhone(contactPhone);
        return this;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getDefaultReservationDuration() {
        return this.defaultReservationDuration;
    }

    public Brand defaultReservationDuration(Integer defaultReservationDuration) {
        this.setDefaultReservationDuration(defaultReservationDuration);
        return this;
    }

    public void setDefaultReservationDuration(Integer defaultReservationDuration) {
        this.defaultReservationDuration = defaultReservationDuration;
    }

    public Integer getMaxAdvanceBookingDays() {
        return this.maxAdvanceBookingDays;
    }

    public Brand maxAdvanceBookingDays(Integer maxAdvanceBookingDays) {
        this.setMaxAdvanceBookingDays(maxAdvanceBookingDays);
        return this;
    }

    public void setMaxAdvanceBookingDays(Integer maxAdvanceBookingDays) {
        this.maxAdvanceBookingDays = maxAdvanceBookingDays;
    }

    public Integer getCancellationDeadlineHours() {
        return this.cancellationDeadlineHours;
    }

    public Brand cancellationDeadlineHours(Integer cancellationDeadlineHours) {
        this.setCancellationDeadlineHours(cancellationDeadlineHours);
        return this;
    }

    public void setCancellationDeadlineHours(Integer cancellationDeadlineHours) {
        this.cancellationDeadlineHours = cancellationDeadlineHours;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Brand isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Brand createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(Set<Location> locations) {
        if (this.locations != null) {
            this.locations.forEach(i -> i.setBrand(null));
        }
        if (locations != null) {
            locations.forEach(i -> i.setBrand(this));
        }
        this.locations = locations;
    }

    public Brand locations(Set<Location> locations) {
        this.setLocations(locations);
        return this;
    }

    public Brand addLocations(Location location) {
        this.locations.add(location);
        location.setBrand(this);
        return this;
    }

    public Brand removeLocations(Location location) {
        this.locations.remove(location);
        location.setBrand(null);
        return this;
    }

    public Set<MenuCategory> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<MenuCategory> menuCategories) {
        if (this.categories != null) {
            this.categories.forEach(i -> i.setBrand(null));
        }
        if (menuCategories != null) {
            menuCategories.forEach(i -> i.setBrand(this));
        }
        this.categories = menuCategories;
    }

    public Brand categories(Set<MenuCategory> menuCategories) {
        this.setCategories(menuCategories);
        return this;
    }

    public Brand addCategories(MenuCategory menuCategory) {
        this.categories.add(menuCategory);
        menuCategory.setBrand(this);
        return this;
    }

    public Brand removeCategories(MenuCategory menuCategory) {
        this.categories.remove(menuCategory);
        menuCategory.setBrand(null);
        return this;
    }

    public Set<Promotion> getPromotions() {
        return this.promotions;
    }

    public void setPromotions(Set<Promotion> promotions) {
        if (this.promotions != null) {
            this.promotions.forEach(i -> i.setBrand(null));
        }
        if (promotions != null) {
            promotions.forEach(i -> i.setBrand(this));
        }
        this.promotions = promotions;
    }

    public Brand promotions(Set<Promotion> promotions) {
        this.setPromotions(promotions);
        return this;
    }

    public Brand addPromotions(Promotion promotion) {
        this.promotions.add(promotion);
        promotion.setBrand(this);
        return this;
    }

    public Brand removePromotions(Promotion promotion) {
        this.promotions.remove(promotion);
        promotion.setBrand(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Brand)) {
            return false;
        }
        return getId() != null && getId().equals(((Brand) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Brand{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", primaryColor='" + getPrimaryColor() + "'" +
            ", secondaryColor='" + getSecondaryColor() + "'" +
            ", website='" + getWebsite() + "'" +
            ", contactEmail='" + getContactEmail() + "'" +
            ", contactPhone='" + getContactPhone() + "'" +
            ", defaultReservationDuration=" + getDefaultReservationDuration() +
            ", maxAdvanceBookingDays=" + getMaxAdvanceBookingDays() +
            ", cancellationDeadlineHours=" + getCancellationDeadlineHours() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
