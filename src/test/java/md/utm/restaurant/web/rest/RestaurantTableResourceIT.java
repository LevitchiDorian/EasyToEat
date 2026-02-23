package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.RestaurantTableAsserts.*;
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
import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.domain.enumeration.TableShape;
import md.utm.restaurant.domain.enumeration.TableStatus;
import md.utm.restaurant.repository.RestaurantTableRepository;
import md.utm.restaurant.service.RestaurantTableService;
import md.utm.restaurant.service.dto.RestaurantTableDTO;
import md.utm.restaurant.service.mapper.RestaurantTableMapper;
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
 * Integration tests for the {@link RestaurantTableResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RestaurantTableResourceIT {

    private static final String DEFAULT_TABLE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TABLE_NUMBER = "BBBBBBBBBB";

    private static final TableShape DEFAULT_SHAPE = TableShape.ROUND;
    private static final TableShape UPDATED_SHAPE = TableShape.SQUARE;

    private static final Integer DEFAULT_MIN_CAPACITY = 1;
    private static final Integer UPDATED_MIN_CAPACITY = 2;
    private static final Integer SMALLER_MIN_CAPACITY = 1 - 1;

    private static final Integer DEFAULT_MAX_CAPACITY = 1;
    private static final Integer UPDATED_MAX_CAPACITY = 2;
    private static final Integer SMALLER_MAX_CAPACITY = 1 - 1;

    private static final Double DEFAULT_POSITION_X = 1D;
    private static final Double UPDATED_POSITION_X = 2D;
    private static final Double SMALLER_POSITION_X = 1D - 1D;

    private static final Double DEFAULT_POSITION_Y = 1D;
    private static final Double UPDATED_POSITION_Y = 2D;
    private static final Double SMALLER_POSITION_Y = 1D - 1D;

    private static final Double DEFAULT_WIDTH_PX = 1D;
    private static final Double UPDATED_WIDTH_PX = 2D;
    private static final Double SMALLER_WIDTH_PX = 1D - 1D;

    private static final Double DEFAULT_HEIGHT_PX = 1D;
    private static final Double UPDATED_HEIGHT_PX = 2D;
    private static final Double SMALLER_HEIGHT_PX = 1D - 1D;

    private static final Double DEFAULT_ROTATION = 1D;
    private static final Double UPDATED_ROTATION = 2D;
    private static final Double SMALLER_ROTATION = 1D - 1D;

    private static final TableStatus DEFAULT_STATUS = TableStatus.AVAILABLE;
    private static final TableStatus UPDATED_STATUS = TableStatus.RESERVED;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/restaurant-tables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private RestaurantTableRepository restaurantTableRepositoryMock;

    @Autowired
    private RestaurantTableMapper restaurantTableMapper;

    @Mock
    private RestaurantTableService restaurantTableServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRestaurantTableMockMvc;

    private RestaurantTable restaurantTable;

    private RestaurantTable insertedRestaurantTable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantTable createEntity(EntityManager em) {
        RestaurantTable restaurantTable = new RestaurantTable()
            .tableNumber(DEFAULT_TABLE_NUMBER)
            .shape(DEFAULT_SHAPE)
            .minCapacity(DEFAULT_MIN_CAPACITY)
            .maxCapacity(DEFAULT_MAX_CAPACITY)
            .positionX(DEFAULT_POSITION_X)
            .positionY(DEFAULT_POSITION_Y)
            .widthPx(DEFAULT_WIDTH_PX)
            .heightPx(DEFAULT_HEIGHT_PX)
            .rotation(DEFAULT_ROTATION)
            .status(DEFAULT_STATUS)
            .isActive(DEFAULT_IS_ACTIVE)
            .notes(DEFAULT_NOTES);
        // Add required entity
        DiningRoom diningRoom;
        if (TestUtil.findAll(em, DiningRoom.class).isEmpty()) {
            diningRoom = DiningRoomResourceIT.createEntity(em);
            em.persist(diningRoom);
            em.flush();
        } else {
            diningRoom = TestUtil.findAll(em, DiningRoom.class).get(0);
        }
        restaurantTable.setRoom(diningRoom);
        return restaurantTable;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantTable createUpdatedEntity(EntityManager em) {
        RestaurantTable updatedRestaurantTable = new RestaurantTable()
            .tableNumber(UPDATED_TABLE_NUMBER)
            .shape(UPDATED_SHAPE)
            .minCapacity(UPDATED_MIN_CAPACITY)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX)
            .rotation(UPDATED_ROTATION)
            .status(UPDATED_STATUS)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);
        // Add required entity
        DiningRoom diningRoom;
        if (TestUtil.findAll(em, DiningRoom.class).isEmpty()) {
            diningRoom = DiningRoomResourceIT.createUpdatedEntity(em);
            em.persist(diningRoom);
            em.flush();
        } else {
            diningRoom = TestUtil.findAll(em, DiningRoom.class).get(0);
        }
        updatedRestaurantTable.setRoom(diningRoom);
        return updatedRestaurantTable;
    }

    @BeforeEach
    void initTest() {
        restaurantTable = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRestaurantTable != null) {
            restaurantTableRepository.delete(insertedRestaurantTable);
            insertedRestaurantTable = null;
        }
    }

    @Test
    @Transactional
    void createRestaurantTable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);
        var returnedRestaurantTableDTO = om.readValue(
            restRestaurantTableMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RestaurantTableDTO.class
        );

        // Validate the RestaurantTable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRestaurantTable = restaurantTableMapper.toEntity(returnedRestaurantTableDTO);
        assertRestaurantTableUpdatableFieldsEquals(returnedRestaurantTable, getPersistedRestaurantTable(returnedRestaurantTable));

        insertedRestaurantTable = returnedRestaurantTable;
    }

    @Test
    @Transactional
    void createRestaurantTableWithExistingId() throws Exception {
        // Create the RestaurantTable with an existing ID
        restaurantTable.setId(1L);
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTableNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantTable.setTableNumber(null);

        // Create the RestaurantTable, which fails.
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShapeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantTable.setShape(null);

        // Create the RestaurantTable, which fails.
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantTable.setMinCapacity(null);

        // Create the RestaurantTable, which fails.
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantTable.setMaxCapacity(null);

        // Create the RestaurantTable, which fails.
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantTable.setStatus(null);

        // Create the RestaurantTable, which fails.
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurantTable.setIsActive(null);

        // Create the RestaurantTable, which fails.
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        restRestaurantTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRestaurantTables() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList
        restRestaurantTableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantTable.getId().intValue())))
            .andExpect(jsonPath("$.[*].tableNumber").value(hasItem(DEFAULT_TABLE_NUMBER)))
            .andExpect(jsonPath("$.[*].shape").value(hasItem(DEFAULT_SHAPE.toString())))
            .andExpect(jsonPath("$.[*].minCapacity").value(hasItem(DEFAULT_MIN_CAPACITY)))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].positionX").value(hasItem(DEFAULT_POSITION_X)))
            .andExpect(jsonPath("$.[*].positionY").value(hasItem(DEFAULT_POSITION_Y)))
            .andExpect(jsonPath("$.[*].widthPx").value(hasItem(DEFAULT_WIDTH_PX)))
            .andExpect(jsonPath("$.[*].heightPx").value(hasItem(DEFAULT_HEIGHT_PX)))
            .andExpect(jsonPath("$.[*].rotation").value(hasItem(DEFAULT_ROTATION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRestaurantTablesWithEagerRelationshipsIsEnabled() throws Exception {
        when(restaurantTableServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRestaurantTableMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(restaurantTableServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRestaurantTablesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(restaurantTableServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRestaurantTableMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(restaurantTableRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRestaurantTable() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get the restaurantTable
        restRestaurantTableMockMvc
            .perform(get(ENTITY_API_URL_ID, restaurantTable.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(restaurantTable.getId().intValue()))
            .andExpect(jsonPath("$.tableNumber").value(DEFAULT_TABLE_NUMBER))
            .andExpect(jsonPath("$.shape").value(DEFAULT_SHAPE.toString()))
            .andExpect(jsonPath("$.minCapacity").value(DEFAULT_MIN_CAPACITY))
            .andExpect(jsonPath("$.maxCapacity").value(DEFAULT_MAX_CAPACITY))
            .andExpect(jsonPath("$.positionX").value(DEFAULT_POSITION_X))
            .andExpect(jsonPath("$.positionY").value(DEFAULT_POSITION_Y))
            .andExpect(jsonPath("$.widthPx").value(DEFAULT_WIDTH_PX))
            .andExpect(jsonPath("$.heightPx").value(DEFAULT_HEIGHT_PX))
            .andExpect(jsonPath("$.rotation").value(DEFAULT_ROTATION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getRestaurantTablesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        Long id = restaurantTable.getId();

        defaultRestaurantTableFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRestaurantTableFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRestaurantTableFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByTableNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where tableNumber equals to
        defaultRestaurantTableFiltering("tableNumber.equals=" + DEFAULT_TABLE_NUMBER, "tableNumber.equals=" + UPDATED_TABLE_NUMBER);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByTableNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where tableNumber in
        defaultRestaurantTableFiltering(
            "tableNumber.in=" + DEFAULT_TABLE_NUMBER + "," + UPDATED_TABLE_NUMBER,
            "tableNumber.in=" + UPDATED_TABLE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByTableNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where tableNumber is not null
        defaultRestaurantTableFiltering("tableNumber.specified=true", "tableNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByTableNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where tableNumber contains
        defaultRestaurantTableFiltering("tableNumber.contains=" + DEFAULT_TABLE_NUMBER, "tableNumber.contains=" + UPDATED_TABLE_NUMBER);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByTableNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where tableNumber does not contain
        defaultRestaurantTableFiltering(
            "tableNumber.doesNotContain=" + UPDATED_TABLE_NUMBER,
            "tableNumber.doesNotContain=" + DEFAULT_TABLE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByShapeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where shape equals to
        defaultRestaurantTableFiltering("shape.equals=" + DEFAULT_SHAPE, "shape.equals=" + UPDATED_SHAPE);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByShapeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where shape in
        defaultRestaurantTableFiltering("shape.in=" + DEFAULT_SHAPE + "," + UPDATED_SHAPE, "shape.in=" + UPDATED_SHAPE);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByShapeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where shape is not null
        defaultRestaurantTableFiltering("shape.specified=true", "shape.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity equals to
        defaultRestaurantTableFiltering("minCapacity.equals=" + DEFAULT_MIN_CAPACITY, "minCapacity.equals=" + UPDATED_MIN_CAPACITY);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity in
        defaultRestaurantTableFiltering(
            "minCapacity.in=" + DEFAULT_MIN_CAPACITY + "," + UPDATED_MIN_CAPACITY,
            "minCapacity.in=" + UPDATED_MIN_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity is not null
        defaultRestaurantTableFiltering("minCapacity.specified=true", "minCapacity.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity is greater than or equal to
        defaultRestaurantTableFiltering(
            "minCapacity.greaterThanOrEqual=" + DEFAULT_MIN_CAPACITY,
            "minCapacity.greaterThanOrEqual=" + UPDATED_MIN_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity is less than or equal to
        defaultRestaurantTableFiltering(
            "minCapacity.lessThanOrEqual=" + DEFAULT_MIN_CAPACITY,
            "minCapacity.lessThanOrEqual=" + SMALLER_MIN_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity is less than
        defaultRestaurantTableFiltering("minCapacity.lessThan=" + UPDATED_MIN_CAPACITY, "minCapacity.lessThan=" + DEFAULT_MIN_CAPACITY);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMinCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where minCapacity is greater than
        defaultRestaurantTableFiltering(
            "minCapacity.greaterThan=" + SMALLER_MIN_CAPACITY,
            "minCapacity.greaterThan=" + DEFAULT_MIN_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity equals to
        defaultRestaurantTableFiltering("maxCapacity.equals=" + DEFAULT_MAX_CAPACITY, "maxCapacity.equals=" + UPDATED_MAX_CAPACITY);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity in
        defaultRestaurantTableFiltering(
            "maxCapacity.in=" + DEFAULT_MAX_CAPACITY + "," + UPDATED_MAX_CAPACITY,
            "maxCapacity.in=" + UPDATED_MAX_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity is not null
        defaultRestaurantTableFiltering("maxCapacity.specified=true", "maxCapacity.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity is greater than or equal to
        defaultRestaurantTableFiltering(
            "maxCapacity.greaterThanOrEqual=" + DEFAULT_MAX_CAPACITY,
            "maxCapacity.greaterThanOrEqual=" + UPDATED_MAX_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity is less than or equal to
        defaultRestaurantTableFiltering(
            "maxCapacity.lessThanOrEqual=" + DEFAULT_MAX_CAPACITY,
            "maxCapacity.lessThanOrEqual=" + SMALLER_MAX_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity is less than
        defaultRestaurantTableFiltering("maxCapacity.lessThan=" + UPDATED_MAX_CAPACITY, "maxCapacity.lessThan=" + DEFAULT_MAX_CAPACITY);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByMaxCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where maxCapacity is greater than
        defaultRestaurantTableFiltering(
            "maxCapacity.greaterThan=" + SMALLER_MAX_CAPACITY,
            "maxCapacity.greaterThan=" + DEFAULT_MAX_CAPACITY
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX equals to
        defaultRestaurantTableFiltering("positionX.equals=" + DEFAULT_POSITION_X, "positionX.equals=" + UPDATED_POSITION_X);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX in
        defaultRestaurantTableFiltering(
            "positionX.in=" + DEFAULT_POSITION_X + "," + UPDATED_POSITION_X,
            "positionX.in=" + UPDATED_POSITION_X
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX is not null
        defaultRestaurantTableFiltering("positionX.specified=true", "positionX.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX is greater than or equal to
        defaultRestaurantTableFiltering(
            "positionX.greaterThanOrEqual=" + DEFAULT_POSITION_X,
            "positionX.greaterThanOrEqual=" + UPDATED_POSITION_X
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX is less than or equal to
        defaultRestaurantTableFiltering(
            "positionX.lessThanOrEqual=" + DEFAULT_POSITION_X,
            "positionX.lessThanOrEqual=" + SMALLER_POSITION_X
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX is less than
        defaultRestaurantTableFiltering("positionX.lessThan=" + UPDATED_POSITION_X, "positionX.lessThan=" + DEFAULT_POSITION_X);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionXIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionX is greater than
        defaultRestaurantTableFiltering("positionX.greaterThan=" + SMALLER_POSITION_X, "positionX.greaterThan=" + DEFAULT_POSITION_X);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY equals to
        defaultRestaurantTableFiltering("positionY.equals=" + DEFAULT_POSITION_Y, "positionY.equals=" + UPDATED_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY in
        defaultRestaurantTableFiltering(
            "positionY.in=" + DEFAULT_POSITION_Y + "," + UPDATED_POSITION_Y,
            "positionY.in=" + UPDATED_POSITION_Y
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY is not null
        defaultRestaurantTableFiltering("positionY.specified=true", "positionY.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY is greater than or equal to
        defaultRestaurantTableFiltering(
            "positionY.greaterThanOrEqual=" + DEFAULT_POSITION_Y,
            "positionY.greaterThanOrEqual=" + UPDATED_POSITION_Y
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY is less than or equal to
        defaultRestaurantTableFiltering(
            "positionY.lessThanOrEqual=" + DEFAULT_POSITION_Y,
            "positionY.lessThanOrEqual=" + SMALLER_POSITION_Y
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY is less than
        defaultRestaurantTableFiltering("positionY.lessThan=" + UPDATED_POSITION_Y, "positionY.lessThan=" + DEFAULT_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByPositionYIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where positionY is greater than
        defaultRestaurantTableFiltering("positionY.greaterThan=" + SMALLER_POSITION_Y, "positionY.greaterThan=" + DEFAULT_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx equals to
        defaultRestaurantTableFiltering("widthPx.equals=" + DEFAULT_WIDTH_PX, "widthPx.equals=" + UPDATED_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx in
        defaultRestaurantTableFiltering("widthPx.in=" + DEFAULT_WIDTH_PX + "," + UPDATED_WIDTH_PX, "widthPx.in=" + UPDATED_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx is not null
        defaultRestaurantTableFiltering("widthPx.specified=true", "widthPx.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx is greater than or equal to
        defaultRestaurantTableFiltering("widthPx.greaterThanOrEqual=" + DEFAULT_WIDTH_PX, "widthPx.greaterThanOrEqual=" + UPDATED_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx is less than or equal to
        defaultRestaurantTableFiltering("widthPx.lessThanOrEqual=" + DEFAULT_WIDTH_PX, "widthPx.lessThanOrEqual=" + SMALLER_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx is less than
        defaultRestaurantTableFiltering("widthPx.lessThan=" + UPDATED_WIDTH_PX, "widthPx.lessThan=" + DEFAULT_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByWidthPxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where widthPx is greater than
        defaultRestaurantTableFiltering("widthPx.greaterThan=" + SMALLER_WIDTH_PX, "widthPx.greaterThan=" + DEFAULT_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx equals to
        defaultRestaurantTableFiltering("heightPx.equals=" + DEFAULT_HEIGHT_PX, "heightPx.equals=" + UPDATED_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx in
        defaultRestaurantTableFiltering("heightPx.in=" + DEFAULT_HEIGHT_PX + "," + UPDATED_HEIGHT_PX, "heightPx.in=" + UPDATED_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx is not null
        defaultRestaurantTableFiltering("heightPx.specified=true", "heightPx.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx is greater than or equal to
        defaultRestaurantTableFiltering(
            "heightPx.greaterThanOrEqual=" + DEFAULT_HEIGHT_PX,
            "heightPx.greaterThanOrEqual=" + UPDATED_HEIGHT_PX
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx is less than or equal to
        defaultRestaurantTableFiltering("heightPx.lessThanOrEqual=" + DEFAULT_HEIGHT_PX, "heightPx.lessThanOrEqual=" + SMALLER_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx is less than
        defaultRestaurantTableFiltering("heightPx.lessThan=" + UPDATED_HEIGHT_PX, "heightPx.lessThan=" + DEFAULT_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByHeightPxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where heightPx is greater than
        defaultRestaurantTableFiltering("heightPx.greaterThan=" + SMALLER_HEIGHT_PX, "heightPx.greaterThan=" + DEFAULT_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation equals to
        defaultRestaurantTableFiltering("rotation.equals=" + DEFAULT_ROTATION, "rotation.equals=" + UPDATED_ROTATION);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation in
        defaultRestaurantTableFiltering("rotation.in=" + DEFAULT_ROTATION + "," + UPDATED_ROTATION, "rotation.in=" + UPDATED_ROTATION);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation is not null
        defaultRestaurantTableFiltering("rotation.specified=true", "rotation.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation is greater than or equal to
        defaultRestaurantTableFiltering(
            "rotation.greaterThanOrEqual=" + DEFAULT_ROTATION,
            "rotation.greaterThanOrEqual=" + UPDATED_ROTATION
        );
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation is less than or equal to
        defaultRestaurantTableFiltering("rotation.lessThanOrEqual=" + DEFAULT_ROTATION, "rotation.lessThanOrEqual=" + SMALLER_ROTATION);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation is less than
        defaultRestaurantTableFiltering("rotation.lessThan=" + UPDATED_ROTATION, "rotation.lessThan=" + DEFAULT_ROTATION);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRotationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where rotation is greater than
        defaultRestaurantTableFiltering("rotation.greaterThan=" + SMALLER_ROTATION, "rotation.greaterThan=" + DEFAULT_ROTATION);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where status equals to
        defaultRestaurantTableFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where status in
        defaultRestaurantTableFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where status is not null
        defaultRestaurantTableFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where isActive equals to
        defaultRestaurantTableFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where isActive in
        defaultRestaurantTableFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where isActive is not null
        defaultRestaurantTableFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where notes equals to
        defaultRestaurantTableFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where notes in
        defaultRestaurantTableFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where notes is not null
        defaultRestaurantTableFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where notes contains
        defaultRestaurantTableFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        // Get all the restaurantTableList where notes does not contain
        defaultRestaurantTableFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllRestaurantTablesByRoomIsEqualToSomething() throws Exception {
        DiningRoom room;
        if (TestUtil.findAll(em, DiningRoom.class).isEmpty()) {
            restaurantTableRepository.saveAndFlush(restaurantTable);
            room = DiningRoomResourceIT.createEntity(em);
        } else {
            room = TestUtil.findAll(em, DiningRoom.class).get(0);
        }
        em.persist(room);
        em.flush();
        restaurantTable.setRoom(room);
        restaurantTableRepository.saveAndFlush(restaurantTable);
        Long roomId = room.getId();
        // Get all the restaurantTableList where room equals to roomId
        defaultRestaurantTableShouldBeFound("roomId.equals=" + roomId);

        // Get all the restaurantTableList where room equals to (roomId + 1)
        defaultRestaurantTableShouldNotBeFound("roomId.equals=" + (roomId + 1));
    }

    private void defaultRestaurantTableFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRestaurantTableShouldBeFound(shouldBeFound);
        defaultRestaurantTableShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRestaurantTableShouldBeFound(String filter) throws Exception {
        restRestaurantTableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantTable.getId().intValue())))
            .andExpect(jsonPath("$.[*].tableNumber").value(hasItem(DEFAULT_TABLE_NUMBER)))
            .andExpect(jsonPath("$.[*].shape").value(hasItem(DEFAULT_SHAPE.toString())))
            .andExpect(jsonPath("$.[*].minCapacity").value(hasItem(DEFAULT_MIN_CAPACITY)))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].positionX").value(hasItem(DEFAULT_POSITION_X)))
            .andExpect(jsonPath("$.[*].positionY").value(hasItem(DEFAULT_POSITION_Y)))
            .andExpect(jsonPath("$.[*].widthPx").value(hasItem(DEFAULT_WIDTH_PX)))
            .andExpect(jsonPath("$.[*].heightPx").value(hasItem(DEFAULT_HEIGHT_PX)))
            .andExpect(jsonPath("$.[*].rotation").value(hasItem(DEFAULT_ROTATION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restRestaurantTableMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRestaurantTableShouldNotBeFound(String filter) throws Exception {
        restRestaurantTableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRestaurantTableMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRestaurantTable() throws Exception {
        // Get the restaurantTable
        restRestaurantTableMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRestaurantTable() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurantTable
        RestaurantTable updatedRestaurantTable = restaurantTableRepository.findById(restaurantTable.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRestaurantTable are not directly saved in db
        em.detach(updatedRestaurantTable);
        updatedRestaurantTable
            .tableNumber(UPDATED_TABLE_NUMBER)
            .shape(UPDATED_SHAPE)
            .minCapacity(UPDATED_MIN_CAPACITY)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX)
            .rotation(UPDATED_ROTATION)
            .status(UPDATED_STATUS)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(updatedRestaurantTable);

        restRestaurantTableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantTableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurantTableDTO))
            )
            .andExpect(status().isOk());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRestaurantTableToMatchAllProperties(updatedRestaurantTable);
    }

    @Test
    @Transactional
    void putNonExistingRestaurantTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantTable.setId(longCount.incrementAndGet());

        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantTableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantTableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurantTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRestaurantTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantTable.setId(longCount.incrementAndGet());

        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantTableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurantTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRestaurantTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantTable.setId(longCount.incrementAndGet());

        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantTableMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRestaurantTableWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurantTable using partial update
        RestaurantTable partialUpdatedRestaurantTable = new RestaurantTable();
        partialUpdatedRestaurantTable.setId(restaurantTable.getId());

        partialUpdatedRestaurantTable
            .shape(UPDATED_SHAPE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .positionX(UPDATED_POSITION_X)
            .heightPx(UPDATED_HEIGHT_PX)
            .rotation(UPDATED_ROTATION);

        restRestaurantTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurantTable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRestaurantTable))
            )
            .andExpect(status().isOk());

        // Validate the RestaurantTable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurantTableUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRestaurantTable, restaurantTable),
            getPersistedRestaurantTable(restaurantTable)
        );
    }

    @Test
    @Transactional
    void fullUpdateRestaurantTableWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurantTable using partial update
        RestaurantTable partialUpdatedRestaurantTable = new RestaurantTable();
        partialUpdatedRestaurantTable.setId(restaurantTable.getId());

        partialUpdatedRestaurantTable
            .tableNumber(UPDATED_TABLE_NUMBER)
            .shape(UPDATED_SHAPE)
            .minCapacity(UPDATED_MIN_CAPACITY)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX)
            .rotation(UPDATED_ROTATION)
            .status(UPDATED_STATUS)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);

        restRestaurantTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurantTable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRestaurantTable))
            )
            .andExpect(status().isOk());

        // Validate the RestaurantTable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurantTableUpdatableFieldsEquals(
            partialUpdatedRestaurantTable,
            getPersistedRestaurantTable(partialUpdatedRestaurantTable)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRestaurantTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantTable.setId(longCount.incrementAndGet());

        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, restaurantTableDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(restaurantTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRestaurantTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantTable.setId(longCount.incrementAndGet());

        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(restaurantTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRestaurantTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantTable.setId(longCount.incrementAndGet());

        // Create the RestaurantTable
        RestaurantTableDTO restaurantTableDTO = restaurantTableMapper.toDto(restaurantTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantTableMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(restaurantTableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RestaurantTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRestaurantTable() throws Exception {
        // Initialize the database
        insertedRestaurantTable = restaurantTableRepository.saveAndFlush(restaurantTable);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the restaurantTable
        restRestaurantTableMockMvc
            .perform(delete(ENTITY_API_URL_ID, restaurantTable.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return restaurantTableRepository.count();
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

    protected RestaurantTable getPersistedRestaurantTable(RestaurantTable restaurantTable) {
        return restaurantTableRepository.findById(restaurantTable.getId()).orElseThrow();
    }

    protected void assertPersistedRestaurantTableToMatchAllProperties(RestaurantTable expectedRestaurantTable) {
        assertRestaurantTableAllPropertiesEquals(expectedRestaurantTable, getPersistedRestaurantTable(expectedRestaurantTable));
    }

    protected void assertPersistedRestaurantTableToMatchUpdatableProperties(RestaurantTable expectedRestaurantTable) {
        assertRestaurantTableAllUpdatablePropertiesEquals(expectedRestaurantTable, getPersistedRestaurantTable(expectedRestaurantTable));
    }
}
