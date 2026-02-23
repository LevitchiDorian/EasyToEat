package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.PromotionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.Promotion}.
 */
public interface PromotionService {
    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save.
     * @return the persisted entity.
     */
    PromotionDTO save(PromotionDTO promotionDTO);

    /**
     * Updates a promotion.
     *
     * @param promotionDTO the entity to update.
     * @return the persisted entity.
     */
    PromotionDTO update(PromotionDTO promotionDTO);

    /**
     * Partially updates a promotion.
     *
     * @param promotionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PromotionDTO> partialUpdate(PromotionDTO promotionDTO);

    /**
     * Get all the promotions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PromotionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" promotion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PromotionDTO> findOne(Long id);

    /**
     * Delete the "id" promotion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
