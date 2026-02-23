package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.domain.OrderItem;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.service.dto.MenuItemDTO;
import md.utm.restaurant.service.dto.OrderItemDTO;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemName")
    @Mapping(target = "order", source = "order", qualifiedByName = "restaurantOrderId")
    OrderItemDTO toDto(OrderItem s);

    @Named("menuItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuItemDTO toDtoMenuItemName(MenuItem menuItem);

    @Named("restaurantOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantOrderDTO toDtoRestaurantOrderId(RestaurantOrder restaurantOrder);
}
