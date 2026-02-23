package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.MenuItemOptionTestSamples.*;
import static md.utm.restaurant.domain.MenuItemOptionValueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemOptionValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemOptionValue.class);
        MenuItemOptionValue menuItemOptionValue1 = getMenuItemOptionValueSample1();
        MenuItemOptionValue menuItemOptionValue2 = new MenuItemOptionValue();
        assertThat(menuItemOptionValue1).isNotEqualTo(menuItemOptionValue2);

        menuItemOptionValue2.setId(menuItemOptionValue1.getId());
        assertThat(menuItemOptionValue1).isEqualTo(menuItemOptionValue2);

        menuItemOptionValue2 = getMenuItemOptionValueSample2();
        assertThat(menuItemOptionValue1).isNotEqualTo(menuItemOptionValue2);
    }

    @Test
    void optionTest() {
        MenuItemOptionValue menuItemOptionValue = getMenuItemOptionValueRandomSampleGenerator();
        MenuItemOption menuItemOptionBack = getMenuItemOptionRandomSampleGenerator();

        menuItemOptionValue.setOption(menuItemOptionBack);
        assertThat(menuItemOptionValue.getOption()).isEqualTo(menuItemOptionBack);

        menuItemOptionValue.option(null);
        assertThat(menuItemOptionValue.getOption()).isNull();
    }
}
