package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.RestaurantOrderAsserts.*;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Promotion;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.domain.enumeration.OrderStatus;
import md.utm.restaurant.repository.RestaurantOrderRepository;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.service.RestaurantOrderService;
import md.utm.restaurant.service.dto.RestaurantOrderDTO;
import md.utm.restaurant.service.mapper.RestaurantOrderMapper;
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
 * Integration tests for the {@link RestaurantOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RestaurantOrderResourceIT {

    private static final String DEFAULT_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_CODE = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DRAFT;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.SUBMITTED;

    private static final Boolean DEFAULT_IS_PRE_ORDER = false;
    private static final Boolean UPDATED_IS_PRE_ORDER = true;

    private static final Instant DEFAULT_SCHEDULED_FOR = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCHEDULED_FOR = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_SUBTOTAL = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TAX_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final String DEFAULT_SPECIAL_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_INSTRUCTIONS = "BBBBBBBBBB";

    private static final Instant DEFAULT_ESTIMATED_READY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ESTIMATED_READY_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/restaurant-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RestaurantOrderRepository restaurantOrderRepositoryMock;

    @Autowired
    private RestaurantOrderMapper restaurantOrderMapper;

    @Mock
    private RestaurantOrderService restaurantOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRestaurantOrderMockMvc;

    private RestaurantOrder restaurantOrder;

    private RestaurantOrder insertedRestaurantOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantOrder createEntity() {
        return new RestaurantOrder()
            .orderCode(DEFAULT_ORDER_CODE)
            .status(DEFAULT_STATUS)
            .isPreOrder(DEFAULT_IS_PRE_ORDER)
            .scheduledFor(DEFAULT_SCHEDULED_FOR)
            .subtotal(DEFAULT_SUBTOTAL)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .specialInstructions(DEFAULT_SPECIAL_INSTRUCTIONS)
            .estimatedReadyTime(DEFAULT_ESTIMATED_READY_TIME)
            .confirmedAt(DEFAULT_CONFIRMED_AT)
            .completedAt(DEFAULT_COMPLETED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantOrder createUpdatedEntity() {
        return new RestaurantOrder()
            .orderCode(UPDATED_ORDER_CODE)
            .status(UPDATED_STATUS)
            .isPreOrder(UPDATED_IS_PRE_ORDER)
            .scheduledFor(UPDATED_SCHEDULED_FOR)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .estimatedReadyTime(UPDATED_ESTIMATED_READY_TIME)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        restaurantOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRestaurantOrder != null) {
            restaurantOrderRepository.delete(insertedRestaurantOrder);
            insertedRestaurantOrder = null;
        }
    }

    @Test
    @Transactional
    void createRestaurantOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);
        var returnedRestaurantOrderDTO = om.readValue(
            restRestaurantOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RestaurantOrderDTO.class
        );

        // Validate the RestaurantOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRestaurantOrder = restaurantOrderMapper.toEntity(returnedRestaurantOrderDTO);
        assertRestaurantOrderUpdatableFieldsEquals(returnedRestaurantOrder, getPersistedRestaurantOrder(returnedRestaurantOrder));

        insertedRestaurantOrder = returnedRestaurantOrder;
    }

    @Test
    @Transactional
    void createRestaurantOrderWithExistingId() throws Exception {
        // Create the RestaurantOrder with an existing ID
        restaurantOrder.setId(1L);
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantOrder.setOrderCode(null);

        // Create the RestaurantOrder, which fails.
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantOrder.setStatus(null);

        // Create the RestaurantOrder, which fails.
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPreOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantOrder.setIsPreOrder(null);

        // Create the RestaurantOrder, which fails.
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantOrder.setSubtotal(null);

        // Create the RestaurantOrder, which fails.
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantOrder.setTotalAmount(null);

        // Create the RestaurantOrder, which fails.
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantOrder.setCreatedAt(null);

        // Create the RestaurantOrder, which fails.
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        restRestaurantOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRestaurantOrders() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList
        restRestaurantOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderCode").value(hasItem(DEFAULT_ORDER_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isPreOrder").value(hasItem(DEFAULT_IS_PRE_ORDER)))
            .andExpect(jsonPath("$.[*].scheduledFor").value(hasItem(DEFAULT_SCHEDULED_FOR.toString())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].specialInstructions").value(hasItem(DEFAULT_SPECIAL_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].estimatedReadyTime").value(hasItem(DEFAULT_ESTIMATED_READY_TIME.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRestaurantOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(restaurantOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRestaurantOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(restaurantOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRestaurantOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(restaurantOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRestaurantOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(restaurantOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRestaurantOrder() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get the restaurantOrder
        restRestaurantOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, restaurantOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(restaurantOrder.getId().intValue()))
            .andExpect(jsonPath("$.orderCode").value(DEFAULT_ORDER_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isPreOrder").value(DEFAULT_IS_PRE_ORDER))
            .andExpect(jsonPath("$.scheduledFor").value(DEFAULT_SCHEDULED_FOR.toString()))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.taxAmount").value(sameNumber(DEFAULT_TAX_AMOUNT)))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.specialInstructions").value(DEFAULT_SPECIAL_INSTRUCTIONS))
            .andExpect(jsonPath("$.estimatedReadyTime").value(DEFAULT_ESTIMATED_READY_TIME.toString()))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getRestaurantOrdersByIdFiltering() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        Long id = restaurantOrder.getId();

        defaultRestaurantOrderFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRestaurantOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRestaurantOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByOrderCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where orderCode equals to
        defaultRestaurantOrderFiltering("orderCode.equals=" + DEFAULT_ORDER_CODE, "orderCode.equals=" + UPDATED_ORDER_CODE);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByOrderCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where orderCode in
        defaultRestaurantOrderFiltering(
            "orderCode.in=" + DEFAULT_ORDER_CODE + "," + UPDATED_ORDER_CODE,
            "orderCode.in=" + UPDATED_ORDER_CODE
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByOrderCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where orderCode is not null
        defaultRestaurantOrderFiltering("orderCode.specified=true", "orderCode.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByOrderCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where orderCode contains
        defaultRestaurantOrderFiltering("orderCode.contains=" + DEFAULT_ORDER_CODE, "orderCode.contains=" + UPDATED_ORDER_CODE);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByOrderCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where orderCode does not contain
        defaultRestaurantOrderFiltering("orderCode.doesNotContain=" + UPDATED_ORDER_CODE, "orderCode.doesNotContain=" + DEFAULT_ORDER_CODE);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where status equals to
        defaultRestaurantOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where status in
        defaultRestaurantOrderFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where status is not null
        defaultRestaurantOrderFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByIsPreOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where isPreOrder equals to
        defaultRestaurantOrderFiltering("isPreOrder.equals=" + DEFAULT_IS_PRE_ORDER, "isPreOrder.equals=" + UPDATED_IS_PRE_ORDER);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByIsPreOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where isPreOrder in
        defaultRestaurantOrderFiltering(
            "isPreOrder.in=" + DEFAULT_IS_PRE_ORDER + "," + UPDATED_IS_PRE_ORDER,
            "isPreOrder.in=" + UPDATED_IS_PRE_ORDER
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByIsPreOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where isPreOrder is not null
        defaultRestaurantOrderFiltering("isPreOrder.specified=true", "isPreOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByScheduledForIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where scheduledFor equals to
        defaultRestaurantOrderFiltering("scheduledFor.equals=" + DEFAULT_SCHEDULED_FOR, "scheduledFor.equals=" + UPDATED_SCHEDULED_FOR);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByScheduledForIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where scheduledFor in
        defaultRestaurantOrderFiltering(
            "scheduledFor.in=" + DEFAULT_SCHEDULED_FOR + "," + UPDATED_SCHEDULED_FOR,
            "scheduledFor.in=" + UPDATED_SCHEDULED_FOR
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByScheduledForIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where scheduledFor is not null
        defaultRestaurantOrderFiltering("scheduledFor.specified=true", "scheduledFor.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal equals to
        defaultRestaurantOrderFiltering("subtotal.equals=" + DEFAULT_SUBTOTAL, "subtotal.equals=" + UPDATED_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal in
        defaultRestaurantOrderFiltering("subtotal.in=" + DEFAULT_SUBTOTAL + "," + UPDATED_SUBTOTAL, "subtotal.in=" + UPDATED_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal is not null
        defaultRestaurantOrderFiltering("subtotal.specified=true", "subtotal.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal is greater than or equal to
        defaultRestaurantOrderFiltering(
            "subtotal.greaterThanOrEqual=" + DEFAULT_SUBTOTAL,
            "subtotal.greaterThanOrEqual=" + UPDATED_SUBTOTAL
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal is less than or equal to
        defaultRestaurantOrderFiltering("subtotal.lessThanOrEqual=" + DEFAULT_SUBTOTAL, "subtotal.lessThanOrEqual=" + SMALLER_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal is less than
        defaultRestaurantOrderFiltering("subtotal.lessThan=" + UPDATED_SUBTOTAL, "subtotal.lessThan=" + DEFAULT_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersBySubtotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where subtotal is greater than
        defaultRestaurantOrderFiltering("subtotal.greaterThan=" + SMALLER_SUBTOTAL, "subtotal.greaterThan=" + DEFAULT_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount equals to
        defaultRestaurantOrderFiltering(
            "discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount in
        defaultRestaurantOrderFiltering(
            "discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount is not null
        defaultRestaurantOrderFiltering("discountAmount.specified=true", "discountAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount is greater than or equal to
        defaultRestaurantOrderFiltering(
            "discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount is less than or equal to
        defaultRestaurantOrderFiltering(
            "discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount is less than
        defaultRestaurantOrderFiltering(
            "discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByDiscountAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where discountAmount is greater than
        defaultRestaurantOrderFiltering(
            "discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT,
            "discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount equals to
        defaultRestaurantOrderFiltering("taxAmount.equals=" + DEFAULT_TAX_AMOUNT, "taxAmount.equals=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount in
        defaultRestaurantOrderFiltering(
            "taxAmount.in=" + DEFAULT_TAX_AMOUNT + "," + UPDATED_TAX_AMOUNT,
            "taxAmount.in=" + UPDATED_TAX_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount is not null
        defaultRestaurantOrderFiltering("taxAmount.specified=true", "taxAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount is greater than or equal to
        defaultRestaurantOrderFiltering(
            "taxAmount.greaterThanOrEqual=" + DEFAULT_TAX_AMOUNT,
            "taxAmount.greaterThanOrEqual=" + UPDATED_TAX_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount is less than or equal to
        defaultRestaurantOrderFiltering(
            "taxAmount.lessThanOrEqual=" + DEFAULT_TAX_AMOUNT,
            "taxAmount.lessThanOrEqual=" + SMALLER_TAX_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount is less than
        defaultRestaurantOrderFiltering("taxAmount.lessThan=" + UPDATED_TAX_AMOUNT, "taxAmount.lessThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTaxAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where taxAmount is greater than
        defaultRestaurantOrderFiltering("taxAmount.greaterThan=" + SMALLER_TAX_AMOUNT, "taxAmount.greaterThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount equals to
        defaultRestaurantOrderFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount in
        defaultRestaurantOrderFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount is not null
        defaultRestaurantOrderFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount is greater than or equal to
        defaultRestaurantOrderFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount is less than or equal to
        defaultRestaurantOrderFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount is less than
        defaultRestaurantOrderFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where totalAmount is greater than
        defaultRestaurantOrderFiltering(
            "totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT,
            "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByEstimatedReadyTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where estimatedReadyTime equals to
        defaultRestaurantOrderFiltering(
            "estimatedReadyTime.equals=" + DEFAULT_ESTIMATED_READY_TIME,
            "estimatedReadyTime.equals=" + UPDATED_ESTIMATED_READY_TIME
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByEstimatedReadyTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where estimatedReadyTime in
        defaultRestaurantOrderFiltering(
            "estimatedReadyTime.in=" + DEFAULT_ESTIMATED_READY_TIME + "," + UPDATED_ESTIMATED_READY_TIME,
            "estimatedReadyTime.in=" + UPDATED_ESTIMATED_READY_TIME
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByEstimatedReadyTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where estimatedReadyTime is not null
        defaultRestaurantOrderFiltering("estimatedReadyTime.specified=true", "estimatedReadyTime.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where confirmedAt equals to
        defaultRestaurantOrderFiltering("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT, "confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where confirmedAt in
        defaultRestaurantOrderFiltering(
            "confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT,
            "confirmedAt.in=" + UPDATED_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where confirmedAt is not null
        defaultRestaurantOrderFiltering("confirmedAt.specified=true", "confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByCompletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where completedAt equals to
        defaultRestaurantOrderFiltering("completedAt.equals=" + DEFAULT_COMPLETED_AT, "completedAt.equals=" + UPDATED_COMPLETED_AT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByCompletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where completedAt in
        defaultRestaurantOrderFiltering(
            "completedAt.in=" + DEFAULT_COMPLETED_AT + "," + UPDATED_COMPLETED_AT,
            "completedAt.in=" + UPDATED_COMPLETED_AT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByCompletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where completedAt is not null
        defaultRestaurantOrderFiltering("completedAt.specified=true", "completedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where createdAt equals to
        defaultRestaurantOrderFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where createdAt in
        defaultRestaurantOrderFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where createdAt is not null
        defaultRestaurantOrderFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where updatedAt equals to
        defaultRestaurantOrderFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where updatedAt in
        defaultRestaurantOrderFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList where updatedAt is not null
        defaultRestaurantOrderFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            restaurantOrderRepository.saveAndFlush(restaurantOrder);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        restaurantOrder.setLocation(location);
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        Long locationId = location.getId();
        // Get all the restaurantOrderList where location equals to locationId
        defaultRestaurantOrderShouldBeFound("locationId.equals=" + locationId);

        // Get all the restaurantOrderList where location equals to (locationId + 1)
        defaultRestaurantOrderShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByClientIsEqualToSomething() throws Exception {
        User client;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            restaurantOrderRepository.saveAndFlush(restaurantOrder);
            client = UserResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(client);
        em.flush();
        restaurantOrder.setClient(client);
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        Long clientId = client.getId();
        // Get all the restaurantOrderList where client equals to clientId
        defaultRestaurantOrderShouldBeFound("clientId.equals=" + clientId);

        // Get all the restaurantOrderList where client equals to (clientId + 1)
        defaultRestaurantOrderShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByAssignedWaiterIsEqualToSomething() throws Exception {
        User assignedWaiter;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            restaurantOrderRepository.saveAndFlush(restaurantOrder);
            assignedWaiter = UserResourceIT.createEntity();
        } else {
            assignedWaiter = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(assignedWaiter);
        em.flush();
        restaurantOrder.setAssignedWaiter(assignedWaiter);
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        Long assignedWaiterId = assignedWaiter.getId();
        // Get all the restaurantOrderList where assignedWaiter equals to assignedWaiterId
        defaultRestaurantOrderShouldBeFound("assignedWaiterId.equals=" + assignedWaiterId);

        // Get all the restaurantOrderList where assignedWaiter equals to (assignedWaiterId + 1)
        defaultRestaurantOrderShouldNotBeFound("assignedWaiterId.equals=" + (assignedWaiterId + 1));
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByTableIsEqualToSomething() throws Exception {
        RestaurantTable table;
        if (TestUtil.findAll(em, RestaurantTable.class).isEmpty()) {
            restaurantOrderRepository.saveAndFlush(restaurantOrder);
            table = RestaurantTableResourceIT.createEntity(em);
        } else {
            table = TestUtil.findAll(em, RestaurantTable.class).get(0);
        }
        em.persist(table);
        em.flush();
        restaurantOrder.setTable(table);
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        Long tableId = table.getId();
        // Get all the restaurantOrderList where table equals to tableId
        defaultRestaurantOrderShouldBeFound("tableId.equals=" + tableId);

        // Get all the restaurantOrderList where table equals to (tableId + 1)
        defaultRestaurantOrderShouldNotBeFound("tableId.equals=" + (tableId + 1));
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByPromotionIsEqualToSomething() throws Exception {
        Promotion promotion;
        if (TestUtil.findAll(em, Promotion.class).isEmpty()) {
            restaurantOrderRepository.saveAndFlush(restaurantOrder);
            promotion = PromotionResourceIT.createEntity(em);
        } else {
            promotion = TestUtil.findAll(em, Promotion.class).get(0);
        }
        em.persist(promotion);
        em.flush();
        restaurantOrder.setPromotion(promotion);
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        Long promotionId = promotion.getId();
        // Get all the restaurantOrderList where promotion equals to promotionId
        defaultRestaurantOrderShouldBeFound("promotionId.equals=" + promotionId);

        // Get all the restaurantOrderList where promotion equals to (promotionId + 1)
        defaultRestaurantOrderShouldNotBeFound("promotionId.equals=" + (promotionId + 1));
    }

    @Test
    @Transactional
    void getAllRestaurantOrdersByReservationIsEqualToSomething() throws Exception {
        Reservation reservation;
        if (TestUtil.findAll(em, Reservation.class).isEmpty()) {
            restaurantOrderRepository.saveAndFlush(restaurantOrder);
            reservation = ReservationResourceIT.createEntity();
        } else {
            reservation = TestUtil.findAll(em, Reservation.class).get(0);
        }
        em.persist(reservation);
        em.flush();
        restaurantOrder.setReservation(reservation);
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        Long reservationId = reservation.getId();
        // Get all the restaurantOrderList where reservation equals to reservationId
        defaultRestaurantOrderShouldBeFound("reservationId.equals=" + reservationId);

        // Get all the restaurantOrderList where reservation equals to (reservationId + 1)
        defaultRestaurantOrderShouldNotBeFound("reservationId.equals=" + (reservationId + 1));
    }

    private void defaultRestaurantOrderFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRestaurantOrderShouldBeFound(shouldBeFound);
        defaultRestaurantOrderShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRestaurantOrderShouldBeFound(String filter) throws Exception {
        restRestaurantOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderCode").value(hasItem(DEFAULT_ORDER_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isPreOrder").value(hasItem(DEFAULT_IS_PRE_ORDER)))
            .andExpect(jsonPath("$.[*].scheduledFor").value(hasItem(DEFAULT_SCHEDULED_FOR.toString())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].specialInstructions").value(hasItem(DEFAULT_SPECIAL_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].estimatedReadyTime").value(hasItem(DEFAULT_ESTIMATED_READY_TIME.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restRestaurantOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRestaurantOrderShouldNotBeFound(String filter) throws Exception {
        restRestaurantOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRestaurantOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRestaurantOrder() throws Exception {
        // Get the restaurantOrder
        restRestaurantOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRestaurantOrder() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurantOrder
        RestaurantOrder updatedRestaurantOrder = restaurantOrderRepository.findById(restaurantOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRestaurantOrder are not directly saved in db
        em.detach(updatedRestaurantOrder);
        updatedRestaurantOrder
            .orderCode(UPDATED_ORDER_CODE)
            .status(UPDATED_STATUS)
            .isPreOrder(UPDATED_IS_PRE_ORDER)
            .scheduledFor(UPDATED_SCHEDULED_FOR)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .estimatedReadyTime(UPDATED_ESTIMATED_READY_TIME)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(updatedRestaurantOrder);

        restRestaurantOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurantOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRestaurantOrderToMatchAllProperties(updatedRestaurantOrder);
    }

    @Test
    @Transactional
    void putNonExistingRestaurantOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantOrder.setId(longCount.incrementAndGet());

        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurantOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRestaurantOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantOrder.setId(longCount.incrementAndGet());

        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurantOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRestaurantOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantOrder.setId(longCount.incrementAndGet());

        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRestaurantOrderWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurantOrder using partial update
        RestaurantOrder partialUpdatedRestaurantOrder = new RestaurantOrder();
        partialUpdatedRestaurantOrder.setId(restaurantOrder.getId());

        partialUpdatedRestaurantOrder
            .orderCode(UPDATED_ORDER_CODE)
            .status(UPDATED_STATUS)
            .isPreOrder(UPDATED_IS_PRE_ORDER)
            .scheduledFor(UPDATED_SCHEDULED_FOR)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .createdAt(UPDATED_CREATED_AT);

        restRestaurantOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurantOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRestaurantOrder))
            )
            .andExpect(status().isOk());

        // Validate the RestaurantOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurantOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRestaurantOrder, restaurantOrder),
            getPersistedRestaurantOrder(restaurantOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateRestaurantOrderWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurantOrder using partial update
        RestaurantOrder partialUpdatedRestaurantOrder = new RestaurantOrder();
        partialUpdatedRestaurantOrder.setId(restaurantOrder.getId());

        partialUpdatedRestaurantOrder
            .orderCode(UPDATED_ORDER_CODE)
            .status(UPDATED_STATUS)
            .isPreOrder(UPDATED_IS_PRE_ORDER)
            .scheduledFor(UPDATED_SCHEDULED_FOR)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .estimatedReadyTime(UPDATED_ESTIMATED_READY_TIME)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restRestaurantOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurantOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRestaurantOrder))
            )
            .andExpect(status().isOk());

        // Validate the RestaurantOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurantOrderUpdatableFieldsEquals(
            partialUpdatedRestaurantOrder,
            getPersistedRestaurantOrder(partialUpdatedRestaurantOrder)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRestaurantOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantOrder.setId(longCount.incrementAndGet());

        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, restaurantOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(restaurantOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRestaurantOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantOrder.setId(longCount.incrementAndGet());

        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(restaurantOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRestaurantOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantOrder.setId(longCount.incrementAndGet());

        // Create the RestaurantOrder
        RestaurantOrderDTO restaurantOrderDTO = restaurantOrderMapper.toDto(restaurantOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(restaurantOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RestaurantOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRestaurantOrder() throws Exception {
        // Initialize the database
        insertedRestaurantOrder = restaurantOrderRepository.saveAndFlush(restaurantOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the restaurantOrder
        restRestaurantOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, restaurantOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return restaurantOrderRepository.count();
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

    protected RestaurantOrder getPersistedRestaurantOrder(RestaurantOrder restaurantOrder) {
        return restaurantOrderRepository.findById(restaurantOrder.getId()).orElseThrow();
    }

    protected void assertPersistedRestaurantOrderToMatchAllProperties(RestaurantOrder expectedRestaurantOrder) {
        assertRestaurantOrderAllPropertiesEquals(expectedRestaurantOrder, getPersistedRestaurantOrder(expectedRestaurantOrder));
    }

    protected void assertPersistedRestaurantOrderToMatchUpdatableProperties(RestaurantOrder expectedRestaurantOrder) {
        assertRestaurantOrderAllUpdatablePropertiesEquals(expectedRestaurantOrder, getPersistedRestaurantOrder(expectedRestaurantOrder));
    }
}
