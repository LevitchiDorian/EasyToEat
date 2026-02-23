package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.MenuItemAllergen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MenuItemAllergen entity.
 */
@Repository
public interface MenuItemAllergenRepository extends JpaRepository<MenuItemAllergen, Long> {
    default Optional<MenuItemAllergen> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MenuItemAllergen> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MenuItemAllergen> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select menuItemAllergen from MenuItemAllergen menuItemAllergen left join fetch menuItemAllergen.menuItem",
        countQuery = "select count(menuItemAllergen) from MenuItemAllergen menuItemAllergen"
    )
    Page<MenuItemAllergen> findAllWithToOneRelationships(Pageable pageable);

    @Query("select menuItemAllergen from MenuItemAllergen menuItemAllergen left join fetch menuItemAllergen.menuItem")
    List<MenuItemAllergen> findAllWithToOneRelationships();

    @Query(
        "select menuItemAllergen from MenuItemAllergen menuItemAllergen left join fetch menuItemAllergen.menuItem where menuItemAllergen.id =:id"
    )
    Optional<MenuItemAllergen> findOneWithToOneRelationships(@Param("id") Long id);
}
