package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemAllergenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemAllergenDTO.class);
        MenuItemAllergenDTO menuItemAllergenDTO1 = new MenuItemAllergenDTO();
        menuItemAllergenDTO1.setId(1L);
        MenuItemAllergenDTO menuItemAllergenDTO2 = new MenuItemAllergenDTO();
        assertThat(menuItemAllergenDTO1).isNotEqualTo(menuItemAllergenDTO2);
        menuItemAllergenDTO2.setId(menuItemAllergenDTO1.getId());
        assertThat(menuItemAllergenDTO1).isEqualTo(menuItemAllergenDTO2);
        menuItemAllergenDTO2.setId(2L);
        assertThat(menuItemAllergenDTO1).isNotEqualTo(menuItemAllergenDTO2);
        menuItemAllergenDTO1.setId(null);
        assertThat(menuItemAllergenDTO1).isNotEqualTo(menuItemAllergenDTO2);
    }
}
