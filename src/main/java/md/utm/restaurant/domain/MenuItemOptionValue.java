package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MenuItemOptionValue.
 */
@Entity
@Table(name = "menu_item_option_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemOptionValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "label", length = 100, nullable = false)
    private String label;

    @NotNull
    @Column(name = "price_adjustment", precision = 21, scale = 2, nullable = false)
    private BigDecimal priceAdjustment;

    @NotNull
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Min(value = 0)
    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "values", "menuItem" }, allowSetters = true)
    private MenuItemOption option;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MenuItemOptionValue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public MenuItemOptionValue label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPriceAdjustment() {
        return this.priceAdjustment;
    }

    public MenuItemOptionValue priceAdjustment(BigDecimal priceAdjustment) {
        this.setPriceAdjustment(priceAdjustment);
        return this;
    }

    public void setPriceAdjustment(BigDecimal priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public MenuItemOptionValue isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public MenuItemOptionValue isAvailable(Boolean isAvailable) {
        this.setIsAvailable(isAvailable);
        return this;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public MenuItemOptionValue displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public MenuItemOption getOption() {
        return this.option;
    }

    public void setOption(MenuItemOption menuItemOption) {
        this.option = menuItemOption;
    }

    public MenuItemOptionValue option(MenuItemOption menuItemOption) {
        this.setOption(menuItemOption);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemOptionValue)) {
            return false;
        }
        return getId() != null && getId().equals(((MenuItemOptionValue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemOptionValue{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", priceAdjustment=" + getPriceAdjustment() +
            ", isDefault='" + getIsDefault() + "'" +
            ", isAvailable='" + getIsAvailable() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            "}";
    }
}
