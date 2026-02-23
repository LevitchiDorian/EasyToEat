package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
import md.utm.restaurant.service.dto.MenuItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuItem} and its DTO {@link MenuItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuItemMapper extends EntityMapper<MenuItemDTO, MenuItem> {
    @Mapping(target = "category", source = "category", qualifiedByName = "menuCategoryName")
    MenuItemDTO toDto(MenuItem s);

    @Named("menuCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuCategoryDTO toDtoMenuCategoryName(MenuCategory menuCategory);
}
