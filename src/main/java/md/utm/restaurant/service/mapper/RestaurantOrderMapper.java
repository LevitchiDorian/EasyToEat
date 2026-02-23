package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Promotion;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.PromotionDTO;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import md.utm.restaurant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RestaurantOrder} and its DTO {@link RestaurantOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantOrderMapper extends EntityMapper<RestaurantOrderDTO, RestaurantOrder> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    @Mapping(target = "client", source = "client", qualifiedByName = "userLogin")
    @Mapping(target = "assignedWaiter", source = "assignedWaiter", qualifiedByName = "userLogin")
    @Mapping(target = "table", source = "table", qualifiedByName = "restaurantTableTableNumber")
    @Mapping(target = "promotion", source = "promotion", qualifiedByName = "promotionCode")
    @Mapping(target = "reservation", source = "reservation", qualifiedByName = "reservationId")
    RestaurantOrderDTO toDto(RestaurantOrder s);

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

    @Named("restaurantTableTableNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tableNumber", source = "tableNumber")
    RestaurantTableDTO toDtoRestaurantTableTableNumber(RestaurantTable restaurantTable);

    @Named("promotionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    PromotionDTO toDtoPromotionCode(Promotion promotion);

    @Named("reservationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReservationDTO toDtoReservationId(Reservation reservation);
}
