package md.utm.restaurant.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.ReservationTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReservationTable entity.
 */
@Repository
public interface ReservationTableRepository extends JpaRepository<ReservationTable, Long> {
    default Optional<ReservationTable> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReservationTable> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReservationTable> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reservationTable from ReservationTable reservationTable left join fetch reservationTable.table",
        countQuery = "select count(reservationTable) from ReservationTable reservationTable"
    )
    Page<ReservationTable> findAllWithToOneRelationships(Pageable pageable);

    @Query("select reservationTable from ReservationTable reservationTable left join fetch reservationTable.table")
    List<ReservationTable> findAllWithToOneRelationships();

    @Query(
        "select reservationTable from ReservationTable reservationTable left join fetch reservationTable.table where reservationTable.id =:id"
    )
    Optional<ReservationTable> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
        SELECT rt FROM ReservationTable rt
        JOIN FETCH rt.reservation r
        JOIN FETCH r.client c
        JOIN FETCH rt.table t
        JOIN t.room rm
        WHERE rm.location.id = :locationId
          AND r.reservationDate = :date
          AND r.status IN ('PENDING', 'CONFIRMED')
        """
    )
    List<ReservationTable> findActiveByLocationAndDate(@Param("locationId") Long locationId, @Param("date") LocalDate date);
}
