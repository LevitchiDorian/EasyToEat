package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.WaitingListRepository;
import md.utm.restaurant.service.WaitingListQueryService;
import md.utm.restaurant.service.WaitingListService;
import md.utm.restaurant.service.criteria.WaitingListCriteria;
import md.utm.restaurant.service.dto.WaitingListDTO;
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
 * REST controller for managing {@link md.utm.restaurant.domain.WaitingList}.
 */
@RestController
@RequestMapping("/api/waiting-lists")
public class WaitingListResource {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingListResource.class);

    private static final String ENTITY_NAME = "waitingList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WaitingListService waitingListService;

    private final WaitingListRepository waitingListRepository;

    private final WaitingListQueryService waitingListQueryService;

    public WaitingListResource(
        WaitingListService waitingListService,
        WaitingListRepository waitingListRepository,
        WaitingListQueryService waitingListQueryService
    ) {
        this.waitingListService = waitingListService;
        this.waitingListRepository = waitingListRepository;
        this.waitingListQueryService = waitingListQueryService;
    }

    /**
     * {@code POST  /waiting-lists} : Create a new waitingList.
     *
     * @param waitingListDTO the waitingListDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new waitingListDTO, or with status {@code 400 (Bad Request)} if the waitingList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WaitingListDTO> createWaitingList(@Valid @RequestBody WaitingListDTO waitingListDTO) throws URISyntaxException {
        LOG.debug("REST request to save WaitingList : {}", waitingListDTO);
        if (waitingListDTO.getId() != null) {
            throw new BadRequestAlertException("A new waitingList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        waitingListDTO = waitingListService.save(waitingListDTO);
        return ResponseEntity.created(new URI("/api/waiting-lists/" + waitingListDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, waitingListDTO.getId().toString()))
            .body(waitingListDTO);
    }

    /**
     * {@code PUT  /waiting-lists/:id} : Updates an existing waitingList.
     *
     * @param id the id of the waitingListDTO to save.
     * @param waitingListDTO the waitingListDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated waitingListDTO,
     * or with status {@code 400 (Bad Request)} if the waitingListDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the waitingListDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WaitingListDTO> updateWaitingList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WaitingListDTO waitingListDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WaitingList : {}, {}", id, waitingListDTO);
        if (waitingListDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, waitingListDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!waitingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        waitingListDTO = waitingListService.update(waitingListDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, waitingListDTO.getId().toString()))
            .body(waitingListDTO);
    }

    /**
     * {@code PATCH  /waiting-lists/:id} : Partial updates given fields of an existing waitingList, field will ignore if it is null
     *
     * @param id the id of the waitingListDTO to save.
     * @param waitingListDTO the waitingListDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated waitingListDTO,
     * or with status {@code 400 (Bad Request)} if the waitingListDTO is not valid,
     * or with status {@code 404 (Not Found)} if the waitingListDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the waitingListDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WaitingListDTO> partialUpdateWaitingList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WaitingListDTO waitingListDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WaitingList partially : {}, {}", id, waitingListDTO);
        if (waitingListDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, waitingListDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!waitingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WaitingListDTO> result = waitingListService.partialUpdate(waitingListDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, waitingListDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /waiting-lists} : get all the waitingLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of waitingLists in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WaitingListDTO>> getAllWaitingLists(
        WaitingListCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WaitingLists by criteria: {}", criteria);

        Page<WaitingListDTO> page = waitingListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /waiting-lists/count} : count all the waitingLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWaitingLists(WaitingListCriteria criteria) {
        LOG.debug("REST request to count WaitingLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(waitingListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /waiting-lists/:id} : get the "id" waitingList.
     *
     * @param id the id of the waitingListDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the waitingListDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WaitingListDTO> getWaitingList(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WaitingList : {}", id);
        Optional<WaitingListDTO> waitingListDTO = waitingListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(waitingListDTO);
    }

    /**
     * {@code DELETE  /waiting-lists/:id} : delete the "id" waitingList.
     *
     * @param id the id of the waitingListDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitingList(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WaitingList : {}", id);
        waitingListService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
