package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.LocationHoursRepository;
import md.utm.restaurant.service.LocationHoursService;
import md.utm.restaurant.service.dto.LocationHoursDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.LocationHours}.
 */
@RestController
@RequestMapping("/api/location-hours")
public class LocationHoursResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocationHoursResource.class);

    private static final String ENTITY_NAME = "locationHours";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocationHoursService locationHoursService;

    private final LocationHoursRepository locationHoursRepository;

    public LocationHoursResource(LocationHoursService locationHoursService, LocationHoursRepository locationHoursRepository) {
        this.locationHoursService = locationHoursService;
        this.locationHoursRepository = locationHoursRepository;
    }

    /**
     * {@code POST  /location-hours} : Create a new locationHours.
     *
     * @param locationHoursDTO the locationHoursDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locationHoursDTO, or with status {@code 400 (Bad Request)} if the locationHours has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LocationHoursDTO> createLocationHours(@Valid @RequestBody LocationHoursDTO locationHoursDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LocationHours : {}", locationHoursDTO);
        if (locationHoursDTO.getId() != null) {
            throw new BadRequestAlertException("A new locationHours cannot already have an ID", ENTITY_NAME, "idexists");
        }
        locationHoursDTO = locationHoursService.save(locationHoursDTO);
        return ResponseEntity.created(new URI("/api/location-hours/" + locationHoursDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, locationHoursDTO.getId().toString()))
            .body(locationHoursDTO);
    }

    /**
     * {@code PUT  /location-hours/:id} : Updates an existing locationHours.
     *
     * @param id the id of the locationHoursDTO to save.
     * @param locationHoursDTO the locationHoursDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationHoursDTO,
     * or with status {@code 400 (Bad Request)} if the locationHoursDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locationHoursDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationHoursDTO> updateLocationHours(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LocationHoursDTO locationHoursDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LocationHours : {}, {}", id, locationHoursDTO);
        if (locationHoursDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationHoursDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationHoursRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        locationHoursDTO = locationHoursService.update(locationHoursDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, locationHoursDTO.getId().toString()))
            .body(locationHoursDTO);
    }

    /**
     * {@code PATCH  /location-hours/:id} : Partial updates given fields of an existing locationHours, field will ignore if it is null
     *
     * @param id the id of the locationHoursDTO to save.
     * @param locationHoursDTO the locationHoursDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationHoursDTO,
     * or with status {@code 400 (Bad Request)} if the locationHoursDTO is not valid,
     * or with status {@code 404 (Not Found)} if the locationHoursDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the locationHoursDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LocationHoursDTO> partialUpdateLocationHours(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LocationHoursDTO locationHoursDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LocationHours partially : {}, {}", id, locationHoursDTO);
        if (locationHoursDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationHoursDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationHoursRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LocationHoursDTO> result = locationHoursService.partialUpdate(locationHoursDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, locationHoursDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /location-hours} : get all the locationHours.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of locationHours in body.
     */
    @GetMapping("")
    public List<LocationHoursDTO> getAllLocationHours(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all LocationHours");
        return locationHoursService.findAll();
    }

    /**
     * {@code GET  /location-hours/:id} : get the "id" locationHours.
     *
     * @param id the id of the locationHoursDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locationHoursDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationHoursDTO> getLocationHours(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LocationHours : {}", id);
        Optional<LocationHoursDTO> locationHoursDTO = locationHoursService.findOne(id);
        return ResponseUtil.wrapOrNotFound(locationHoursDTO);
    }

    /**
     * {@code DELETE  /location-hours/:id} : delete the "id" locationHours.
     *
     * @param id the id of the locationHoursDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationHours(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LocationHours : {}", id);
        locationHoursService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
