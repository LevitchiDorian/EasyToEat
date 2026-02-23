package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.RestaurantTableAsserts.*;
import static md.utm.restaurant.domain.RestaurantTableTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurantTableMapperTest {

    private RestaurantTableMapper restaurantTableMapper;

    @BeforeEach
    void setUp() {
        restaurantTableMapper = new RestaurantTableMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRestaurantTableSample1();
        var actual = restaurantTableMapper.toEntity(restaurantTableMapper.toDto(expected));
        assertRestaurantTableAllPropertiesEquals(expected, actual);
    }
}
