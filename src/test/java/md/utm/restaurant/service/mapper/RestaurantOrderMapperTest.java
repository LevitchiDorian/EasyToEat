package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.RestaurantOrderAsserts.*;
import static md.utm.restaurant.domain.RestaurantOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurantOrderMapperTest {

    private RestaurantOrderMapper restaurantOrderMapper;

    @BeforeEach
    void setUp() {
        restaurantOrderMapper = new RestaurantOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRestaurantOrderSample1();
        var actual = restaurantOrderMapper.toEntity(restaurantOrderMapper.toDto(expected));
        assertRestaurantOrderAllPropertiesEquals(expected, actual);
    }
}
