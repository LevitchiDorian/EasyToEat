package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.MenuItemOptionValueRepository;
import md.utm.restaurant.service.MenuItemOptionValueService;
import md.utm.restaurant.service.dto.MenuItemOptionValueDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.MenuItemOptionValue}.
 */
@RestController
@RequestMapping("/api/menu-item-option-values")
public class MenuItemOptionValueResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemOptionValueResource.class);

    private static final String ENTITY_NAME = "menuItemOptionValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuItemOptionValueService menuItemOptionValueService;

    private final MenuItemOptionValueRepository menuItemOptionValueRepository;

    public MenuItemOptionValueResource(
        MenuItemOptionValueService menuItemOptionValueService,
        MenuItemOptionValueRepository menuItemOptionValueRepository
    ) {
        this.menuItemOptionValueService = menuItemOptionValueService;
        this.menuItemOptionValueRepository = menuItemOptionValueRepository;
    }

    /**
     * {@code POST  /menu-item-option-values} : Create a new menuItemOptionValue.
     *
     * @param menuItemOptionValueDTO the menuItemOptionValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuItemOptionValueDTO, or with status {@code 400 (Bad Request)} if the menuItemOptionValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MenuItemOptionValueDTO> createMenuItemOptionValue(
        @Valid @RequestBody MenuItemOptionValueDTO menuItemOptionValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save MenuItemOptionValue : {}", menuItemOptionValueDTO);
        if (menuItemOptionValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuItemOptionValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuItemOptionValueDTO = menuItemOptionValueService.save(menuItemOptionValueDTO);
        return ResponseEntity.created(new URI("/api/menu-item-option-values/" + menuItemOptionValueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, menuItemOptionValueDTO.getId().toString()))
            .body(menuItemOptionValueDTO);
    }

    /**
     * {@code PUT  /menu-item-option-values/:id} : Updates an existing menuItemOptionValue.
     *
     * @param id the id of the menuItemOptionValueDTO to save.
     * @param menuItemOptionValueDTO the menuItemOptionValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemOptionValueDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemOptionValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuItemOptionValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemOptionValueDTO> updateMenuItemOptionValue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MenuItemOptionValueDTO menuItemOptionValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuItemOptionValue : {}, {}", id, menuItemOptionValueDTO);
        if (menuItemOptionValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemOptionValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuItemOptionValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        menuItemOptionValueDTO = menuItemOptionValueService.update(menuItemOptionValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuItemOptionValueDTO.getId().toString()))
            .body(menuItemOptionValueDTO);
    }

    /**
     * {@code PATCH  /menu-item-option-values/:id} : Partial updates given fields of an existing menuItemOptionValue, field will ignore if it is null
     *
     * @param id the id of the menuItemOptionValueDTO to save.
     * @param menuItemOptionValueDTO the menuItemOptionValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemOptionValueDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemOptionValueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuItemOptionValueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuItemOptionValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MenuItemOptionValueDTO> partialUpdateMenuItemOptionValue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MenuItemOptionValueDTO menuItemOptionValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuItemOptionValue partially : {}, {}", id, menuItemOptionValueDTO);
        if (menuItemOptionValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemOptionValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuItemOptionValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MenuItemOptionValueDTO> result = menuItemOptionValueService.partialUpdate(menuItemOptionValueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuItemOptionValueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /menu-item-option-values} : get all the menuItemOptionValues.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuItemOptionValues in body.
     */
    @GetMapping("")
    public List<MenuItemOptionValueDTO> getAllMenuItemOptionValues(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MenuItemOptionValues");
        return menuItemOptionValueService.findAll();
    }

    /**
     * {@code GET  /menu-item-option-values/:id} : get the "id" menuItemOptionValue.
     *
     * @param id the id of the menuItemOptionValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuItemOptionValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemOptionValueDTO> getMenuItemOptionValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MenuItemOptionValue : {}", id);
        Optional<MenuItemOptionValueDTO> menuItemOptionValueDTO = menuItemOptionValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuItemOptionValueDTO);
    }

    /**
     * {@code DELETE  /menu-item-option-values/:id} : delete the "id" menuItemOptionValue.
     *
     * @param id the id of the menuItemOptionValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItemOptionValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MenuItemOptionValue : {}", id);
        menuItemOptionValueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
