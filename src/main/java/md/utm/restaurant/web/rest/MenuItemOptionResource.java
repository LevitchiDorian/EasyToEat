package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.MenuItemOptionRepository;
import md.utm.restaurant.service.MenuItemOptionService;
import md.utm.restaurant.service.dto.MenuItemOptionDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.MenuItemOption}.
 */
@RestController
@RequestMapping("/api/menu-item-options")
public class MenuItemOptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemOptionResource.class);

    private static final String ENTITY_NAME = "menuItemOption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuItemOptionService menuItemOptionService;

    private final MenuItemOptionRepository menuItemOptionRepository;

    public MenuItemOptionResource(MenuItemOptionService menuItemOptionService, MenuItemOptionRepository menuItemOptionRepository) {
        this.menuItemOptionService = menuItemOptionService;
        this.menuItemOptionRepository = menuItemOptionRepository;
    }

    /**
     * {@code POST  /menu-item-options} : Create a new menuItemOption.
     *
     * @param menuItemOptionDTO the menuItemOptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuItemOptionDTO, or with status {@code 400 (Bad Request)} if the menuItemOption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MenuItemOptionDTO> createMenuItemOption(@Valid @RequestBody MenuItemOptionDTO menuItemOptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MenuItemOption : {}", menuItemOptionDTO);
        if (menuItemOptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuItemOption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuItemOptionDTO = menuItemOptionService.save(menuItemOptionDTO);
        return ResponseEntity.created(new URI("/api/menu-item-options/" + menuItemOptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, menuItemOptionDTO.getId().toString()))
            .body(menuItemOptionDTO);
    }

    /**
     * {@code PUT  /menu-item-options/:id} : Updates an existing menuItemOption.
     *
     * @param id the id of the menuItemOptionDTO to save.
     * @param menuItemOptionDTO the menuItemOptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemOptionDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemOptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuItemOptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemOptionDTO> updateMenuItemOption(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MenuItemOptionDTO menuItemOptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuItemOption : {}, {}", id, menuItemOptionDTO);
        if (menuItemOptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemOptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuItemOptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        menuItemOptionDTO = menuItemOptionService.update(menuItemOptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuItemOptionDTO.getId().toString()))
            .body(menuItemOptionDTO);
    }

    /**
     * {@code PATCH  /menu-item-options/:id} : Partial updates given fields of an existing menuItemOption, field will ignore if it is null
     *
     * @param id the id of the menuItemOptionDTO to save.
     * @param menuItemOptionDTO the menuItemOptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemOptionDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemOptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuItemOptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuItemOptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MenuItemOptionDTO> partialUpdateMenuItemOption(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MenuItemOptionDTO menuItemOptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuItemOption partially : {}, {}", id, menuItemOptionDTO);
        if (menuItemOptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemOptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuItemOptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MenuItemOptionDTO> result = menuItemOptionService.partialUpdate(menuItemOptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuItemOptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /menu-item-options} : get all the menuItemOptions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuItemOptions in body.
     */
    @GetMapping("")
    public List<MenuItemOptionDTO> getAllMenuItemOptions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MenuItemOptions");
        return menuItemOptionService.findAll();
    }

    /**
     * {@code GET  /menu-item-options/:id} : get the "id" menuItemOption.
     *
     * @param id the id of the menuItemOptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuItemOptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemOptionDTO> getMenuItemOption(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MenuItemOption : {}", id);
        Optional<MenuItemOptionDTO> menuItemOptionDTO = menuItemOptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuItemOptionDTO);
    }

    /**
     * {@code DELETE  /menu-item-options/:id} : delete the "id" menuItemOption.
     *
     * @param id the id of the menuItemOptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItemOption(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MenuItemOption : {}", id);
        menuItemOptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
