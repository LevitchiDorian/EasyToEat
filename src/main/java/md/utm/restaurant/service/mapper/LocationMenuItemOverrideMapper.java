package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.LocationMenuItemOverride;
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.LocationMenuItemOverrideDTO;
import md.utm.restaurant.service.dto.MenuItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LocationMenuItemOverride} and its DTO {@link LocationMenuItemOverrideDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationMenuItemOverrideMapper extends EntityMapper<LocationMenuItemOverrideDTO, LocationMenuItemOverride> {
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    LocationMenuItemOverrideDTO toDto(LocationMenuItemOverride s);

    @Named("menuItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MenuItemDTO toDtoMenuItemName(MenuItem menuItem);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);
}
