package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.repository.DiningRoomRepository;
import md.utm.restaurant.service.criteria.DiningRoomCriteria;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.mapper.DiningRoomMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DiningRoom} entities in the database.
 * The main input is a {@link DiningRoomCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DiningRoomDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DiningRoomQueryService extends QueryService<DiningRoom> {

    private static final Logger LOG = LoggerFactory.getLogger(DiningRoomQueryService.class);

    private final DiningRoomRepository diningRoomRepository;

    private final DiningRoomMapper diningRoomMapper;

    public DiningRoomQueryService(DiningRoomRepository diningRoomRepository, DiningRoomMapper diningRoomMapper) {
        this.diningRoomRepository = diningRoomRepository;
        this.diningRoomMapper = diningRoomMapper;
    }

    /**
     * Return a {@link Page} of {@link DiningRoomDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DiningRoomDTO> findByCriteria(DiningRoomCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DiningRoom> specification = createSpecification(criteria);
        return diningRoomRepository.findAll(specification, page).map(diningRoomMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DiningRoomCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DiningRoom> specification = createSpecification(criteria);
        return diningRoomRepository.count(specification);
    }

    /**
     * Function to convert {@link DiningRoomCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DiningRoom> createSpecification(DiningRoomCriteria criteria) {
        Specification<DiningRoom> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), DiningRoom_.id),
                buildStringSpecification(criteria.getName(), DiningRoom_.name),
                buildStringSpecification(criteria.getDescription(), DiningRoom_.description),
                buildRangeSpecification(criteria.getFloor(), DiningRoom_.floor),
                buildRangeSpecification(criteria.getCapacity(), DiningRoom_.capacity),
                buildSpecification(criteria.getIsActive(), DiningRoom_.isActive),
                buildStringSpecification(criteria.getFloorPlanUrl(), DiningRoom_.floorPlanUrl),
                buildRangeSpecification(criteria.getWidthPx(), DiningRoom_.widthPx),
                buildRangeSpecification(criteria.getHeightPx(), DiningRoom_.heightPx),
                buildSpecification(criteria.getTablesId(), root -> root.join(DiningRoom_.tables, JoinType.LEFT).get(RestaurantTable_.id)),
                buildSpecification(criteria.getLocationId(), root -> root.join(DiningRoom_.location, JoinType.LEFT).get(Location_.id))
            );
        }
        return specification;
    }
}
