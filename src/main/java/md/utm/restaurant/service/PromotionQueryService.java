package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.Promotion;
import md.utm.restaurant.repository.PromotionRepository;
import md.utm.restaurant.service.criteria.PromotionCriteria;
import md.utm.restaurant.service.dto.PromotionDTO;
import md.utm.restaurant.service.mapper.PromotionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Promotion} entities in the database.
 * The main input is a {@link PromotionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PromotionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PromotionQueryService extends QueryService<Promotion> {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionQueryService.class);

    private final PromotionRepository promotionRepository;

    private final PromotionMapper promotionMapper;

    public PromotionQueryService(PromotionRepository promotionRepository, PromotionMapper promotionMapper) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
    }

    /**
     * Return a {@link Page} of {@link PromotionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PromotionDTO> findByCriteria(PromotionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Promotion> specification = createSpecification(criteria);
        return promotionRepository.findAll(specification, page).map(promotionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PromotionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Promotion> specification = createSpecification(criteria);
        return promotionRepository.count(specification);
    }

    /**
     * Function to convert {@link PromotionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Promotion> createSpecification(PromotionCriteria criteria) {
        Specification<Promotion> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Promotion_.id),
                buildStringSpecification(criteria.getCode(), Promotion_.code),
                buildStringSpecification(criteria.getName(), Promotion_.name),
                buildSpecification(criteria.getDiscountType(), Promotion_.discountType),
                buildRangeSpecification(criteria.getDiscountValue(), Promotion_.discountValue),
                buildRangeSpecification(criteria.getMinimumOrderAmount(), Promotion_.minimumOrderAmount),
                buildRangeSpecification(criteria.getMaxUsageCount(), Promotion_.maxUsageCount),
                buildRangeSpecification(criteria.getCurrentUsageCount(), Promotion_.currentUsageCount),
                buildRangeSpecification(criteria.getStartDate(), Promotion_.startDate),
                buildRangeSpecification(criteria.getEndDate(), Promotion_.endDate),
                buildSpecification(criteria.getIsActive(), Promotion_.isActive),
                buildSpecification(criteria.getBrandId(), root -> root.join(Promotion_.brand, JoinType.LEFT).get(Brand_.id)),
                buildSpecification(criteria.getLocationId(), root -> root.join(Promotion_.location, JoinType.LEFT).get(Location_.id))
            );
        }
        return specification;
    }
}
