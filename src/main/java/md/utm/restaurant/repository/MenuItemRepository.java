package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MenuItem entity.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {
    default Optional<MenuItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MenuItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MenuItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select menuItem from MenuItem menuItem left join fetch menuItem.category",
        countQuery = "select count(menuItem) from MenuItem menuItem"
    )
    Page<MenuItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select menuItem from MenuItem menuItem left join fetch menuItem.category")
    List<MenuItem> findAllWithToOneRelationships();

    @Query("select menuItem from MenuItem menuItem left join fetch menuItem.category where menuItem.id =:id")
    Optional<MenuItem> findOneWithToOneRelationships(@Param("id") Long id);
}
