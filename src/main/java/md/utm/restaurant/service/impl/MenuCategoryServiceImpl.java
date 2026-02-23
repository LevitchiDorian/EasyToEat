package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.repository.MenuCategoryRepository;
import md.utm.restaurant.service.MenuCategoryService;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
import md.utm.restaurant.service.mapper.MenuCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.MenuCategory}.
 */
@Service
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCategoryServiceImpl.class);

    private final MenuCategoryRepository menuCategoryRepository;

    private final MenuCategoryMapper menuCategoryMapper;

    public MenuCategoryServiceImpl(MenuCategoryRepository menuCategoryRepository, MenuCategoryMapper menuCategoryMapper) {
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuCategoryMapper = menuCategoryMapper;
    }

    @Override
    public MenuCategoryDTO save(MenuCategoryDTO menuCategoryDTO) {
        LOG.debug("Request to save MenuCategory : {}", menuCategoryDTO);
        MenuCategory menuCategory = menuCategoryMapper.toEntity(menuCategoryDTO);
        menuCategory = menuCategoryRepository.save(menuCategory);
        return menuCategoryMapper.toDto(menuCategory);
    }

    @Override
    public MenuCategoryDTO update(MenuCategoryDTO menuCategoryDTO) {
        LOG.debug("Request to update MenuCategory : {}", menuCategoryDTO);
        MenuCategory menuCategory = menuCategoryMapper.toEntity(menuCategoryDTO);
        menuCategory = menuCategoryRepository.save(menuCategory);
        return menuCategoryMapper.toDto(menuCategory);
    }

    @Override
    public Optional<MenuCategoryDTO> partialUpdate(MenuCategoryDTO menuCategoryDTO) {
        LOG.debug("Request to partially update MenuCategory : {}", menuCategoryDTO);

        return menuCategoryRepository
            .findById(menuCategoryDTO.getId())
            .map(existingMenuCategory -> {
                menuCategoryMapper.partialUpdate(existingMenuCategory, menuCategoryDTO);

                return existingMenuCategory;
            })
            .map(menuCategoryRepository::save)
            .map(menuCategoryMapper::toDto);
    }

    public Page<MenuCategoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return menuCategoryRepository.findAllWithEagerRelationships(pageable).map(menuCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get MenuCategory : {}", id);
        return menuCategoryRepository.findOneWithEagerRelationships(id).map(menuCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MenuCategory : {}", id);
        menuCategoryRepository.deleteById(id);
    }
}
