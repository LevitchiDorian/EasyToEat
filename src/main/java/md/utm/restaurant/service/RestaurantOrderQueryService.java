package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.repository.RestaurantOrderRepository;
import md.utm.restaurant.service.criteria.RestaurantOrderCriteria;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.service.mapper.RestaurantOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RestaurantOrder} entities in the database.
 * The main input is a {@link RestaurantOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RestaurantOrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RestaurantOrderQueryService extends QueryService<RestaurantOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantOrderQueryService.class);

    private final RestaurantOrderRepository restaurantOrderRepository;

    private final RestaurantOrderMapper restaurantOrderMapper;

    public RestaurantOrderQueryService(RestaurantOrderRepository restaurantOrderRepository, RestaurantOrderMapper restaurantOrderMapper) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantOrderMapper = restaurantOrderMapper;
    }

    /**
     * Return a {@link Page} of {@link RestaurantOrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RestaurantOrderDTO> findByCriteria(RestaurantOrderCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RestaurantOrder> specification = createSpecification(criteria);
        return restaurantOrderRepository.findAll(specification, page).map(restaurantOrderMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RestaurantOrderCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RestaurantOrder> specification = createSpecification(criteria);
        return restaurantOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link RestaurantOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RestaurantOrder> createSpecification(RestaurantOrderCriteria criteria) {
        Specification<RestaurantOrder> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), RestaurantOrder_.id),
                buildStringSpecification(criteria.getOrderCode(), RestaurantOrder_.orderCode),
                buildSpecification(criteria.getStatus(), RestaurantOrder_.status),
                buildSpecification(criteria.getIsPreOrder(), RestaurantOrder_.isPreOrder),
                buildRangeSpecification(criteria.getScheduledFor(), RestaurantOrder_.scheduledFor),
                buildRangeSpecification(criteria.getSubtotal(), RestaurantOrder_.subtotal),
                buildRangeSpecification(criteria.getDiscountAmount(), RestaurantOrder_.discountAmount),
                buildRangeSpecification(criteria.getTaxAmount(), RestaurantOrder_.taxAmount),
                buildRangeSpecification(criteria.getTotalAmount(), RestaurantOrder_.totalAmount),
                buildRangeSpecification(criteria.getEstimatedReadyTime(), RestaurantOrder_.estimatedReadyTime),
                buildRangeSpecification(criteria.getConfirmedAt(), RestaurantOrder_.confirmedAt),
                buildRangeSpecification(criteria.getCompletedAt(), RestaurantOrder_.completedAt),
                buildRangeSpecification(criteria.getCreatedAt(), RestaurantOrder_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), RestaurantOrder_.updatedAt),
                buildSpecification(criteria.getItemsId(), root -> root.join(RestaurantOrder_.items, JoinType.LEFT).get(OrderItem_.id)),
                buildSpecification(criteria.getPaymentsId(), root -> root.join(RestaurantOrder_.payments, JoinType.LEFT).get(Payment_.id)),
                buildSpecification(criteria.getLocationId(), root -> root.join(RestaurantOrder_.location, JoinType.LEFT).get(Location_.id)),
                buildSpecification(criteria.getClientId(), root -> root.join(RestaurantOrder_.client, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getAssignedWaiterId(), root ->
                    root.join(RestaurantOrder_.assignedWaiter, JoinType.LEFT).get(User_.id)
                ),
                buildSpecification(criteria.getTableId(), root -> root.join(RestaurantOrder_.table, JoinType.LEFT).get(RestaurantTable_.id)
                ),
                buildSpecification(criteria.getPromotionId(), root ->
                    root.join(RestaurantOrder_.promotion, JoinType.LEFT).get(Promotion_.id)
                ),
                buildSpecification(criteria.getReservationId(), root ->
                    root.join(RestaurantOrder_.reservation, JoinType.LEFT).get(Reservation_.id)
                )
            );
        }
        return specification;
    }
}
