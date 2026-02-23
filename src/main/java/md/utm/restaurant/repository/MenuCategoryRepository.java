package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MenuCategory entity.
 */
@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long>, JpaSpecificationExecutor<MenuCategory> {
    default Optional<MenuCategory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MenuCategory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MenuCategory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select menuCategory from MenuCategory menuCategory left join fetch menuCategory.parent left join fetch menuCategory.brand",
        countQuery = "select count(menuCategory) from MenuCategory menuCategory"
    )
    Page<MenuCategory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select menuCategory from MenuCategory menuCategory left join fetch menuCategory.parent left join fetch menuCategory.brand")
    List<MenuCategory> findAllWithToOneRelationships();

    @Query(
        "select menuCategory from MenuCategory menuCategory left join fetch menuCategory.parent left join fetch menuCategory.brand where menuCategory.id =:id"
    )
    Optional<MenuCategory> findOneWithToOneRelationships(@Param("id") Long id);
}
