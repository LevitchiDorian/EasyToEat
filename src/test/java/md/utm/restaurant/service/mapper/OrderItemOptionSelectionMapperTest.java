package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.OrderItemOptionSelectionAsserts.*;
import static md.utm.restaurant.domain.OrderItemOptionSelectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemOptionSelectionMapperTest {

    private OrderItemOptionSelectionMapper orderItemOptionSelectionMapper;

    @BeforeEach
    void setUp() {
        orderItemOptionSelectionMapper = new OrderItemOptionSelectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrderItemOptionSelectionSample1();
        var actual = orderItemOptionSelectionMapper.toEntity(orderItemOptionSelectionMapper.toDto(expected));
        assertOrderItemOptionSelectionAllPropertiesEquals(expected, actual);
    }
}
