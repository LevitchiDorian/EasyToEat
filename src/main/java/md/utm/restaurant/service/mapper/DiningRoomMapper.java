package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DiningRoom} and its DTO {@link DiningRoomDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiningRoomMapper extends EntityMapper<DiningRoomDTO, DiningRoom> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    DiningRoomDTO toDto(DiningRoom s);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);
}
