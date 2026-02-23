package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.repository.RestaurantTableRepository;
import md.utm.restaurant.service.RestaurantTableService;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import md.utm.restaurant.service.mapper.RestaurantTableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.RestaurantTable}.
 */
@Service
@Transactional
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantTableServiceImpl.class);

    private final RestaurantTableRepository restaurantTableRepository;

    private final RestaurantTableMapper restaurantTableMapper;

    public RestaurantTableServiceImpl(RestaurantTableRepository restaurantTableRepository, RestaurantTableMapper restaurantTableMapper) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantTableMapper = restaurantTableMapper;
    }

    @Override
    public RestaurantTableDTO save(RestaurantTableDTO restaurantTableDTO) {
        LOG.debug("Request to save RestaurantTable : {}", restaurantTableDTO);
        RestaurantTable restaurantTable = restaurantTableMapper.toEntity(restaurantTableDTO);
        restaurantTable = restaurantTableRepository.save(restaurantTable);
        return restaurantTableMapper.toDto(restaurantTable);
    }

    @Override
    public RestaurantTableDTO update(RestaurantTableDTO restaurantTableDTO) {
        LOG.debug("Request to update RestaurantTable : {}", restaurantTableDTO);
        RestaurantTable restaurantTable = restaurantTableMapper.toEntity(restaurantTableDTO);
        restaurantTable = restaurantTableRepository.save(restaurantTable);
        return restaurantTableMapper.toDto(restaurantTable);
    }

    @Override
    public Optional<RestaurantTableDTO> partialUpdate(RestaurantTableDTO restaurantTableDTO) {
        LOG.debug("Request to partially update RestaurantTable : {}", restaurantTableDTO);

        return restaurantTableRepository
            .findById(restaurantTableDTO.getId())
            .map(existingRestaurantTable -> {
                restaurantTableMapper.partialUpdate(existingRestaurantTable, restaurantTableDTO);

                return existingRestaurantTable;
            })
            .map(restaurantTableRepository::save)
            .map(restaurantTableMapper::toDto);
    }

    public Page<RestaurantTableDTO> findAllWithEagerRelationships(Pageable pageable) {
        return restaurantTableRepository.findAllWithEagerRelationships(pageable).map(restaurantTableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantTableDTO> findOne(Long id) {
        LOG.debug("Request to get RestaurantTable : {}", id);
        return restaurantTableRepository.findOneWithEagerRelationships(id).map(restaurantTableMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete RestaurantTable : {}", id);
        restaurantTableRepository.deleteById(id);
    }
}
