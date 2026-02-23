package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.service.dto.BrandDTO;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuCategory} and its DTO {@link MenuCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuCategoryMapper extends EntityMapper<MenuCategoryDTO, MenuCategory> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "menuCategoryName")
    @Mapping(target = "brand", source = "brand", qualifiedByName = "brandName")
    MenuCategoryDTO toDto(MenuCategory s);

    @Named("menuCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuCategoryDTO toDtoMenuCategoryName(MenuCategory menuCategory);

    @Named("brandName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BrandDTO toDtoBrandName(Brand brand);
}
