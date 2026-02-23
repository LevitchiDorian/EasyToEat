package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.domain.MenuItemAllergen;
import md.utm.restaurant.service.dto.MenuItemAllergenDTO;
import md.utm.restaurant.service.dto.MenuItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuItemAllergen} and its DTO {@link MenuItemAllergenDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuItemAllergenMapper extends EntityMapper<MenuItemAllergenDTO, MenuItemAllergen> {
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemName")
    MenuItemAllergenDTO toDto(MenuItemAllergen s);

    @Named("menuItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuItemDTO toDtoMenuItemName(MenuItem menuItem);
}
