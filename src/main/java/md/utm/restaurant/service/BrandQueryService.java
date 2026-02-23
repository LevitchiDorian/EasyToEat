package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.repository.BrandRepository;
import md.utm.restaurant.service.criteria.BrandCriteria;
import md.utm.restaurant.service.dto.BrandDTO;
import md.utm.restaurant.service.mapper.BrandMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Brand} entities in the database.
 * The main input is a {@link BrandCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BrandDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BrandQueryService extends QueryService<Brand> {

    private static final Logger LOG = LoggerFactory.getLogger(BrandQueryService.class);

    private final BrandRepository brandRepository;

    private final BrandMapper brandMapper;

    public BrandQueryService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    /**
     * Return a {@link Page} of {@link BrandDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BrandDTO> findByCriteria(BrandCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Brand> specification = createSpecification(criteria);
        return brandRepository.findAll(specification, page).map(brandMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BrandCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Brand> specification = createSpecification(criteria);
        return brandRepository.count(specification);
    }

    /**
     * Function to convert {@link BrandCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Brand> createSpecification(BrandCriteria criteria) {
        Specification<Brand> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Brand_.id),
                buildStringSpecification(criteria.getName(), Brand_.name),
                buildStringSpecification(criteria.getLogoUrl(), Brand_.logoUrl),
                buildStringSpecification(criteria.getCoverImageUrl(), Brand_.coverImageUrl),
                buildStringSpecification(criteria.getPrimaryColor(), Brand_.primaryColor),
                buildStringSpecification(criteria.getSecondaryColor(), Brand_.secondaryColor),
                buildStringSpecification(criteria.getWebsite(), Brand_.website),
                buildStringSpecification(criteria.getContactEmail(), Brand_.contactEmail),
                buildStringSpecification(criteria.getContactPhone(), Brand_.contactPhone),
                buildRangeSpecification(criteria.getDefaultReservationDuration(), Brand_.defaultReservationDuration),
                buildRangeSpecification(criteria.getMaxAdvanceBookingDays(), Brand_.maxAdvanceBookingDays),
                buildRangeSpecification(criteria.getCancellationDeadlineHours(), Brand_.cancellationDeadlineHours),
                buildSpecification(criteria.getIsActive(), Brand_.isActive),
                buildRangeSpecification(criteria.getCreatedAt(), Brand_.createdAt),
                buildSpecification(criteria.getLocationsId(), root -> root.join(Brand_.locations, JoinType.LEFT).get(Location_.id)),
                buildSpecification(criteria.getCategoriesId(), root -> root.join(Brand_.categories, JoinType.LEFT).get(MenuCategory_.id)),
                buildSpecification(criteria.getPromotionsId(), root -> root.join(Brand_.promotions, JoinType.LEFT).get(Promotion_.id))
            );
        }
        return specification;
    }
}
