package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.DiningRoomAsserts.*;
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
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.repository.DiningRoomRepository;
import md.utm.restaurant.service.DiningRoomService;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.mapper.DiningRoomMapper;
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
 * Integration tests for the {@link DiningRoomResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DiningRoomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_FLOOR = 1;
    private static final Integer UPDATED_FLOOR = 2;
    private static final Integer SMALLER_FLOOR = 1 - 1;

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;
    private static final Integer SMALLER_CAPACITY = 1 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_FLOOR_PLAN_URL = "AAAAAAAAAA";
    private static final String UPDATED_FLOOR_PLAN_URL = "BBBBBBBBBB";

    private static final Double DEFAULT_WIDTH_PX = 1D;
    private static final Double UPDATED_WIDTH_PX = 2D;
    private static final Double SMALLER_WIDTH_PX = 1D - 1D;

    private static final Double DEFAULT_HEIGHT_PX = 1D;
    private static final Double UPDATED_HEIGHT_PX = 2D;
    private static final Double SMALLER_HEIGHT_PX = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/dining-rooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DiningRoomRepository diningRoomRepository;

    @Mock
    private DiningRoomRepository diningRoomRepositoryMock;

    @Autowired
    private DiningRoomMapper diningRoomMapper;

    @Mock
    private DiningRoomService diningRoomServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiningRoomMockMvc;

    private DiningRoom diningRoom;

    private DiningRoom insertedDiningRoom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DiningRoom createEntity(EntityManager em) {
        DiningRoom diningRoom = new DiningRoom()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .floor(DEFAULT_FLOOR)
            .capacity(DEFAULT_CAPACITY)
            .isActive(DEFAULT_IS_ACTIVE)
            .floorPlanUrl(DEFAULT_FLOOR_PLAN_URL)
            .widthPx(DEFAULT_WIDTH_PX)
            .heightPx(DEFAULT_HEIGHT_PX);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        diningRoom.setLocation(location);
        return diningRoom;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DiningRoom createUpdatedEntity(EntityManager em) {
        DiningRoom updatedDiningRoom = new DiningRoom()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .floor(UPDATED_FLOOR)
            .capacity(UPDATED_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE)
            .floorPlanUrl(UPDATED_FLOOR_PLAN_URL)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createUpdatedEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        updatedDiningRoom.setLocation(location);
        return updatedDiningRoom;
    }

    @BeforeEach
    void initTest() {
        diningRoom = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDiningRoom != null) {
            diningRoomRepository.delete(insertedDiningRoom);
            insertedDiningRoom = null;
        }
    }

    @Test
    @Transactional
    void createDiningRoom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);
        var returnedDiningRoomDTO = om.readValue(
            restDiningRoomMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diningRoomDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DiningRoomDTO.class
        );

        // Validate the DiningRoom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDiningRoom = diningRoomMapper.toEntity(returnedDiningRoomDTO);
        assertDiningRoomUpdatableFieldsEquals(returnedDiningRoom, getPersistedDiningRoom(returnedDiningRoom));

        insertedDiningRoom = returnedDiningRoom;
    }

    @Test
    @Transactional
    void createDiningRoomWithExistingId() throws Exception {
        // Create the DiningRoom with an existing ID
        diningRoom.setId(1L);
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiningRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diningRoomDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        diningRoom.setName(null);

        // Create the DiningRoom, which fails.
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        restDiningRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diningRoomDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        diningRoom.setCapacity(null);

        // Create the DiningRoom, which fails.
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        restDiningRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diningRoomDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        diningRoom.setIsActive(null);

        // Create the DiningRoom, which fails.
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        restDiningRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diningRoomDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDiningRooms() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList
        restDiningRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diningRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].floor").value(hasItem(DEFAULT_FLOOR)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].floorPlanUrl").value(hasItem(DEFAULT_FLOOR_PLAN_URL)))
            .andExpect(jsonPath("$.[*].widthPx").value(hasItem(DEFAULT_WIDTH_PX)))
            .andExpect(jsonPath("$.[*].heightPx").value(hasItem(DEFAULT_HEIGHT_PX)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiningRoomsWithEagerRelationshipsIsEnabled() throws Exception {
        when(diningRoomServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiningRoomMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(diningRoomServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiningRoomsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(diningRoomServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiningRoomMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(diningRoomRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDiningRoom() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get the diningRoom
        restDiningRoomMockMvc
            .perform(get(ENTITY_API_URL_ID, diningRoom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(diningRoom.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.floor").value(DEFAULT_FLOOR))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.floorPlanUrl").value(DEFAULT_FLOOR_PLAN_URL))
            .andExpect(jsonPath("$.widthPx").value(DEFAULT_WIDTH_PX))
            .andExpect(jsonPath("$.heightPx").value(DEFAULT_HEIGHT_PX));
    }

    @Test
    @Transactional
    void getDiningRoomsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        Long id = diningRoom.getId();

        defaultDiningRoomFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDiningRoomFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDiningRoomFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where name equals to
        defaultDiningRoomFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where name in
        defaultDiningRoomFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where name is not null
        defaultDiningRoomFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where name contains
        defaultDiningRoomFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where name does not contain
        defaultDiningRoomFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where description equals to
        defaultDiningRoomFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where description in
        defaultDiningRoomFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDiningRoomsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where description is not null
        defaultDiningRoomFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where description contains
        defaultDiningRoomFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where description does not contain
        defaultDiningRoomFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor equals to
        defaultDiningRoomFiltering("floor.equals=" + DEFAULT_FLOOR, "floor.equals=" + UPDATED_FLOOR);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor in
        defaultDiningRoomFiltering("floor.in=" + DEFAULT_FLOOR + "," + UPDATED_FLOOR, "floor.in=" + UPDATED_FLOOR);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor is not null
        defaultDiningRoomFiltering("floor.specified=true", "floor.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor is greater than or equal to
        defaultDiningRoomFiltering("floor.greaterThanOrEqual=" + DEFAULT_FLOOR, "floor.greaterThanOrEqual=" + UPDATED_FLOOR);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor is less than or equal to
        defaultDiningRoomFiltering("floor.lessThanOrEqual=" + DEFAULT_FLOOR, "floor.lessThanOrEqual=" + SMALLER_FLOOR);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor is less than
        defaultDiningRoomFiltering("floor.lessThan=" + UPDATED_FLOOR, "floor.lessThan=" + DEFAULT_FLOOR);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floor is greater than
        defaultDiningRoomFiltering("floor.greaterThan=" + SMALLER_FLOOR, "floor.greaterThan=" + DEFAULT_FLOOR);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity equals to
        defaultDiningRoomFiltering("capacity.equals=" + DEFAULT_CAPACITY, "capacity.equals=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity in
        defaultDiningRoomFiltering("capacity.in=" + DEFAULT_CAPACITY + "," + UPDATED_CAPACITY, "capacity.in=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity is not null
        defaultDiningRoomFiltering("capacity.specified=true", "capacity.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity is greater than or equal to
        defaultDiningRoomFiltering("capacity.greaterThanOrEqual=" + DEFAULT_CAPACITY, "capacity.greaterThanOrEqual=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity is less than or equal to
        defaultDiningRoomFiltering("capacity.lessThanOrEqual=" + DEFAULT_CAPACITY, "capacity.lessThanOrEqual=" + SMALLER_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity is less than
        defaultDiningRoomFiltering("capacity.lessThan=" + UPDATED_CAPACITY, "capacity.lessThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where capacity is greater than
        defaultDiningRoomFiltering("capacity.greaterThan=" + SMALLER_CAPACITY, "capacity.greaterThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where isActive equals to
        defaultDiningRoomFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where isActive in
        defaultDiningRoomFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where isActive is not null
        defaultDiningRoomFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorPlanUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floorPlanUrl equals to
        defaultDiningRoomFiltering("floorPlanUrl.equals=" + DEFAULT_FLOOR_PLAN_URL, "floorPlanUrl.equals=" + UPDATED_FLOOR_PLAN_URL);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorPlanUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floorPlanUrl in
        defaultDiningRoomFiltering(
            "floorPlanUrl.in=" + DEFAULT_FLOOR_PLAN_URL + "," + UPDATED_FLOOR_PLAN_URL,
            "floorPlanUrl.in=" + UPDATED_FLOOR_PLAN_URL
        );
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorPlanUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floorPlanUrl is not null
        defaultDiningRoomFiltering("floorPlanUrl.specified=true", "floorPlanUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorPlanUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floorPlanUrl contains
        defaultDiningRoomFiltering("floorPlanUrl.contains=" + DEFAULT_FLOOR_PLAN_URL, "floorPlanUrl.contains=" + UPDATED_FLOOR_PLAN_URL);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByFloorPlanUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where floorPlanUrl does not contain
        defaultDiningRoomFiltering(
            "floorPlanUrl.doesNotContain=" + UPDATED_FLOOR_PLAN_URL,
            "floorPlanUrl.doesNotContain=" + DEFAULT_FLOOR_PLAN_URL
        );
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx equals to
        defaultDiningRoomFiltering("widthPx.equals=" + DEFAULT_WIDTH_PX, "widthPx.equals=" + UPDATED_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx in
        defaultDiningRoomFiltering("widthPx.in=" + DEFAULT_WIDTH_PX + "," + UPDATED_WIDTH_PX, "widthPx.in=" + UPDATED_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx is not null
        defaultDiningRoomFiltering("widthPx.specified=true", "widthPx.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx is greater than or equal to
        defaultDiningRoomFiltering("widthPx.greaterThanOrEqual=" + DEFAULT_WIDTH_PX, "widthPx.greaterThanOrEqual=" + UPDATED_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx is less than or equal to
        defaultDiningRoomFiltering("widthPx.lessThanOrEqual=" + DEFAULT_WIDTH_PX, "widthPx.lessThanOrEqual=" + SMALLER_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx is less than
        defaultDiningRoomFiltering("widthPx.lessThan=" + UPDATED_WIDTH_PX, "widthPx.lessThan=" + DEFAULT_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByWidthPxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where widthPx is greater than
        defaultDiningRoomFiltering("widthPx.greaterThan=" + SMALLER_WIDTH_PX, "widthPx.greaterThan=" + DEFAULT_WIDTH_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx equals to
        defaultDiningRoomFiltering("heightPx.equals=" + DEFAULT_HEIGHT_PX, "heightPx.equals=" + UPDATED_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx in
        defaultDiningRoomFiltering("heightPx.in=" + DEFAULT_HEIGHT_PX + "," + UPDATED_HEIGHT_PX, "heightPx.in=" + UPDATED_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx is not null
        defaultDiningRoomFiltering("heightPx.specified=true", "heightPx.specified=false");
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx is greater than or equal to
        defaultDiningRoomFiltering("heightPx.greaterThanOrEqual=" + DEFAULT_HEIGHT_PX, "heightPx.greaterThanOrEqual=" + UPDATED_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx is less than or equal to
        defaultDiningRoomFiltering("heightPx.lessThanOrEqual=" + DEFAULT_HEIGHT_PX, "heightPx.lessThanOrEqual=" + SMALLER_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx is less than
        defaultDiningRoomFiltering("heightPx.lessThan=" + UPDATED_HEIGHT_PX, "heightPx.lessThan=" + DEFAULT_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByHeightPxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        // Get all the diningRoomList where heightPx is greater than
        defaultDiningRoomFiltering("heightPx.greaterThan=" + SMALLER_HEIGHT_PX, "heightPx.greaterThan=" + DEFAULT_HEIGHT_PX);
    }

    @Test
    @Transactional
    void getAllDiningRoomsByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            diningRoomRepository.saveAndFlush(diningRoom);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        diningRoom.setLocation(location);
        diningRoomRepository.saveAndFlush(diningRoom);
        Long locationId = location.getId();
        // Get all the diningRoomList where location equals to locationId
        defaultDiningRoomShouldBeFound("locationId.equals=" + locationId);

        // Get all the diningRoomList where location equals to (locationId + 1)
        defaultDiningRoomShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultDiningRoomFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDiningRoomShouldBeFound(shouldBeFound);
        defaultDiningRoomShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDiningRoomShouldBeFound(String filter) throws Exception {
        restDiningRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diningRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].floor").value(hasItem(DEFAULT_FLOOR)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].floorPlanUrl").value(hasItem(DEFAULT_FLOOR_PLAN_URL)))
            .andExpect(jsonPath("$.[*].widthPx").value(hasItem(DEFAULT_WIDTH_PX)))
            .andExpect(jsonPath("$.[*].heightPx").value(hasItem(DEFAULT_HEIGHT_PX)));

        // Check, that the count call also returns 1
        restDiningRoomMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDiningRoomShouldNotBeFound(String filter) throws Exception {
        restDiningRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDiningRoomMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDiningRoom() throws Exception {
        // Get the diningRoom
        restDiningRoomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiningRoom() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the diningRoom
        DiningRoom updatedDiningRoom = diningRoomRepository.findById(diningRoom.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiningRoom are not directly saved in db
        em.detach(updatedDiningRoom);
        updatedDiningRoom
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .floor(UPDATED_FLOOR)
            .capacity(UPDATED_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE)
            .floorPlanUrl(UPDATED_FLOOR_PLAN_URL)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX);
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(updatedDiningRoom);

        restDiningRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, diningRoomDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(diningRoomDTO))
            )
            .andExpect(status().isOk());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDiningRoomToMatchAllProperties(updatedDiningRoom);
    }

    @Test
    @Transactional
    void putNonExistingDiningRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        diningRoom.setId(longCount.incrementAndGet());

        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiningRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, diningRoomDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(diningRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiningRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        diningRoom.setId(longCount.incrementAndGet());

        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiningRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(diningRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiningRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        diningRoom.setId(longCount.incrementAndGet());

        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiningRoomMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diningRoomDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDiningRoomWithPatch() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the diningRoom using partial update
        DiningRoom partialUpdatedDiningRoom = new DiningRoom();
        partialUpdatedDiningRoom.setId(diningRoom.getId());

        partialUpdatedDiningRoom.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).floor(UPDATED_FLOOR).isActive(UPDATED_IS_ACTIVE);

        restDiningRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiningRoom.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiningRoom))
            )
            .andExpect(status().isOk());

        // Validate the DiningRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiningRoomUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDiningRoom, diningRoom),
            getPersistedDiningRoom(diningRoom)
        );
    }

    @Test
    @Transactional
    void fullUpdateDiningRoomWithPatch() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the diningRoom using partial update
        DiningRoom partialUpdatedDiningRoom = new DiningRoom();
        partialUpdatedDiningRoom.setId(diningRoom.getId());

        partialUpdatedDiningRoom
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .floor(UPDATED_FLOOR)
            .capacity(UPDATED_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE)
            .floorPlanUrl(UPDATED_FLOOR_PLAN_URL)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX);

        restDiningRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiningRoom.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiningRoom))
            )
            .andExpect(status().isOk());

        // Validate the DiningRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiningRoomUpdatableFieldsEquals(partialUpdatedDiningRoom, getPersistedDiningRoom(partialUpdatedDiningRoom));
    }

    @Test
    @Transactional
    void patchNonExistingDiningRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        diningRoom.setId(longCount.incrementAndGet());

        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiningRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, diningRoomDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(diningRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiningRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        diningRoom.setId(longCount.incrementAndGet());

        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiningRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(diningRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiningRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        diningRoom.setId(longCount.incrementAndGet());

        // Create the DiningRoom
        DiningRoomDTO diningRoomDTO = diningRoomMapper.toDto(diningRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiningRoomMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(diningRoomDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DiningRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDiningRoom() throws Exception {
        // Initialize the database
        insertedDiningRoom = diningRoomRepository.saveAndFlush(diningRoom);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the diningRoom
        restDiningRoomMockMvc
            .perform(delete(ENTITY_API_URL_ID, diningRoom.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return diningRoomRepository.count();
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

    protected DiningRoom getPersistedDiningRoom(DiningRoom diningRoom) {
        return diningRoomRepository.findById(diningRoom.getId()).orElseThrow();
    }

    protected void assertPersistedDiningRoomToMatchAllProperties(DiningRoom expectedDiningRoom) {
        assertDiningRoomAllPropertiesEquals(expectedDiningRoom, getPersistedDiningRoom(expectedDiningRoom));
    }

    protected void assertPersistedDiningRoomToMatchUpdatableProperties(DiningRoom expectedDiningRoom) {
        assertDiningRoomAllUpdatablePropertiesEquals(expectedDiningRoom, getPersistedDiningRoom(expectedDiningRoom));
    }
}
