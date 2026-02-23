package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Notification;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.NotificationDTO;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "recipient", source = "recipient", qualifiedByName = "userLogin")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    @Mapping(target = "reservation", source = "reservation", qualifiedByName = "reservationReservationCode")
    @Mapping(target = "order", source = "order", qualifiedByName = "restaurantOrderOrderCode")
    NotificationDTO toDto(Notification s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

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

    @Named("restaurantOrderOrderCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderCode", source = "orderCode")
    RestaurantOrderDTO toDtoRestaurantOrderOrderCode(RestaurantOrder restaurantOrder);
}
