package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.BrandTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.MenuCategoryTestSamples.*;
import static md.utm.restaurant.domain.PromotionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BrandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = getBrandSample1();
        Brand brand2 = new Brand();
        assertThat(brand1).isNotEqualTo(brand2);

        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);

        brand2 = getBrandSample2();
        assertThat(brand1).isNotEqualTo(brand2);
    }

    @Test
    void locationsTest() {
        Brand brand = getBrandRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        brand.addLocations(locationBack);
        assertThat(brand.getLocations()).containsOnly(locationBack);
        assertThat(locationBack.getBrand()).isEqualTo(brand);

        brand.removeLocations(locationBack);
        assertThat(brand.getLocations()).doesNotContain(locationBack);
        assertThat(locationBack.getBrand()).isNull();

        brand.locations(new HashSet<>(Set.of(locationBack)));
        assertThat(brand.getLocations()).containsOnly(locationBack);
        assertThat(locationBack.getBrand()).isEqualTo(brand);

        brand.setLocations(new HashSet<>());
        assertThat(brand.getLocations()).doesNotContain(locationBack);
        assertThat(locationBack.getBrand()).isNull();
    }

    @Test
    void categoriesTest() {
        Brand brand = getBrandRandomSampleGenerator();
        MenuCategory menuCategoryBack = getMenuCategoryRandomSampleGenerator();

        brand.addCategories(menuCategoryBack);
        assertThat(brand.getCategories()).containsOnly(menuCategoryBack);
        assertThat(menuCategoryBack.getBrand()).isEqualTo(brand);

        brand.removeCategories(menuCategoryBack);
        assertThat(brand.getCategories()).doesNotContain(menuCategoryBack);
        assertThat(menuCategoryBack.getBrand()).isNull();

        brand.categories(new HashSet<>(Set.of(menuCategoryBack)));
        assertThat(brand.getCategories()).containsOnly(menuCategoryBack);
        assertThat(menuCategoryBack.getBrand()).isEqualTo(brand);

        brand.setCategories(new HashSet<>());
        assertThat(brand.getCategories()).doesNotContain(menuCategoryBack);
        assertThat(menuCategoryBack.getBrand()).isNull();
    }

    @Test
    void promotionsTest() {
        Brand brand = getBrandRandomSampleGenerator();
        Promotion promotionBack = getPromotionRandomSampleGenerator();

        brand.addPromotions(promotionBack);
        assertThat(brand.getPromotions()).containsOnly(promotionBack);
        assertThat(promotionBack.getBrand()).isEqualTo(brand);

        brand.removePromotions(promotionBack);
        assertThat(brand.getPromotions()).doesNotContain(promotionBack);
        assertThat(promotionBack.getBrand()).isNull();

        brand.promotions(new HashSet<>(Set.of(promotionBack)));
        assertThat(brand.getPromotions()).containsOnly(promotionBack);
        assertThat(promotionBack.getBrand()).isEqualTo(brand);

        brand.setPromotions(new HashSet<>());
        assertThat(brand.getPromotions()).doesNotContain(promotionBack);
        assertThat(promotionBack.getBrand()).isNull();
    }
}
