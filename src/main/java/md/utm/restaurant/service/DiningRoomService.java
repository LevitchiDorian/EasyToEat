package md.utm.restaurant.service;

import java.util.Optional;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.DiningRoom}.
 */
public interface DiningRoomService {
    /**
     * Save a diningRoom.
     *
     * @param diningRoomDTO the entity to save.
     * @return the persisted entity.
     */
    DiningRoomDTO save(DiningRoomDTO diningRoomDTO);

    /**
     * Updates a diningRoom.
     *
     * @param diningRoomDTO the entity to update.
     * @return the persisted entity.
     */
    DiningRoomDTO update(DiningRoomDTO diningRoomDTO);

    /**
     * Partially updates a diningRoom.
     *
     * @param diningRoomDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DiningRoomDTO> partialUpdate(DiningRoomDTO diningRoomDTO);

    /**
     * Get all the diningRooms with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DiningRoomDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" diningRoom.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DiningRoomDTO> findOne(Long id);

    /**
     * Delete the "id" diningRoom.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
