package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.MenuCategoryAsserts.*;
import static md.utm.restaurant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.repository.MenuCategoryRepository;
import md.utm.restaurant.service.MenuCategoryService;
import md.utm.restaurant.service.dto.MenuCategoryDTO;
import md.utm.restaurant.service.mapper.MenuCategoryMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MenuCategoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MenuCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;
    private static final Integer SMALLER_DISPLAY_ORDER = 0 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/menu-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepositoryMock;

    @Autowired
    private MenuCategoryMapper menuCategoryMapper;

    @Mock
    private MenuCategoryService menuCategoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMenuCategoryMockMvc;

    private MenuCategory menuCategory;

    private MenuCategory insertedMenuCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuCategory createEntity(EntityManager em) {
        MenuCategory menuCategory = new MenuCategory()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .imageUrl(DEFAULT_IMAGE_URL)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .isActive(DEFAULT_IS_ACTIVE);
        // Add required entity
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            brand = BrandResourceIT.createEntity();
            em.persist(brand);
            em.flush();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        menuCategory.setBrand(brand);
        return menuCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuCategory createUpdatedEntity(EntityManager em) {
        MenuCategory updatedMenuCategory = new MenuCategory()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);
        // Add required entity
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            brand = BrandResourceIT.createUpdatedEntity();
            em.persist(brand);
            em.flush();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        updatedMenuCategory.setBrand(brand);
        return updatedMenuCategory;
    }

    @BeforeEach
    void initTest() {
        menuCategory = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuCategory != null) {
            menuCategoryRepository.delete(insertedMenuCategory);
            insertedMenuCategory = null;
        }
    }

    @Test
    @Transactional
    void createMenuCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);
        var returnedMenuCategoryDTO = om.readValue(
            restMenuCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuCategoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MenuCategoryDTO.class
        );

        // Validate the MenuCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuCategory = menuCategoryMapper.toEntity(returnedMenuCategoryDTO);
        assertMenuCategoryUpdatableFieldsEquals(returnedMenuCategory, getPersistedMenuCategory(returnedMenuCategory));

        insertedMenuCategory = returnedMenuCategory;
    }

    @Test
    @Transactional
    void createMenuCategoryWithExistingId() throws Exception {
        // Create the MenuCategory with an existing ID
        menuCategory.setId(1L);
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMenuCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuCategory.setName(null);

        // Create the MenuCategory, which fails.
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        restMenuCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuCategory.setDisplayOrder(null);

        // Create the MenuCategory, which fails.
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        restMenuCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuCategory.setIsActive(null);

        // Create the MenuCategory, which fails.
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        restMenuCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMenuCategories() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList
        restMenuCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuCategoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(menuCategoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuCategoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(menuCategoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuCategoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(menuCategoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuCategoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(menuCategoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMenuCategory() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get the menuCategory
        restMenuCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, menuCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(menuCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getMenuCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        Long id = menuCategory.getId();

        defaultMenuCategoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMenuCategoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMenuCategoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where name equals to
        defaultMenuCategoryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where name in
        defaultMenuCategoryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where name is not null
        defaultMenuCategoryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where name contains
        defaultMenuCategoryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where name does not contain
        defaultMenuCategoryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where description equals to
        defaultMenuCategoryFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where description in
        defaultMenuCategoryFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where description is not null
        defaultMenuCategoryFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where description contains
        defaultMenuCategoryFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where description does not contain
        defaultMenuCategoryFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where imageUrl equals to
        defaultMenuCategoryFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where imageUrl in
        defaultMenuCategoryFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where imageUrl is not null
        defaultMenuCategoryFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where imageUrl contains
        defaultMenuCategoryFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where imageUrl does not contain
        defaultMenuCategoryFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder equals to
        defaultMenuCategoryFiltering("displayOrder.equals=" + DEFAULT_DISPLAY_ORDER, "displayOrder.equals=" + UPDATED_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder in
        defaultMenuCategoryFiltering(
            "displayOrder.in=" + DEFAULT_DISPLAY_ORDER + "," + UPDATED_DISPLAY_ORDER,
            "displayOrder.in=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder is not null
        defaultMenuCategoryFiltering("displayOrder.specified=true", "displayOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder is greater than or equal to
        defaultMenuCategoryFiltering(
            "displayOrder.greaterThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.greaterThanOrEqual=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder is less than or equal to
        defaultMenuCategoryFiltering(
            "displayOrder.lessThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.lessThanOrEqual=" + SMALLER_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder is less than
        defaultMenuCategoryFiltering("displayOrder.lessThan=" + UPDATED_DISPLAY_ORDER, "displayOrder.lessThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByDisplayOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where displayOrder is greater than
        defaultMenuCategoryFiltering(
            "displayOrder.greaterThan=" + SMALLER_DISPLAY_ORDER,
            "displayOrder.greaterThan=" + DEFAULT_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where isActive equals to
        defaultMenuCategoryFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where isActive in
        defaultMenuCategoryFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        // Get all the menuCategoryList where isActive is not null
        defaultMenuCategoryFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByParentIsEqualToSomething() throws Exception {
        MenuCategory parent;
        if (TestUtil.findAll(em, MenuCategory.class).isEmpty()) {
            menuCategoryRepository.saveAndFlush(menuCategory);
            parent = MenuCategoryResourceIT.createEntity(em);
        } else {
            parent = TestUtil.findAll(em, MenuCategory.class).get(0);
        }
        em.persist(parent);
        em.flush();
        menuCategory.setParent(parent);
        menuCategoryRepository.saveAndFlush(menuCategory);
        Long parentId = parent.getId();
        // Get all the menuCategoryList where parent equals to parentId
        defaultMenuCategoryShouldBeFound("parentId.equals=" + parentId);

        // Get all the menuCategoryList where parent equals to (parentId + 1)
        defaultMenuCategoryShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    @Test
    @Transactional
    void getAllMenuCategoriesByBrandIsEqualToSomething() throws Exception {
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            menuCategoryRepository.saveAndFlush(menuCategory);
            brand = BrandResourceIT.createEntity();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        em.persist(brand);
        em.flush();
        menuCategory.setBrand(brand);
        menuCategoryRepository.saveAndFlush(menuCategory);
        Long brandId = brand.getId();
        // Get all the menuCategoryList where brand equals to brandId
        defaultMenuCategoryShouldBeFound("brandId.equals=" + brandId);

        // Get all the menuCategoryList where brand equals to (brandId + 1)
        defaultMenuCategoryShouldNotBeFound("brandId.equals=" + (brandId + 1));
    }

    private void defaultMenuCategoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMenuCategoryShouldBeFound(shouldBeFound);
        defaultMenuCategoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMenuCategoryShouldBeFound(String filter) throws Exception {
        restMenuCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restMenuCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMenuCategoryShouldNotBeFound(String filter) throws Exception {
        restMenuCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMenuCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMenuCategory() throws Exception {
        // Get the menuCategory
        restMenuCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMenuCategory() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuCategory
        MenuCategory updatedMenuCategory = menuCategoryRepository.findById(menuCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMenuCategory are not directly saved in db
        em.detach(updatedMenuCategory);
        updatedMenuCategory
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(updatedMenuCategory);

        restMenuCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuCategoryToMatchAllProperties(updatedMenuCategory);
    }

    @Test
    @Transactional
    void putNonExistingMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategory.setId(longCount.incrementAndGet());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategory.setId(longCount.incrementAndGet());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategory.setId(longCount.incrementAndGet());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMenuCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuCategory using partial update
        MenuCategory partialUpdatedMenuCategory = new MenuCategory();
        partialUpdatedMenuCategory.setId(menuCategory.getId());

        partialUpdatedMenuCategory.imageUrl(UPDATED_IMAGE_URL);

        restMenuCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuCategory))
            )
            .andExpect(status().isOk());

        // Validate the MenuCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuCategory, menuCategory),
            getPersistedMenuCategory(menuCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdateMenuCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuCategory using partial update
        MenuCategory partialUpdatedMenuCategory = new MenuCategory();
        partialUpdatedMenuCategory.setId(menuCategory.getId());

        partialUpdatedMenuCategory
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);

        restMenuCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuCategory))
            )
            .andExpect(status().isOk());

        // Validate the MenuCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuCategoryUpdatableFieldsEquals(partialUpdatedMenuCategory, getPersistedMenuCategory(partialUpdatedMenuCategory));
    }

    @Test
    @Transactional
    void patchNonExistingMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategory.setId(longCount.incrementAndGet());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, menuCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategory.setId(longCount.incrementAndGet());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategory.setId(longCount.incrementAndGet());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(menuCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMenuCategory() throws Exception {
        // Initialize the database
        insertedMenuCategory = menuCategoryRepository.saveAndFlush(menuCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuCategory
        restMenuCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, menuCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuCategoryRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected MenuCategory getPersistedMenuCategory(MenuCategory menuCategory) {
        return menuCategoryRepository.findById(menuCategory.getId()).orElseThrow();
    }

    protected void assertPersistedMenuCategoryToMatchAllProperties(MenuCategory expectedMenuCategory) {
        assertMenuCategoryAllPropertiesEquals(expectedMenuCategory, getPersistedMenuCategory(expectedMenuCategory));
    }

    protected void assertPersistedMenuCategoryToMatchUpdatableProperties(MenuCategory expectedMenuCategory) {
        assertMenuCategoryAllUpdatablePropertiesEquals(expectedMenuCategory, getPersistedMenuCategory(expectedMenuCategory));
    }
}
