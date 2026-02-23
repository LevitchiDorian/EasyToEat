package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.domain.MenuItemOption;
import md.utm.restaurant.service.dto.MenuItemDTO;
import md.utm.restaurant.service.dto.MenuItemOptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuItemOption} and its DTO {@link MenuItemOptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuItemOptionMapper extends EntityMapper<MenuItemOptionDTO, MenuItemOption> {
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemName")
    MenuItemOptionDTO toDto(MenuItemOption s);

    @Named("menuItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuItemDTO toDtoMenuItemName(MenuItem menuItem);
}
