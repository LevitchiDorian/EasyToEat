package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.MenuItemAsserts.*;
import static md.utm.restaurant.web.rest.TestUtil.createUpdateProxyForBean;
import static md.utm.restaurant.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.MenuCategory;
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.repository.MenuItemRepository;
import md.utm.restaurant.service.MenuItemService;
import md.utm.restaurant.service.dto.MenuItemDTO;
import md.utm.restaurant.service.mapper.MenuItemMapper;
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
 * Integration tests for the {@link MenuItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MenuItemResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_DISCOUNTED_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNTED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNTED_PRICE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_PREPARATION_TIME_MINUTES = 0;
    private static final Integer UPDATED_PREPARATION_TIME_MINUTES = 1;
    private static final Integer SMALLER_PREPARATION_TIME_MINUTES = 0 - 1;

    private static final Integer DEFAULT_CALORIES = 0;
    private static final Integer UPDATED_CALORIES = 1;
    private static final Integer SMALLER_CALORIES = 0 - 1;

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_AVAILABLE = false;
    private static final Boolean UPDATED_IS_AVAILABLE = true;

    private static final Boolean DEFAULT_IS_FEATURED = false;
    private static final Boolean UPDATED_IS_FEATURED = true;

    private static final Boolean DEFAULT_IS_VEGETARIAN = false;
    private static final Boolean UPDATED_IS_VEGETARIAN = true;

    private static final Boolean DEFAULT_IS_VEGAN = false;
    private static final Boolean UPDATED_IS_VEGAN = true;

    private static final Boolean DEFAULT_IS_GLUTEN_FREE = false;
    private static final Boolean UPDATED_IS_GLUTEN_FREE = true;

    private static final Integer DEFAULT_SPICY_LEVEL = 0;
    private static final Integer UPDATED_SPICY_LEVEL = 1;
    private static final Integer SMALLER_SPICY_LEVEL = 0 - 1;

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;
    private static final Integer SMALLER_DISPLAY_ORDER = 0 - 1;

    private static final String ENTITY_API_URL = "/api/menu-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuItemRepository menuItemRepositoryMock;

    @Autowired
    private MenuItemMapper menuItemMapper;

    @Mock
    private MenuItemService menuItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMenuItemMockMvc;

    private MenuItem menuItem;

    private MenuItem insertedMenuItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItem createEntity(EntityManager em) {
        MenuItem menuItem = new MenuItem()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .discountedPrice(DEFAULT_DISCOUNTED_PRICE)
            .preparationTimeMinutes(DEFAULT_PREPARATION_TIME_MINUTES)
            .calories(DEFAULT_CALORIES)
            .imageUrl(DEFAULT_IMAGE_URL)
            .isAvailable(DEFAULT_IS_AVAILABLE)
            .isFeatured(DEFAULT_IS_FEATURED)
            .isVegetarian(DEFAULT_IS_VEGETARIAN)
            .isVegan(DEFAULT_IS_VEGAN)
            .isGlutenFree(DEFAULT_IS_GLUTEN_FREE)
            .spicyLevel(DEFAULT_SPICY_LEVEL)
            .displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        MenuCategory menuCategory;
        if (TestUtil.findAll(em, MenuCategory.class).isEmpty()) {
            menuCategory = MenuCategoryResourceIT.createEntity(em);
            em.persist(menuCategory);
            em.flush();
        } else {
            menuCategory = TestUtil.findAll(em, MenuCategory.class).get(0);
        }
        menuItem.setCategory(menuCategory);
        return menuItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItem createUpdatedEntity(EntityManager em) {
        MenuItem updatedMenuItem = new MenuItem()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .discountedPrice(UPDATED_DISCOUNTED_PRICE)
            .preparationTimeMinutes(UPDATED_PREPARATION_TIME_MINUTES)
            .calories(UPDATED_CALORIES)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isFeatured(UPDATED_IS_FEATURED)
            .isVegetarian(UPDATED_IS_VEGETARIAN)
            .isVegan(UPDATED_IS_VEGAN)
            .isGlutenFree(UPDATED_IS_GLUTEN_FREE)
            .spicyLevel(UPDATED_SPICY_LEVEL)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        MenuCategory menuCategory;
        if (TestUtil.findAll(em, MenuCategory.class).isEmpty()) {
            menuCategory = MenuCategoryResourceIT.createUpdatedEntity(em);
            em.persist(menuCategory);
            em.flush();
        } else {
            menuCategory = TestUtil.findAll(em, MenuCategory.class).get(0);
        }
        updatedMenuItem.setCategory(menuCategory);
        return updatedMenuItem;
    }

    @BeforeEach
    void initTest() {
        menuItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItem != null) {
            menuItemRepository.delete(insertedMenuItem);
            insertedMenuItem = null;
        }
    }

    @Test
    @Transactional
    void createMenuItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);
        var returnedMenuItemDTO = om.readValue(
            restMenuItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MenuItemDTO.class
        );

        // Validate the MenuItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItem = menuItemMapper.toEntity(returnedMenuItemDTO);
        assertMenuItemUpdatableFieldsEquals(returnedMenuItem, getPersistedMenuItem(returnedMenuItem));

        insertedMenuItem = returnedMenuItem;
    }

    @Test
    @Transactional
    void createMenuItemWithExistingId() throws Exception {
        // Create the MenuItem with an existing ID
        menuItem.setId(1L);
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMenuItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItem.setName(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        restMenuItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItem.setPrice(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        restMenuItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItem.setIsAvailable(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        restMenuItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsFeaturedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItem.setIsFeatured(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        restMenuItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItem.setDisplayOrder(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        restMenuItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMenuItems() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList
        restMenuItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].discountedPrice").value(hasItem(sameNumber(DEFAULT_DISCOUNTED_PRICE))))
            .andExpect(jsonPath("$.[*].preparationTimeMinutes").value(hasItem(DEFAULT_PREPARATION_TIME_MINUTES)))
            .andExpect(jsonPath("$.[*].calories").value(hasItem(DEFAULT_CALORIES)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isAvailable").value(hasItem(DEFAULT_IS_AVAILABLE)))
            .andExpect(jsonPath("$.[*].isFeatured").value(hasItem(DEFAULT_IS_FEATURED)))
            .andExpect(jsonPath("$.[*].isVegetarian").value(hasItem(DEFAULT_IS_VEGETARIAN)))
            .andExpect(jsonPath("$.[*].isVegan").value(hasItem(DEFAULT_IS_VEGAN)))
            .andExpect(jsonPath("$.[*].isGlutenFree").value(hasItem(DEFAULT_IS_GLUTEN_FREE)))
            .andExpect(jsonPath("$.[*].spicyLevel").value(hasItem(DEFAULT_SPICY_LEVEL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(menuItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(menuItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(menuItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(menuItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMenuItem() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get the menuItem
        restMenuItemMockMvc
            .perform(get(ENTITY_API_URL_ID, menuItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(menuItem.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.discountedPrice").value(sameNumber(DEFAULT_DISCOUNTED_PRICE)))
            .andExpect(jsonPath("$.preparationTimeMinutes").value(DEFAULT_PREPARATION_TIME_MINUTES))
            .andExpect(jsonPath("$.calories").value(DEFAULT_CALORIES))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.isAvailable").value(DEFAULT_IS_AVAILABLE))
            .andExpect(jsonPath("$.isFeatured").value(DEFAULT_IS_FEATURED))
            .andExpect(jsonPath("$.isVegetarian").value(DEFAULT_IS_VEGETARIAN))
            .andExpect(jsonPath("$.isVegan").value(DEFAULT_IS_VEGAN))
            .andExpect(jsonPath("$.isGlutenFree").value(DEFAULT_IS_GLUTEN_FREE))
            .andExpect(jsonPath("$.spicyLevel").value(DEFAULT_SPICY_LEVEL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getMenuItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        Long id = menuItem.getId();

        defaultMenuItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMenuItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMenuItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMenuItemsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where name equals to
        defaultMenuItemFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenuItemsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where name in
        defaultMenuItemFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenuItemsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where name is not null
        defaultMenuItemFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where name contains
        defaultMenuItemFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenuItemsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where name does not contain
        defaultMenuItemFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price equals to
        defaultMenuItemFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price in
        defaultMenuItemFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price is not null
        defaultMenuItemFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price is greater than or equal to
        defaultMenuItemFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price is less than or equal to
        defaultMenuItemFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price is less than
        defaultMenuItemFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where price is greater than
        defaultMenuItemFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice equals to
        defaultMenuItemFiltering(
            "discountedPrice.equals=" + DEFAULT_DISCOUNTED_PRICE,
            "discountedPrice.equals=" + UPDATED_DISCOUNTED_PRICE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice in
        defaultMenuItemFiltering(
            "discountedPrice.in=" + DEFAULT_DISCOUNTED_PRICE + "," + UPDATED_DISCOUNTED_PRICE,
            "discountedPrice.in=" + UPDATED_DISCOUNTED_PRICE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice is not null
        defaultMenuItemFiltering("discountedPrice.specified=true", "discountedPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice is greater than or equal to
        defaultMenuItemFiltering(
            "discountedPrice.greaterThanOrEqual=" + DEFAULT_DISCOUNTED_PRICE,
            "discountedPrice.greaterThanOrEqual=" + UPDATED_DISCOUNTED_PRICE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice is less than or equal to
        defaultMenuItemFiltering(
            "discountedPrice.lessThanOrEqual=" + DEFAULT_DISCOUNTED_PRICE,
            "discountedPrice.lessThanOrEqual=" + SMALLER_DISCOUNTED_PRICE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice is less than
        defaultMenuItemFiltering(
            "discountedPrice.lessThan=" + UPDATED_DISCOUNTED_PRICE,
            "discountedPrice.lessThan=" + DEFAULT_DISCOUNTED_PRICE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDiscountedPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where discountedPrice is greater than
        defaultMenuItemFiltering(
            "discountedPrice.greaterThan=" + SMALLER_DISCOUNTED_PRICE,
            "discountedPrice.greaterThan=" + DEFAULT_DISCOUNTED_PRICE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes equals to
        defaultMenuItemFiltering(
            "preparationTimeMinutes.equals=" + DEFAULT_PREPARATION_TIME_MINUTES,
            "preparationTimeMinutes.equals=" + UPDATED_PREPARATION_TIME_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes in
        defaultMenuItemFiltering(
            "preparationTimeMinutes.in=" + DEFAULT_PREPARATION_TIME_MINUTES + "," + UPDATED_PREPARATION_TIME_MINUTES,
            "preparationTimeMinutes.in=" + UPDATED_PREPARATION_TIME_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes is not null
        defaultMenuItemFiltering("preparationTimeMinutes.specified=true", "preparationTimeMinutes.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes is greater than or equal to
        defaultMenuItemFiltering(
            "preparationTimeMinutes.greaterThanOrEqual=" + DEFAULT_PREPARATION_TIME_MINUTES,
            "preparationTimeMinutes.greaterThanOrEqual=" + (DEFAULT_PREPARATION_TIME_MINUTES + 1)
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes is less than or equal to
        defaultMenuItemFiltering(
            "preparationTimeMinutes.lessThanOrEqual=" + DEFAULT_PREPARATION_TIME_MINUTES,
            "preparationTimeMinutes.lessThanOrEqual=" + SMALLER_PREPARATION_TIME_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes is less than
        defaultMenuItemFiltering(
            "preparationTimeMinutes.lessThan=" + (DEFAULT_PREPARATION_TIME_MINUTES + 1),
            "preparationTimeMinutes.lessThan=" + DEFAULT_PREPARATION_TIME_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByPreparationTimeMinutesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where preparationTimeMinutes is greater than
        defaultMenuItemFiltering(
            "preparationTimeMinutes.greaterThan=" + SMALLER_PREPARATION_TIME_MINUTES,
            "preparationTimeMinutes.greaterThan=" + DEFAULT_PREPARATION_TIME_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories equals to
        defaultMenuItemFiltering("calories.equals=" + DEFAULT_CALORIES, "calories.equals=" + UPDATED_CALORIES);
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories in
        defaultMenuItemFiltering("calories.in=" + DEFAULT_CALORIES + "," + UPDATED_CALORIES, "calories.in=" + UPDATED_CALORIES);
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories is not null
        defaultMenuItemFiltering("calories.specified=true", "calories.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories is greater than or equal to
        defaultMenuItemFiltering("calories.greaterThanOrEqual=" + DEFAULT_CALORIES, "calories.greaterThanOrEqual=" + UPDATED_CALORIES);
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories is less than or equal to
        defaultMenuItemFiltering("calories.lessThanOrEqual=" + DEFAULT_CALORIES, "calories.lessThanOrEqual=" + SMALLER_CALORIES);
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories is less than
        defaultMenuItemFiltering("calories.lessThan=" + UPDATED_CALORIES, "calories.lessThan=" + DEFAULT_CALORIES);
    }

    @Test
    @Transactional
    void getAllMenuItemsByCaloriesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where calories is greater than
        defaultMenuItemFiltering("calories.greaterThan=" + SMALLER_CALORIES, "calories.greaterThan=" + DEFAULT_CALORIES);
    }

    @Test
    @Transactional
    void getAllMenuItemsByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where imageUrl equals to
        defaultMenuItemFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuItemsByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where imageUrl in
        defaultMenuItemFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuItemsByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where imageUrl is not null
        defaultMenuItemFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where imageUrl contains
        defaultMenuItemFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuItemsByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where imageUrl does not contain
        defaultMenuItemFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsAvailableIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isAvailable equals to
        defaultMenuItemFiltering("isAvailable.equals=" + DEFAULT_IS_AVAILABLE, "isAvailable.equals=" + UPDATED_IS_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsAvailableIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isAvailable in
        defaultMenuItemFiltering(
            "isAvailable.in=" + DEFAULT_IS_AVAILABLE + "," + UPDATED_IS_AVAILABLE,
            "isAvailable.in=" + UPDATED_IS_AVAILABLE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsAvailableIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isAvailable is not null
        defaultMenuItemFiltering("isAvailable.specified=true", "isAvailable.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsFeaturedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isFeatured equals to
        defaultMenuItemFiltering("isFeatured.equals=" + DEFAULT_IS_FEATURED, "isFeatured.equals=" + UPDATED_IS_FEATURED);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsFeaturedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isFeatured in
        defaultMenuItemFiltering(
            "isFeatured.in=" + DEFAULT_IS_FEATURED + "," + UPDATED_IS_FEATURED,
            "isFeatured.in=" + UPDATED_IS_FEATURED
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsFeaturedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isFeatured is not null
        defaultMenuItemFiltering("isFeatured.specified=true", "isFeatured.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsVegetarianIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isVegetarian equals to
        defaultMenuItemFiltering("isVegetarian.equals=" + DEFAULT_IS_VEGETARIAN, "isVegetarian.equals=" + UPDATED_IS_VEGETARIAN);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsVegetarianIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isVegetarian in
        defaultMenuItemFiltering(
            "isVegetarian.in=" + DEFAULT_IS_VEGETARIAN + "," + UPDATED_IS_VEGETARIAN,
            "isVegetarian.in=" + UPDATED_IS_VEGETARIAN
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsVegetarianIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isVegetarian is not null
        defaultMenuItemFiltering("isVegetarian.specified=true", "isVegetarian.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsVeganIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isVegan equals to
        defaultMenuItemFiltering("isVegan.equals=" + DEFAULT_IS_VEGAN, "isVegan.equals=" + UPDATED_IS_VEGAN);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsVeganIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isVegan in
        defaultMenuItemFiltering("isVegan.in=" + DEFAULT_IS_VEGAN + "," + UPDATED_IS_VEGAN, "isVegan.in=" + UPDATED_IS_VEGAN);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsVeganIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isVegan is not null
        defaultMenuItemFiltering("isVegan.specified=true", "isVegan.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsGlutenFreeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isGlutenFree equals to
        defaultMenuItemFiltering("isGlutenFree.equals=" + DEFAULT_IS_GLUTEN_FREE, "isGlutenFree.equals=" + UPDATED_IS_GLUTEN_FREE);
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsGlutenFreeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isGlutenFree in
        defaultMenuItemFiltering(
            "isGlutenFree.in=" + DEFAULT_IS_GLUTEN_FREE + "," + UPDATED_IS_GLUTEN_FREE,
            "isGlutenFree.in=" + UPDATED_IS_GLUTEN_FREE
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByIsGlutenFreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where isGlutenFree is not null
        defaultMenuItemFiltering("isGlutenFree.specified=true", "isGlutenFree.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel equals to
        defaultMenuItemFiltering("spicyLevel.equals=" + DEFAULT_SPICY_LEVEL, "spicyLevel.equals=" + UPDATED_SPICY_LEVEL);
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel in
        defaultMenuItemFiltering(
            "spicyLevel.in=" + DEFAULT_SPICY_LEVEL + "," + UPDATED_SPICY_LEVEL,
            "spicyLevel.in=" + UPDATED_SPICY_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel is not null
        defaultMenuItemFiltering("spicyLevel.specified=true", "spicyLevel.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel is greater than or equal to
        defaultMenuItemFiltering(
            "spicyLevel.greaterThanOrEqual=" + DEFAULT_SPICY_LEVEL,
            "spicyLevel.greaterThanOrEqual=" + (DEFAULT_SPICY_LEVEL + 1)
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel is less than or equal to
        defaultMenuItemFiltering("spicyLevel.lessThanOrEqual=" + DEFAULT_SPICY_LEVEL, "spicyLevel.lessThanOrEqual=" + SMALLER_SPICY_LEVEL);
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel is less than
        defaultMenuItemFiltering("spicyLevel.lessThan=" + (DEFAULT_SPICY_LEVEL + 1), "spicyLevel.lessThan=" + DEFAULT_SPICY_LEVEL);
    }

    @Test
    @Transactional
    void getAllMenuItemsBySpicyLevelIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where spicyLevel is greater than
        defaultMenuItemFiltering("spicyLevel.greaterThan=" + SMALLER_SPICY_LEVEL, "spicyLevel.greaterThan=" + DEFAULT_SPICY_LEVEL);
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder equals to
        defaultMenuItemFiltering("displayOrder.equals=" + DEFAULT_DISPLAY_ORDER, "displayOrder.equals=" + UPDATED_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder in
        defaultMenuItemFiltering(
            "displayOrder.in=" + DEFAULT_DISPLAY_ORDER + "," + UPDATED_DISPLAY_ORDER,
            "displayOrder.in=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder is not null
        defaultMenuItemFiltering("displayOrder.specified=true", "displayOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder is greater than or equal to
        defaultMenuItemFiltering(
            "displayOrder.greaterThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.greaterThanOrEqual=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder is less than or equal to
        defaultMenuItemFiltering(
            "displayOrder.lessThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.lessThanOrEqual=" + SMALLER_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder is less than
        defaultMenuItemFiltering("displayOrder.lessThan=" + UPDATED_DISPLAY_ORDER, "displayOrder.lessThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllMenuItemsByDisplayOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        // Get all the menuItemList where displayOrder is greater than
        defaultMenuItemFiltering("displayOrder.greaterThan=" + SMALLER_DISPLAY_ORDER, "displayOrder.greaterThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllMenuItemsByCategoryIsEqualToSomething() throws Exception {
        MenuCategory category;
        if (TestUtil.findAll(em, MenuCategory.class).isEmpty()) {
            menuItemRepository.saveAndFlush(menuItem);
            category = MenuCategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, MenuCategory.class).get(0);
        }
        em.persist(category);
        em.flush();
        menuItem.setCategory(category);
        menuItemRepository.saveAndFlush(menuItem);
        Long categoryId = category.getId();
        // Get all the menuItemList where category equals to categoryId
        defaultMenuItemShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the menuItemList where category equals to (categoryId + 1)
        defaultMenuItemShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    private void defaultMenuItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMenuItemShouldBeFound(shouldBeFound);
        defaultMenuItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMenuItemShouldBeFound(String filter) throws Exception {
        restMenuItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].discountedPrice").value(hasItem(sameNumber(DEFAULT_DISCOUNTED_PRICE))))
            .andExpect(jsonPath("$.[*].preparationTimeMinutes").value(hasItem(DEFAULT_PREPARATION_TIME_MINUTES)))
            .andExpect(jsonPath("$.[*].calories").value(hasItem(DEFAULT_CALORIES)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isAvailable").value(hasItem(DEFAULT_IS_AVAILABLE)))
            .andExpect(jsonPath("$.[*].isFeatured").value(hasItem(DEFAULT_IS_FEATURED)))
            .andExpect(jsonPath("$.[*].isVegetarian").value(hasItem(DEFAULT_IS_VEGETARIAN)))
            .andExpect(jsonPath("$.[*].isVegan").value(hasItem(DEFAULT_IS_VEGAN)))
            .andExpect(jsonPath("$.[*].isGlutenFree").value(hasItem(DEFAULT_IS_GLUTEN_FREE)))
            .andExpect(jsonPath("$.[*].spicyLevel").value(hasItem(DEFAULT_SPICY_LEVEL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));

        // Check, that the count call also returns 1
        restMenuItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMenuItemShouldNotBeFound(String filter) throws Exception {
        restMenuItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMenuItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMenuItem() throws Exception {
        // Get the menuItem
        restMenuItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMenuItem() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItem
        MenuItem updatedMenuItem = menuItemRepository.findById(menuItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMenuItem are not directly saved in db
        em.detach(updatedMenuItem);
        updatedMenuItem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .discountedPrice(UPDATED_DISCOUNTED_PRICE)
            .preparationTimeMinutes(UPDATED_PREPARATION_TIME_MINUTES)
            .calories(UPDATED_CALORIES)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isFeatured(UPDATED_IS_FEATURED)
            .isVegetarian(UPDATED_IS_VEGETARIAN)
            .isVegan(UPDATED_IS_VEGAN)
            .isGlutenFree(UPDATED_IS_GLUTEN_FREE)
            .spicyLevel(UPDATED_SPICY_LEVEL)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(updatedMenuItem);

        restMenuItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemToMatchAllProperties(updatedMenuItem);
    }

    @Test
    @Transactional
    void putNonExistingMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItem.setId(longCount.incrementAndGet());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItem.setId(longCount.incrementAndGet());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItem.setId(longCount.incrementAndGet());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMenuItemWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItem using partial update
        MenuItem partialUpdatedMenuItem = new MenuItem();
        partialUpdatedMenuItem.setId(menuItem.getId());

        partialUpdatedMenuItem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountedPrice(UPDATED_DISCOUNTED_PRICE)
            .preparationTimeMinutes(UPDATED_PREPARATION_TIME_MINUTES)
            .calories(UPDATED_CALORIES)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isVegan(UPDATED_IS_VEGAN)
            .isGlutenFree(UPDATED_IS_GLUTEN_FREE)
            .spicyLevel(UPDATED_SPICY_LEVEL)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restMenuItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItem))
            )
            .andExpect(status().isOk());

        // Validate the MenuItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMenuItem, menuItem), getPersistedMenuItem(menuItem));
    }

    @Test
    @Transactional
    void fullUpdateMenuItemWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItem using partial update
        MenuItem partialUpdatedMenuItem = new MenuItem();
        partialUpdatedMenuItem.setId(menuItem.getId());

        partialUpdatedMenuItem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .discountedPrice(UPDATED_DISCOUNTED_PRICE)
            .preparationTimeMinutes(UPDATED_PREPARATION_TIME_MINUTES)
            .calories(UPDATED_CALORIES)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isFeatured(UPDATED_IS_FEATURED)
            .isVegetarian(UPDATED_IS_VEGETARIAN)
            .isVegan(UPDATED_IS_VEGAN)
            .isGlutenFree(UPDATED_IS_GLUTEN_FREE)
            .spicyLevel(UPDATED_SPICY_LEVEL)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restMenuItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItem))
            )
            .andExpect(status().isOk());

        // Validate the MenuItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemUpdatableFieldsEquals(partialUpdatedMenuItem, getPersistedMenuItem(partialUpdatedMenuItem));
    }

    @Test
    @Transactional
    void patchNonExistingMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItem.setId(longCount.incrementAndGet());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, menuItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItem.setId(longCount.incrementAndGet());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItem.setId(longCount.incrementAndGet());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(menuItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMenuItem() throws Exception {
        // Initialize the database
        insertedMenuItem = menuItemRepository.saveAndFlush(menuItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuItem
        restMenuItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, menuItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuItemRepository.count();
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

    protected MenuItem getPersistedMenuItem(MenuItem menuItem) {
        return menuItemRepository.findById(menuItem.getId()).orElseThrow();
    }

    protected void assertPersistedMenuItemToMatchAllProperties(MenuItem expectedMenuItem) {
        assertMenuItemAllPropertiesEquals(expectedMenuItem, getPersistedMenuItem(expectedMenuItem));
    }

    protected void assertPersistedMenuItemToMatchUpdatableProperties(MenuItem expectedMenuItem) {
        assertMenuItemAllUpdatablePropertiesEquals(expectedMenuItem, getPersistedMenuItem(expectedMenuItem));
    }
}
