package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.repository.ReservationRepository;
import md.utm.restaurant.service.criteria.ReservationCriteria;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.mapper.ReservationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Reservation} entities in the database.
 * The main input is a {@link ReservationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReservationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReservationQueryService extends QueryService<Reservation> {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationQueryService.class);

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    public ReservationQueryService(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    /**
     * Return a {@link Page} of {@link ReservationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> findByCriteria(ReservationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reservation> specification = createSpecification(criteria);
        return reservationRepository.findAll(specification, page).map(reservationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReservationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Reservation> specification = createSpecification(criteria);
        return reservationRepository.count(specification);
    }

    /**
     * Function to convert {@link ReservationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reservation> createSpecification(ReservationCriteria criteria) {
        Specification<Reservation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Reservation_.id),
                buildStringSpecification(criteria.getReservationCode(), Reservation_.reservationCode),
                buildRangeSpecification(criteria.getReservationDate(), Reservation_.reservationDate),
                buildStringSpecification(criteria.getStartTime(), Reservation_.startTime),
                buildStringSpecification(criteria.getEndTime(), Reservation_.endTime),
                buildRangeSpecification(criteria.getPartySize(), Reservation_.partySize),
                buildSpecification(criteria.getStatus(), Reservation_.status),
                buildRangeSpecification(criteria.getReminderSentAt(), Reservation_.reminderSentAt),
                buildRangeSpecification(criteria.getConfirmedAt(), Reservation_.confirmedAt),
                buildRangeSpecification(criteria.getCancelledAt(), Reservation_.cancelledAt),
                buildStringSpecification(criteria.getCancellationReason(), Reservation_.cancellationReason),
                buildRangeSpecification(criteria.getCreatedAt(), Reservation_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Reservation_.updatedAt),
                buildSpecification(criteria.getTableAssignmentsId(), root ->
                    root.join(Reservation_.tableAssignments, JoinType.LEFT).get(ReservationTable_.id)
                ),
                buildSpecification(criteria.getOrdersId(), root -> root.join(Reservation_.orders, JoinType.LEFT).get(RestaurantOrder_.id)),
                buildSpecification(criteria.getLocationId(), root -> root.join(Reservation_.location, JoinType.LEFT).get(Location_.id)),
                buildSpecification(criteria.getClientId(), root -> root.join(Reservation_.client, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getRoomId(), root -> root.join(Reservation_.room, JoinType.LEFT).get(DiningRoom_.id))
            );
        }
        return specification;
    }
}
