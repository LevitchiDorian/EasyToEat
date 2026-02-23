package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.MenuItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.MenuItem}.
 */
public interface MenuItemService {
    /**
     * Save a menuItem.
     *
     * @param menuItemDTO the entity to save.
     * @return the persisted entity.
     */
    MenuItemDTO save(MenuItemDTO menuItemDTO);

    /**
     * Updates a menuItem.
     *
     * @param menuItemDTO the entity to update.
     * @return the persisted entity.
     */
    MenuItemDTO update(MenuItemDTO menuItemDTO);

    /**
     * Partially updates a menuItem.
     *
     * @param menuItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MenuItemDTO> partialUpdate(MenuItemDTO menuItemDTO);

    /**
     * Get all the menuItems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MenuItemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" menuItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MenuItemDTO> findOne(Long id);

    /**
     * Delete the "id" menuItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
