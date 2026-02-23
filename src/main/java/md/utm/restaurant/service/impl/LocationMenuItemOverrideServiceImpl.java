package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.LocationMenuItemOverride;
import md.utm.restaurant.repository.LocationMenuItemOverrideRepository;
import md.utm.restaurant.service.LocationMenuItemOverrideService;
import md.utm.restaurant.service.dto.LocationMenuItemOverrideDTO;
import md.utm.restaurant.service.mapper.LocationMenuItemOverrideMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.LocationMenuItemOverride}.
 */
@Service
@Transactional
public class LocationMenuItemOverrideServiceImpl implements LocationMenuItemOverrideService {

    private static final Logger LOG = LoggerFactory.getLogger(LocationMenuItemOverrideServiceImpl.class);

    private final LocationMenuItemOverrideRepository locationMenuItemOverrideRepository;

    private final LocationMenuItemOverrideMapper locationMenuItemOverrideMapper;

    public LocationMenuItemOverrideServiceImpl(
        LocationMenuItemOverrideRepository locationMenuItemOverrideRepository,
        LocationMenuItemOverrideMapper locationMenuItemOverrideMapper
    ) {
        this.locationMenuItemOverrideRepository = locationMenuItemOverrideRepository;
        this.locationMenuItemOverrideMapper = locationMenuItemOverrideMapper;
    }

    @Override
    public LocationMenuItemOverrideDTO save(LocationMenuItemOverrideDTO locationMenuItemOverrideDTO) {
        LOG.debug("Request to save LocationMenuItemOverride : {}", locationMenuItemOverrideDTO);
        LocationMenuItemOverride locationMenuItemOverride = locationMenuItemOverrideMapper.toEntity(locationMenuItemOverrideDTO);
        locationMenuItemOverride = locationMenuItemOverrideRepository.save(locationMenuItemOverride);
        return locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);
    }

    @Override
    public LocationMenuItemOverrideDTO update(LocationMenuItemOverrideDTO locationMenuItemOverrideDTO) {
        LOG.debug("Request to update LocationMenuItemOverride : {}", locationMenuItemOverrideDTO);
        LocationMenuItemOverride locationMenuItemOverride = locationMenuItemOverrideMapper.toEntity(locationMenuItemOverrideDTO);
        locationMenuItemOverride = locationMenuItemOverrideRepository.save(locationMenuItemOverride);
        return locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);
    }

    @Override
    public Optional<LocationMenuItemOverrideDTO> partialUpdate(LocationMenuItemOverrideDTO locationMenuItemOverrideDTO) {
        LOG.debug("Request to partially update LocationMenuItemOverride : {}", locationMenuItemOverrideDTO);

        return locationMenuItemOverrideRepository
            .findById(locationMenuItemOverrideDTO.getId())
            .map(existingLocationMenuItemOverride -> {
                locationMenuItemOverrideMapper.partialUpdate(existingLocationMenuItemOverride, locationMenuItemOverrideDTO);

                return existingLocationMenuItemOverride;
            })
            .map(locationMenuItemOverrideRepository::save)
            .map(locationMenuItemOverrideMapper::toDto);
    }

    public Page<LocationMenuItemOverrideDTO> findAllWithEagerRelationships(Pageable pageable) {
        return locationMenuItemOverrideRepository.findAllWithEagerRelationships(pageable).map(locationMenuItemOverrideMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocationMenuItemOverrideDTO> findOne(Long id) {
        LOG.debug("Request to get LocationMenuItemOverride : {}", id);
        return locationMenuItemOverrideRepository.findOneWithEagerRelationships(id).map(locationMenuItemOverrideMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete LocationMenuItemOverride : {}", id);
        locationMenuItemOverrideRepository.deleteById(id);
    }
}
