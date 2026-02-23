package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.MenuItemOptionTestSamples.*;
import static md.utm.restaurant.domain.MenuItemOptionValueTestSamples.*;
import static md.utm.restaurant.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemOptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemOption.class);
        MenuItemOption menuItemOption1 = getMenuItemOptionSample1();
        MenuItemOption menuItemOption2 = new MenuItemOption();
        assertThat(menuItemOption1).isNotEqualTo(menuItemOption2);

        menuItemOption2.setId(menuItemOption1.getId());
        assertThat(menuItemOption1).isEqualTo(menuItemOption2);

        menuItemOption2 = getMenuItemOptionSample2();
        assertThat(menuItemOption1).isNotEqualTo(menuItemOption2);
    }

    @Test
    void valuesTest() {
        MenuItemOption menuItemOption = getMenuItemOptionRandomSampleGenerator();
        MenuItemOptionValue menuItemOptionValueBack = getMenuItemOptionValueRandomSampleGenerator();

        menuItemOption.addValues(menuItemOptionValueBack);
        assertThat(menuItemOption.getValues()).containsOnly(menuItemOptionValueBack);
        assertThat(menuItemOptionValueBack.getOption()).isEqualTo(menuItemOption);

        menuItemOption.removeValues(menuItemOptionValueBack);
        assertThat(menuItemOption.getValues()).doesNotContain(menuItemOptionValueBack);
        assertThat(menuItemOptionValueBack.getOption()).isNull();

        menuItemOption.values(new HashSet<>(Set.of(menuItemOptionValueBack)));
        assertThat(menuItemOption.getValues()).containsOnly(menuItemOptionValueBack);
        assertThat(menuItemOptionValueBack.getOption()).isEqualTo(menuItemOption);

        menuItemOption.setValues(new HashSet<>());
        assertThat(menuItemOption.getValues()).doesNotContain(menuItemOptionValueBack);
        assertThat(menuItemOptionValueBack.getOption()).isNull();
    }

    @Test
    void menuItemTest() {
        MenuItemOption menuItemOption = getMenuItemOptionRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        menuItemOption.setMenuItem(menuItemBack);
        assertThat(menuItemOption.getMenuItem()).isEqualTo(menuItemBack);

        menuItemOption.menuItem(null);
        assertThat(menuItemOption.getMenuItem()).isNull();
    }
}
