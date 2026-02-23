package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Promotion entity.
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {
    default Optional<Promotion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Promotion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Promotion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select promotion from Promotion promotion left join fetch promotion.brand left join fetch promotion.location",
        countQuery = "select count(promotion) from Promotion promotion"
    )
    Page<Promotion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select promotion from Promotion promotion left join fetch promotion.brand left join fetch promotion.location")
    List<Promotion> findAllWithToOneRelationships();

    @Query(
        "select promotion from Promotion promotion left join fetch promotion.brand left join fetch promotion.location where promotion.id =:id"
    )
    Optional<Promotion> findOneWithToOneRelationships(@Param("id") Long id);
}
