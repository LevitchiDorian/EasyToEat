package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.DiningRoomRepository;
import md.utm.restaurant.service.DiningRoomQueryService;
import md.utm.restaurant.service.DiningRoomService;
import md.utm.restaurant.service.criteria.DiningRoomCriteria;
import md.utm.restaurant.service.dto.DiningRoomDTO;
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
 * REST controller for managing {@link md.utm.restaurant.domain.DiningRoom}.
 */
@RestController
@RequestMapping("/api/dining-rooms")
public class DiningRoomResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiningRoomResource.class);

    private static final String ENTITY_NAME = "diningRoom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DiningRoomService diningRoomService;

    private final DiningRoomRepository diningRoomRepository;

    private final DiningRoomQueryService diningRoomQueryService;

    public DiningRoomResource(
        DiningRoomService diningRoomService,
        DiningRoomRepository diningRoomRepository,
        DiningRoomQueryService diningRoomQueryService
    ) {
        this.diningRoomService = diningRoomService;
        this.diningRoomRepository = diningRoomRepository;
        this.diningRoomQueryService = diningRoomQueryService;
    }

    /**
     * {@code POST  /dining-rooms} : Create a new diningRoom.
     *
     * @param diningRoomDTO the diningRoomDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new diningRoomDTO, or with status {@code 400 (Bad Request)} if the diningRoom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DiningRoomDTO> createDiningRoom(@Valid @RequestBody DiningRoomDTO diningRoomDTO) throws URISyntaxException {
        LOG.debug("REST request to save DiningRoom : {}", diningRoomDTO);
        if (diningRoomDTO.getId() != null) {
            throw new BadRequestAlertException("A new diningRoom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        diningRoomDTO = diningRoomService.save(diningRoomDTO);
        return ResponseEntity.created(new URI("/api/dining-rooms/" + diningRoomDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, diningRoomDTO.getId().toString()))
            .body(diningRoomDTO);
    }

    /**
     * {@code PUT  /dining-rooms/:id} : Updates an existing diningRoom.
     *
     * @param id the id of the diningRoomDTO to save.
     * @param diningRoomDTO the diningRoomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diningRoomDTO,
     * or with status {@code 400 (Bad Request)} if the diningRoomDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the diningRoomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiningRoomDTO> updateDiningRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DiningRoomDTO diningRoomDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DiningRoom : {}, {}", id, diningRoomDTO);
        if (diningRoomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diningRoomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diningRoomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        diningRoomDTO = diningRoomService.update(diningRoomDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diningRoomDTO.getId().toString()))
            .body(diningRoomDTO);
    }

    /**
     * {@code PATCH  /dining-rooms/:id} : Partial updates given fields of an existing diningRoom, field will ignore if it is null
     *
     * @param id the id of the diningRoomDTO to save.
     * @param diningRoomDTO the diningRoomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diningRoomDTO,
     * or with status {@code 400 (Bad Request)} if the diningRoomDTO is not valid,
     * or with status {@code 404 (Not Found)} if the diningRoomDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the diningRoomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DiningRoomDTO> partialUpdateDiningRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DiningRoomDTO diningRoomDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DiningRoom partially : {}, {}", id, diningRoomDTO);
        if (diningRoomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diningRoomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diningRoomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DiningRoomDTO> result = diningRoomService.partialUpdate(diningRoomDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diningRoomDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /dining-rooms} : get all the diningRooms.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diningRooms in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DiningRoomDTO>> getAllDiningRooms(
        DiningRoomCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DiningRooms by criteria: {}", criteria);

        Page<DiningRoomDTO> page = diningRoomQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dining-rooms/count} : count all the diningRooms.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDiningRooms(DiningRoomCriteria criteria) {
        LOG.debug("REST request to count DiningRooms by criteria: {}", criteria);
        return ResponseEntity.ok().body(diningRoomQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dining-rooms/:id} : get the "id" diningRoom.
     *
     * @param id the id of the diningRoomDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the diningRoomDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiningRoomDTO> getDiningRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DiningRoom : {}", id);
        Optional<DiningRoomDTO> diningRoomDTO = diningRoomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(diningRoomDTO);
    }

    /**
     * {@code DELETE  /dining-rooms/:id} : delete the "id" diningRoom.
     *
     * @param id the id of the diningRoomDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiningRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DiningRoom : {}", id);
        diningRoomService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
