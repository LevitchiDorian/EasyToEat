package md.utm.restaurant.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.MenuItemOptionValue;
import md.utm.restaurant.repository.MenuItemOptionValueRepository;
import md.utm.restaurant.service.dto.MenuItemOptionValueDTO;
import md.utm.restaurant.service.mapper.MenuItemOptionValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.MenuItemOptionValue}.
 */
@Service
@Transactional
public class MenuItemOptionValueService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemOptionValueService.class);

    private final MenuItemOptionValueRepository menuItemOptionValueRepository;

    private final MenuItemOptionValueMapper menuItemOptionValueMapper;

    public MenuItemOptionValueService(
        MenuItemOptionValueRepository menuItemOptionValueRepository,
        MenuItemOptionValueMapper menuItemOptionValueMapper
    ) {
        this.menuItemOptionValueRepository = menuItemOptionValueRepository;
        this.menuItemOptionValueMapper = menuItemOptionValueMapper;
    }

    /**
     * Save a menuItemOptionValue.
     *
     * @param menuItemOptionValueDTO the entity to save.
     * @return the persisted entity.
     */
    public MenuItemOptionValueDTO save(MenuItemOptionValueDTO menuItemOptionValueDTO) {
        LOG.debug("Request to save MenuItemOptionValue : {}", menuItemOptionValueDTO);
        MenuItemOptionValue menuItemOptionValue = menuItemOptionValueMapper.toEntity(menuItemOptionValueDTO);
        menuItemOptionValue = menuItemOptionValueRepository.save(menuItemOptionValue);
        return menuItemOptionValueMapper.toDto(menuItemOptionValue);
    }

    /**
     * Update a menuItemOptionValue.
     *
     * @param menuItemOptionValueDTO the entity to save.
     * @return the persisted entity.
     */
    public MenuItemOptionValueDTO update(MenuItemOptionValueDTO menuItemOptionValueDTO) {
        LOG.debug("Request to update MenuItemOptionValue : {}", menuItemOptionValueDTO);
        MenuItemOptionValue menuItemOptionValue = menuItemOptionValueMapper.toEntity(menuItemOptionValueDTO);
        menuItemOptionValue = menuItemOptionValueRepository.save(menuItemOptionValue);
        return menuItemOptionValueMapper.toDto(menuItemOptionValue);
    }

    /**
     * Partially update a menuItemOptionValue.
     *
     * @param menuItemOptionValueDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MenuItemOptionValueDTO> partialUpdate(MenuItemOptionValueDTO menuItemOptionValueDTO) {
        LOG.debug("Request to partially update MenuItemOptionValue : {}", menuItemOptionValueDTO);

        return menuItemOptionValueRepository
            .findById(menuItemOptionValueDTO.getId())
            .map(existingMenuItemOptionValue -> {
                menuItemOptionValueMapper.partialUpdate(existingMenuItemOptionValue, menuItemOptionValueDTO);

                return existingMenuItemOptionValue;
            })
            .map(menuItemOptionValueRepository::save)
            .map(menuItemOptionValueMapper::toDto);
    }

    /**
     * Get all the menuItemOptionValues.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MenuItemOptionValueDTO> findAll() {
        LOG.debug("Request to get all MenuItemOptionValues");
        return menuItemOptionValueRepository
            .findAll()
            .stream()
            .map(menuItemOptionValueMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the menuItemOptionValues with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MenuItemOptionValueDTO> findAllWithEagerRelationships(Pageable pageable) {
        return menuItemOptionValueRepository.findAllWithEagerRelationships(pageable).map(menuItemOptionValueMapper::toDto);
    }

    /**
     * Get one menuItemOptionValue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MenuItemOptionValueDTO> findOne(Long id) {
        LOG.debug("Request to get MenuItemOptionValue : {}", id);
        return menuItemOptionValueRepository.findOneWithEagerRelationships(id).map(menuItemOptionValueMapper::toDto);
    }

    /**
     * Delete the menuItemOptionValue by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MenuItemOptionValue : {}", id);
        menuItemOptionValueRepository.deleteById(id);
    }
}
