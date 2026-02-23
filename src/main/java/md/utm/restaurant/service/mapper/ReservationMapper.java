package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reservation} and its DTO {@link ReservationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    @Mapping(target = "client", source = "client", qualifiedByName = "userLogin")
    @Mapping(target = "room", source = "room", qualifiedByName = "diningRoomName")
    ReservationDTO toDto(Reservation s);

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
