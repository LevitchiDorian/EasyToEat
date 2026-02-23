package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.WaitingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WaitingList entity.
 */
@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Long>, JpaSpecificationExecutor<WaitingList> {
    @Query("select waitingList from WaitingList waitingList where waitingList.client.login = ?#{authentication.name}")
    List<WaitingList> findByClientIsCurrentUser();

    default Optional<WaitingList> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WaitingList> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WaitingList> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select waitingList from WaitingList waitingList left join fetch waitingList.location left join fetch waitingList.client left join fetch waitingList.room",
        countQuery = "select count(waitingList) from WaitingList waitingList"
    )
    Page<WaitingList> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select waitingList from WaitingList waitingList left join fetch waitingList.location left join fetch waitingList.client left join fetch waitingList.room"
    )
    List<WaitingList> findAllWithToOneRelationships();

    @Query(
        "select waitingList from WaitingList waitingList left join fetch waitingList.location left join fetch waitingList.client left join fetch waitingList.room where waitingList.id =:id"
    )
    Optional<WaitingList> findOneWithToOneRelationships(@Param("id") Long id);
}
