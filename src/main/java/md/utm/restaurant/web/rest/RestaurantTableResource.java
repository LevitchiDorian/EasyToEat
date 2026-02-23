package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.RestaurantTableRepository;
import md.utm.restaurant.service.RestaurantTableQueryService;
import md.utm.restaurant.service.RestaurantTableService;
import md.utm.restaurant.service.criteria.RestaurantTableCriteria;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
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
 * REST controller for managing {@link md.utm.restaurant.domain.RestaurantTable}.
 */
@RestController
@RequestMapping("/api/restaurant-tables")
public class RestaurantTableResource {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantTableResource.class);

    private static final String ENTITY_NAME = "restaurantTable";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurantTableService restaurantTableService;

    private final RestaurantTableRepository restaurantTableRepository;

    private final RestaurantTableQueryService restaurantTableQueryService;

    public RestaurantTableResource(
        RestaurantTableService restaurantTableService,
        RestaurantTableRepository restaurantTableRepository,
        RestaurantTableQueryService restaurantTableQueryService
    ) {
        this.restaurantTableService = restaurantTableService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantTableQueryService = restaurantTableQueryService;
    }

    /**
     * {@code POST  /restaurant-tables} : Create a new restaurantTable.
     *
     * @param restaurantTableDTO the restaurantTableDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurantTableDTO, or with status {@code 400 (Bad Request)} if the restaurantTable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RestaurantTableDTO> createRestaurantTable(@Valid @RequestBody RestaurantTableDTO restaurantTableDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RestaurantTable : {}", restaurantTableDTO);
        if (restaurantTableDTO.getId() != null) {
            throw new BadRequestAlertException("A new restaurantTable cannot already have an ID", ENTITY_NAME, "idexists");
        }
        restaurantTableDTO = restaurantTableService.save(restaurantTableDTO);
        return ResponseEntity.created(new URI("/api/restaurant-tables/" + restaurantTableDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, restaurantTableDTO.getId().toString()))
            .body(restaurantTableDTO);
    }

    /**
     * {@code PUT  /restaurant-tables/:id} : Updates an existing restaurantTable.
     *
     * @param id the id of the restaurantTableDTO to save.
     * @param restaurantTableDTO the restaurantTableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantTableDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantTableDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurantTableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTableDTO> updateRestaurantTable(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RestaurantTableDTO restaurantTableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RestaurantTable : {}, {}", id, restaurantTableDTO);
        if (restaurantTableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantTableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurantTableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        restaurantTableDTO = restaurantTableService.update(restaurantTableDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, restaurantTableDTO.getId().toString()))
            .body(restaurantTableDTO);
    }

    /**
     * {@code PATCH  /restaurant-tables/:id} : Partial updates given fields of an existing restaurantTable, field will ignore if it is null
     *
     * @param id the id of the restaurantTableDTO to save.
     * @param restaurantTableDTO the restaurantTableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantTableDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantTableDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurantTableDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurantTableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RestaurantTableDTO> partialUpdateRestaurantTable(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RestaurantTableDTO restaurantTableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RestaurantTable partially : {}, {}", id, restaurantTableDTO);
        if (restaurantTableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantTableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurantTableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RestaurantTableDTO> result = restaurantTableService.partialUpdate(restaurantTableDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, restaurantTableDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /restaurant-tables} : get all the restaurantTables.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurantTables in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RestaurantTableDTO>> getAllRestaurantTables(
        RestaurantTableCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RestaurantTables by criteria: {}", criteria);

        Page<RestaurantTableDTO> page = restaurantTableQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /restaurant-tables/count} : count all the restaurantTables.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRestaurantTables(RestaurantTableCriteria criteria) {
        LOG.debug("REST request to count RestaurantTables by criteria: {}", criteria);
        return ResponseEntity.ok().body(restaurantTableQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /restaurant-tables/:id} : get the "id" restaurantTable.
     *
     * @param id the id of the restaurantTableDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurantTableDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableDTO> getRestaurantTable(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RestaurantTable : {}", id);
        Optional<RestaurantTableDTO> restaurantTableDTO = restaurantTableService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurantTableDTO);
    }

    /**
     * {@code DELETE  /restaurant-tables/:id} : delete the "id" restaurantTable.
     *
     * @param id the id of the restaurantTableDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantTable(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RestaurantTable : {}", id);
        restaurantTableService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
