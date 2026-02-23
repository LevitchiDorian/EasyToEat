package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.WaitingListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.WaitingList}.
 */
public interface WaitingListService {
    /**
     * Save a waitingList.
     *
     * @param waitingListDTO the entity to save.
     * @return the persisted entity.
     */
    WaitingListDTO save(WaitingListDTO waitingListDTO);

    /**
     * Updates a waitingList.
     *
     * @param waitingListDTO the entity to update.
     * @return the persisted entity.
     */
    WaitingListDTO update(WaitingListDTO waitingListDTO);

    /**
     * Partially updates a waitingList.
     *
     * @param waitingListDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WaitingListDTO> partialUpdate(WaitingListDTO waitingListDTO);

    /**
     * Get all the waitingLists with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WaitingListDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" waitingList.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WaitingListDTO> findOne(Long id);

    /**
     * Delete the "id" waitingList.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
