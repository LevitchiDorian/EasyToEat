package md.utm.restaurant.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.LocationHours;
import md.utm.restaurant.repository.LocationHoursRepository;
import md.utm.restaurant.service.dto.LocationHoursDTO;
import md.utm.restaurant.service.mapper.LocationHoursMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.LocationHours}.
 */
@Service
@Transactional
public class LocationHoursService {

    private static final Logger LOG = LoggerFactory.getLogger(LocationHoursService.class);

    private final LocationHoursRepository locationHoursRepository;

    private final LocationHoursMapper locationHoursMapper;

    public LocationHoursService(LocationHoursRepository locationHoursRepository, LocationHoursMapper locationHoursMapper) {
        this.locationHoursRepository = locationHoursRepository;
        this.locationHoursMapper = locationHoursMapper;
    }

    /**
     * Save a locationHours.
     *
     * @param locationHoursDTO the entity to save.
     * @return the persisted entity.
     */
    public LocationHoursDTO save(LocationHoursDTO locationHoursDTO) {
        LOG.debug("Request to save LocationHours : {}", locationHoursDTO);
        LocationHours locationHours = locationHoursMapper.toEntity(locationHoursDTO);
        locationHours = locationHoursRepository.save(locationHours);
        return locationHoursMapper.toDto(locationHours);
    }

    /**
     * Update a locationHours.
     *
     * @param locationHoursDTO the entity to save.
     * @return the persisted entity.
     */
    public LocationHoursDTO update(LocationHoursDTO locationHoursDTO) {
        LOG.debug("Request to update LocationHours : {}", locationHoursDTO);
        LocationHours locationHours = locationHoursMapper.toEntity(locationHoursDTO);
        locationHours = locationHoursRepository.save(locationHours);
        return locationHoursMapper.toDto(locationHours);
    }

    /**
     * Partially update a locationHours.
     *
     * @param locationHoursDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LocationHoursDTO> partialUpdate(LocationHoursDTO locationHoursDTO) {
        LOG.debug("Request to partially update LocationHours : {}", locationHoursDTO);

        return locationHoursRepository
            .findById(locationHoursDTO.getId())
            .map(existingLocationHours -> {
                locationHoursMapper.partialUpdate(existingLocationHours, locationHoursDTO);

                return existingLocationHours;
            })
            .map(locationHoursRepository::save)
            .map(locationHoursMapper::toDto);
    }

    /**
     * Get all the locationHours.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LocationHoursDTO> findAll() {
        LOG.debug("Request to get all LocationHours");
        return locationHoursRepository.findAll().stream().map(locationHoursMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the locationHours with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LocationHoursDTO> findAllWithEagerRelationships(Pageable pageable) {
        return locationHoursRepository.findAllWithEagerRelationships(pageable).map(locationHoursMapper::toDto);
    }

    /**
     * Get one locationHours by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LocationHoursDTO> findOne(Long id) {
        LOG.debug("Request to get LocationHours : {}", id);
        return locationHoursRepository.findOneWithEagerRelationships(id).map(locationHoursMapper::toDto);
    }

    /**
     * Delete the locationHours by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LocationHours : {}", id);
        locationHoursRepository.deleteById(id);
    }
}
