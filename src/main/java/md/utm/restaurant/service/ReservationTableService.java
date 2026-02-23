package md.utm.restaurant.service;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.service.dto.ReservationTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link md.utm.restaurant.domain.ReservationTable}.
 */
public interface ReservationTableService {
    /**
     * Save a reservationTable.
     *
     * @param reservationTableDTO the entity to save.
     * @return the persisted entity.
     */
    ReservationTableDTO save(ReservationTableDTO reservationTableDTO);

    /**
     * Updates a reservationTable.
     *
     * @param reservationTableDTO the entity to update.
     * @return the persisted entity.
     */
    ReservationTableDTO update(ReservationTableDTO reservationTableDTO);

    /**
     * Partially updates a reservationTable.
     *
     * @param reservationTableDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReservationTableDTO> partialUpdate(ReservationTableDTO reservationTableDTO);

    /**
     * Get all the reservationTables.
     *
     * @return the list of entities.
     */
    List<ReservationTableDTO> findAll();

    /**
     * Get all the reservationTables with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReservationTableDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" reservationTable.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReservationTableDTO> findOne(Long id);

    /**
     * Delete the "id" reservationTable.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
