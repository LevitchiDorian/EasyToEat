package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DiningRoom.
 */
@Entity
@Table(name = "dining_room")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiningRoom implements Serializable {

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

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "floor")
    private Integer floor;

    @NotNull
    @Min(value = 1)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Size(max = 500)
    @Column(name = "floor_plan_url", length = 500)
    private String floorPlanUrl;

    @Column(name = "width_px")
    private Double widthPx;

    @Column(name = "height_px")
    private Double heightPx;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "room" }, allowSetters = true)
    private Set<RestaurantTable> tables = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DiningRoom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public DiningRoom name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public DiningRoom description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFloor() {
        return this.floor;
    }

    public DiningRoom floor(Integer floor) {
        this.setFloor(floor);
        return this;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public DiningRoom capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public DiningRoom isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getFloorPlanUrl() {
        return this.floorPlanUrl;
    }

    public DiningRoom floorPlanUrl(String floorPlanUrl) {
        this.setFloorPlanUrl(floorPlanUrl);
        return this;
    }

    public void setFloorPlanUrl(String floorPlanUrl) {
        this.floorPlanUrl = floorPlanUrl;
    }

    public Double getWidthPx() {
        return this.widthPx;
    }

    public DiningRoom widthPx(Double widthPx) {
        this.setWidthPx(widthPx);
        return this;
    }

    public void setWidthPx(Double widthPx) {
        this.widthPx = widthPx;
    }

    public Double getHeightPx() {
        return this.heightPx;
    }

    public DiningRoom heightPx(Double heightPx) {
        this.setHeightPx(heightPx);
        return this;
    }

    public void setHeightPx(Double heightPx) {
        this.heightPx = heightPx;
    }

    public Set<RestaurantTable> getTables() {
        return this.tables;
    }

    public void setTables(Set<RestaurantTable> restaurantTables) {
        if (this.tables != null) {
            this.tables.forEach(i -> i.setRoom(null));
        }
        if (restaurantTables != null) {
            restaurantTables.forEach(i -> i.setRoom(this));
        }
        this.tables = restaurantTables;
    }

    public DiningRoom tables(Set<RestaurantTable> restaurantTables) {
        this.setTables(restaurantTables);
        return this;
    }

    public DiningRoom addTables(RestaurantTable restaurantTable) {
        this.tables.add(restaurantTable);
        restaurantTable.setRoom(this);
        return this;
    }

    public DiningRoom removeTables(RestaurantTable restaurantTable) {
        this.tables.remove(restaurantTable);
        restaurantTable.setRoom(null);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DiningRoom location(Location location) {
        this.setLocation(location);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiningRoom)) {
            return false;
        }
        return getId() != null && getId().equals(((DiningRoom) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiningRoom{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", floor=" + getFloor() +
            ", capacity=" + getCapacity() +
            ", isActive='" + getIsActive() + "'" +
            ", floorPlanUrl='" + getFloorPlanUrl() + "'" +
            ", widthPx=" + getWidthPx() +
            ", heightPx=" + getHeightPx() +
            "}";
    }
}
