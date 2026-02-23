package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import md.utm.restaurant.domain.enumeration.TableShape;
import md.utm.restaurant.domain.enumeration.TableStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RestaurantTable.
 */
@Entity
@Table(name = "restaurant_table")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "table_number", length = 20, nullable = false)
    private String tableNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shape", nullable = false)
    private TableShape shape;

    @NotNull
    @Min(value = 1)
    @Column(name = "min_capacity", nullable = false)
    private Integer minCapacity;

    @NotNull
    @Min(value = 1)
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "position_x")
    private Double positionX;

    @Column(name = "position_y")
    private Double positionY;

    @Column(name = "width_px")
    private Double widthPx;

    @Column(name = "height_px")
    private Double heightPx;

    @Column(name = "rotation")
    private Double rotation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableStatus status;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tables", "location" }, allowSetters = true)
    private DiningRoom room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RestaurantTable id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableNumber() {
        return this.tableNumber;
    }

    public RestaurantTable tableNumber(String tableNumber) {
        this.setTableNumber(tableNumber);
        return this;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public TableShape getShape() {
        return this.shape;
    }

    public RestaurantTable shape(TableShape shape) {
        this.setShape(shape);
        return this;
    }

    public void setShape(TableShape shape) {
        this.shape = shape;
    }

    public Integer getMinCapacity() {
        return this.minCapacity;
    }

    public RestaurantTable minCapacity(Integer minCapacity) {
        this.setMinCapacity(minCapacity);
        return this;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }

    public Integer getMaxCapacity() {
        return this.maxCapacity;
    }

    public RestaurantTable maxCapacity(Integer maxCapacity) {
        this.setMaxCapacity(maxCapacity);
        return this;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Double getPositionX() {
        return this.positionX;
    }

    public RestaurantTable positionX(Double positionX) {
        this.setPositionX(positionX);
        return this;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return this.positionY;
    }

    public RestaurantTable positionY(Double positionY) {
        this.setPositionY(positionY);
        return this;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public Double getWidthPx() {
        return this.widthPx;
    }

    public RestaurantTable widthPx(Double widthPx) {
        this.setWidthPx(widthPx);
        return this;
    }

    public void setWidthPx(Double widthPx) {
        this.widthPx = widthPx;
    }

    public Double getHeightPx() {
        return this.heightPx;
    }

    public RestaurantTable heightPx(Double heightPx) {
        this.setHeightPx(heightPx);
        return this;
    }

    public void setHeightPx(Double heightPx) {
        this.heightPx = heightPx;
    }

    public Double getRotation() {
        return this.rotation;
    }

    public RestaurantTable rotation(Double rotation) {
        this.setRotation(rotation);
        return this;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public TableStatus getStatus() {
        return this.status;
    }

    public RestaurantTable status(TableStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public RestaurantTable isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return this.notes;
    }

    public RestaurantTable notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DiningRoom getRoom() {
        return this.room;
    }

    public void setRoom(DiningRoom diningRoom) {
        this.room = diningRoom;
    }

    public RestaurantTable room(DiningRoom diningRoom) {
        this.setRoom(diningRoom);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantTable)) {
            return false;
        }
        return getId() != null && getId().equals(((RestaurantTable) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantTable{" +
            "id=" + getId() +
            ", tableNumber='" + getTableNumber() + "'" +
            ", shape='" + getShape() + "'" +
            ", minCapacity=" + getMinCapacity() +
            ", maxCapacity=" + getMaxCapacity() +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", widthPx=" + getWidthPx() +
            ", heightPx=" + getHeightPx() +
            ", rotation=" + getRotation() +
            ", status='" + getStatus() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
