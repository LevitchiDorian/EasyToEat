package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.OrderItemOptionSelectionRepository;
import md.utm.restaurant.service.OrderItemOptionSelectionService;
import md.utm.restaurant.service.dto.OrderItemOptionSelectionDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.OrderItemOptionSelection}.
 */
@RestController
@RequestMapping("/api/order-item-option-selections")
public class OrderItemOptionSelectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemOptionSelectionResource.class);

    private static final String ENTITY_NAME = "orderItemOptionSelection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemOptionSelectionService orderItemOptionSelectionService;

    private final OrderItemOptionSelectionRepository orderItemOptionSelectionRepository;

    public OrderItemOptionSelectionResource(
        OrderItemOptionSelectionService orderItemOptionSelectionService,
        OrderItemOptionSelectionRepository orderItemOptionSelectionRepository
    ) {
        this.orderItemOptionSelectionService = orderItemOptionSelectionService;
        this.orderItemOptionSelectionRepository = orderItemOptionSelectionRepository;
    }

    /**
     * {@code POST  /order-item-option-selections} : Create a new orderItemOptionSelection.
     *
     * @param orderItemOptionSelectionDTO the orderItemOptionSelectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItemOptionSelectionDTO, or with status {@code 400 (Bad Request)} if the orderItemOptionSelection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderItemOptionSelectionDTO> createOrderItemOptionSelection(
        @Valid @RequestBody OrderItemOptionSelectionDTO orderItemOptionSelectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save OrderItemOptionSelection : {}", orderItemOptionSelectionDTO);
        if (orderItemOptionSelectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderItemOptionSelection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderItemOptionSelectionDTO = orderItemOptionSelectionService.save(orderItemOptionSelectionDTO);
        return ResponseEntity.created(new URI("/api/order-item-option-selections/" + orderItemOptionSelectionDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, orderItemOptionSelectionDTO.getId().toString())
            )
            .body(orderItemOptionSelectionDTO);
    }

    /**
     * {@code PUT  /order-item-option-selections/:id} : Updates an existing orderItemOptionSelection.
     *
     * @param id the id of the orderItemOptionSelectionDTO to save.
     * @param orderItemOptionSelectionDTO the orderItemOptionSelectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemOptionSelectionDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemOptionSelectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItemOptionSelectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemOptionSelectionDTO> updateOrderItemOptionSelection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderItemOptionSelectionDTO orderItemOptionSelectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderItemOptionSelection : {}, {}", id, orderItemOptionSelectionDTO);
        if (orderItemOptionSelectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemOptionSelectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemOptionSelectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderItemOptionSelectionDTO = orderItemOptionSelectionService.update(orderItemOptionSelectionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItemOptionSelectionDTO.getId().toString()))
            .body(orderItemOptionSelectionDTO);
    }

    /**
     * {@code PATCH  /order-item-option-selections/:id} : Partial updates given fields of an existing orderItemOptionSelection, field will ignore if it is null
     *
     * @param id the id of the orderItemOptionSelectionDTO to save.
     * @param orderItemOptionSelectionDTO the orderItemOptionSelectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemOptionSelectionDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemOptionSelectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderItemOptionSelectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItemOptionSelectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderItemOptionSelectionDTO> partialUpdateOrderItemOptionSelection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderItemOptionSelectionDTO orderItemOptionSelectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderItemOptionSelection partially : {}, {}", id, orderItemOptionSelectionDTO);
        if (orderItemOptionSelectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemOptionSelectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemOptionSelectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderItemOptionSelectionDTO> result = orderItemOptionSelectionService.partialUpdate(orderItemOptionSelectionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItemOptionSelectionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-item-option-selections} : get all the orderItemOptionSelections.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItemOptionSelections in body.
     */
    @GetMapping("")
    public List<OrderItemOptionSelectionDTO> getAllOrderItemOptionSelections(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all OrderItemOptionSelections");
        return orderItemOptionSelectionService.findAll();
    }

    /**
     * {@code GET  /order-item-option-selections/:id} : get the "id" orderItemOptionSelection.
     *
     * @param id the id of the orderItemOptionSelectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItemOptionSelectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemOptionSelectionDTO> getOrderItemOptionSelection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrderItemOptionSelection : {}", id);
        Optional<OrderItemOptionSelectionDTO> orderItemOptionSelectionDTO = orderItemOptionSelectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderItemOptionSelectionDTO);
    }

    /**
     * {@code DELETE  /order-item-option-selections/:id} : delete the "id" orderItemOptionSelection.
     *
     * @param id the id of the orderItemOptionSelectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItemOptionSelection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrderItemOptionSelection : {}", id);
        orderItemOptionSelectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
