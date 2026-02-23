package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.MenuItemOptionValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MenuItemOptionValue entity.
 */
@Repository
public interface MenuItemOptionValueRepository extends JpaRepository<MenuItemOptionValue, Long> {
    default Optional<MenuItemOptionValue> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MenuItemOptionValue> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MenuItemOptionValue> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select menuItemOptionValue from MenuItemOptionValue menuItemOptionValue left join fetch menuItemOptionValue.option",
        countQuery = "select count(menuItemOptionValue) from MenuItemOptionValue menuItemOptionValue"
    )
    Page<MenuItemOptionValue> findAllWithToOneRelationships(Pageable pageable);

    @Query("select menuItemOptionValue from MenuItemOptionValue menuItemOptionValue left join fetch menuItemOptionValue.option")
    List<MenuItemOptionValue> findAllWithToOneRelationships();

    @Query(
        "select menuItemOptionValue from MenuItemOptionValue menuItemOptionValue left join fetch menuItemOptionValue.option where menuItemOptionValue.id =:id"
    )
    Optional<MenuItemOptionValue> findOneWithToOneRelationships(@Param("id") Long id);
}
