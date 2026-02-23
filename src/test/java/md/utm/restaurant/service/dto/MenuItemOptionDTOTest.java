package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemOptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemOptionDTO.class);
        MenuItemOptionDTO menuItemOptionDTO1 = new MenuItemOptionDTO();
        menuItemOptionDTO1.setId(1L);
        MenuItemOptionDTO menuItemOptionDTO2 = new MenuItemOptionDTO();
        assertThat(menuItemOptionDTO1).isNotEqualTo(menuItemOptionDTO2);
        menuItemOptionDTO2.setId(menuItemOptionDTO1.getId());
        assertThat(menuItemOptionDTO1).isEqualTo(menuItemOptionDTO2);
        menuItemOptionDTO2.setId(2L);
        assertThat(menuItemOptionDTO1).isNotEqualTo(menuItemOptionDTO2);
        menuItemOptionDTO1.setId(null);
        assertThat(menuItemOptionDTO1).isNotEqualTo(menuItemOptionDTO2);
    }
}
