package md.utm.restaurant.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.MenuItemOption;
import md.utm.restaurant.repository.MenuItemOptionRepository;
import md.utm.restaurant.service.dto.MenuItemOptionDTO;
import md.utm.restaurant.service.mapper.MenuItemOptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.MenuItemOption}.
 */
@Service
@Transactional
public class MenuItemOptionService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemOptionService.class);

    private final MenuItemOptionRepository menuItemOptionRepository;

    private final MenuItemOptionMapper menuItemOptionMapper;

    public MenuItemOptionService(MenuItemOptionRepository menuItemOptionRepository, MenuItemOptionMapper menuItemOptionMapper) {
        this.menuItemOptionRepository = menuItemOptionRepository;
        this.menuItemOptionMapper = menuItemOptionMapper;
    }

    /**
     * Save a menuItemOption.
     *
     * @param menuItemOptionDTO the entity to save.
     * @return the persisted entity.
     */
    public MenuItemOptionDTO save(MenuItemOptionDTO menuItemOptionDTO) {
        LOG.debug("Request to save MenuItemOption : {}", menuItemOptionDTO);
        MenuItemOption menuItemOption = menuItemOptionMapper.toEntity(menuItemOptionDTO);
        menuItemOption = menuItemOptionRepository.save(menuItemOption);
        return menuItemOptionMapper.toDto(menuItemOption);
    }

    /**
     * Update a menuItemOption.
     *
     * @param menuItemOptionDTO the entity to save.
     * @return the persisted entity.
     */
    public MenuItemOptionDTO update(MenuItemOptionDTO menuItemOptionDTO) {
        LOG.debug("Request to update MenuItemOption : {}", menuItemOptionDTO);
        MenuItemOption menuItemOption = menuItemOptionMapper.toEntity(menuItemOptionDTO);
        menuItemOption = menuItemOptionRepository.save(menuItemOption);
        return menuItemOptionMapper.toDto(menuItemOption);
    }

    /**
     * Partially update a menuItemOption.
     *
     * @param menuItemOptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MenuItemOptionDTO> partialUpdate(MenuItemOptionDTO menuItemOptionDTO) {
        LOG.debug("Request to partially update MenuItemOption : {}", menuItemOptionDTO);

        return menuItemOptionRepository
            .findById(menuItemOptionDTO.getId())
            .map(existingMenuItemOption -> {
                menuItemOptionMapper.partialUpdate(existingMenuItemOption, menuItemOptionDTO);

                return existingMenuItemOption;
            })
            .map(menuItemOptionRepository::save)
            .map(menuItemOptionMapper::toDto);
    }

    /**
     * Get all the menuItemOptions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MenuItemOptionDTO> findAll() {
        LOG.debug("Request to get all MenuItemOptions");
        return menuItemOptionRepository
            .findAll()
            .stream()
            .map(menuItemOptionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the menuItemOptions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MenuItemOptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return menuItemOptionRepository.findAllWithEagerRelationships(pageable).map(menuItemOptionMapper::toDto);
    }

    /**
     * Get one menuItemOption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MenuItemOptionDTO> findOne(Long id) {
        LOG.debug("Request to get MenuItemOption : {}", id);
        return menuItemOptionRepository.findOneWithEagerRelationships(id).map(menuItemOptionMapper::toDto);
    }

    /**
     * Delete the menuItemOption by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MenuItemOption : {}", id);
        menuItemOptionRepository.deleteById(id);
    }
}
