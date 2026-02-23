package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.ReservationTable;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.dto.ReservationTableDTO;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReservationTable} and its DTO {@link ReservationTableDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationTableMapper extends EntityMapper<ReservationTableDTO, ReservationTable> {
    @Mapping(target = "table", source = "table", qualifiedByName = "restaurantTableTableNumber")
    @Mapping(target = "reservation", source = "reservation", qualifiedByName = "reservationId")
    ReservationTableDTO toDto(ReservationTable s);

    @Named("restaurantTableTableNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tableNumber", source = "tableNumber")
    RestaurantTableDTO toDtoRestaurantTableTableNumber(RestaurantTable restaurantTable);

    @Named("reservationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReservationDTO toDtoReservationId(Reservation reservation);
}
