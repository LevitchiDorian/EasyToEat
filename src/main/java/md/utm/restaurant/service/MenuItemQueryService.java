package md.utm.restaurant.service;

import jakarta.persistence.criteria.JoinType;
import md.utm.restaurant.domain.*; // for static metamodels
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.repository.MenuItemRepository;
import md.utm.restaurant.service.criteria.MenuItemCriteria;
import md.utm.restaurant.service.dto.MenuItemDTO;
import md.utm.restaurant.service.mapper.MenuItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link MenuItem} entities in the database.
 * The main input is a {@link MenuItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MenuItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MenuItemQueryService extends QueryService<MenuItem> {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemQueryService.class);

    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;

    public MenuItemQueryService(MenuItemRepository menuItemRepository, MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
    }

    /**
     * Return a {@link Page} of {@link MenuItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MenuItemDTO> findByCriteria(MenuItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MenuItem> specification = createSpecification(criteria);
        return menuItemRepository.findAll(specification, page).map(menuItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MenuItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<MenuItem> specification = createSpecification(criteria);
        return menuItemRepository.count(specification);
    }

    /**
     * Function to convert {@link MenuItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MenuItem> createSpecification(MenuItemCriteria criteria) {
        Specification<MenuItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), MenuItem_.id),
                buildStringSpecification(criteria.getName(), MenuItem_.name),
                buildRangeSpecification(criteria.getPrice(), MenuItem_.price),
                buildRangeSpecification(criteria.getDiscountedPrice(), MenuItem_.discountedPrice),
                buildRangeSpecification(criteria.getPreparationTimeMinutes(), MenuItem_.preparationTimeMinutes),
                buildRangeSpecification(criteria.getCalories(), MenuItem_.calories),
                buildStringSpecification(criteria.getImageUrl(), MenuItem_.imageUrl),
                buildSpecification(criteria.getIsAvailable(), MenuItem_.isAvailable),
                buildSpecification(criteria.getIsFeatured(), MenuItem_.isFeatured),
                buildSpecification(criteria.getIsVegetarian(), MenuItem_.isVegetarian),
                buildSpecification(criteria.getIsVegan(), MenuItem_.isVegan),
                buildSpecification(criteria.getIsGlutenFree(), MenuItem_.isGlutenFree),
                buildRangeSpecification(criteria.getSpicyLevel(), MenuItem_.spicyLevel),
                buildRangeSpecification(criteria.getDisplayOrder(), MenuItem_.displayOrder),
                buildSpecification(criteria.getAllergensId(), root ->
                    root.join(MenuItem_.allergens, JoinType.LEFT).get(MenuItemAllergen_.id)
                ),
                buildSpecification(criteria.getOptionsId(), root -> root.join(MenuItem_.options, JoinType.LEFT).get(MenuItemOption_.id)),
                buildSpecification(criteria.getCategoryId(), root -> root.join(MenuItem_.category, JoinType.LEFT).get(MenuCategory_.id))
            );
        }
        return specification;
    }
}
