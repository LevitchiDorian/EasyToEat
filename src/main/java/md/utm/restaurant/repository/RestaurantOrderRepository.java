package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.RestaurantOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RestaurantOrder entity.
 */
@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long>, JpaSpecificationExecutor<RestaurantOrder> {
    @Query("select restaurantOrder from RestaurantOrder restaurantOrder where restaurantOrder.client.login = ?#{authentication.name}")
    List<RestaurantOrder> findByClientIsCurrentUser();

    @Query(
        "select restaurantOrder from RestaurantOrder restaurantOrder where restaurantOrder.assignedWaiter.login = ?#{authentication.name}"
    )
    List<RestaurantOrder> findByAssignedWaiterIsCurrentUser();

    default Optional<RestaurantOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RestaurantOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RestaurantOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select restaurantOrder from RestaurantOrder restaurantOrder left join fetch restaurantOrder.location left join fetch restaurantOrder.client left join fetch restaurantOrder.assignedWaiter left join fetch restaurantOrder.table left join fetch restaurantOrder.promotion",
        countQuery = "select count(restaurantOrder) from RestaurantOrder restaurantOrder"
    )
    Page<RestaurantOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select restaurantOrder from RestaurantOrder restaurantOrder left join fetch restaurantOrder.location left join fetch restaurantOrder.client left join fetch restaurantOrder.assignedWaiter left join fetch restaurantOrder.table left join fetch restaurantOrder.promotion"
    )
    List<RestaurantOrder> findAllWithToOneRelationships();

    @Query(
        "select restaurantOrder from RestaurantOrder restaurantOrder left join fetch restaurantOrder.location left join fetch restaurantOrder.client left join fetch restaurantOrder.assignedWaiter left join fetch restaurantOrder.table left join fetch restaurantOrder.promotion where restaurantOrder.id =:id"
    )
    Optional<RestaurantOrder> findOneWithToOneRelationships(@Param("id") Long id);
}
