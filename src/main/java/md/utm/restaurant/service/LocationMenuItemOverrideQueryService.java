package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.LocationMenuItemOverride;
import md.utm.restaurant.repository.LocationMenuItemOverrideRepository;
import md.utm.restaurant.service.criteria.LocationMenuItemOverrideCriteria;
import md.utm.restaurant.service.dto.LocationMenuItemOverrideDTO;
import md.utm.restaurant.service.mapper.LocationMenuItemOverrideMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LocationMenuItemOverride} entities in the database.
 * The main input is a {@link LocationMenuItemOverrideCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LocationMenuItemOverrideDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LocationMenuItemOverrideQueryService extends QueryService<LocationMenuItemOverride> {

    private static final Logger LOG = LoggerFactory.getLogger(LocationMenuItemOverrideQueryService.class);

    private final LocationMenuItemOverrideRepository locationMenuItemOverrideRepository;

    private final LocationMenuItemOverrideMapper locationMenuItemOverrideMapper;

    public LocationMenuItemOverrideQueryService(
        LocationMenuItemOverrideRepository locationMenuItemOverrideRepository,
        LocationMenuItemOverrideMapper locationMenuItemOverrideMapper
    ) {
        this.locationMenuItemOverrideRepository = locationMenuItemOverrideRepository;
        this.locationMenuItemOverrideMapper = locationMenuItemOverrideMapper;
    }

    /**
     * Return a {@link Page} of {@link LocationMenuItemOverrideDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LocationMenuItemOverrideDTO> findByCriteria(LocationMenuItemOverrideCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LocationMenuItemOverride> specification = createSpecification(criteria);
        return locationMenuItemOverrideRepository.findAll(specification, page).map(locationMenuItemOverrideMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LocationMenuItemOverrideCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LocationMenuItemOverride> specification = createSpecification(criteria);
        return locationMenuItemOverrideRepository.count(specification);
    }

    /**
     * Function to convert {@link LocationMenuItemOverrideCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LocationMenuItemOverride> createSpecification(LocationMenuItemOverrideCriteria criteria) {
        Specification<LocationMenuItemOverride> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), LocationMenuItemOverride_.id),
                buildSpecification(criteria.getIsAvailableAtLocation(), LocationMenuItemOverride_.isAvailableAtLocation),
                buildRangeSpecification(criteria.getPriceOverride(), LocationMenuItemOverride_.priceOverride),
                buildRangeSpecification(criteria.getPreparationTimeOverride(), LocationMenuItemOverride_.preparationTimeOverride),
                buildStringSpecification(criteria.getNotes(), LocationMenuItemOverride_.notes),
                buildSpecification(criteria.getMenuItemId(), root ->
                    root.join(LocationMenuItemOverride_.menuItem, JoinType.LEFT).get(MenuItem_.id)
                ),
                buildSpecification(criteria.getLocationId(), root ->
                    root.join(LocationMenuItemOverride_.location, JoinType.LEFT).get(Location_.id)
                )
            );
        }
        return specification;
    }
}
