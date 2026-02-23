package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.LocationMenuItemOverrideDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.LocationMenuItemOverride}.
 */
public interface LocationMenuItemOverrideService {
    /**
     * Save a locationMenuItemOverride.
     *
     * @param locationMenuItemOverrideDTO the entity to save.
     * @return the persisted entity.
     */
    LocationMenuItemOverrideDTO save(LocationMenuItemOverrideDTO locationMenuItemOverrideDTO);

    /**
     * Updates a locationMenuItemOverride.
     *
     * @param locationMenuItemOverrideDTO the entity to update.
     * @return the persisted entity.
     */
    LocationMenuItemOverrideDTO update(LocationMenuItemOverrideDTO locationMenuItemOverrideDTO);

    /**
     * Partially updates a locationMenuItemOverride.
     *
     * @param locationMenuItemOverrideDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LocationMenuItemOverrideDTO> partialUpdate(LocationMenuItemOverrideDTO locationMenuItemOverrideDTO);

    /**
     * Get all the locationMenuItemOverrides with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LocationMenuItemOverrideDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" locationMenuItemOverride.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LocationMenuItemOverrideDTO> findOne(Long id);

    /**
     * Delete the "id" locationMenuItemOverride.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
