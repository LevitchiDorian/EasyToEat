package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemOptionValueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemOptionValueDTO.class);
        MenuItemOptionValueDTO menuItemOptionValueDTO1 = new MenuItemOptionValueDTO();
        menuItemOptionValueDTO1.setId(1L);
        MenuItemOptionValueDTO menuItemOptionValueDTO2 = new MenuItemOptionValueDTO();
        assertThat(menuItemOptionValueDTO1).isNotEqualTo(menuItemOptionValueDTO2);
        menuItemOptionValueDTO2.setId(menuItemOptionValueDTO1.getId());
        assertThat(menuItemOptionValueDTO1).isEqualTo(menuItemOptionValueDTO2);
        menuItemOptionValueDTO2.setId(2L);
        assertThat(menuItemOptionValueDTO1).isNotEqualTo(menuItemOptionValueDTO2);
        menuItemOptionValueDTO1.setId(null);
        assertThat(menuItemOptionValueDTO1).isNotEqualTo(menuItemOptionValueDTO2);
    }
}
