package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.LocationHours;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.LocationHoursDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LocationHours} and its DTO {@link LocationHoursDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationHoursMapper extends EntityMapper<LocationHoursDTO, LocationHours> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    LocationHoursDTO toDto(LocationHours s);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);
}
