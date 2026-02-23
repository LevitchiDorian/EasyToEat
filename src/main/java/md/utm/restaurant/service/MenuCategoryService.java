package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.MenuCategory}.
 */
public interface MenuCategoryService {
    /**
     * Save a menuCategory.
     *
     * @param menuCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    MenuCategoryDTO save(MenuCategoryDTO menuCategoryDTO);

    /**
     * Updates a menuCategory.
     *
     * @param menuCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    MenuCategoryDTO update(MenuCategoryDTO menuCategoryDTO);

    /**
     * Partially updates a menuCategory.
     *
     * @param menuCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MenuCategoryDTO> partialUpdate(MenuCategoryDTO menuCategoryDTO);

    /**
     * Get all the menuCategories with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MenuCategoryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" menuCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MenuCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" menuCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
