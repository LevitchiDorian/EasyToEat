package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RestaurantTable} and its DTO {@link RestaurantTableDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantTableMapper extends EntityMapper<RestaurantTableDTO, RestaurantTable> {
    @Mapping(target = "room", source = "room", qualifiedByName = "diningRoomName")
    RestaurantTableDTO toDto(RestaurantTable s);

    @Named("diningRoomName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DiningRoomDTO toDtoDiningRoomName(DiningRoom diningRoom);
}
