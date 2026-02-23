package md.utm.restaurant.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.OrderItemOptionSelection;
import md.utm.restaurant.repository.OrderItemOptionSelectionRepository;
import md.utm.restaurant.service.dto.OrderItemOptionSelectionDTO;
import md.utm.restaurant.service.mapper.OrderItemOptionSelectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.OrderItemOptionSelection}.
 */
@Service
@Transactional
public class OrderItemOptionSelectionService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemOptionSelectionService.class);

    private final OrderItemOptionSelectionRepository orderItemOptionSelectionRepository;

    private final OrderItemOptionSelectionMapper orderItemOptionSelectionMapper;

    public OrderItemOptionSelectionService(
        OrderItemOptionSelectionRepository orderItemOptionSelectionRepository,
        OrderItemOptionSelectionMapper orderItemOptionSelectionMapper
    ) {
        this.orderItemOptionSelectionRepository = orderItemOptionSelectionRepository;
        this.orderItemOptionSelectionMapper = orderItemOptionSelectionMapper;
    }

    /**
     * Save a orderItemOptionSelection.
     *
     * @param orderItemOptionSelectionDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderItemOptionSelectionDTO save(OrderItemOptionSelectionDTO orderItemOptionSelectionDTO) {
        LOG.debug("Request to save OrderItemOptionSelection : {}", orderItemOptionSelectionDTO);
        OrderItemOptionSelection orderItemOptionSelection = orderItemOptionSelectionMapper.toEntity(orderItemOptionSelectionDTO);
        orderItemOptionSelection = orderItemOptionSelectionRepository.save(orderItemOptionSelection);
        return orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);
    }

    /**
     * Update a orderItemOptionSelection.
     *
     * @param orderItemOptionSelectionDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderItemOptionSelectionDTO update(OrderItemOptionSelectionDTO orderItemOptionSelectionDTO) {
        LOG.debug("Request to update OrderItemOptionSelection : {}", orderItemOptionSelectionDTO);
        OrderItemOptionSelection orderItemOptionSelection = orderItemOptionSelectionMapper.toEntity(orderItemOptionSelectionDTO);
        orderItemOptionSelection = orderItemOptionSelectionRepository.save(orderItemOptionSelection);
        return orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);
    }

    /**
     * Partially update a orderItemOptionSelection.
     *
     * @param orderItemOptionSelectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderItemOptionSelectionDTO> partialUpdate(OrderItemOptionSelectionDTO orderItemOptionSelectionDTO) {
        LOG.debug("Request to partially update OrderItemOptionSelection : {}", orderItemOptionSelectionDTO);

        return orderItemOptionSelectionRepository
            .findById(orderItemOptionSelectionDTO.getId())
            .map(existingOrderItemOptionSelection -> {
                orderItemOptionSelectionMapper.partialUpdate(existingOrderItemOptionSelection, orderItemOptionSelectionDTO);

                return existingOrderItemOptionSelection;
            })
            .map(orderItemOptionSelectionRepository::save)
            .map(orderItemOptionSelectionMapper::toDto);
    }

    /**
     * Get all the orderItemOptionSelections.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderItemOptionSelectionDTO> findAll() {
        LOG.debug("Request to get all OrderItemOptionSelections");
        return orderItemOptionSelectionRepository
            .findAll()
            .stream()
            .map(orderItemOptionSelectionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the orderItemOptionSelections with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OrderItemOptionSelectionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderItemOptionSelectionRepository.findAllWithEagerRelationships(pageable).map(orderItemOptionSelectionMapper::toDto);
    }

    /**
     * Get one orderItemOptionSelection by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderItemOptionSelectionDTO> findOne(Long id) {
        LOG.debug("Request to get OrderItemOptionSelection : {}", id);
        return orderItemOptionSelectionRepository.findOneWithEagerRelationships(id).map(orderItemOptionSelectionMapper::toDto);
    }

    /**
     * Delete the orderItemOptionSelection by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OrderItemOptionSelection : {}", id);
        orderItemOptionSelectionRepository.deleteById(id);
    }
}
