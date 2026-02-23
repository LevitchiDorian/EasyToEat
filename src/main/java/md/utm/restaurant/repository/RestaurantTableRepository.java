package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.RestaurantTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RestaurantTable entity.
 */
@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long>, JpaSpecificationExecutor<RestaurantTable> {
    default Optional<RestaurantTable> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RestaurantTable> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RestaurantTable> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select restaurantTable from RestaurantTable restaurantTable left join fetch restaurantTable.room",
        countQuery = "select count(restaurantTable) from RestaurantTable restaurantTable"
    )
    Page<RestaurantTable> findAllWithToOneRelationships(Pageable pageable);

    @Query("select restaurantTable from RestaurantTable restaurantTable left join fetch restaurantTable.room")
    List<RestaurantTable> findAllWithToOneRelationships();

    @Query("select restaurantTable from RestaurantTable restaurantTable left join fetch restaurantTable.room where restaurantTable.id =:id")
    Optional<RestaurantTable> findOneWithToOneRelationships(@Param("id") Long id);
}
