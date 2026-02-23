package md.utm.restaurant.service.mapper;

import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Promotion;
import md.utm.restaurant.service.dto.BrandDTO;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.dto.PromotionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Promotion} and its DTO {@link PromotionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper extends EntityMapper<PromotionDTO, Promotion> {
    @Mapping(target = "brand", source = "brand", qualifiedByName = "brandName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    PromotionDTO toDto(Promotion s);

    @Named("brandName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BrandDTO toDtoBrandName(Brand brand);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);
}
