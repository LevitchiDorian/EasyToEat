package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.DiningRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DiningRoom entity.
 */
@Repository
public interface DiningRoomRepository extends JpaRepository<DiningRoom, Long>, JpaSpecificationExecutor<DiningRoom> {
    default Optional<DiningRoom> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DiningRoom> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DiningRoom> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select diningRoom from DiningRoom diningRoom left join fetch diningRoom.location",
        countQuery = "select count(diningRoom) from DiningRoom diningRoom"
    )
    Page<DiningRoom> findAllWithToOneRelationships(Pageable pageable);

    @Query("select diningRoom from DiningRoom diningRoom left join fetch diningRoom.location")
    List<DiningRoom> findAllWithToOneRelationships();

    @Query("select diningRoom from DiningRoom diningRoom left join fetch diningRoom.location where diningRoom.id =:id")
    Optional<DiningRoom> findOneWithToOneRelationships(@Param("id") Long id);
}
