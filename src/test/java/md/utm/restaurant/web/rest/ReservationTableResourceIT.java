package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.ReservationTableAsserts.*;
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
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.ReservationTable;
import md.utm.restaurant.repository.ReservationTableRepository;
import md.utm.restaurant.service.ReservationTableService;
import md.utm.restaurant.service.dto.ReservationTableDTO;
import md.utm.restaurant.service.mapper.ReservationTableMapper;
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
 * Integration tests for the {@link ReservationTableResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReservationTableResourceIT {

    private static final Instant DEFAULT_ASSIGNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reservation-tables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReservationTableRepository reservationTableRepository;

    @Mock
    private ReservationTableRepository reservationTableRepositoryMock;

    @Autowired
    private ReservationTableMapper reservationTableMapper;

    @Mock
    private ReservationTableService reservationTableServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReservationTableMockMvc;

    private ReservationTable reservationTable;

    private ReservationTable insertedReservationTable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReservationTable createEntity(EntityManager em) {
        ReservationTable reservationTable = new ReservationTable().assignedAt(DEFAULT_ASSIGNED_AT).notes(DEFAULT_NOTES);
        // Add required entity
        Reservation reservation;
        if (TestUtil.findAll(em, Reservation.class).isEmpty()) {
            reservation = ReservationResourceIT.createEntity();
            em.persist(reservation);
            em.flush();
        } else {
            reservation = TestUtil.findAll(em, Reservation.class).get(0);
        }
        reservationTable.setReservation(reservation);
        return reservationTable;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReservationTable createUpdatedEntity(EntityManager em) {
        ReservationTable updatedReservationTable = new ReservationTable().assignedAt(UPDATED_ASSIGNED_AT).notes(UPDATED_NOTES);
        // Add required entity
        Reservation reservation;
        if (TestUtil.findAll(em, Reservation.class).isEmpty()) {
            reservation = ReservationResourceIT.createUpdatedEntity();
            em.persist(reservation);
            em.flush();
        } else {
            reservation = TestUtil.findAll(em, Reservation.class).get(0);
        }
        updatedReservationTable.setReservation(reservation);
        return updatedReservationTable;
    }

    @BeforeEach
    void initTest() {
        reservationTable = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedReservationTable != null) {
            reservationTableRepository.delete(insertedReservationTable);
            insertedReservationTable = null;
        }
    }

    @Test
    @Transactional
    void createReservationTable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);
        var returnedReservationTableDTO = om.readValue(
            restReservationTableMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationTableDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReservationTableDTO.class
        );

        // Validate the ReservationTable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReservationTable = reservationTableMapper.toEntity(returnedReservationTableDTO);
        assertReservationTableUpdatableFieldsEquals(returnedReservationTable, getPersistedReservationTable(returnedReservationTable));

        insertedReservationTable = returnedReservationTable;
    }

    @Test
    @Transactional
    void createReservationTableWithExistingId() throws Exception {
        // Create the ReservationTable with an existing ID
        reservationTable.setId(1L);
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReservationTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationTableDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAssignedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservationTable.setAssignedAt(null);

        // Create the ReservationTable, which fails.
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        restReservationTableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationTableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReservationTables() throws Exception {
        // Initialize the database
        insertedReservationTable = reservationTableRepository.saveAndFlush(reservationTable);

        // Get all the reservationTableList
        restReservationTableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservationTable.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(DEFAULT_ASSIGNED_AT.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReservationTablesWithEagerRelationshipsIsEnabled() throws Exception {
        when(reservationTableServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReservationTableMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reservationTableServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReservationTablesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reservationTableServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReservationTableMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reservationTableRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReservationTable() throws Exception {
        // Initialize the database
        insertedReservationTable = reservationTableRepository.saveAndFlush(reservationTable);

        // Get the reservationTable
        restReservationTableMockMvc
            .perform(get(ENTITY_API_URL_ID, reservationTable.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reservationTable.getId().intValue()))
            .andExpect(jsonPath("$.assignedAt").value(DEFAULT_ASSIGNED_AT.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingReservationTable() throws Exception {
        // Get the reservationTable
        restReservationTableMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReservationTable() throws Exception {
        // Initialize the database
        insertedReservationTable = reservationTableRepository.saveAndFlush(reservationTable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservationTable
        ReservationTable updatedReservationTable = reservationTableRepository.findById(reservationTable.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReservationTable are not directly saved in db
        em.detach(updatedReservationTable);
        updatedReservationTable.assignedAt(UPDATED_ASSIGNED_AT).notes(UPDATED_NOTES);
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(updatedReservationTable);

        restReservationTableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationTableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationTableDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReservationTableToMatchAllProperties(updatedReservationTable);
    }

    @Test
    @Transactional
    void putNonExistingReservationTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservationTable.setId(longCount.incrementAndGet());

        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationTableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationTableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReservationTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservationTable.setId(longCount.incrementAndGet());

        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationTableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReservationTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservationTable.setId(longCount.incrementAndGet());

        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationTableMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationTableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReservationTableWithPatch() throws Exception {
        // Initialize the database
        insertedReservationTable = reservationTableRepository.saveAndFlush(reservationTable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservationTable using partial update
        ReservationTable partialUpdatedReservationTable = new ReservationTable();
        partialUpdatedReservationTable.setId(reservationTable.getId());

        restReservationTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReservationTable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReservationTable))
            )
            .andExpect(status().isOk());

        // Validate the ReservationTable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReservationTableUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReservationTable, reservationTable),
            getPersistedReservationTable(reservationTable)
        );
    }

    @Test
    @Transactional
    void fullUpdateReservationTableWithPatch() throws Exception {
        // Initialize the database
        insertedReservationTable = reservationTableRepository.saveAndFlush(reservationTable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservationTable using partial update
        ReservationTable partialUpdatedReservationTable = new ReservationTable();
        partialUpdatedReservationTable.setId(reservationTable.getId());

        partialUpdatedReservationTable.assignedAt(UPDATED_ASSIGNED_AT).notes(UPDATED_NOTES);

        restReservationTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReservationTable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReservationTable))
            )
            .andExpect(status().isOk());

        // Validate the ReservationTable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReservationTableUpdatableFieldsEquals(
            partialUpdatedReservationTable,
            getPersistedReservationTable(partialUpdatedReservationTable)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReservationTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservationTable.setId(longCount.incrementAndGet());

        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reservationTableDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reservationTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReservationTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservationTable.setId(longCount.incrementAndGet());

        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationTableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reservationTableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReservationTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservationTable.setId(longCount.incrementAndGet());

        // Create the ReservationTable
        ReservationTableDTO reservationTableDTO = reservationTableMapper.toDto(reservationTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationTableMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reservationTableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReservationTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReservationTable() throws Exception {
        // Initialize the database
        insertedReservationTable = reservationTableRepository.saveAndFlush(reservationTable);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reservationTable
        restReservationTableMockMvc
            .perform(delete(ENTITY_API_URL_ID, reservationTable.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reservationTableRepository.count();
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

    protected ReservationTable getPersistedReservationTable(ReservationTable reservationTable) {
        return reservationTableRepository.findById(reservationTable.getId()).orElseThrow();
    }

    protected void assertPersistedReservationTableToMatchAllProperties(ReservationTable expectedReservationTable) {
        assertReservationTableAllPropertiesEquals(expectedReservationTable, getPersistedReservationTable(expectedReservationTable));
    }

    protected void assertPersistedReservationTableToMatchUpdatableProperties(ReservationTable expectedReservationTable) {
        assertReservationTableAllUpdatablePropertiesEquals(
            expectedReservationTable,
            getPersistedReservationTable(expectedReservationTable)
        );
    }
}
