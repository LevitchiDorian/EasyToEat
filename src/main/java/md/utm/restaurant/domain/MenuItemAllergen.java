package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import md.utm.restaurant.domain.enumeration.AllergenType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MenuItemAllergen.
 */
@Entity
@Table(name = "menu_item_allergen")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemAllergen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen", nullable = false)
    private AllergenType allergen;

    @Size(max = 255)
    @Column(name = "notes", length = 255)
    private String notes;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "allergens", "options", "category" }, allowSetters = true)
    private MenuItem menuItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MenuItemAllergen id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AllergenType getAllergen() {
        return this.allergen;
    }

    public MenuItemAllergen allergen(AllergenType allergen) {
        this.setAllergen(allergen);
        return this;
    }

    public void setAllergen(AllergenType allergen) {
        this.allergen = allergen;
    }

    public String getNotes() {
        return this.notes;
    }

    public MenuItemAllergen notes(String notes) {
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

    public MenuItemAllergen menuItem(MenuItem menuItem) {
        this.setMenuItem(menuItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemAllergen)) {
            return false;
        }
        return getId() != null && getId().equals(((MenuItemAllergen) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemAllergen{" +
            "id=" + getId() +
            ", allergen='" + getAllergen() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
