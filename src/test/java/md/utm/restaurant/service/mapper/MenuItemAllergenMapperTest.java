package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.MenuItemAllergenAsserts.*;
import static md.utm.restaurant.domain.MenuItemAllergenTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemAllergenMapperTest {

    private MenuItemAllergenMapper menuItemAllergenMapper;

    @BeforeEach
    void setUp() {
        menuItemAllergenMapper = new MenuItemAllergenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemAllergenSample1();
        var actual = menuItemAllergenMapper.toEntity(menuItemAllergenMapper.toDto(expected));
        assertMenuItemAllergenAllPropertiesEquals(expected, actual);
    }
}
