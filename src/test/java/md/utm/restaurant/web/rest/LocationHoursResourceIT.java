package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.LocationHoursAsserts.*;
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
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.LocationHours;
import md.utm.restaurant.domain.enumeration.DayOfWeek;
import md.utm.restaurant.repository.LocationHoursRepository;
import md.utm.restaurant.service.LocationHoursService;
import md.utm.restaurant.service.dto.LocationHoursDTO;
import md.utm.restaurant.service.mapper.LocationHoursMapper;
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
 * Integration tests for the {@link LocationHoursResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LocationHoursResourceIT {

    private static final DayOfWeek DEFAULT_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final DayOfWeek UPDATED_DAY_OF_WEEK = DayOfWeek.TUESDAY;

    private static final String DEFAULT_OPEN_TIME = "AAAAA";
    private static final String UPDATED_OPEN_TIME = "BBBBB";

    private static final String DEFAULT_CLOSE_TIME = "AAAAA";
    private static final String UPDATED_CLOSE_TIME = "BBBBB";

    private static final Boolean DEFAULT_IS_CLOSED = false;
    private static final Boolean UPDATED_IS_CLOSED = true;

    private static final String DEFAULT_SPECIAL_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/location-hours";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocationHoursRepository locationHoursRepository;

    @Mock
    private LocationHoursRepository locationHoursRepositoryMock;

    @Autowired
    private LocationHoursMapper locationHoursMapper;

    @Mock
    private LocationHoursService locationHoursServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocationHoursMockMvc;

    private LocationHours locationHours;

    private LocationHours insertedLocationHours;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocationHours createEntity(EntityManager em) {
        LocationHours locationHours = new LocationHours()
            .dayOfWeek(DEFAULT_DAY_OF_WEEK)
            .openTime(DEFAULT_OPEN_TIME)
            .closeTime(DEFAULT_CLOSE_TIME)
            .isClosed(DEFAULT_IS_CLOSED)
            .specialNote(DEFAULT_SPECIAL_NOTE);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        locationHours.setLocation(location);
        return locationHours;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocationHours createUpdatedEntity(EntityManager em) {
        LocationHours updatedLocationHours = new LocationHours()
            .dayOfWeek(UPDATED_DAY_OF_WEEK)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .isClosed(UPDATED_IS_CLOSED)
            .specialNote(UPDATED_SPECIAL_NOTE);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createUpdatedEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        updatedLocationHours.setLocation(location);
        return updatedLocationHours;
    }

    @BeforeEach
    void initTest() {
        locationHours = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLocationHours != null) {
            locationHoursRepository.delete(insertedLocationHours);
            insertedLocationHours = null;
        }
    }

    @Test
    @Transactional
    void createLocationHours() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);
        var returnedLocationHoursDTO = om.readValue(
            restLocationHoursMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocationHoursDTO.class
        );

        // Validate the LocationHours in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLocationHours = locationHoursMapper.toEntity(returnedLocationHoursDTO);
        assertLocationHoursUpdatableFieldsEquals(returnedLocationHours, getPersistedLocationHours(returnedLocationHours));

        insertedLocationHours = returnedLocationHours;
    }

    @Test
    @Transactional
    void createLocationHoursWithExistingId() throws Exception {
        // Create the LocationHours with an existing ID
        locationHours.setId(1L);
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocationHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDayOfWeekIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locationHours.setDayOfWeek(null);

        // Create the LocationHours, which fails.
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        restLocationHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOpenTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locationHours.setOpenTime(null);

        // Create the LocationHours, which fails.
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        restLocationHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCloseTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locationHours.setCloseTime(null);

        // Create the LocationHours, which fails.
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        restLocationHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsClosedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locationHours.setIsClosed(null);

        // Create the LocationHours, which fails.
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        restLocationHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLocationHours() throws Exception {
        // Initialize the database
        insertedLocationHours = locationHoursRepository.saveAndFlush(locationHours);

        // Get all the locationHoursList
        restLocationHoursMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locationHours.getId().intValue())))
            .andExpect(jsonPath("$.[*].dayOfWeek").value(hasItem(DEFAULT_DAY_OF_WEEK.toString())))
            .andExpect(jsonPath("$.[*].openTime").value(hasItem(DEFAULT_OPEN_TIME)))
            .andExpect(jsonPath("$.[*].closeTime").value(hasItem(DEFAULT_CLOSE_TIME)))
            .andExpect(jsonPath("$.[*].isClosed").value(hasItem(DEFAULT_IS_CLOSED)))
            .andExpect(jsonPath("$.[*].specialNote").value(hasItem(DEFAULT_SPECIAL_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationHoursWithEagerRelationshipsIsEnabled() throws Exception {
        when(locationHoursServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationHoursMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(locationHoursServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationHoursWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(locationHoursServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationHoursMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(locationHoursRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLocationHours() throws Exception {
        // Initialize the database
        insertedLocationHours = locationHoursRepository.saveAndFlush(locationHours);

        // Get the locationHours
        restLocationHoursMockMvc
            .perform(get(ENTITY_API_URL_ID, locationHours.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(locationHours.getId().intValue()))
            .andExpect(jsonPath("$.dayOfWeek").value(DEFAULT_DAY_OF_WEEK.toString()))
            .andExpect(jsonPath("$.openTime").value(DEFAULT_OPEN_TIME))
            .andExpect(jsonPath("$.closeTime").value(DEFAULT_CLOSE_TIME))
            .andExpect(jsonPath("$.isClosed").value(DEFAULT_IS_CLOSED))
            .andExpect(jsonPath("$.specialNote").value(DEFAULT_SPECIAL_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingLocationHours() throws Exception {
        // Get the locationHours
        restLocationHoursMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocationHours() throws Exception {
        // Initialize the database
        insertedLocationHours = locationHoursRepository.saveAndFlush(locationHours);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationHours
        LocationHours updatedLocationHours = locationHoursRepository.findById(locationHours.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLocationHours are not directly saved in db
        em.detach(updatedLocationHours);
        updatedLocationHours
            .dayOfWeek(UPDATED_DAY_OF_WEEK)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .isClosed(UPDATED_IS_CLOSED)
            .specialNote(UPDATED_SPECIAL_NOTE);
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(updatedLocationHours);

        restLocationHoursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationHoursDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationHoursDTO))
            )
            .andExpect(status().isOk());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocationHoursToMatchAllProperties(updatedLocationHours);
    }

    @Test
    @Transactional
    void putNonExistingLocationHours() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationHours.setId(longCount.incrementAndGet());

        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationHoursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationHoursDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationHoursDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocationHours() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationHours.setId(longCount.incrementAndGet());

        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationHoursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationHoursDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocationHours() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationHours.setId(longCount.incrementAndGet());

        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationHoursMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLocationHoursWithPatch() throws Exception {
        // Initialize the database
        insertedLocationHours = locationHoursRepository.saveAndFlush(locationHours);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationHours using partial update
        LocationHours partialUpdatedLocationHours = new LocationHours();
        partialUpdatedLocationHours.setId(locationHours.getId());

        partialUpdatedLocationHours.dayOfWeek(UPDATED_DAY_OF_WEEK).closeTime(UPDATED_CLOSE_TIME);

        restLocationHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocationHours.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocationHours))
            )
            .andExpect(status().isOk());

        // Validate the LocationHours in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationHoursUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLocationHours, locationHours),
            getPersistedLocationHours(locationHours)
        );
    }

    @Test
    @Transactional
    void fullUpdateLocationHoursWithPatch() throws Exception {
        // Initialize the database
        insertedLocationHours = locationHoursRepository.saveAndFlush(locationHours);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationHours using partial update
        LocationHours partialUpdatedLocationHours = new LocationHours();
        partialUpdatedLocationHours.setId(locationHours.getId());

        partialUpdatedLocationHours
            .dayOfWeek(UPDATED_DAY_OF_WEEK)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .isClosed(UPDATED_IS_CLOSED)
            .specialNote(UPDATED_SPECIAL_NOTE);

        restLocationHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocationHours.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocationHours))
            )
            .andExpect(status().isOk());

        // Validate the LocationHours in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationHoursUpdatableFieldsEquals(partialUpdatedLocationHours, getPersistedLocationHours(partialUpdatedLocationHours));
    }

    @Test
    @Transactional
    void patchNonExistingLocationHours() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationHours.setId(longCount.incrementAndGet());

        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, locationHoursDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationHoursDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocationHours() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationHours.setId(longCount.incrementAndGet());

        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationHoursDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocationHours() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationHours.setId(longCount.incrementAndGet());

        // Create the LocationHours
        LocationHoursDTO locationHoursDTO = locationHoursMapper.toDto(locationHours);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationHoursMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(locationHoursDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LocationHours in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLocationHours() throws Exception {
        // Initialize the database
        insertedLocationHours = locationHoursRepository.saveAndFlush(locationHours);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the locationHours
        restLocationHoursMockMvc
            .perform(delete(ENTITY_API_URL_ID, locationHours.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return locationHoursRepository.count();
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

    protected LocationHours getPersistedLocationHours(LocationHours locationHours) {
        return locationHoursRepository.findById(locationHours.getId()).orElseThrow();
    }

    protected void assertPersistedLocationHoursToMatchAllProperties(LocationHours expectedLocationHours) {
        assertLocationHoursAllPropertiesEquals(expectedLocationHours, getPersistedLocationHours(expectedLocationHours));
    }

    protected void assertPersistedLocationHoursToMatchUpdatableProperties(LocationHours expectedLocationHours) {
        assertLocationHoursAllUpdatablePropertiesEquals(expectedLocationHours, getPersistedLocationHours(expectedLocationHours));
    }
}
