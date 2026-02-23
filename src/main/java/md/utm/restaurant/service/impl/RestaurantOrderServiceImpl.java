package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.repository.RestaurantOrderRepository;
import md.utm.restaurant.service.RestaurantOrderService;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.service.mapper.RestaurantOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.RestaurantOrder}.
 */
@Service
@Transactional
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantOrderServiceImpl.class);

    private final RestaurantOrderRepository restaurantOrderRepository;

    private final RestaurantOrderMapper restaurantOrderMapper;

    public RestaurantOrderServiceImpl(RestaurantOrderRepository restaurantOrderRepository, RestaurantOrderMapper restaurantOrderMapper) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantOrderMapper = restaurantOrderMapper;
    }

    @Override
    public RestaurantOrderDTO save(RestaurantOrderDTO restaurantOrderDTO) {
        LOG.debug("Request to save RestaurantOrder : {}", restaurantOrderDTO);
        RestaurantOrder restaurantOrder = restaurantOrderMapper.toEntity(restaurantOrderDTO);
        restaurantOrder = restaurantOrderRepository.save(restaurantOrder);
        return restaurantOrderMapper.toDto(restaurantOrder);
    }

    @Override
    public RestaurantOrderDTO update(RestaurantOrderDTO restaurantOrderDTO) {
        LOG.debug("Request to update RestaurantOrder : {}", restaurantOrderDTO);
        RestaurantOrder restaurantOrder = restaurantOrderMapper.toEntity(restaurantOrderDTO);
        restaurantOrder = restaurantOrderRepository.save(restaurantOrder);
        return restaurantOrderMapper.toDto(restaurantOrder);
    }

    @Override
    public Optional<RestaurantOrderDTO> partialUpdate(RestaurantOrderDTO restaurantOrderDTO) {
        LOG.debug("Request to partially update RestaurantOrder : {}", restaurantOrderDTO);

        return restaurantOrderRepository
            .findById(restaurantOrderDTO.getId())
            .map(existingRestaurantOrder -> {
                restaurantOrderMapper.partialUpdate(existingRestaurantOrder, restaurantOrderDTO);

                return existingRestaurantOrder;
            })
            .map(restaurantOrderRepository::save)
            .map(restaurantOrderMapper::toDto);
    }

    public Page<RestaurantOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return restaurantOrderRepository.findAllWithEagerRelationships(pageable).map(restaurantOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantOrderDTO> findOne(Long id) {
        LOG.debug("Request to get RestaurantOrder : {}", id);
        return restaurantOrderRepository.findOneWithEagerRelationships(id).map(restaurantOrderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete RestaurantOrder : {}", id);
        restaurantOrderRepository.deleteById(id);
    }
}
