package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.MenuItemOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MenuItemOption entity.
 */
@Repository
public interface MenuItemOptionRepository extends JpaRepository<MenuItemOption, Long> {
    default Optional<MenuItemOption> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MenuItemOption> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MenuItemOption> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select menuItemOption from MenuItemOption menuItemOption left join fetch menuItemOption.menuItem",
        countQuery = "select count(menuItemOption) from MenuItemOption menuItemOption"
    )
    Page<MenuItemOption> findAllWithToOneRelationships(Pageable pageable);

    @Query("select menuItemOption from MenuItemOption menuItemOption left join fetch menuItemOption.menuItem")
    List<MenuItemOption> findAllWithToOneRelationships();

    @Query("select menuItemOption from MenuItemOption menuItemOption left join fetch menuItemOption.menuItem where menuItemOption.id =:id")
    Optional<MenuItemOption> findOneWithToOneRelationships(@Param("id") Long id);
}
