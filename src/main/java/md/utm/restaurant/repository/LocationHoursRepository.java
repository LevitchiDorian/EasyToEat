package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.LocationHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LocationHours entity.
 */
@Repository
public interface LocationHoursRepository extends JpaRepository<LocationHours, Long> {
    default Optional<LocationHours> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LocationHours> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LocationHours> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select locationHours from LocationHours locationHours left join fetch locationHours.location",
        countQuery = "select count(locationHours) from LocationHours locationHours"
    )
    Page<LocationHours> findAllWithToOneRelationships(Pageable pageable);

    @Query("select locationHours from LocationHours locationHours left join fetch locationHours.location")
    List<LocationHours> findAllWithToOneRelationships();

    @Query("select locationHours from LocationHours locationHours left join fetch locationHours.location where locationHours.id =:id")
    Optional<LocationHours> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select lh from LocationHours lh where lh.location.id in :locationIds and lh.dayOfWeek = :day")
    List<LocationHours> findByLocationIdsAndDay(@Param("locationIds") List<Long> locationIds, @Param("day") java.time.DayOfWeek day);

    @Query("select lh from LocationHours lh where lh.location.id = :locationId and lh.dayOfWeek = :day")
    Optional<LocationHours> findByLocationIdAndDay(@Param("locationId") Long locationId, @Param("day") java.time.DayOfWeek day);
}
