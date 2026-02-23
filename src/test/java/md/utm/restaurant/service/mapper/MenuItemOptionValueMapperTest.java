package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.MenuItemOptionValueAsserts.*;
import static md.utm.restaurant.domain.MenuItemOptionValueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemOptionValueMapperTest {

    private MenuItemOptionValueMapper menuItemOptionValueMapper;

    @BeforeEach
    void setUp() {
        menuItemOptionValueMapper = new MenuItemOptionValueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemOptionValueSample1();
        var actual = menuItemOptionValueMapper.toEntity(menuItemOptionValueMapper.toDto(expected));
        assertMenuItemOptionValueAllPropertiesEquals(expected, actual);
    }
}
