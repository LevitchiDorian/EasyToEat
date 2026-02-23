package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.MenuItemAsserts.*;
import static md.utm.restaurant.domain.MenuItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemMapperTest {

    private MenuItemMapper menuItemMapper;

    @BeforeEach
    void setUp() {
        menuItemMapper = new MenuItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemSample1();
        var actual = menuItemMapper.toEntity(menuItemMapper.toDto(expected));
        assertMenuItemAllPropertiesEquals(expected, actual);
    }
}
