package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.domain.WaitingList;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.UserDTO;
import md.utm.restaurant.service.dto.WaitingListDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WaitingList} and its DTO {@link WaitingListDTO}.
 */
@Mapper(componentModel = "spring")
public interface WaitingListMapper extends EntityMapper<WaitingListDTO, WaitingList> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    @Mapping(target = "client", source = "client", qualifiedByName = "userLogin")
    @Mapping(target = "room", source = "room", qualifiedByName = "diningRoomName")
    WaitingListDTO toDto(WaitingList s);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("diningRoomName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DiningRoomDTO toDtoDiningRoomName(DiningRoom diningRoom);
}
