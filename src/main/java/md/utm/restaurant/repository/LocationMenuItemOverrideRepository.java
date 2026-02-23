package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.LocationMenuItemOverride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LocationMenuItemOverride entity.
 */
@Repository
public interface LocationMenuItemOverrideRepository
    extends JpaRepository<LocationMenuItemOverride, Long>, JpaSpecificationExecutor<LocationMenuItemOverride> {
    default Optional<LocationMenuItemOverride> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LocationMenuItemOverride> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LocationMenuItemOverride> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select locationMenuItemOverride from LocationMenuItemOverride locationMenuItemOverride left join fetch locationMenuItemOverride.menuItem left join fetch locationMenuItemOverride.location",
        countQuery = "select count(locationMenuItemOverride) from LocationMenuItemOverride locationMenuItemOverride"
    )
    Page<LocationMenuItemOverride> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select locationMenuItemOverride from LocationMenuItemOverride locationMenuItemOverride left join fetch locationMenuItemOverride.menuItem left join fetch locationMenuItemOverride.location"
    )
    List<LocationMenuItemOverride> findAllWithToOneRelationships();

    @Query(
        "select locationMenuItemOverride from LocationMenuItemOverride locationMenuItemOverride left join fetch locationMenuItemOverride.menuItem left join fetch locationMenuItemOverride.location where locationMenuItemOverride.id =:id"
    )
    Optional<LocationMenuItemOverride> findOneWithToOneRelationships(@Param("id") Long id);
}
