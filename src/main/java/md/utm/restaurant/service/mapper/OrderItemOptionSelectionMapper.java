package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.MenuItemOptionValue;
import md.utm.restaurant.domain.OrderItem;
import md.utm.restaurant.domain.OrderItemOptionSelection;
import md.utm.restaurant.service.dto.MenuItemOptionValueDTO;
import md.utm.restaurant.service.dto.OrderItemDTO;
import md.utm.restaurant.service.dto.OrderItemOptionSelectionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItemOptionSelection} and its DTO {@link OrderItemOptionSelectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemOptionSelectionMapper extends EntityMapper<OrderItemOptionSelectionDTO, OrderItemOptionSelection> {
    @Mapping(target = "optionValue", source = "optionValue", qualifiedByName = "menuItemOptionValueLabel")
    @Mapping(target = "orderItem", source = "orderItem", qualifiedByName = "orderItemId")
    OrderItemOptionSelectionDTO toDto(OrderItemOptionSelection s);

    @Named("menuItemOptionValueLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    MenuItemOptionValueDTO toDtoMenuItemOptionValueLabel(MenuItemOptionValue menuItemOptionValue);

    @Named("orderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderItemDTO toDtoOrderItemId(OrderItem orderItem);
}
