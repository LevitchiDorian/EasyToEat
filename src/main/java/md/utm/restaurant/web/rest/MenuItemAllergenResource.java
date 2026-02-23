package md.utm.restaurant.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.repository.MenuItemAllergenRepository;
import md.utm.restaurant.service.MenuItemAllergenService;
import md.utm.restaurant.service.dto.MenuItemAllergenDTO;
import md.utm.restaurant.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link md.utm.restaurant.domain.MenuItemAllergen}.
 */
@RestController
@RequestMapping("/api/menu-item-allergens")
public class MenuItemAllergenResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemAllergenResource.class);

    private static final String ENTITY_NAME = "menuItemAllergen";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuItemAllergenService menuItemAllergenService;

    private final MenuItemAllergenRepository menuItemAllergenRepository;

    public MenuItemAllergenResource(
        MenuItemAllergenService menuItemAllergenService,
        MenuItemAllergenRepository menuItemAllergenRepository
    ) {
        this.menuItemAllergenService = menuItemAllergenService;
        this.menuItemAllergenRepository = menuItemAllergenRepository;
    }

    /**
     * {@code POST  /menu-item-allergens} : Create a new menuItemAllergen.
     *
     * @param menuItemAllergenDTO the menuItemAllergenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuItemAllergenDTO, or with status {@code 400 (Bad Request)} if the menuItemAllergen has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MenuItemAllergenDTO> createMenuItemAllergen(@Valid @RequestBody MenuItemAllergenDTO menuItemAllergenDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MenuItemAllergen : {}", menuItemAllergenDTO);
        if (menuItemAllergenDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuItemAllergen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuItemAllergenDTO = menuItemAllergenService.save(menuItemAllergenDTO);
        return ResponseEntity.created(new URI("/api/menu-item-allergens/" + menuItemAllergenDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, menuItemAllergenDTO.getId().toString()))
            .body(menuItemAllergenDTO);
    }

    /**
     * {@code PUT  /menu-item-allergens/:id} : Updates an existing menuItemAllergen.
     *
     * @param id the id of the menuItemAllergenDTO to save.
     * @param menuItemAllergenDTO the menuItemAllergenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemAllergenDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemAllergenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuItemAllergenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemAllergenDTO> updateMenuItemAllergen(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MenuItemAllergenDTO menuItemAllergenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuItemAllergen : {}, {}", id, menuItemAllergenDTO);
        if (menuItemAllergenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemAllergenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuItemAllergenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        menuItemAllergenDTO = menuItemAllergenService.update(menuItemAllergenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuItemAllergenDTO.getId().toString()))
            .body(menuItemAllergenDTO);
    }

    /**
     * {@code PATCH  /menu-item-allergens/:id} : Partial updates given fields of an existing menuItemAllergen, field will ignore if it is null
     *
     * @param id the id of the menuItemAllergenDTO to save.
     * @param menuItemAllergenDTO the menuItemAllergenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemAllergenDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemAllergenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuItemAllergenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuItemAllergenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MenuItemAllergenDTO> partialUpdateMenuItemAllergen(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MenuItemAllergenDTO menuItemAllergenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuItemAllergen partially : {}, {}", id, menuItemAllergenDTO);
        if (menuItemAllergenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemAllergenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuItemAllergenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MenuItemAllergenDTO> result = menuItemAllergenService.partialUpdate(menuItemAllergenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuItemAllergenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /menu-item-allergens} : get all the menuItemAllergens.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuItemAllergens in body.
     */
    @GetMapping("")
    public List<MenuItemAllergenDTO> getAllMenuItemAllergens(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MenuItemAllergens");
        return menuItemAllergenService.findAll();
    }

    /**
     * {@code GET  /menu-item-allergens/:id} : get the "id" menuItemAllergen.
     *
     * @param id the id of the menuItemAllergenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuItemAllergenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemAllergenDTO> getMenuItemAllergen(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MenuItemAllergen : {}", id);
        Optional<MenuItemAllergenDTO> menuItemAllergenDTO = menuItemAllergenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuItemAllergenDTO);
    }

    /**
     * {@code DELETE  /menu-item-allergens/:id} : delete the "id" menuItemAllergen.
     *
     * @param id the id of the menuItemAllergenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItemAllergen(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MenuItemAllergen : {}", id);
        menuItemAllergenService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
