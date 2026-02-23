package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.MenuCategoryAsserts.*;
import static md.utm.restaurant.domain.MenuCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuCategoryMapperTest {

    private MenuCategoryMapper menuCategoryMapper;

    @BeforeEach
    void setUp() {
        menuCategoryMapper = new MenuCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuCategorySample1();
        var actual = menuCategoryMapper.toEntity(menuCategoryMapper.toDto(expected));
        assertMenuCategoryAllPropertiesEquals(expected, actual);
    }
}
