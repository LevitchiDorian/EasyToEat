package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.RestaurantTable}.
 */
public interface RestaurantTableService {
    /**
     * Save a restaurantTable.
     *
     * @param restaurantTableDTO the entity to save.
     * @return the persisted entity.
     */
    RestaurantTableDTO save(RestaurantTableDTO restaurantTableDTO);

    /**
     * Updates a restaurantTable.
     *
     * @param restaurantTableDTO the entity to update.
     * @return the persisted entity.
     */
    RestaurantTableDTO update(RestaurantTableDTO restaurantTableDTO);

    /**
     * Partially updates a restaurantTable.
     *
     * @param restaurantTableDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RestaurantTableDTO> partialUpdate(RestaurantTableDTO restaurantTableDTO);

    /**
     * Get all the restaurantTables with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RestaurantTableDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" restaurantTable.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RestaurantTableDTO> findOne(Long id);

    /**
     * Delete the "id" restaurantTable.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
