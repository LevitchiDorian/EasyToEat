package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.LocationAsserts.*;
import static md.utm.restaurant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.repository.LocationRepository;
import md.utm.restaurant.service.LocationService;
import md.utm.restaurant.service.dto.LocationDTO;
import md.utm.restaurant.service.mapper.LocationMapper;
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
 * Integration tests for the {@link LocationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LocationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final Integer DEFAULT_RESERVATION_DURATION_OVERRIDE = 15;
    private static final Integer UPDATED_RESERVATION_DURATION_OVERRIDE = 16;
    private static final Integer SMALLER_RESERVATION_DURATION_OVERRIDE = 15 - 1;

    private static final Integer DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE = 1;
    private static final Integer UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE = 2;
    private static final Integer SMALLER_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE = 1 - 1;

    private static final Integer DEFAULT_CANCELLATION_DEADLINE_OVERRIDE = 0;
    private static final Integer UPDATED_CANCELLATION_DEADLINE_OVERRIDE = 1;
    private static final Integer SMALLER_CANCELLATION_DEADLINE_OVERRIDE = 0 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocationRepository locationRepository;

    @Mock
    private LocationRepository locationRepositoryMock;

    @Autowired
    private LocationMapper locationMapper;

    @Mock
    private LocationService locationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocationMockMvc;

    private Location location;

    private Location insertedLocation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Location createEntity(EntityManager em) {
        Location location = new Location()
            .name(DEFAULT_NAME)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .reservationDurationOverride(DEFAULT_RESERVATION_DURATION_OVERRIDE)
            .maxAdvanceBookingDaysOverride(DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE)
            .cancellationDeadlineOverride(DEFAULT_CANCELLATION_DEADLINE_OVERRIDE)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            brand = BrandResourceIT.createEntity();
            em.persist(brand);
            em.flush();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        location.setBrand(brand);
        return location;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Location createUpdatedEntity(EntityManager em) {
        Location updatedLocation = new Location()
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .reservationDurationOverride(UPDATED_RESERVATION_DURATION_OVERRIDE)
            .maxAdvanceBookingDaysOverride(UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE)
            .cancellationDeadlineOverride(UPDATED_CANCELLATION_DEADLINE_OVERRIDE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            brand = BrandResourceIT.createUpdatedEntity();
            em.persist(brand);
            em.flush();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        updatedLocation.setBrand(brand);
        return updatedLocation;
    }

    @BeforeEach
    void initTest() {
        location = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLocation != null) {
            locationRepository.delete(insertedLocation);
            insertedLocation = null;
        }
    }

    @Test
    @Transactional
    void createLocation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);
        var returnedLocationDTO = om.readValue(
            restLocationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocationDTO.class
        );

        // Validate the Location in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLocation = locationMapper.toEntity(returnedLocationDTO);
        assertLocationUpdatableFieldsEquals(returnedLocation, getPersistedLocation(returnedLocation));

        insertedLocation = returnedLocation;
    }

    @Test
    @Transactional
    void createLocationWithExistingId() throws Exception {
        // Create the Location with an existing ID
        location.setId(1L);
        LocationDTO locationDTO = locationMapper.toDto(location);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setName(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setAddress(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setCity(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setPhone(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setEmail(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setIsActive(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        location.setCreatedAt(null);

        // Create the Location, which fails.
        LocationDTO locationDTO = locationMapper.toDto(location);

        restLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLocations() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList
        restLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(location.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].reservationDurationOverride").value(hasItem(DEFAULT_RESERVATION_DURATION_OVERRIDE)))
            .andExpect(jsonPath("$.[*].maxAdvanceBookingDaysOverride").value(hasItem(DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE)))
            .andExpect(jsonPath("$.[*].cancellationDeadlineOverride").value(hasItem(DEFAULT_CANCELLATION_DEADLINE_OVERRIDE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(locationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(locationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(locationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(locationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLocation() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get the location
        restLocationMockMvc
            .perform(get(ENTITY_API_URL_ID, location.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(location.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.reservationDurationOverride").value(DEFAULT_RESERVATION_DURATION_OVERRIDE))
            .andExpect(jsonPath("$.maxAdvanceBookingDaysOverride").value(DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE))
            .andExpect(jsonPath("$.cancellationDeadlineOverride").value(DEFAULT_CANCELLATION_DEADLINE_OVERRIDE))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getLocationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        Long id = location.getId();

        defaultLocationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLocationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLocationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLocationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where name equals to
        defaultLocationFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLocationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where name in
        defaultLocationFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLocationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where name is not null
        defaultLocationFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where name contains
        defaultLocationFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLocationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where name does not contain
        defaultLocationFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllLocationsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where address equals to
        defaultLocationFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLocationsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where address in
        defaultLocationFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLocationsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where address is not null
        defaultLocationFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where address contains
        defaultLocationFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLocationsByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where address does not contain
        defaultLocationFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLocationsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where city equals to
        defaultLocationFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllLocationsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where city in
        defaultLocationFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllLocationsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where city is not null
        defaultLocationFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where city contains
        defaultLocationFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllLocationsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where city does not contain
        defaultLocationFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllLocationsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where phone equals to
        defaultLocationFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllLocationsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where phone in
        defaultLocationFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllLocationsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where phone is not null
        defaultLocationFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where phone contains
        defaultLocationFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllLocationsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where phone does not contain
        defaultLocationFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllLocationsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where email equals to
        defaultLocationFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocationsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where email in
        defaultLocationFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocationsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where email is not null
        defaultLocationFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where email contains
        defaultLocationFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocationsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where email does not contain
        defaultLocationFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude equals to
        defaultLocationFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude in
        defaultLocationFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude is not null
        defaultLocationFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude is greater than or equal to
        defaultLocationFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude is less than or equal to
        defaultLocationFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude is less than
        defaultLocationFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where latitude is greater than
        defaultLocationFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude equals to
        defaultLocationFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude in
        defaultLocationFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude is not null
        defaultLocationFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude is greater than or equal to
        defaultLocationFiltering("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude is less than or equal to
        defaultLocationFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude is less than
        defaultLocationFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where longitude is greater than
        defaultLocationFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride equals to
        defaultLocationFiltering(
            "reservationDurationOverride.equals=" + DEFAULT_RESERVATION_DURATION_OVERRIDE,
            "reservationDurationOverride.equals=" + UPDATED_RESERVATION_DURATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride in
        defaultLocationFiltering(
            "reservationDurationOverride.in=" + DEFAULT_RESERVATION_DURATION_OVERRIDE + "," + UPDATED_RESERVATION_DURATION_OVERRIDE,
            "reservationDurationOverride.in=" + UPDATED_RESERVATION_DURATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride is not null
        defaultLocationFiltering("reservationDurationOverride.specified=true", "reservationDurationOverride.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride is greater than or equal to
        defaultLocationFiltering(
            "reservationDurationOverride.greaterThanOrEqual=" + DEFAULT_RESERVATION_DURATION_OVERRIDE,
            "reservationDurationOverride.greaterThanOrEqual=" + (DEFAULT_RESERVATION_DURATION_OVERRIDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride is less than or equal to
        defaultLocationFiltering(
            "reservationDurationOverride.lessThanOrEqual=" + DEFAULT_RESERVATION_DURATION_OVERRIDE,
            "reservationDurationOverride.lessThanOrEqual=" + SMALLER_RESERVATION_DURATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride is less than
        defaultLocationFiltering(
            "reservationDurationOverride.lessThan=" + (DEFAULT_RESERVATION_DURATION_OVERRIDE + 1),
            "reservationDurationOverride.lessThan=" + DEFAULT_RESERVATION_DURATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByReservationDurationOverrideIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where reservationDurationOverride is greater than
        defaultLocationFiltering(
            "reservationDurationOverride.greaterThan=" + SMALLER_RESERVATION_DURATION_OVERRIDE,
            "reservationDurationOverride.greaterThan=" + DEFAULT_RESERVATION_DURATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride equals to
        defaultLocationFiltering(
            "maxAdvanceBookingDaysOverride.equals=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE,
            "maxAdvanceBookingDaysOverride.equals=" + UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride in
        defaultLocationFiltering(
            "maxAdvanceBookingDaysOverride.in=" +
            DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE +
            "," +
            UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE,
            "maxAdvanceBookingDaysOverride.in=" + UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride is not null
        defaultLocationFiltering("maxAdvanceBookingDaysOverride.specified=true", "maxAdvanceBookingDaysOverride.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride is greater than or equal to
        defaultLocationFiltering(
            "maxAdvanceBookingDaysOverride.greaterThanOrEqual=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE,
            "maxAdvanceBookingDaysOverride.greaterThanOrEqual=" + (DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride is less than or equal to
        defaultLocationFiltering(
            "maxAdvanceBookingDaysOverride.lessThanOrEqual=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE,
            "maxAdvanceBookingDaysOverride.lessThanOrEqual=" + SMALLER_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride is less than
        defaultLocationFiltering(
            "maxAdvanceBookingDaysOverride.lessThan=" + (DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE + 1),
            "maxAdvanceBookingDaysOverride.lessThan=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByMaxAdvanceBookingDaysOverrideIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where maxAdvanceBookingDaysOverride is greater than
        defaultLocationFiltering(
            "maxAdvanceBookingDaysOverride.greaterThan=" + SMALLER_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE,
            "maxAdvanceBookingDaysOverride.greaterThan=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride equals to
        defaultLocationFiltering(
            "cancellationDeadlineOverride.equals=" + DEFAULT_CANCELLATION_DEADLINE_OVERRIDE,
            "cancellationDeadlineOverride.equals=" + UPDATED_CANCELLATION_DEADLINE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride in
        defaultLocationFiltering(
            "cancellationDeadlineOverride.in=" + DEFAULT_CANCELLATION_DEADLINE_OVERRIDE + "," + UPDATED_CANCELLATION_DEADLINE_OVERRIDE,
            "cancellationDeadlineOverride.in=" + UPDATED_CANCELLATION_DEADLINE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride is not null
        defaultLocationFiltering("cancellationDeadlineOverride.specified=true", "cancellationDeadlineOverride.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride is greater than or equal to
        defaultLocationFiltering(
            "cancellationDeadlineOverride.greaterThanOrEqual=" + DEFAULT_CANCELLATION_DEADLINE_OVERRIDE,
            "cancellationDeadlineOverride.greaterThanOrEqual=" + (DEFAULT_CANCELLATION_DEADLINE_OVERRIDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride is less than or equal to
        defaultLocationFiltering(
            "cancellationDeadlineOverride.lessThanOrEqual=" + DEFAULT_CANCELLATION_DEADLINE_OVERRIDE,
            "cancellationDeadlineOverride.lessThanOrEqual=" + SMALLER_CANCELLATION_DEADLINE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride is less than
        defaultLocationFiltering(
            "cancellationDeadlineOverride.lessThan=" + (DEFAULT_CANCELLATION_DEADLINE_OVERRIDE + 1),
            "cancellationDeadlineOverride.lessThan=" + DEFAULT_CANCELLATION_DEADLINE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByCancellationDeadlineOverrideIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where cancellationDeadlineOverride is greater than
        defaultLocationFiltering(
            "cancellationDeadlineOverride.greaterThan=" + SMALLER_CANCELLATION_DEADLINE_OVERRIDE,
            "cancellationDeadlineOverride.greaterThan=" + DEFAULT_CANCELLATION_DEADLINE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where isActive equals to
        defaultLocationFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllLocationsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where isActive in
        defaultLocationFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllLocationsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where isActive is not null
        defaultLocationFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where createdAt equals to
        defaultLocationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllLocationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where createdAt in
        defaultLocationFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllLocationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        // Get all the locationList where createdAt is not null
        defaultLocationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationsByBrandIsEqualToSomething() throws Exception {
        Brand brand;
        if (TestUtil.findAll(em, Brand.class).isEmpty()) {
            locationRepository.saveAndFlush(location);
            brand = BrandResourceIT.createEntity();
        } else {
            brand = TestUtil.findAll(em, Brand.class).get(0);
        }
        em.persist(brand);
        em.flush();
        location.setBrand(brand);
        locationRepository.saveAndFlush(location);
        Long brandId = brand.getId();
        // Get all the locationList where brand equals to brandId
        defaultLocationShouldBeFound("brandId.equals=" + brandId);

        // Get all the locationList where brand equals to (brandId + 1)
        defaultLocationShouldNotBeFound("brandId.equals=" + (brandId + 1));
    }

    private void defaultLocationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLocationShouldBeFound(shouldBeFound);
        defaultLocationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLocationShouldBeFound(String filter) throws Exception {
        restLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(location.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].reservationDurationOverride").value(hasItem(DEFAULT_RESERVATION_DURATION_OVERRIDE)))
            .andExpect(jsonPath("$.[*].maxAdvanceBookingDaysOverride").value(hasItem(DEFAULT_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE)))
            .andExpect(jsonPath("$.[*].cancellationDeadlineOverride").value(hasItem(DEFAULT_CANCELLATION_DEADLINE_OVERRIDE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restLocationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLocationShouldNotBeFound(String filter) throws Exception {
        restLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLocationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLocation() throws Exception {
        // Get the location
        restLocationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocation() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the location
        Location updatedLocation = locationRepository.findById(location.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLocation are not directly saved in db
        em.detach(updatedLocation);
        updatedLocation
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .reservationDurationOverride(UPDATED_RESERVATION_DURATION_OVERRIDE)
            .maxAdvanceBookingDaysOverride(UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE)
            .cancellationDeadlineOverride(UPDATED_CANCELLATION_DEADLINE_OVERRIDE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
        LocationDTO locationDTO = locationMapper.toDto(updatedLocation);

        restLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocationToMatchAllProperties(updatedLocation);
    }

    @Test
    @Transactional
    void putNonExistingLocation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        location.setId(longCount.incrementAndGet());

        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        location.setId(longCount.incrementAndGet());

        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        location.setId(longCount.incrementAndGet());

        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLocationWithPatch() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the location using partial update
        Location partialUpdatedLocation = new Location();
        partialUpdatedLocation.setId(location.getId());

        partialUpdatedLocation
            .name(UPDATED_NAME)
            .city(UPDATED_CITY)
            .phone(UPDATED_PHONE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .reservationDurationOverride(UPDATED_RESERVATION_DURATION_OVERRIDE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);

        restLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocation))
            )
            .andExpect(status().isOk());

        // Validate the Location in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLocation, location), getPersistedLocation(location));
    }

    @Test
    @Transactional
    void fullUpdateLocationWithPatch() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the location using partial update
        Location partialUpdatedLocation = new Location();
        partialUpdatedLocation.setId(location.getId());

        partialUpdatedLocation
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .reservationDurationOverride(UPDATED_RESERVATION_DURATION_OVERRIDE)
            .maxAdvanceBookingDaysOverride(UPDATED_MAX_ADVANCE_BOOKING_DAYS_OVERRIDE)
            .cancellationDeadlineOverride(UPDATED_CANCELLATION_DEADLINE_OVERRIDE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);

        restLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocation))
            )
            .andExpect(status().isOk());

        // Validate the Location in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationUpdatableFieldsEquals(partialUpdatedLocation, getPersistedLocation(partialUpdatedLocation));
    }

    @Test
    @Transactional
    void patchNonExistingLocation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        location.setId(longCount.incrementAndGet());

        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, locationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        location.setId(longCount.incrementAndGet());

        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        location.setId(longCount.incrementAndGet());

        // Create the Location
        LocationDTO locationDTO = locationMapper.toDto(location);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(locationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Location in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLocation() throws Exception {
        // Initialize the database
        insertedLocation = locationRepository.saveAndFlush(location);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the location
        restLocationMockMvc
            .perform(delete(ENTITY_API_URL_ID, location.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return locationRepository.count();
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

    protected Location getPersistedLocation(Location location) {
        return locationRepository.findById(location.getId()).orElseThrow();
    }

    protected void assertPersistedLocationToMatchAllProperties(Location expectedLocation) {
        assertLocationAllPropertiesEquals(expectedLocation, getPersistedLocation(expectedLocation));
    }

    protected void assertPersistedLocationToMatchUpdatableProperties(Location expectedLocation) {
        assertLocationAllUpdatablePropertiesEquals(expectedLocation, getPersistedLocation(expectedLocation));
    }
}
