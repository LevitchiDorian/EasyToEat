package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.repository.MenuItemRepository;
import md.utm.restaurant.service.MenuItemService;
import md.utm.restaurant.service.dto.MenuItemDTO;
import md.utm.restaurant.service.mapper.MenuItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.MenuItem}.
 */
@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemServiceImpl.class);

    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
    }

    @Override
    public MenuItemDTO save(MenuItemDTO menuItemDTO) {
        LOG.debug("Request to save MenuItem : {}", menuItemDTO);
        MenuItem menuItem = menuItemMapper.toEntity(menuItemDTO);
        menuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(menuItem);
    }

    @Override
    public MenuItemDTO update(MenuItemDTO menuItemDTO) {
        LOG.debug("Request to update MenuItem : {}", menuItemDTO);
        MenuItem menuItem = menuItemMapper.toEntity(menuItemDTO);
        menuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(menuItem);
    }

    @Override
    public Optional<MenuItemDTO> partialUpdate(MenuItemDTO menuItemDTO) {
        LOG.debug("Request to partially update MenuItem : {}", menuItemDTO);

        return menuItemRepository
            .findById(menuItemDTO.getId())
            .map(existingMenuItem -> {
                menuItemMapper.partialUpdate(existingMenuItem, menuItemDTO);

                return existingMenuItem;
            })
            .map(menuItemRepository::save)
            .map(menuItemMapper::toDto);
    }

    public Page<MenuItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return menuItemRepository.findAllWithEagerRelationships(pageable).map(menuItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItemDTO> findOne(Long id) {
        LOG.debug("Request to get MenuItem : {}", id);
        return menuItemRepository.findOneWithEagerRelationships(id).map(menuItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MenuItem : {}", id);
        menuItemRepository.deleteById(id);
    }
}
