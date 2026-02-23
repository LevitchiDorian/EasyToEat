package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.repository.LocationRepository;
import md.utm.restaurant.service.criteria.LocationCriteria;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.mapper.LocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Location} entities in the database.
 * The main input is a {@link LocationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LocationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LocationQueryService extends QueryService<Location> {

    private static final Logger LOG = LoggerFactory.getLogger(LocationQueryService.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    public LocationQueryService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    /**
     * Return a {@link Page} of {@link LocationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LocationDTO> findByCriteria(LocationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Location> specification = createSpecification(criteria);
        return locationRepository.findAll(specification, page).map(locationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LocationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Location> specification = createSpecification(criteria);
        return locationRepository.count(specification);
    }

    /**
     * Function to convert {@link LocationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Location> createSpecification(LocationCriteria criteria) {
        Specification<Location> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Location_.id),
                buildStringSpecification(criteria.getName(), Location_.name),
                buildStringSpecification(criteria.getAddress(), Location_.address),
                buildStringSpecification(criteria.getCity(), Location_.city),
                buildStringSpecification(criteria.getPhone(), Location_.phone),
                buildStringSpecification(criteria.getEmail(), Location_.email),
                buildRangeSpecification(criteria.getLatitude(), Location_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Location_.longitude),
                buildRangeSpecification(criteria.getReservationDurationOverride(), Location_.reservationDurationOverride),
                buildRangeSpecification(criteria.getMaxAdvanceBookingDaysOverride(), Location_.maxAdvanceBookingDaysOverride),
                buildRangeSpecification(criteria.getCancellationDeadlineOverride(), Location_.cancellationDeadlineOverride),
                buildSpecification(criteria.getIsActive(), Location_.isActive),
                buildRangeSpecification(criteria.getCreatedAt(), Location_.createdAt),
                buildSpecification(criteria.getHoursId(), root -> root.join(Location_.hours, JoinType.LEFT).get(LocationHours_.id)),
                buildSpecification(criteria.getRoomsId(), root -> root.join(Location_.rooms, JoinType.LEFT).get(DiningRoom_.id)),
                buildSpecification(criteria.getLocalPromotionsId(), root ->
                    root.join(Location_.localPromotions, JoinType.LEFT).get(Promotion_.id)
                ),
                buildSpecification(criteria.getMenuOverridesId(), root ->
                    root.join(Location_.menuOverrides, JoinType.LEFT).get(LocationMenuItemOverride_.id)
                ),
                buildSpecification(criteria.getBrandId(), root -> root.join(Location_.brand, JoinType.LEFT).get(Brand_.id))
            );
        }
        return specification;
    }
}
