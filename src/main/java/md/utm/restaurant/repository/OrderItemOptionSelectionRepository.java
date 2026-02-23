package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.OrderItemOptionSelection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderItemOptionSelection entity.
 */
@Repository
public interface OrderItemOptionSelectionRepository extends JpaRepository<OrderItemOptionSelection, Long> {
    default Optional<OrderItemOptionSelection> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OrderItemOptionSelection> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OrderItemOptionSelection> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select orderItemOptionSelection from OrderItemOptionSelection orderItemOptionSelection left join fetch orderItemOptionSelection.optionValue",
        countQuery = "select count(orderItemOptionSelection) from OrderItemOptionSelection orderItemOptionSelection"
    )
    Page<OrderItemOptionSelection> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select orderItemOptionSelection from OrderItemOptionSelection orderItemOptionSelection left join fetch orderItemOptionSelection.optionValue"
    )
    List<OrderItemOptionSelection> findAllWithToOneRelationships();

    @Query(
        "select orderItemOptionSelection from OrderItemOptionSelection orderItemOptionSelection left join fetch orderItemOptionSelection.optionValue where orderItemOptionSelection.id =:id"
    )
    Optional<OrderItemOptionSelection> findOneWithToOneRelationships(@Param("id") Long id);
}
