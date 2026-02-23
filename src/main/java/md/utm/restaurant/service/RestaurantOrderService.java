package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.RestaurantOrder}.
 */
public interface RestaurantOrderService {
    /**
     * Save a restaurantOrder.
     *
     * @param restaurantOrderDTO the entity to save.
     * @return the persisted entity.
     */
    RestaurantOrderDTO save(RestaurantOrderDTO restaurantOrderDTO);

    /**
     * Updates a restaurantOrder.
     *
     * @param restaurantOrderDTO the entity to update.
     * @return the persisted entity.
     */
    RestaurantOrderDTO update(RestaurantOrderDTO restaurantOrderDTO);

    /**
     * Partially updates a restaurantOrder.
     *
     * @param restaurantOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RestaurantOrderDTO> partialUpdate(RestaurantOrderDTO restaurantOrderDTO);

    /**
     * Get all the restaurantOrders with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RestaurantOrderDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" restaurantOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RestaurantOrderDTO> findOne(Long id);

    /**
     * Delete the "id" restaurantOrder.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
