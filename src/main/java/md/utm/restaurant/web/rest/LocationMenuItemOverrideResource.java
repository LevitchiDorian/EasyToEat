package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.LocationMenuItemOverrideRepository;
import md.utm.restaurant.service.LocationMenuItemOverrideQueryService;
import md.utm.restaurant.service.LocationMenuItemOverrideService;
import md.utm.restaurant.service.criteria.LocationMenuItemOverrideCriteria;
import md.utm.restaurant.service.dto.LocationMenuItemOverrideDTO;
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
 * REST controller for managing {@link md.utm.restaurant.domain.LocationMenuItemOverride}.
 */
@RestController
@RequestMapping("/api/location-menu-item-overrides")
public class LocationMenuItemOverrideResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocationMenuItemOverrideResource.class);

    private static final String ENTITY_NAME = "locationMenuItemOverride";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocationMenuItemOverrideService locationMenuItemOverrideService;

    private final LocationMenuItemOverrideRepository locationMenuItemOverrideRepository;

    private final LocationMenuItemOverrideQueryService locationMenuItemOverrideQueryService;

    public LocationMenuItemOverrideResource(
        LocationMenuItemOverrideService locationMenuItemOverrideService,
        LocationMenuItemOverrideRepository locationMenuItemOverrideRepository,
        LocationMenuItemOverrideQueryService locationMenuItemOverrideQueryService
    ) {
        this.locationMenuItemOverrideService = locationMenuItemOverrideService;
        this.locationMenuItemOverrideRepository = locationMenuItemOverrideRepository;
        this.locationMenuItemOverrideQueryService = locationMenuItemOverrideQueryService;
    }

    /**
     * {@code POST  /location-menu-item-overrides} : Create a new locationMenuItemOverride.
     *
     * @param locationMenuItemOverrideDTO the locationMenuItemOverrideDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locationMenuItemOverrideDTO, or with status {@code 400 (Bad Request)} if the locationMenuItemOverride has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LocationMenuItemOverrideDTO> createLocationMenuItemOverride(
        @Valid @RequestBody LocationMenuItemOverrideDTO locationMenuItemOverrideDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save LocationMenuItemOverride : {}", locationMenuItemOverrideDTO);
        if (locationMenuItemOverrideDTO.getId() != null) {
            throw new BadRequestAlertException("A new locationMenuItemOverride cannot already have an ID", ENTITY_NAME, "idexists");
        }
        locationMenuItemOverrideDTO = locationMenuItemOverrideService.save(locationMenuItemOverrideDTO);
        return ResponseEntity.created(new URI("/api/location-menu-item-overrides/" + locationMenuItemOverrideDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, locationMenuItemOverrideDTO.getId().toString())
            )
            .body(locationMenuItemOverrideDTO);
    }

    /**
     * {@code PUT  /location-menu-item-overrides/:id} : Updates an existing locationMenuItemOverride.
     *
     * @param id the id of the locationMenuItemOverrideDTO to save.
     * @param locationMenuItemOverrideDTO the locationMenuItemOverrideDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationMenuItemOverrideDTO,
     * or with status {@code 400 (Bad Request)} if the locationMenuItemOverrideDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locationMenuItemOverrideDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationMenuItemOverrideDTO> updateLocationMenuItemOverride(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LocationMenuItemOverrideDTO locationMenuItemOverrideDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LocationMenuItemOverride : {}, {}", id, locationMenuItemOverrideDTO);
        if (locationMenuItemOverrideDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationMenuItemOverrideDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationMenuItemOverrideRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        locationMenuItemOverrideDTO = locationMenuItemOverrideService.update(locationMenuItemOverrideDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, locationMenuItemOverrideDTO.getId().toString()))
            .body(locationMenuItemOverrideDTO);
    }

    /**
     * {@code PATCH  /location-menu-item-overrides/:id} : Partial updates given fields of an existing locationMenuItemOverride, field will ignore if it is null
     *
     * @param id the id of the locationMenuItemOverrideDTO to save.
     * @param locationMenuItemOverrideDTO the locationMenuItemOverrideDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationMenuItemOverrideDTO,
     * or with status {@code 400 (Bad Request)} if the locationMenuItemOverrideDTO is not valid,
     * or with status {@code 404 (Not Found)} if the locationMenuItemOverrideDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the locationMenuItemOverrideDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LocationMenuItemOverrideDTO> partialUpdateLocationMenuItemOverride(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LocationMenuItemOverrideDTO locationMenuItemOverrideDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LocationMenuItemOverride partially : {}, {}", id, locationMenuItemOverrideDTO);
        if (locationMenuItemOverrideDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationMenuItemOverrideDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationMenuItemOverrideRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LocationMenuItemOverrideDTO> result = locationMenuItemOverrideService.partialUpdate(locationMenuItemOverrideDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, locationMenuItemOverrideDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /location-menu-item-overrides} : get all the locationMenuItemOverrides.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of locationMenuItemOverrides in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LocationMenuItemOverrideDTO>> getAllLocationMenuItemOverrides(
        LocationMenuItemOverrideCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get LocationMenuItemOverrides by criteria: {}", criteria);

        Page<LocationMenuItemOverrideDTO> page = locationMenuItemOverrideQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /location-menu-item-overrides/count} : count all the locationMenuItemOverrides.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLocationMenuItemOverrides(LocationMenuItemOverrideCriteria criteria) {
        LOG.debug("REST request to count LocationMenuItemOverrides by criteria: {}", criteria);
        return ResponseEntity.ok().body(locationMenuItemOverrideQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /location-menu-item-overrides/:id} : get the "id" locationMenuItemOverride.
     *
     * @param id the id of the locationMenuItemOverrideDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locationMenuItemOverrideDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationMenuItemOverrideDTO> getLocationMenuItemOverride(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LocationMenuItemOverride : {}", id);
        Optional<LocationMenuItemOverrideDTO> locationMenuItemOverrideDTO = locationMenuItemOverrideService.findOne(id);
        return ResponseUtil.wrapOrNotFound(locationMenuItemOverrideDTO);
    }

    /**
     * {@code DELETE  /location-menu-item-overrides/:id} : delete the "id" locationMenuItemOverride.
     *
     * @param id the id of the locationMenuItemOverrideDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationMenuItemOverride(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LocationMenuItemOverride : {}", id);
        locationMenuItemOverrideService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
