package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.Review;
import md.utm.restaurant.repository.ReviewRepository;
import md.utm.restaurant.service.criteria.ReviewCriteria;
import md.utm.restaurant.service.dto.ReviewDTO;
import md.utm.restaurant.service.mapper.ReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Review} entities in the database.
 * The main input is a {@link ReviewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReviewDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReviewQueryService extends QueryService<Review> {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewQueryService.class);

    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;

    public ReviewQueryService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    /**
     * Return a {@link Page} of {@link ReviewDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> findByCriteria(ReviewCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Review> specification = createSpecification(criteria);
        return reviewRepository.findAll(specification, page).map(reviewMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReviewCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Review> specification = createSpecification(criteria);
        return reviewRepository.count(specification);
    }

    /**
     * Function to convert {@link ReviewCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Review> createSpecification(ReviewCriteria criteria) {
        Specification<Review> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Review_.id),
                buildRangeSpecification(criteria.getOverallRating(), Review_.overallRating),
                buildRangeSpecification(criteria.getFoodRating(), Review_.foodRating),
                buildRangeSpecification(criteria.getServiceRating(), Review_.serviceRating),
                buildRangeSpecification(criteria.getAmbienceRating(), Review_.ambienceRating),
                buildSpecification(criteria.getIsApproved(), Review_.isApproved),
                buildSpecification(criteria.getIsAnonymous(), Review_.isAnonymous),
                buildRangeSpecification(criteria.getCreatedAt(), Review_.createdAt),
                buildSpecification(criteria.getLocationId(), root -> root.join(Review_.location, JoinType.LEFT).get(Location_.id)),
                buildSpecification(criteria.getReservationId(), root -> root.join(Review_.reservation, JoinType.LEFT).get(Reservation_.id)),
                buildSpecification(criteria.getClientId(), root -> root.join(Review_.client, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
