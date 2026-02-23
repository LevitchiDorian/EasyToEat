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
 * A MenuItemOption.
 */
@Entity
@Table(name = "menu_item_option")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Min(value = 1)
    @Column(name = "max_selections")
    private Integer maxSelections;

    @Min(value = 0)
    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "option")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "option" }, allowSetters = true)
    private Set<MenuItemOptionValue> values = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "allergens", "options", "category" }, allowSetters = true)
    private MenuItem menuItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MenuItemOption id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MenuItemOption name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsRequired() {
        return this.isRequired;
    }

    public MenuItemOption isRequired(Boolean isRequired) {
        this.setIsRequired(isRequired);
        return this;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getMaxSelections() {
        return this.maxSelections;
    }

    public MenuItemOption maxSelections(Integer maxSelections) {
        this.setMaxSelections(maxSelections);
        return this;
    }

    public void setMaxSelections(Integer maxSelections) {
        this.maxSelections = maxSelections;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public MenuItemOption displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<MenuItemOptionValue> getValues() {
        return this.values;
    }

    public void setValues(Set<MenuItemOptionValue> menuItemOptionValues) {
        if (this.values != null) {
            this.values.forEach(i -> i.setOption(null));
        }
        if (menuItemOptionValues != null) {
            menuItemOptionValues.forEach(i -> i.setOption(this));
        }
        this.values = menuItemOptionValues;
    }

    public MenuItemOption values(Set<MenuItemOptionValue> menuItemOptionValues) {
        this.setValues(menuItemOptionValues);
        return this;
    }

    public MenuItemOption addValues(MenuItemOptionValue menuItemOptionValue) {
        this.values.add(menuItemOptionValue);
        menuItemOptionValue.setOption(this);
        return this;
    }

    public MenuItemOption removeValues(MenuItemOptionValue menuItemOptionValue) {
        this.values.remove(menuItemOptionValue);
        menuItemOptionValue.setOption(null);
        return this;
    }

    public MenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public MenuItemOption menuItem(MenuItem menuItem) {
        this.setMenuItem(menuItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemOption)) {
            return false;
        }
        return getId() != null && getId().equals(((MenuItemOption) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemOption{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            ", maxSelections=" + getMaxSelections() +
            ", displayOrder=" + getDisplayOrder() +
            "}";
    }
}
