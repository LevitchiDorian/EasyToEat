package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.MenuCategoryTestSamples.*;
import static md.utm.restaurant.domain.MenuItemAllergenTestSamples.*;
import static md.utm.restaurant.domain.MenuItemOptionTestSamples.*;
import static md.utm.restaurant.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItem.class);
        MenuItem menuItem1 = getMenuItemSample1();
        MenuItem menuItem2 = new MenuItem();
        assertThat(menuItem1).isNotEqualTo(menuItem2);

        menuItem2.setId(menuItem1.getId());
        assertThat(menuItem1).isEqualTo(menuItem2);

        menuItem2 = getMenuItemSample2();
        assertThat(menuItem1).isNotEqualTo(menuItem2);
    }

    @Test
    void allergensTest() {
        MenuItem menuItem = getMenuItemRandomSampleGenerator();
        MenuItemAllergen menuItemAllergenBack = getMenuItemAllergenRandomSampleGenerator();

        menuItem.addAllergens(menuItemAllergenBack);
        assertThat(menuItem.getAllergens()).containsOnly(menuItemAllergenBack);
        assertThat(menuItemAllergenBack.getMenuItem()).isEqualTo(menuItem);

        menuItem.removeAllergens(menuItemAllergenBack);
        assertThat(menuItem.getAllergens()).doesNotContain(menuItemAllergenBack);
        assertThat(menuItemAllergenBack.getMenuItem()).isNull();

        menuItem.allergens(new HashSet<>(Set.of(menuItemAllergenBack)));
        assertThat(menuItem.getAllergens()).containsOnly(menuItemAllergenBack);
        assertThat(menuItemAllergenBack.getMenuItem()).isEqualTo(menuItem);

        menuItem.setAllergens(new HashSet<>());
        assertThat(menuItem.getAllergens()).doesNotContain(menuItemAllergenBack);
        assertThat(menuItemAllergenBack.getMenuItem()).isNull();
    }

    @Test
    void optionsTest() {
        MenuItem menuItem = getMenuItemRandomSampleGenerator();
        MenuItemOption menuItemOptionBack = getMenuItemOptionRandomSampleGenerator();

        menuItem.addOptions(menuItemOptionBack);
        assertThat(menuItem.getOptions()).containsOnly(menuItemOptionBack);
        assertThat(menuItemOptionBack.getMenuItem()).isEqualTo(menuItem);

        menuItem.removeOptions(menuItemOptionBack);
        assertThat(menuItem.getOptions()).doesNotContain(menuItemOptionBack);
        assertThat(menuItemOptionBack.getMenuItem()).isNull();

        menuItem.options(new HashSet<>(Set.of(menuItemOptionBack)));
        assertThat(menuItem.getOptions()).containsOnly(menuItemOptionBack);
        assertThat(menuItemOptionBack.getMenuItem()).isEqualTo(menuItem);

        menuItem.setOptions(new HashSet<>());
        assertThat(menuItem.getOptions()).doesNotContain(menuItemOptionBack);
        assertThat(menuItemOptionBack.getMenuItem()).isNull();
    }

    @Test
    void categoryTest() {
        MenuItem menuItem = getMenuItemRandomSampleGenerator();
        MenuCategory menuCategoryBack = getMenuCategoryRandomSampleGenerator();

        menuItem.setCategory(menuCategoryBack);
        assertThat(menuItem.getCategory()).isEqualTo(menuCategoryBack);

        menuItem.category(null);
        assertThat(menuItem.getCategory()).isNull();
    }
}
