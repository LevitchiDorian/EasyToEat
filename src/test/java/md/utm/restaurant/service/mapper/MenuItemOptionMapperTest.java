package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.MenuItemOptionAsserts.*;
import static md.utm.restaurant.domain.MenuItemOptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemOptionMapperTest {

    private MenuItemOptionMapper menuItemOptionMapper;

    @BeforeEach
    void setUp() {
        menuItemOptionMapper = new MenuItemOptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemOptionSample1();
        var actual = menuItemOptionMapper.toEntity(menuItemOptionMapper.toDto(expected));
        assertMenuItemOptionAllPropertiesEquals(expected, actual);
    }
}
