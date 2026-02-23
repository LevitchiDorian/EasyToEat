package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.MenuCategoryRepository;
import md.utm.restaurant.service.MenuCategoryQueryService;
import md.utm.restaurant.service.MenuCategoryService;
import md.utm.restaurant.service.criteria.MenuCategoryCriteria;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
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
 * REST controller for managing {@link md.utm.restaurant.domain.MenuCategory}.
 */
@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCategoryResource.class);

    private static final String ENTITY_NAME = "menuCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuCategoryService menuCategoryService;

    private final MenuCategoryRepository menuCategoryRepository;

    private final MenuCategoryQueryService menuCategoryQueryService;

    public MenuCategoryResource(
        MenuCategoryService menuCategoryService,
        MenuCategoryRepository menuCategoryRepository,
        MenuCategoryQueryService menuCategoryQueryService
    ) {
        this.menuCategoryService = menuCategoryService;
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuCategoryQueryService = menuCategoryQueryService;
    }

    /**
     * {@code POST  /menu-categories} : Create a new menuCategory.
     *
     * @param menuCategoryDTO the menuCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuCategoryDTO, or with status {@code 400 (Bad Request)} if the menuCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MenuCategoryDTO> createMenuCategory(@Valid @RequestBody MenuCategoryDTO menuCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MenuCategory : {}", menuCategoryDTO);
        if (menuCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuCategoryDTO = menuCategoryService.save(menuCategoryDTO);
        return ResponseEntity.created(new URI("/api/menu-categories/" + menuCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, menuCategoryDTO.getId().toString()))
            .body(menuCategoryDTO);
    }

    /**
     * {@code PUT  /menu-categories/:id} : Updates an existing menuCategory.
     *
     * @param id the id of the menuCategoryDTO to save.
     * @param menuCategoryDTO the menuCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the menuCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuCategoryDTO> updateMenuCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MenuCategoryDTO menuCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuCategory : {}, {}", id, menuCategoryDTO);
        if (menuCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        menuCategoryDTO = menuCategoryService.update(menuCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuCategoryDTO.getId().toString()))
            .body(menuCategoryDTO);
    }

    /**
     * {@code PATCH  /menu-categories/:id} : Partial updates given fields of an existing menuCategory, field will ignore if it is null
     *
     * @param id the id of the menuCategoryDTO to save.
     * @param menuCategoryDTO the menuCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the menuCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MenuCategoryDTO> partialUpdateMenuCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MenuCategoryDTO menuCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuCategory partially : {}, {}", id, menuCategoryDTO);
        if (menuCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MenuCategoryDTO> result = menuCategoryService.partialUpdate(menuCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /menu-categories} : get all the menuCategories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuCategories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MenuCategoryDTO>> getAllMenuCategories(
        MenuCategoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get MenuCategories by criteria: {}", criteria);

        Page<MenuCategoryDTO> page = menuCategoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /menu-categories/count} : count all the menuCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMenuCategories(MenuCategoryCriteria criteria) {
        LOG.debug("REST request to count MenuCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(menuCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /menu-categories/:id} : get the "id" menuCategory.
     *
     * @param id the id of the menuCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuCategoryDTO> getMenuCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MenuCategory : {}", id);
        Optional<MenuCategoryDTO> menuCategoryDTO = menuCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuCategoryDTO);
    }

    /**
     * {@code DELETE  /menu-categories/:id} : delete the "id" menuCategory.
     *
     * @param id the id of the menuCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MenuCategory : {}", id);
        menuCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
