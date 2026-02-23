package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Payment;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.service.dto.PaymentDTO;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "processedBy", source = "processedBy", qualifiedByName = "userLogin")
    @Mapping(target = "order", source = "order", qualifiedByName = "restaurantOrderOrderCode")
    PaymentDTO toDto(Payment s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("restaurantOrderOrderCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderCode", source = "orderCode")
    RestaurantOrderDTO toDtoRestaurantOrderOrderCode(RestaurantOrder restaurantOrder);
}
