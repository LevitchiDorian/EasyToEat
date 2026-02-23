package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.LocationMenuItemOverrideAsserts.*;
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
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.LocationMenuItemOverride;
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.repository.LocationMenuItemOverrideRepository;
import md.utm.restaurant.service.LocationMenuItemOverrideService;
import md.utm.restaurant.service.dto.LocationMenuItemOverrideDTO;
import md.utm.restaurant.service.mapper.LocationMenuItemOverrideMapper;
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
 * Integration tests for the {@link LocationMenuItemOverrideResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LocationMenuItemOverrideResourceIT {

    private static final Boolean DEFAULT_IS_AVAILABLE_AT_LOCATION = false;
    private static final Boolean UPDATED_IS_AVAILABLE_AT_LOCATION = true;

    private static final BigDecimal DEFAULT_PRICE_OVERRIDE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE_OVERRIDE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE_OVERRIDE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_PREPARATION_TIME_OVERRIDE = 0;
    private static final Integer UPDATED_PREPARATION_TIME_OVERRIDE = 1;
    private static final Integer SMALLER_PREPARATION_TIME_OVERRIDE = 0 - 1;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/location-menu-item-overrides";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocationMenuItemOverrideRepository locationMenuItemOverrideRepository;

    @Mock
    private LocationMenuItemOverrideRepository locationMenuItemOverrideRepositoryMock;

    @Autowired
    private LocationMenuItemOverrideMapper locationMenuItemOverrideMapper;

    @Mock
    private LocationMenuItemOverrideService locationMenuItemOverrideServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocationMenuItemOverrideMockMvc;

    private LocationMenuItemOverride locationMenuItemOverride;

    private LocationMenuItemOverride insertedLocationMenuItemOverride;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocationMenuItemOverride createEntity(EntityManager em) {
        LocationMenuItemOverride locationMenuItemOverride = new LocationMenuItemOverride()
            .isAvailableAtLocation(DEFAULT_IS_AVAILABLE_AT_LOCATION)
            .priceOverride(DEFAULT_PRICE_OVERRIDE)
            .preparationTimeOverride(DEFAULT_PREPARATION_TIME_OVERRIDE)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        locationMenuItemOverride.setLocation(location);
        return locationMenuItemOverride;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocationMenuItemOverride createUpdatedEntity(EntityManager em) {
        LocationMenuItemOverride updatedLocationMenuItemOverride = new LocationMenuItemOverride()
            .isAvailableAtLocation(UPDATED_IS_AVAILABLE_AT_LOCATION)
            .priceOverride(UPDATED_PRICE_OVERRIDE)
            .preparationTimeOverride(UPDATED_PREPARATION_TIME_OVERRIDE)
            .notes(UPDATED_NOTES);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createUpdatedEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        updatedLocationMenuItemOverride.setLocation(location);
        return updatedLocationMenuItemOverride;
    }

    @BeforeEach
    void initTest() {
        locationMenuItemOverride = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLocationMenuItemOverride != null) {
            locationMenuItemOverrideRepository.delete(insertedLocationMenuItemOverride);
            insertedLocationMenuItemOverride = null;
        }
    }

    @Test
    @Transactional
    void createLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);
        var returnedLocationMenuItemOverrideDTO = om.readValue(
            restLocationMenuItemOverrideMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocationMenuItemOverrideDTO.class
        );

        // Validate the LocationMenuItemOverride in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLocationMenuItemOverride = locationMenuItemOverrideMapper.toEntity(returnedLocationMenuItemOverrideDTO);
        assertLocationMenuItemOverrideUpdatableFieldsEquals(
            returnedLocationMenuItemOverride,
            getPersistedLocationMenuItemOverride(returnedLocationMenuItemOverride)
        );

        insertedLocationMenuItemOverride = returnedLocationMenuItemOverride;
    }

    @Test
    @Transactional
    void createLocationMenuItemOverrideWithExistingId() throws Exception {
        // Create the LocationMenuItemOverride with an existing ID
        locationMenuItemOverride.setId(1L);
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocationMenuItemOverrideMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsAvailableAtLocationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locationMenuItemOverride.setIsAvailableAtLocation(null);

        // Create the LocationMenuItemOverride, which fails.
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        restLocationMenuItemOverrideMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverrides() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList
        restLocationMenuItemOverrideMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locationMenuItemOverride.getId().intValue())))
            .andExpect(jsonPath("$.[*].isAvailableAtLocation").value(hasItem(DEFAULT_IS_AVAILABLE_AT_LOCATION)))
            .andExpect(jsonPath("$.[*].priceOverride").value(hasItem(sameNumber(DEFAULT_PRICE_OVERRIDE))))
            .andExpect(jsonPath("$.[*].preparationTimeOverride").value(hasItem(DEFAULT_PREPARATION_TIME_OVERRIDE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationMenuItemOverridesWithEagerRelationshipsIsEnabled() throws Exception {
        when(locationMenuItemOverrideServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationMenuItemOverrideMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(locationMenuItemOverrideServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationMenuItemOverridesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(locationMenuItemOverrideServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationMenuItemOverrideMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(locationMenuItemOverrideRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLocationMenuItemOverride() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get the locationMenuItemOverride
        restLocationMenuItemOverrideMockMvc
            .perform(get(ENTITY_API_URL_ID, locationMenuItemOverride.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(locationMenuItemOverride.getId().intValue()))
            .andExpect(jsonPath("$.isAvailableAtLocation").value(DEFAULT_IS_AVAILABLE_AT_LOCATION))
            .andExpect(jsonPath("$.priceOverride").value(sameNumber(DEFAULT_PRICE_OVERRIDE)))
            .andExpect(jsonPath("$.preparationTimeOverride").value(DEFAULT_PREPARATION_TIME_OVERRIDE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getLocationMenuItemOverridesByIdFiltering() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        Long id = locationMenuItemOverride.getId();

        defaultLocationMenuItemOverrideFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLocationMenuItemOverrideFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLocationMenuItemOverrideFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByIsAvailableAtLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where isAvailableAtLocation equals to
        defaultLocationMenuItemOverrideFiltering(
            "isAvailableAtLocation.equals=" + DEFAULT_IS_AVAILABLE_AT_LOCATION,
            "isAvailableAtLocation.equals=" + UPDATED_IS_AVAILABLE_AT_LOCATION
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByIsAvailableAtLocationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where isAvailableAtLocation in
        defaultLocationMenuItemOverrideFiltering(
            "isAvailableAtLocation.in=" + DEFAULT_IS_AVAILABLE_AT_LOCATION + "," + UPDATED_IS_AVAILABLE_AT_LOCATION,
            "isAvailableAtLocation.in=" + UPDATED_IS_AVAILABLE_AT_LOCATION
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByIsAvailableAtLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where isAvailableAtLocation is not null
        defaultLocationMenuItemOverrideFiltering("isAvailableAtLocation.specified=true", "isAvailableAtLocation.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride equals to
        defaultLocationMenuItemOverrideFiltering(
            "priceOverride.equals=" + DEFAULT_PRICE_OVERRIDE,
            "priceOverride.equals=" + UPDATED_PRICE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride in
        defaultLocationMenuItemOverrideFiltering(
            "priceOverride.in=" + DEFAULT_PRICE_OVERRIDE + "," + UPDATED_PRICE_OVERRIDE,
            "priceOverride.in=" + UPDATED_PRICE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride is not null
        defaultLocationMenuItemOverrideFiltering("priceOverride.specified=true", "priceOverride.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride is greater than or equal to
        defaultLocationMenuItemOverrideFiltering(
            "priceOverride.greaterThanOrEqual=" + DEFAULT_PRICE_OVERRIDE,
            "priceOverride.greaterThanOrEqual=" + UPDATED_PRICE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride is less than or equal to
        defaultLocationMenuItemOverrideFiltering(
            "priceOverride.lessThanOrEqual=" + DEFAULT_PRICE_OVERRIDE,
            "priceOverride.lessThanOrEqual=" + SMALLER_PRICE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride is less than
        defaultLocationMenuItemOverrideFiltering(
            "priceOverride.lessThan=" + UPDATED_PRICE_OVERRIDE,
            "priceOverride.lessThan=" + DEFAULT_PRICE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPriceOverrideIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where priceOverride is greater than
        defaultLocationMenuItemOverrideFiltering(
            "priceOverride.greaterThan=" + SMALLER_PRICE_OVERRIDE,
            "priceOverride.greaterThan=" + DEFAULT_PRICE_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride equals to
        defaultLocationMenuItemOverrideFiltering(
            "preparationTimeOverride.equals=" + DEFAULT_PREPARATION_TIME_OVERRIDE,
            "preparationTimeOverride.equals=" + UPDATED_PREPARATION_TIME_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride in
        defaultLocationMenuItemOverrideFiltering(
            "preparationTimeOverride.in=" + DEFAULT_PREPARATION_TIME_OVERRIDE + "," + UPDATED_PREPARATION_TIME_OVERRIDE,
            "preparationTimeOverride.in=" + UPDATED_PREPARATION_TIME_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride is not null
        defaultLocationMenuItemOverrideFiltering("preparationTimeOverride.specified=true", "preparationTimeOverride.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride is greater than or equal to
        defaultLocationMenuItemOverrideFiltering(
            "preparationTimeOverride.greaterThanOrEqual=" + DEFAULT_PREPARATION_TIME_OVERRIDE,
            "preparationTimeOverride.greaterThanOrEqual=" + (DEFAULT_PREPARATION_TIME_OVERRIDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride is less than or equal to
        defaultLocationMenuItemOverrideFiltering(
            "preparationTimeOverride.lessThanOrEqual=" + DEFAULT_PREPARATION_TIME_OVERRIDE,
            "preparationTimeOverride.lessThanOrEqual=" + SMALLER_PREPARATION_TIME_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride is less than
        defaultLocationMenuItemOverrideFiltering(
            "preparationTimeOverride.lessThan=" + (DEFAULT_PREPARATION_TIME_OVERRIDE + 1),
            "preparationTimeOverride.lessThan=" + DEFAULT_PREPARATION_TIME_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByPreparationTimeOverrideIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where preparationTimeOverride is greater than
        defaultLocationMenuItemOverrideFiltering(
            "preparationTimeOverride.greaterThan=" + SMALLER_PREPARATION_TIME_OVERRIDE,
            "preparationTimeOverride.greaterThan=" + DEFAULT_PREPARATION_TIME_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where notes equals to
        defaultLocationMenuItemOverrideFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where notes in
        defaultLocationMenuItemOverrideFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where notes is not null
        defaultLocationMenuItemOverrideFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where notes contains
        defaultLocationMenuItemOverrideFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        // Get all the locationMenuItemOverrideList where notes does not contain
        defaultLocationMenuItemOverrideFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByMenuItemIsEqualToSomething() throws Exception {
        MenuItem menuItem;
        if (TestUtil.findAll(em, MenuItem.class).isEmpty()) {
            locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);
            menuItem = MenuItemResourceIT.createEntity(em);
        } else {
            menuItem = TestUtil.findAll(em, MenuItem.class).get(0);
        }
        em.persist(menuItem);
        em.flush();
        locationMenuItemOverride.setMenuItem(menuItem);
        locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);
        Long menuItemId = menuItem.getId();
        // Get all the locationMenuItemOverrideList where menuItem equals to menuItemId
        defaultLocationMenuItemOverrideShouldBeFound("menuItemId.equals=" + menuItemId);

        // Get all the locationMenuItemOverrideList where menuItem equals to (menuItemId + 1)
        defaultLocationMenuItemOverrideShouldNotBeFound("menuItemId.equals=" + (menuItemId + 1));
    }

    @Test
    @Transactional
    void getAllLocationMenuItemOverridesByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        locationMenuItemOverride.setLocation(location);
        locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);
        Long locationId = location.getId();
        // Get all the locationMenuItemOverrideList where location equals to locationId
        defaultLocationMenuItemOverrideShouldBeFound("locationId.equals=" + locationId);

        // Get all the locationMenuItemOverrideList where location equals to (locationId + 1)
        defaultLocationMenuItemOverrideShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultLocationMenuItemOverrideFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLocationMenuItemOverrideShouldBeFound(shouldBeFound);
        defaultLocationMenuItemOverrideShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLocationMenuItemOverrideShouldBeFound(String filter) throws Exception {
        restLocationMenuItemOverrideMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locationMenuItemOverride.getId().intValue())))
            .andExpect(jsonPath("$.[*].isAvailableAtLocation").value(hasItem(DEFAULT_IS_AVAILABLE_AT_LOCATION)))
            .andExpect(jsonPath("$.[*].priceOverride").value(hasItem(sameNumber(DEFAULT_PRICE_OVERRIDE))))
            .andExpect(jsonPath("$.[*].preparationTimeOverride").value(hasItem(DEFAULT_PREPARATION_TIME_OVERRIDE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restLocationMenuItemOverrideMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLocationMenuItemOverrideShouldNotBeFound(String filter) throws Exception {
        restLocationMenuItemOverrideMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLocationMenuItemOverrideMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLocationMenuItemOverride() throws Exception {
        // Get the locationMenuItemOverride
        restLocationMenuItemOverrideMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocationMenuItemOverride() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationMenuItemOverride
        LocationMenuItemOverride updatedLocationMenuItemOverride = locationMenuItemOverrideRepository
            .findById(locationMenuItemOverride.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedLocationMenuItemOverride are not directly saved in db
        em.detach(updatedLocationMenuItemOverride);
        updatedLocationMenuItemOverride
            .isAvailableAtLocation(UPDATED_IS_AVAILABLE_AT_LOCATION)
            .priceOverride(UPDATED_PRICE_OVERRIDE)
            .preparationTimeOverride(UPDATED_PREPARATION_TIME_OVERRIDE)
            .notes(UPDATED_NOTES);
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(updatedLocationMenuItemOverride);

        restLocationMenuItemOverrideMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationMenuItemOverrideDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isOk());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocationMenuItemOverrideToMatchAllProperties(updatedLocationMenuItemOverride);
    }

    @Test
    @Transactional
    void putNonExistingLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationMenuItemOverride.setId(longCount.incrementAndGet());

        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationMenuItemOverrideMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationMenuItemOverrideDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationMenuItemOverride.setId(longCount.incrementAndGet());

        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMenuItemOverrideMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationMenuItemOverride.setId(longCount.incrementAndGet());

        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMenuItemOverrideMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationMenuItemOverrideDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLocationMenuItemOverrideWithPatch() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationMenuItemOverride using partial update
        LocationMenuItemOverride partialUpdatedLocationMenuItemOverride = new LocationMenuItemOverride();
        partialUpdatedLocationMenuItemOverride.setId(locationMenuItemOverride.getId());

        partialUpdatedLocationMenuItemOverride
            .isAvailableAtLocation(UPDATED_IS_AVAILABLE_AT_LOCATION)
            .priceOverride(UPDATED_PRICE_OVERRIDE)
            .preparationTimeOverride(UPDATED_PREPARATION_TIME_OVERRIDE);

        restLocationMenuItemOverrideMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocationMenuItemOverride.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocationMenuItemOverride))
            )
            .andExpect(status().isOk());

        // Validate the LocationMenuItemOverride in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationMenuItemOverrideUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLocationMenuItemOverride, locationMenuItemOverride),
            getPersistedLocationMenuItemOverride(locationMenuItemOverride)
        );
    }

    @Test
    @Transactional
    void fullUpdateLocationMenuItemOverrideWithPatch() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationMenuItemOverride using partial update
        LocationMenuItemOverride partialUpdatedLocationMenuItemOverride = new LocationMenuItemOverride();
        partialUpdatedLocationMenuItemOverride.setId(locationMenuItemOverride.getId());

        partialUpdatedLocationMenuItemOverride
            .isAvailableAtLocation(UPDATED_IS_AVAILABLE_AT_LOCATION)
            .priceOverride(UPDATED_PRICE_OVERRIDE)
            .preparationTimeOverride(UPDATED_PREPARATION_TIME_OVERRIDE)
            .notes(UPDATED_NOTES);

        restLocationMenuItemOverrideMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocationMenuItemOverride.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocationMenuItemOverride))
            )
            .andExpect(status().isOk());

        // Validate the LocationMenuItemOverride in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationMenuItemOverrideUpdatableFieldsEquals(
            partialUpdatedLocationMenuItemOverride,
            getPersistedLocationMenuItemOverride(partialUpdatedLocationMenuItemOverride)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationMenuItemOverride.setId(longCount.incrementAndGet());

        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationMenuItemOverrideMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, locationMenuItemOverrideDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationMenuItemOverride.setId(longCount.incrementAndGet());

        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMenuItemOverrideMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocationMenuItemOverride() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationMenuItemOverride.setId(longCount.incrementAndGet());

        // Create the LocationMenuItemOverride
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = locationMenuItemOverrideMapper.toDto(locationMenuItemOverride);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationMenuItemOverrideMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(locationMenuItemOverrideDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LocationMenuItemOverride in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLocationMenuItemOverride() throws Exception {
        // Initialize the database
        insertedLocationMenuItemOverride = locationMenuItemOverrideRepository.saveAndFlush(locationMenuItemOverride);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the locationMenuItemOverride
        restLocationMenuItemOverrideMockMvc
            .perform(delete(ENTITY_API_URL_ID, locationMenuItemOverride.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return locationMenuItemOverrideRepository.count();
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

    protected LocationMenuItemOverride getPersistedLocationMenuItemOverride(LocationMenuItemOverride locationMenuItemOverride) {
        return locationMenuItemOverrideRepository.findById(locationMenuItemOverride.getId()).orElseThrow();
    }

    protected void assertPersistedLocationMenuItemOverrideToMatchAllProperties(LocationMenuItemOverride expectedLocationMenuItemOverride) {
        assertLocationMenuItemOverrideAllPropertiesEquals(
            expectedLocationMenuItemOverride,
            getPersistedLocationMenuItemOverride(expectedLocationMenuItemOverride)
        );
    }

    protected void assertPersistedLocationMenuItemOverrideToMatchUpdatableProperties(
        LocationMenuItemOverride expectedLocationMenuItemOverride
    ) {
        assertLocationMenuItemOverrideAllUpdatablePropertiesEquals(
            expectedLocationMenuItemOverride,
            getPersistedLocationMenuItemOverride(expectedLocationMenuItemOverride)
        );
    }
}
