package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.BrandTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.PromotionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PromotionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Promotion.class);
        Promotion promotion1 = getPromotionSample1();
        Promotion promotion2 = new Promotion();
        assertThat(promotion1).isNotEqualTo(promotion2);

        promotion2.setId(promotion1.getId());
        assertThat(promotion1).isEqualTo(promotion2);

        promotion2 = getPromotionSample2();
        assertThat(promotion1).isNotEqualTo(promotion2);
    }

    @Test
    void brandTest() {
        Promotion promotion = getPromotionRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        promotion.setBrand(brandBack);
        assertThat(promotion.getBrand()).isEqualTo(brandBack);

        promotion.brand(null);
        assertThat(promotion.getBrand()).isNull();
    }

    @Test
    void locationTest() {
        Promotion promotion = getPromotionRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        promotion.setLocation(locationBack);
        assertThat(promotion.getLocation()).isEqualTo(locationBack);

        promotion.location(null);
        assertThat(promotion.getLocation()).isNull();
    }
}
