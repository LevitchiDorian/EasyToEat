package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.MenuItemAllergenTestSamples.*;
import static md.utm.restaurant.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemAllergenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemAllergen.class);
        MenuItemAllergen menuItemAllergen1 = getMenuItemAllergenSample1();
        MenuItemAllergen menuItemAllergen2 = new MenuItemAllergen();
        assertThat(menuItemAllergen1).isNotEqualTo(menuItemAllergen2);

        menuItemAllergen2.setId(menuItemAllergen1.getId());
        assertThat(menuItemAllergen1).isEqualTo(menuItemAllergen2);

        menuItemAllergen2 = getMenuItemAllergenSample2();
        assertThat(menuItemAllergen1).isNotEqualTo(menuItemAllergen2);
    }

    @Test
    void menuItemTest() {
        MenuItemAllergen menuItemAllergen = getMenuItemAllergenRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        menuItemAllergen.setMenuItem(menuItemBack);
        assertThat(menuItemAllergen.getMenuItem()).isEqualTo(menuItemBack);

        menuItemAllergen.menuItem(null);
        assertThat(menuItemAllergen.getMenuItem()).isNull();
    }
}
