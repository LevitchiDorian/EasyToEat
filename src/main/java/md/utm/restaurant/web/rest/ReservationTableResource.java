package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.ReservationTableRepository;
import md.utm.restaurant.service.ReservationTableService;
import md.utm.restaurant.service.dto.ReservationTableDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.ReservationTable}.
 */
@RestController
@RequestMapping("/api/reservation-tables")
public class ReservationTableResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationTableResource.class);

    private static final String ENTITY_NAME = "reservationTable";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservationTableService reservationTableService;

    private final ReservationTableRepository reservationTableRepository;

    public ReservationTableResource(
        ReservationTableService reservationTableService,
        ReservationTableRepository reservationTableRepository
    ) {
        this.reservationTableService = reservationTableService;
        this.reservationTableRepository = reservationTableRepository;
    }

    /**
     * {@code POST  /reservation-tables} : Create a new reservationTable.
     *
     * @param reservationTableDTO the reservationTableDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reservationTableDTO, or with status {@code 400 (Bad Request)} if the reservationTable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReservationTableDTO> createReservationTable(@Valid @RequestBody ReservationTableDTO reservationTableDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReservationTable : {}", reservationTableDTO);
        if (reservationTableDTO.getId() != null) {
            throw new BadRequestAlertException("A new reservationTable cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reservationTableDTO = reservationTableService.save(reservationTableDTO);
        return ResponseEntity.created(new URI("/api/reservation-tables/" + reservationTableDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, reservationTableDTO.getId().toString()))
            .body(reservationTableDTO);
    }

    /**
     * {@code PUT  /reservation-tables/:id} : Updates an existing reservationTable.
     *
     * @param id the id of the reservationTableDTO to save.
     * @param reservationTableDTO the reservationTableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservationTableDTO,
     * or with status {@code 400 (Bad Request)} if the reservationTableDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservationTableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationTableDTO> updateReservationTable(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReservationTableDTO reservationTableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReservationTable : {}, {}", id, reservationTableDTO);
        if (reservationTableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservationTableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reservationTableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        reservationTableDTO = reservationTableService.update(reservationTableDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservationTableDTO.getId().toString()))
            .body(reservationTableDTO);
    }

    /**
     * {@code PATCH  /reservation-tables/:id} : Partial updates given fields of an existing reservationTable, field will ignore if it is null
     *
     * @param id the id of the reservationTableDTO to save.
     * @param reservationTableDTO the reservationTableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservationTableDTO,
     * or with status {@code 400 (Bad Request)} if the reservationTableDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reservationTableDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reservationTableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReservationTableDTO> partialUpdateReservationTable(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReservationTableDTO reservationTableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReservationTable partially : {}, {}", id, reservationTableDTO);
        if (reservationTableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservationTableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reservationTableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReservationTableDTO> result = reservationTableService.partialUpdate(reservationTableDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservationTableDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reservation-tables} : get all the reservationTables.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reservationTables in body.
     */
    @GetMapping("")
    public List<ReservationTableDTO> getAllReservationTables(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ReservationTables");
        return reservationTableService.findAll();
    }

    /**
     * {@code GET  /reservation-tables/:id} : get the "id" reservationTable.
     *
     * @param id the id of the reservationTableDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reservationTableDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationTableDTO> getReservationTable(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReservationTable : {}", id);
        Optional<ReservationTableDTO> reservationTableDTO = reservationTableService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reservationTableDTO);
    }

    /**
     * {@code DELETE  /reservation-tables/:id} : delete the "id" reservationTable.
     *
     * @param id the id of the reservationTableDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTable(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReservationTable : {}", id);
        reservationTableService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
