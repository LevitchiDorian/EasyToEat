package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.repository.MenuCategoryRepository;
import md.utm.restaurant.service.criteria.MenuCategoryCriteria;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
import md.utm.restaurant.service.mapper.MenuCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link MenuCategory} entities in the database.
 * The main input is a {@link MenuCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MenuCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MenuCategoryQueryService extends QueryService<MenuCategory> {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCategoryQueryService.class);

    private final MenuCategoryRepository menuCategoryRepository;

    private final MenuCategoryMapper menuCategoryMapper;

    public MenuCategoryQueryService(MenuCategoryRepository menuCategoryRepository, MenuCategoryMapper menuCategoryMapper) {
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuCategoryMapper = menuCategoryMapper;
    }

    /**
     * Return a {@link Page} of {@link MenuCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MenuCategoryDTO> findByCriteria(MenuCategoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MenuCategory> specification = createSpecification(criteria);
        return menuCategoryRepository.findAll(specification, page).map(menuCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MenuCategoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<MenuCategory> specification = createSpecification(criteria);
        return menuCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link MenuCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MenuCategory> createSpecification(MenuCategoryCriteria criteria) {
        Specification<MenuCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), MenuCategory_.id),
                buildStringSpecification(criteria.getName(), MenuCategory_.name),
                buildStringSpecification(criteria.getDescription(), MenuCategory_.description),
                buildStringSpecification(criteria.getImageUrl(), MenuCategory_.imageUrl),
                buildRangeSpecification(criteria.getDisplayOrder(), MenuCategory_.displayOrder),
                buildSpecification(criteria.getIsActive(), MenuCategory_.isActive),
                buildSpecification(criteria.getItemsId(), root -> root.join(MenuCategory_.items, JoinType.LEFT).get(MenuItem_.id)),
                buildSpecification(criteria.getParentId(), root -> root.join(MenuCategory_.parent, JoinType.LEFT).get(MenuCategory_.id)),
                buildSpecification(criteria.getBrandId(), root -> root.join(MenuCategory_.brand, JoinType.LEFT).get(Brand_.id))
            );
        }
        return specification;
    }
}
