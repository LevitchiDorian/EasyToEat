package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Review entity.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Query("select review from Review review where review.client.login = ?#{authentication.name}")
    List<Review> findByClientIsCurrentUser();

    default Optional<Review> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Review> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Review> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select review from Review review left join fetch review.location left join fetch review.reservation left join fetch review.client",
        countQuery = "select count(review) from Review review"
    )
    Page<Review> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select review from Review review left join fetch review.location left join fetch review.reservation left join fetch review.client"
    )
    List<Review> findAllWithToOneRelationships();

    @Query(
        "select review from Review review left join fetch review.location left join fetch review.reservation left join fetch review.client where review.id =:id"
    )
    Optional<Review> findOneWithToOneRelationships(@Param("id") Long id);
}
