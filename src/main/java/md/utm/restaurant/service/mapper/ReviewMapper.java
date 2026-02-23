package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.Review;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.dto.ReviewDTO;
import md.utm.restaurant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Review} and its DTO {@link ReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    @Mapping(target = "reservation", source = "reservation", qualifiedByName = "reservationReservationCode")
    @Mapping(target = "client", source = "client", qualifiedByName = "userLogin")
    ReviewDTO toDto(Review s);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);

    @Named("reservationReservationCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reservationCode", source = "reservationCode")
    ReservationDTO toDtoReservationReservationCode(Reservation reservation);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
