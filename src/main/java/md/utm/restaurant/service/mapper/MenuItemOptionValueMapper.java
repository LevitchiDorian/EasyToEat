package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.MenuItemOption;
import md.utm.restaurant.domain.MenuItemOptionValue;
import md.utm.restaurant.service.dto.MenuItemOptionDTO;
import md.utm.restaurant.service.dto.MenuItemOptionValueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuItemOptionValue} and its DTO {@link MenuItemOptionValueDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuItemOptionValueMapper extends EntityMapper<MenuItemOptionValueDTO, MenuItemOptionValue> {
    @Mapping(target = "option", source = "option", qualifiedByName = "menuItemOptionName")
    MenuItemOptionValueDTO toDto(MenuItemOptionValue s);

    @Named("menuItemOptionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuItemOptionDTO toDtoMenuItemOptionName(MenuItemOption menuItemOption);
}
