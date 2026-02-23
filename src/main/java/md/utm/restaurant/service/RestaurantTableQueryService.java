package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.repository.RestaurantTableRepository;
import md.utm.restaurant.service.criteria.RestaurantTableCriteria;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import md.utm.restaurant.service.mapper.RestaurantTableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RestaurantTable} entities in the database.
 * The main input is a {@link RestaurantTableCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RestaurantTableDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RestaurantTableQueryService extends QueryService<RestaurantTable> {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantTableQueryService.class);

    private final RestaurantTableRepository restaurantTableRepository;

    private final RestaurantTableMapper restaurantTableMapper;

    public RestaurantTableQueryService(RestaurantTableRepository restaurantTableRepository, RestaurantTableMapper restaurantTableMapper) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantTableMapper = restaurantTableMapper;
    }

    /**
     * Return a {@link Page} of {@link RestaurantTableDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RestaurantTableDTO> findByCriteria(RestaurantTableCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RestaurantTable> specification = createSpecification(criteria);
        return restaurantTableRepository.findAll(specification, page).map(restaurantTableMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RestaurantTableCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RestaurantTable> specification = createSpecification(criteria);
        return restaurantTableRepository.count(specification);
    }

    /**
     * Function to convert {@link RestaurantTableCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RestaurantTable> createSpecification(RestaurantTableCriteria criteria) {
        Specification<RestaurantTable> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), RestaurantTable_.id),
                buildStringSpecification(criteria.getTableNumber(), RestaurantTable_.tableNumber),
                buildSpecification(criteria.getShape(), RestaurantTable_.shape),
                buildRangeSpecification(criteria.getMinCapacity(), RestaurantTable_.minCapacity),
                buildRangeSpecification(criteria.getMaxCapacity(), RestaurantTable_.maxCapacity),
                buildRangeSpecification(criteria.getPositionX(), RestaurantTable_.positionX),
                buildRangeSpecification(criteria.getPositionY(), RestaurantTable_.positionY),
                buildRangeSpecification(criteria.getWidthPx(), RestaurantTable_.widthPx),
                buildRangeSpecification(criteria.getHeightPx(), RestaurantTable_.heightPx),
                buildRangeSpecification(criteria.getRotation(), RestaurantTable_.rotation),
                buildSpecification(criteria.getStatus(), RestaurantTable_.status),
                buildSpecification(criteria.getIsActive(), RestaurantTable_.isActive),
                buildStringSpecification(criteria.getNotes(), RestaurantTable_.notes),
                buildSpecification(criteria.getRoomId(), root -> root.join(RestaurantTable_.room, JoinType.LEFT).get(DiningRoom_.id))
            );
        }
        return specification;
    }
}
