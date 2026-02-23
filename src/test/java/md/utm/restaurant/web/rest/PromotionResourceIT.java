package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.PromotionAsserts.*;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Promotion;
import md.utm.restaurant.domain.enumeration.DiscountType;
import md.utm.restaurant.repository.PromotionRepository;
import md.utm.restaurant.service.PromotionService;
import md.utm.restaurant.service.dto.PromotionDTO;
import md.utm.restaurant.service.mapper.PromotionMapper;
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
 * Integration tests for the {@link PromotionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PromotionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final DiscountType DEFAULT_DISCOUNT_TYPE = DiscountType.PERCENTAGE;
    private static final DiscountType UPDATED_DISCOUNT_TYPE = DiscountType.FIXED_AMOUNT;

    private static final BigDecimal DEFAULT_DISCOUNT_VALUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_VALUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MINIMUM_ORDER_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MINIMUM_ORDER_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MINIMUM_ORDER_AMOUNT = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_MAX_USAGE_COUNT = 1;
    private static final Integer UPDATED_MAX_USAGE_COUNT = 2;
    private static final Integer SMALLER_MAX_USAGE_COUNT = 1 - 1;

    private static final Integer DEFAULT_CURRENT_USAGE_COUNT = 0;
    private static final Integer UPDATED_CURRENT_USAGE_COUNT = 1;
    private static final Integer SMALLER_CURRENT_USAGE_COUNT = 0 - 1;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/promotions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionRepository promotionRepositoryMock;

    @Autowired
    private PromotionMapper promotionMapper;

    @Mock
    private PromotionService promotionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPromotionMockMvc;

    private Promotion promotion;

    private Promotion insertedPromotion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createEntity(EntityManager em) {
        Promotion promotion = new Promotion()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .discountType(DEFAULT_DISCOUNT_TYPE)
            .discountValue(DEFAULT_DISCOUNT_VALUE)
            .minimumOrderAmount(DEFAULT_MINIMUM_ORDER_AMOUNT)
            .maxUsageCount(DEFAULT_MAX_USAGE_COUNT)
            .currentUsageCount(DEFAULT_CURRENT_USAGE_COUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
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
        promotion.setBrand(brand);
        return promotion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createUpdatedEntity(EntityManager em) {
        Promotion updatedPromotion = new Promotion()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minimumOrderAmount(UPDATED_MINIMUM_ORDER_AMOUNT)
            .maxUsageCount(UPDATED_MAX_USAGE_COUNT)
            .currentUsageCount(UPDATED_CURRENT_USAGE_COUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
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
        updatedPromotion.setBrand(brand);
        return updatedPromotion;
    }

    @BeforeEach
    void initTest() {
        promotion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPromotion != null) {
            promotionRepository.delete(insertedPromotion);
            insertedPromotion = null;
        }
    }

    @Test
    @Transactional
    void createPromotion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);
        var returnedPromotionDTO = om.readValue(
            restPromotionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PromotionDTO.class
        );

        // Validate the Promotion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPromotion = promotionMapper.toEntity(returnedPromotionDTO);
        assertPromotionUpdatableFieldsEquals(returnedPromotion, getPersistedPromotion(returnedPromotion));

        insertedPromotion = returnedPromotion;
    }

    @Test
    @Transactional
    void createPromotionWithExistingId() throws Exception {
        // Create the Promotion with an existing ID
        promotion.setId(1L);
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setCode(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setName(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setDiscountType(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setDiscountValue(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setStartDate(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setEndDate(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setIsActive(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPromotions() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].minimumOrderAmount").value(hasItem(sameNumber(DEFAULT_MINIMUM_ORDER_AMOUNT))))
            .andExpect(jsonPath("$.[*].maxUsageCount").value(hasItem(DEFAULT_MAX_USAGE_COUNT)))
            .andExpect(jsonPath("$.[*].currentUsageCount").value(hasItem(DEFAULT_CURRENT_USAGE_COUNT)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPromotionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(promotionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPromotionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(promotionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPromotionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(promotionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPromotionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(promotionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get the promotion
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL_ID, promotion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(promotion.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.discountType").value(DEFAULT_DISCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.discountValue").value(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .andExpect(jsonPath("$.minimumOrderAmount").value(sameNumber(DEFAULT_MINIMUM_ORDER_AMOUNT)))
            .andExpect(jsonPath("$.maxUsageCount").value(DEFAULT_MAX_USAGE_COUNT))
            .andExpect(jsonPath("$.currentUsageCount").value(DEFAULT_CURRENT_USAGE_COUNT))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getPromotionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        Long id = promotion.getId();

        defaultPromotionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPromotionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPromotionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code equals to
        defaultPromotionFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code in
        defaultPromotionFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code is not null
        defaultPromotionFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code contains
        defaultPromotionFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code does not contain
        defaultPromotionFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name equals to
        defaultPromotionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name in
        defaultPromotionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name is not null
        defaultPromotionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name contains
        defaultPromotionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name does not contain
        defaultPromotionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountType equals to
        defaultPromotionFiltering("discountType.equals=" + DEFAULT_DISCOUNT_TYPE, "discountType.equals=" + UPDATED_DISCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountType in
        defaultPromotionFiltering(
            "discountType.in=" + DEFAULT_DISCOUNT_TYPE + "," + UPDATED_DISCOUNT_TYPE,
            "discountType.in=" + UPDATED_DISCOUNT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountType is not null
        defaultPromotionFiltering("discountType.specified=true", "discountType.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue equals to
        defaultPromotionFiltering("discountValue.equals=" + DEFAULT_DISCOUNT_VALUE, "discountValue.equals=" + UPDATED_DISCOUNT_VALUE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue in
        defaultPromotionFiltering(
            "discountValue.in=" + DEFAULT_DISCOUNT_VALUE + "," + UPDATED_DISCOUNT_VALUE,
            "discountValue.in=" + UPDATED_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is not null
        defaultPromotionFiltering("discountValue.specified=true", "discountValue.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is greater than or equal to
        defaultPromotionFiltering(
            "discountValue.greaterThanOrEqual=" + DEFAULT_DISCOUNT_VALUE,
            "discountValue.greaterThanOrEqual=" + UPDATED_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is less than or equal to
        defaultPromotionFiltering(
            "discountValue.lessThanOrEqual=" + DEFAULT_DISCOUNT_VALUE,
            "discountValue.lessThanOrEqual=" + SMALLER_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is less than
        defaultPromotionFiltering("discountValue.lessThan=" + UPDATED_DISCOUNT_VALUE, "discountValue.lessThan=" + DEFAULT_DISCOUNT_VALUE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is greater than
        defaultPromotionFiltering(
            "discountValue.greaterThan=" + SMALLER_DISCOUNT_VALUE,
            "discountValue.greaterThan=" + DEFAULT_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount equals to
        defaultPromotionFiltering(
            "minimumOrderAmount.equals=" + DEFAULT_MINIMUM_ORDER_AMOUNT,
            "minimumOrderAmount.equals=" + UPDATED_MINIMUM_ORDER_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount in
        defaultPromotionFiltering(
            "minimumOrderAmount.in=" + DEFAULT_MINIMUM_ORDER_AMOUNT + "," + UPDATED_MINIMUM_ORDER_AMOUNT,
            "minimumOrderAmount.in=" + UPDATED_MINIMUM_ORDER_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount is not null
        defaultPromotionFiltering("minimumOrderAmount.specified=true", "minimumOrderAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount is greater than or equal to
        defaultPromotionFiltering(
            "minimumOrderAmount.greaterThanOrEqual=" + DEFAULT_MINIMUM_ORDER_AMOUNT,
            "minimumOrderAmount.greaterThanOrEqual=" + UPDATED_MINIMUM_ORDER_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount is less than or equal to
        defaultPromotionFiltering(
            "minimumOrderAmount.lessThanOrEqual=" + DEFAULT_MINIMUM_ORDER_AMOUNT,
            "minimumOrderAmount.lessThanOrEqual=" + SMALLER_MINIMUM_ORDER_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount is less than
        defaultPromotionFiltering(
            "minimumOrderAmount.lessThan=" + UPDATED_MINIMUM_ORDER_AMOUNT,
            "minimumOrderAmount.lessThan=" + DEFAULT_MINIMUM_ORDER_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMinimumOrderAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where minimumOrderAmount is greater than
        defaultPromotionFiltering(
            "minimumOrderAmount.greaterThan=" + SMALLER_MINIMUM_ORDER_AMOUNT,
            "minimumOrderAmount.greaterThan=" + DEFAULT_MINIMUM_ORDER_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount equals to
        defaultPromotionFiltering("maxUsageCount.equals=" + DEFAULT_MAX_USAGE_COUNT, "maxUsageCount.equals=" + UPDATED_MAX_USAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount in
        defaultPromotionFiltering(
            "maxUsageCount.in=" + DEFAULT_MAX_USAGE_COUNT + "," + UPDATED_MAX_USAGE_COUNT,
            "maxUsageCount.in=" + UPDATED_MAX_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount is not null
        defaultPromotionFiltering("maxUsageCount.specified=true", "maxUsageCount.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount is greater than or equal to
        defaultPromotionFiltering(
            "maxUsageCount.greaterThanOrEqual=" + DEFAULT_MAX_USAGE_COUNT,
            "maxUsageCount.greaterThanOrEqual=" + UPDATED_MAX_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount is less than or equal to
        defaultPromotionFiltering(
            "maxUsageCount.lessThanOrEqual=" + DEFAULT_MAX_USAGE_COUNT,
            "maxUsageCount.lessThanOrEqual=" + SMALLER_MAX_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount is less than
        defaultPromotionFiltering("maxUsageCount.lessThan=" + UPDATED_MAX_USAGE_COUNT, "maxUsageCount.lessThan=" + DEFAULT_MAX_USAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsageCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUsageCount is greater than
        defaultPromotionFiltering(
            "maxUsageCount.greaterThan=" + SMALLER_MAX_USAGE_COUNT,
            "maxUsageCount.greaterThan=" + DEFAULT_MAX_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount equals to
        defaultPromotionFiltering(
            "currentUsageCount.equals=" + DEFAULT_CURRENT_USAGE_COUNT,
            "currentUsageCount.equals=" + UPDATED_CURRENT_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount in
        defaultPromotionFiltering(
            "currentUsageCount.in=" + DEFAULT_CURRENT_USAGE_COUNT + "," + UPDATED_CURRENT_USAGE_COUNT,
            "currentUsageCount.in=" + UPDATED_CURRENT_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount is not null
        defaultPromotionFiltering("currentUsageCount.specified=true", "currentUsageCount.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount is greater than or equal to
        defaultPromotionFiltering(
            "currentUsageCount.greaterThanOrEqual=" + DEFAULT_CURRENT_USAGE_COUNT,
            "currentUsageCount.greaterThanOrEqual=" + UPDATED_CURRENT_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount is less than or equal to
        defaultPromotionFiltering(
            "currentUsageCount.lessThanOrEqual=" + DEFAULT_CURRENT_USAGE_COUNT,
            "currentUsageCount.lessThanOrEqual=" + SMALLER_CURRENT_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount is less than
        defaultPromotionFiltering(
            "currentUsageCount.lessThan=" + UPDATED_CURRENT_USAGE_COUNT,
            "currentUsageCount.lessThan=" + DEFAULT_CURRENT_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByCurrentUsageCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where currentUsageCount is greater than
        defaultPromotionFiltering(
            "currentUsageCount.greaterThan=" + SMALLER_CURRENT_USAGE_COUNT,
            "currentUsageCount.greaterThan=" + DEFAULT_CURRENT_USAGE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate equals to
        defaultPromotionFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate in
        defaultPromotionFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate is not null
        defaultPromotionFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate is greater than or equal to
        defaultPromotionFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate is less than or equal to
        defaultPromotionFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate is less than
        defaultPromotionFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate is greater than
        defaultPromotionFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate equals to
        defaultPromotionFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate in
        defaultPromotionFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate is not null
        defaultPromotionFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate is greater than or equal to
        defaultPromotionFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate is less than or equal to
        defaultPromotionFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate is less than
        defaultPromotionFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate is greater than
        defaultPromotionFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where isActive equals to
        defaultPromotionFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPromotionsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where isActive in
        defaultPromotionFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPromotionsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where isActive is not null
        defaultPromotionFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByBrandIsEqualToSomething() throws Exception {
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            promotionRepository.saveAndFlush(promotion);
            brand = BrandResourceIT.createEntity();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        em.persist(brand);
        em.flush();
        promotion.setBrand(brand);
        promotionRepository.saveAndFlush(promotion);
        Long brandId = brand.getId();
        // Get all the promotionList where brand equals to brandId
        defaultPromotionShouldBeFound("brandId.equals=" + brandId);

        // Get all the promotionList where brand equals to (brandId + 1)
        defaultPromotionShouldNotBeFound("brandId.equals=" + (brandId + 1));
    }

    @Test
    @Transactional
    void getAllPromotionsByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            promotionRepository.saveAndFlush(promotion);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        promotion.setLocation(location);
        promotionRepository.saveAndFlush(promotion);
        Long locationId = location.getId();
        // Get all the promotionList where location equals to locationId
        defaultPromotionShouldBeFound("locationId.equals=" + locationId);

        // Get all the promotionList where location equals to (locationId + 1)
        defaultPromotionShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultPromotionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPromotionShouldBeFound(shouldBeFound);
        defaultPromotionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPromotionShouldBeFound(String filter) throws Exception {
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].minimumOrderAmount").value(hasItem(sameNumber(DEFAULT_MINIMUM_ORDER_AMOUNT))))
            .andExpect(jsonPath("$.[*].maxUsageCount").value(hasItem(DEFAULT_MAX_USAGE_COUNT)))
            .andExpect(jsonPath("$.[*].currentUsageCount").value(hasItem(DEFAULT_CURRENT_USAGE_COUNT)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPromotionShouldNotBeFound(String filter) throws Exception {
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPromotion() throws Exception {
        // Get the promotion
        restPromotionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion
        Promotion updatedPromotion = promotionRepository.findById(promotion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPromotion are not directly saved in db
        em.detach(updatedPromotion);
        updatedPromotion
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minimumOrderAmount(UPDATED_MINIMUM_ORDER_AMOUNT)
            .maxUsageCount(UPDATED_MAX_USAGE_COUNT)
            .currentUsageCount(UPDATED_CURRENT_USAGE_COUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isActive(UPDATED_IS_ACTIVE);
        PromotionDTO promotionDTO = promotionMapper.toDto(updatedPromotion);

        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPromotionToMatchAllProperties(updatedPromotion);
    }

    @Test
    @Transactional
    void putNonExistingPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion
            .discountType(UPDATED_DISCOUNT_TYPE)
            .minimumOrderAmount(UPDATED_MINIMUM_ORDER_AMOUNT)
            .maxUsageCount(UPDATED_MAX_USAGE_COUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPromotion))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPromotionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPromotion, promotion),
            getPersistedPromotion(promotion)
        );
    }

    @Test
    @Transactional
    void fullUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minimumOrderAmount(UPDATED_MINIMUM_ORDER_AMOUNT)
            .maxUsageCount(UPDATED_MAX_USAGE_COUNT)
            .currentUsageCount(UPDATED_CURRENT_USAGE_COUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isActive(UPDATED_IS_ACTIVE);

        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPromotion))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPromotionUpdatableFieldsEquals(partialUpdatedPromotion, getPersistedPromotion(partialUpdatedPromotion));
    }

    @Test
    @Transactional
    void patchNonExistingPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the promotion
        restPromotionMockMvc
            .perform(delete(ENTITY_API_URL_ID, promotion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return promotionRepository.count();
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

    protected Promotion getPersistedPromotion(Promotion promotion) {
        return promotionRepository.findById(promotion.getId()).orElseThrow();
    }

    protected void assertPersistedPromotionToMatchAllProperties(Promotion expectedPromotion) {
        assertPromotionAllPropertiesEquals(expectedPromotion, getPersistedPromotion(expectedPromotion));
    }

    protected void assertPersistedPromotionToMatchUpdatableProperties(Promotion expectedPromotion) {
        assertPromotionAllUpdatablePropertiesEquals(expectedPromotion, getPersistedPromotion(expectedPromotion));
    }
}
