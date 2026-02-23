package md.utm.restaurant.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.MenuItemAllergen;
import md.utm.restaurant.repository.MenuItemAllergenRepository;
import md.utm.restaurant.service.dto.MenuItemAllergenDTO;
import md.utm.restaurant.service.mapper.MenuItemAllergenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.MenuItemAllergen}.
 */
@Service
@Transactional
public class MenuItemAllergenService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemAllergenService.class);

    private final MenuItemAllergenRepository menuItemAllergenRepository;

    private final MenuItemAllergenMapper menuItemAllergenMapper;

    public MenuItemAllergenService(MenuItemAllergenRepository menuItemAllergenRepository, MenuItemAllergenMapper menuItemAllergenMapper) {
        this.menuItemAllergenRepository = menuItemAllergenRepository;
        this.menuItemAllergenMapper = menuItemAllergenMapper;
    }

    /**
     * Save a menuItemAllergen.
     *
     * @param menuItemAllergenDTO the entity to save.
     * @return the persisted entity.
     */
    public MenuItemAllergenDTO save(MenuItemAllergenDTO menuItemAllergenDTO) {
        LOG.debug("Request to save MenuItemAllergen : {}", menuItemAllergenDTO);
        MenuItemAllergen menuItemAllergen = menuItemAllergenMapper.toEntity(menuItemAllergenDTO);
        menuItemAllergen = menuItemAllergenRepository.save(menuItemAllergen);
        return menuItemAllergenMapper.toDto(menuItemAllergen);
    }

    /**
     * Update a menuItemAllergen.
     *
     * @param menuItemAllergenDTO the entity to save.
     * @return the persisted entity.
     */
    public MenuItemAllergenDTO update(MenuItemAllergenDTO menuItemAllergenDTO) {
        LOG.debug("Request to update MenuItemAllergen : {}", menuItemAllergenDTO);
        MenuItemAllergen menuItemAllergen = menuItemAllergenMapper.toEntity(menuItemAllergenDTO);
        menuItemAllergen = menuItemAllergenRepository.save(menuItemAllergen);
        return menuItemAllergenMapper.toDto(menuItemAllergen);
    }

    /**
     * Partially update a menuItemAllergen.
     *
     * @param menuItemAllergenDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MenuItemAllergenDTO> partialUpdate(MenuItemAllergenDTO menuItemAllergenDTO) {
        LOG.debug("Request to partially update MenuItemAllergen : {}", menuItemAllergenDTO);

        return menuItemAllergenRepository
            .findById(menuItemAllergenDTO.getId())
            .map(existingMenuItemAllergen -> {
                menuItemAllergenMapper.partialUpdate(existingMenuItemAllergen, menuItemAllergenDTO);

                return existingMenuItemAllergen;
            })
            .map(menuItemAllergenRepository::save)
            .map(menuItemAllergenMapper::toDto);
    }

    /**
     * Get all the menuItemAllergens.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MenuItemAllergenDTO> findAll() {
        LOG.debug("Request to get all MenuItemAllergens");
        return menuItemAllergenRepository
            .findAll()
            .stream()
            .map(menuItemAllergenMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the menuItemAllergens with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MenuItemAllergenDTO> findAllWithEagerRelationships(Pageable pageable) {
        return menuItemAllergenRepository.findAllWithEagerRelationships(pageable).map(menuItemAllergenMapper::toDto);
    }

    /**
     * Get one menuItemAllergen by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MenuItemAllergenDTO> findOne(Long id) {
        LOG.debug("Request to get MenuItemAllergen : {}", id);
        return menuItemAllergenRepository.findOneWithEagerRelationships(id).map(menuItemAllergenMapper::toDto);
    }

    /**
     * Delete the menuItemAllergen by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MenuItemAllergen : {}", id);
        menuItemAllergenRepository.deleteById(id);
    }
}
