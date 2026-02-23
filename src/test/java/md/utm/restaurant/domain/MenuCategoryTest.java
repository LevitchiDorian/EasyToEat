package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.BrandTestSamples.*;
import static md.utm.restaurant.domain.MenuCategoryTestSamples.*;
import static md.utm.restaurant.domain.MenuCategoryTestSamples.*;
import static md.utm.restaurant.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuCategory.class);
        MenuCategory menuCategory1 = getMenuCategorySample1();
        MenuCategory menuCategory2 = new MenuCategory();
        assertThat(menuCategory1).isNotEqualTo(menuCategory2);

        menuCategory2.setId(menuCategory1.getId());
        assertThat(menuCategory1).isEqualTo(menuCategory2);

        menuCategory2 = getMenuCategorySample2();
        assertThat(menuCategory1).isNotEqualTo(menuCategory2);
    }

    @Test
    void itemsTest() {
        MenuCategory menuCategory = getMenuCategoryRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        menuCategory.addItems(menuItemBack);
        assertThat(menuCategory.getItems()).containsOnly(menuItemBack);
        assertThat(menuItemBack.getCategory()).isEqualTo(menuCategory);

        menuCategory.removeItems(menuItemBack);
        assertThat(menuCategory.getItems()).doesNotContain(menuItemBack);
        assertThat(menuItemBack.getCategory()).isNull();

        menuCategory.items(new HashSet<>(Set.of(menuItemBack)));
        assertThat(menuCategory.getItems()).containsOnly(menuItemBack);
        assertThat(menuItemBack.getCategory()).isEqualTo(menuCategory);

        menuCategory.setItems(new HashSet<>());
        assertThat(menuCategory.getItems()).doesNotContain(menuItemBack);
        assertThat(menuItemBack.getCategory()).isNull();
    }

    @Test
    void parentTest() {
        MenuCategory menuCategory = getMenuCategoryRandomSampleGenerator();
        MenuCategory menuCategoryBack = getMenuCategoryRandomSampleGenerator();

        menuCategory.setParent(menuCategoryBack);
        assertThat(menuCategory.getParent()).isEqualTo(menuCategoryBack);

        menuCategory.parent(null);
        assertThat(menuCategory.getParent()).isNull();
    }

    @Test
    void brandTest() {
        MenuCategory menuCategory = getMenuCategoryRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        menuCategory.setBrand(brandBack);
        assertThat(menuCategory.getBrand()).isEqualTo(brandBack);

        menuCategory.brand(null);
        assertThat(menuCategory.getBrand()).isNull();
    }
}
