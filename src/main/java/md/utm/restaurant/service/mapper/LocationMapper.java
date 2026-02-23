package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.service.dto.BrandDTO;
import md.utm.restaurant.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {
    @Mapping(target = "brand", source = "brand", qualifiedByName = "brandName")
    LocationDTO toDto(Location s);

    @Named("brandName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BrandDTO toDtoBrandName(Brand brand);
}
