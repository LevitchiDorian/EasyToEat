package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.RestaurantOrderRepository;
import md.utm.restaurant.service.RestaurantOrderQueryService;
import md.utm.restaurant.service.RestaurantOrderService;
import md.utm.restaurant.service.criteria.RestaurantOrderCriteria;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.RestaurantOrder}.
 */
@RestController
@RequestMapping("/api/restaurant-orders")
public class RestaurantOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantOrderResource.class);

    private static final String ENTITY_NAME = "restaurantOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurantOrderService restaurantOrderService;

    private final RestaurantOrderRepository restaurantOrderRepository;

    private final RestaurantOrderQueryService restaurantOrderQueryService;

    public RestaurantOrderResource(
        RestaurantOrderService restaurantOrderService,
        RestaurantOrderRepository restaurantOrderRepository,
        RestaurantOrderQueryService restaurantOrderQueryService
    ) {
        this.restaurantOrderService = restaurantOrderService;
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantOrderQueryService = restaurantOrderQueryService;
    }

    /**
     * {@code POST  /restaurant-orders} : Create a new restaurantOrder.
     *
     * @param restaurantOrderDTO the restaurantOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurantOrderDTO, or with status {@code 400 (Bad Request)} if the restaurantOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RestaurantOrderDTO> createRestaurantOrder(@Valid @RequestBody RestaurantOrderDTO restaurantOrderDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RestaurantOrder : {}", restaurantOrderDTO);
        if (restaurantOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new restaurantOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        restaurantOrderDTO = restaurantOrderService.save(restaurantOrderDTO);
        return ResponseEntity.created(new URI("/api/restaurant-orders/" + restaurantOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, restaurantOrderDTO.getId().toString()))
            .body(restaurantOrderDTO);
    }

    /**
     * {@code PUT  /restaurant-orders/:id} : Updates an existing restaurantOrder.
     *
     * @param id the id of the restaurantOrderDTO to save.
     * @param restaurantOrderDTO the restaurantOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantOrderDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurantOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantOrderDTO> updateRestaurantOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RestaurantOrderDTO restaurantOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RestaurantOrder : {}, {}", id, restaurantOrderDTO);
        if (restaurantOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurantOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        restaurantOrderDTO = restaurantOrderService.update(restaurantOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, restaurantOrderDTO.getId().toString()))
            .body(restaurantOrderDTO);
    }

    /**
     * {@code PATCH  /restaurant-orders/:id} : Partial updates given fields of an existing restaurantOrder, field will ignore if it is null
     *
     * @param id the id of the restaurantOrderDTO to save.
     * @param restaurantOrderDTO the restaurantOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantOrderDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurantOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurantOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RestaurantOrderDTO> partialUpdateRestaurantOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RestaurantOrderDTO restaurantOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RestaurantOrder partially : {}, {}", id, restaurantOrderDTO);
        if (restaurantOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurantOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RestaurantOrderDTO> result = restaurantOrderService.partialUpdate(restaurantOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, restaurantOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /restaurant-orders} : get all the restaurantOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurantOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RestaurantOrderDTO>> getAllRestaurantOrders(
        RestaurantOrderCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RestaurantOrders by criteria: {}", criteria);

        Page<RestaurantOrderDTO> page = restaurantOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /restaurant-orders/count} : count all the restaurantOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRestaurantOrders(RestaurantOrderCriteria criteria) {
        LOG.debug("REST request to count RestaurantOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(restaurantOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /restaurant-orders/:id} : get the "id" restaurantOrder.
     *
     * @param id the id of the restaurantOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurantOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantOrderDTO> getRestaurantOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RestaurantOrder : {}", id);
        Optional<RestaurantOrderDTO> restaurantOrderDTO = restaurantOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurantOrderDTO);
    }

    /**
     * {@code DELETE  /restaurant-orders/:id} : delete the "id" restaurantOrder.
     *
     * @param id the id of the restaurantOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RestaurantOrder : {}", id);
        restaurantOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
