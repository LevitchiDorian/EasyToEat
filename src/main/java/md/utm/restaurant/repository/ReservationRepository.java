package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    @Query("select reservation from Reservation reservation where reservation.client.login = ?#{authentication.name}")
    List<Reservation> findByClientIsCurrentUser();

    default Optional<Reservation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reservation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reservation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reservation from Reservation reservation left join fetch reservation.location left join fetch reservation.client left join fetch reservation.room",
        countQuery = "select count(reservation) from Reservation reservation"
    )
    Page<Reservation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select reservation from Reservation reservation left join fetch reservation.location left join fetch reservation.client left join fetch reservation.room"
    )
    List<Reservation> findAllWithToOneRelationships();

    @Query(
        "select reservation from Reservation reservation left join fetch reservation.location left join fetch reservation.client left join fetch reservation.room where reservation.id =:id"
    )
    Optional<Reservation> findOneWithToOneRelationships(@Param("id") Long id);
}
