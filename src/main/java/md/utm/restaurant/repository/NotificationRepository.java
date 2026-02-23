package md.utm.restaurant.repository;

import java.util.List;
import java.util.Optional;
import md.utm.restaurant.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select notification from Notification notification where notification.recipient.login = ?#{authentication.name}")
    List<Notification> findByRecipientIsCurrentUser();

    default Optional<Notification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Notification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Notification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select notification from Notification notification left join fetch notification.recipient left join fetch notification.location left join fetch notification.reservation left join fetch notification.order",
        countQuery = "select count(notification) from Notification notification"
    )
    Page<Notification> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select notification from Notification notification left join fetch notification.recipient left join fetch notification.location left join fetch notification.reservation left join fetch notification.order"
    )
    List<Notification> findAllWithToOneRelationships();

    @Query(
        "select notification from Notification notification left join fetch notification.recipient left join fetch notification.location left join fetch notification.reservation left join fetch notification.order where notification.id =:id"
    )
    Optional<Notification> findOneWithToOneRelationships(@Param("id") Long id);
}
