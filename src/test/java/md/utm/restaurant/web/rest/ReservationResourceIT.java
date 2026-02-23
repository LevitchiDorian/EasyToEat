package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.ReservationAsserts.*;
import static md.utm.restaurant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.domain.enumeration.ReservationStatus;
import md.utm.restaurant.repository.ReservationRepository;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.service.ReservationService;
import md.utm.restaurant.service.dto.ReservationDTO;
import md.utm.restaurant.service.mapper.ReservationMapper;
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
 * Integration tests for the {@link ReservationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReservationResourceIT {

    private static final String DEFAULT_RESERVATION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_RESERVATION_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RESERVATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RESERVATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_RESERVATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_START_TIME = "AAAAA";
    private static final String UPDATED_START_TIME = "BBBBB";

    private static final String DEFAULT_END_TIME = "AAAAA";
    private static final String UPDATED_END_TIME = "BBBBB";

    private static final Integer DEFAULT_PARTY_SIZE = 1;
    private static final Integer UPDATED_PARTY_SIZE = 2;
    private static final Integer SMALLER_PARTY_SIZE = 1 - 1;

    private static final ReservationStatus DEFAULT_STATUS = ReservationStatus.PENDING;
    private static final ReservationStatus UPDATED_STATUS = ReservationStatus.CONFIRMED;

    private static final String DEFAULT_SPECIAL_REQUESTS = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_REQUESTS = "BBBBBBBBBB";

    private static final String DEFAULT_INTERNAL_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_INTERNAL_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_REMINDER_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REMINDER_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CANCELLED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CANCELLED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CANCELLATION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_CANCELLATION_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reservations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepositoryMock;

    @Autowired
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationService reservationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReservationMockMvc;

    private Reservation reservation;

    private Reservation insertedReservation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createEntity() {
        return new Reservation()
            .reservationCode(DEFAULT_RESERVATION_CODE)
            .reservationDate(DEFAULT_RESERVATION_DATE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .partySize(DEFAULT_PARTY_SIZE)
            .status(DEFAULT_STATUS)
            .specialRequests(DEFAULT_SPECIAL_REQUESTS)
            .internalNotes(DEFAULT_INTERNAL_NOTES)
            .reminderSentAt(DEFAULT_REMINDER_SENT_AT)
            .confirmedAt(DEFAULT_CONFIRMED_AT)
            .cancelledAt(DEFAULT_CANCELLED_AT)
            .cancellationReason(DEFAULT_CANCELLATION_REASON)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createUpdatedEntity() {
        return new Reservation()
            .reservationCode(UPDATED_RESERVATION_CODE)
            .reservationDate(UPDATED_RESERVATION_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .partySize(UPDATED_PARTY_SIZE)
            .status(UPDATED_STATUS)
            .specialRequests(UPDATED_SPECIAL_REQUESTS)
            .internalNotes(UPDATED_INTERNAL_NOTES)
            .reminderSentAt(UPDATED_REMINDER_SENT_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancellationReason(UPDATED_CANCELLATION_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        reservation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReservation != null) {
            reservationRepository.delete(insertedReservation);
            insertedReservation = null;
        }
    }

    @Test
    @Transactional
    void createReservation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        var returnedReservationDTO = om.readValue(
            restReservationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReservationDTO.class
        );

        // Validate the Reservation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReservation = reservationMapper.toEntity(returnedReservationDTO);
        assertReservationUpdatableFieldsEquals(returnedReservation, getPersistedReservation(returnedReservation));

        insertedReservation = returnedReservation;
    }

    @Test
    @Transactional
    void createReservationWithExistingId() throws Exception {
        // Create the Reservation with an existing ID
        reservation.setId(1L);
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReservationCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setReservationCode(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReservationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setReservationDate(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setStartTime(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setEndTime(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPartySizeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setPartySize(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setStatus(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setCreatedAt(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReservations() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].reservationCode").value(hasItem(DEFAULT_RESERVATION_CODE)))
            .andExpect(jsonPath("$.[*].reservationDate").value(hasItem(DEFAULT_RESERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME)))
            .andExpect(jsonPath("$.[*].partySize").value(hasItem(DEFAULT_PARTY_SIZE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].specialRequests").value(hasItem(DEFAULT_SPECIAL_REQUESTS)))
            .andExpect(jsonPath("$.[*].internalNotes").value(hasItem(DEFAULT_INTERNAL_NOTES)))
            .andExpect(jsonPath("$.[*].reminderSentAt").value(hasItem(DEFAULT_REMINDER_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].cancelledAt").value(hasItem(DEFAULT_CANCELLED_AT.toString())))
            .andExpect(jsonPath("$.[*].cancellationReason").value(hasItem(DEFAULT_CANCELLATION_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReservationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(reservationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReservationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reservationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReservationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reservationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReservationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reservationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReservation() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get the reservation
        restReservationMockMvc
            .perform(get(ENTITY_API_URL_ID, reservation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reservation.getId().intValue()))
            .andExpect(jsonPath("$.reservationCode").value(DEFAULT_RESERVATION_CODE))
            .andExpect(jsonPath("$.reservationDate").value(DEFAULT_RESERVATION_DATE.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME))
            .andExpect(jsonPath("$.partySize").value(DEFAULT_PARTY_SIZE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.specialRequests").value(DEFAULT_SPECIAL_REQUESTS))
            .andExpect(jsonPath("$.internalNotes").value(DEFAULT_INTERNAL_NOTES))
            .andExpect(jsonPath("$.reminderSentAt").value(DEFAULT_REMINDER_SENT_AT.toString()))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()))
            .andExpect(jsonPath("$.cancelledAt").value(DEFAULT_CANCELLED_AT.toString()))
            .andExpect(jsonPath("$.cancellationReason").value(DEFAULT_CANCELLATION_REASON))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getReservationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        Long id = reservation.getId();

        defaultReservationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReservationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReservationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReservationsByReservationCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationCode equals to
        defaultReservationFiltering(
            "reservationCode.equals=" + DEFAULT_RESERVATION_CODE,
            "reservationCode.equals=" + UPDATED_RESERVATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationCode in
        defaultReservationFiltering(
            "reservationCode.in=" + DEFAULT_RESERVATION_CODE + "," + UPDATED_RESERVATION_CODE,
            "reservationCode.in=" + UPDATED_RESERVATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationCode is not null
        defaultReservationFiltering("reservationCode.specified=true", "reservationCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByReservationCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationCode contains
        defaultReservationFiltering(
            "reservationCode.contains=" + DEFAULT_RESERVATION_CODE,
            "reservationCode.contains=" + UPDATED_RESERVATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationCode does not contain
        defaultReservationFiltering(
            "reservationCode.doesNotContain=" + UPDATED_RESERVATION_CODE,
            "reservationCode.doesNotContain=" + DEFAULT_RESERVATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate equals to
        defaultReservationFiltering(
            "reservationDate.equals=" + DEFAULT_RESERVATION_DATE,
            "reservationDate.equals=" + UPDATED_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate in
        defaultReservationFiltering(
            "reservationDate.in=" + DEFAULT_RESERVATION_DATE + "," + UPDATED_RESERVATION_DATE,
            "reservationDate.in=" + UPDATED_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate is not null
        defaultReservationFiltering("reservationDate.specified=true", "reservationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate is greater than or equal to
        defaultReservationFiltering(
            "reservationDate.greaterThanOrEqual=" + DEFAULT_RESERVATION_DATE,
            "reservationDate.greaterThanOrEqual=" + UPDATED_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate is less than or equal to
        defaultReservationFiltering(
            "reservationDate.lessThanOrEqual=" + DEFAULT_RESERVATION_DATE,
            "reservationDate.lessThanOrEqual=" + SMALLER_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate is less than
        defaultReservationFiltering(
            "reservationDate.lessThan=" + UPDATED_RESERVATION_DATE,
            "reservationDate.lessThan=" + DEFAULT_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate is greater than
        defaultReservationFiltering(
            "reservationDate.greaterThan=" + SMALLER_RESERVATION_DATE,
            "reservationDate.greaterThan=" + DEFAULT_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startTime equals to
        defaultReservationFiltering("startTime.equals=" + DEFAULT_START_TIME, "startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startTime in
        defaultReservationFiltering("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME, "startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startTime is not null
        defaultReservationFiltering("startTime.specified=true", "startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByStartTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startTime contains
        defaultReservationFiltering("startTime.contains=" + DEFAULT_START_TIME, "startTime.contains=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByStartTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startTime does not contain
        defaultReservationFiltering("startTime.doesNotContain=" + UPDATED_START_TIME, "startTime.doesNotContain=" + DEFAULT_START_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endTime equals to
        defaultReservationFiltering("endTime.equals=" + DEFAULT_END_TIME, "endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endTime in
        defaultReservationFiltering("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME, "endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endTime is not null
        defaultReservationFiltering("endTime.specified=true", "endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByEndTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endTime contains
        defaultReservationFiltering("endTime.contains=" + DEFAULT_END_TIME, "endTime.contains=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByEndTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endTime does not contain
        defaultReservationFiltering("endTime.doesNotContain=" + UPDATED_END_TIME, "endTime.doesNotContain=" + DEFAULT_END_TIME);
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize equals to
        defaultReservationFiltering("partySize.equals=" + DEFAULT_PARTY_SIZE, "partySize.equals=" + UPDATED_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize in
        defaultReservationFiltering("partySize.in=" + DEFAULT_PARTY_SIZE + "," + UPDATED_PARTY_SIZE, "partySize.in=" + UPDATED_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize is not null
        defaultReservationFiltering("partySize.specified=true", "partySize.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize is greater than or equal to
        defaultReservationFiltering(
            "partySize.greaterThanOrEqual=" + DEFAULT_PARTY_SIZE,
            "partySize.greaterThanOrEqual=" + (DEFAULT_PARTY_SIZE + 1)
        );
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize is less than or equal to
        defaultReservationFiltering("partySize.lessThanOrEqual=" + DEFAULT_PARTY_SIZE, "partySize.lessThanOrEqual=" + SMALLER_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize is less than
        defaultReservationFiltering("partySize.lessThan=" + (DEFAULT_PARTY_SIZE + 1), "partySize.lessThan=" + DEFAULT_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllReservationsByPartySizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where partySize is greater than
        defaultReservationFiltering("partySize.greaterThan=" + SMALLER_PARTY_SIZE, "partySize.greaterThan=" + DEFAULT_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllReservationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status equals to
        defaultReservationFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReservationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status in
        defaultReservationFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReservationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status is not null
        defaultReservationFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByReminderSentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reminderSentAt equals to
        defaultReservationFiltering(
            "reminderSentAt.equals=" + DEFAULT_REMINDER_SENT_AT,
            "reminderSentAt.equals=" + UPDATED_REMINDER_SENT_AT
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReminderSentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reminderSentAt in
        defaultReservationFiltering(
            "reminderSentAt.in=" + DEFAULT_REMINDER_SENT_AT + "," + UPDATED_REMINDER_SENT_AT,
            "reminderSentAt.in=" + UPDATED_REMINDER_SENT_AT
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReminderSentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reminderSentAt is not null
        defaultReservationFiltering("reminderSentAt.specified=true", "reminderSentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where confirmedAt equals to
        defaultReservationFiltering("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT, "confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllReservationsByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where confirmedAt in
        defaultReservationFiltering(
            "confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT,
            "confirmedAt.in=" + UPDATED_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllReservationsByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where confirmedAt is not null
        defaultReservationFiltering("confirmedAt.specified=true", "confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByCancelledAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancelledAt equals to
        defaultReservationFiltering("cancelledAt.equals=" + DEFAULT_CANCELLED_AT, "cancelledAt.equals=" + UPDATED_CANCELLED_AT);
    }

    @Test
    @Transactional
    void getAllReservationsByCancelledAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancelledAt in
        defaultReservationFiltering(
            "cancelledAt.in=" + DEFAULT_CANCELLED_AT + "," + UPDATED_CANCELLED_AT,
            "cancelledAt.in=" + UPDATED_CANCELLED_AT
        );
    }

    @Test
    @Transactional
    void getAllReservationsByCancelledAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancelledAt is not null
        defaultReservationFiltering("cancelledAt.specified=true", "cancelledAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByCancellationReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancellationReason equals to
        defaultReservationFiltering(
            "cancellationReason.equals=" + DEFAULT_CANCELLATION_REASON,
            "cancellationReason.equals=" + UPDATED_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllReservationsByCancellationReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancellationReason in
        defaultReservationFiltering(
            "cancellationReason.in=" + DEFAULT_CANCELLATION_REASON + "," + UPDATED_CANCELLATION_REASON,
            "cancellationReason.in=" + UPDATED_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllReservationsByCancellationReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancellationReason is not null
        defaultReservationFiltering("cancellationReason.specified=true", "cancellationReason.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByCancellationReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancellationReason contains
        defaultReservationFiltering(
            "cancellationReason.contains=" + DEFAULT_CANCELLATION_REASON,
            "cancellationReason.contains=" + UPDATED_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllReservationsByCancellationReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where cancellationReason does not contain
        defaultReservationFiltering(
            "cancellationReason.doesNotContain=" + UPDATED_CANCELLATION_REASON,
            "cancellationReason.doesNotContain=" + DEFAULT_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllReservationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where createdAt equals to
        defaultReservationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReservationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where createdAt in
        defaultReservationFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReservationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where createdAt is not null
        defaultReservationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where updatedAt equals to
        defaultReservationFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReservationsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where updatedAt in
        defaultReservationFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReservationsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where updatedAt is not null
        defaultReservationFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            reservationRepository.saveAndFlush(reservation);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        reservation.setLocation(location);
        reservationRepository.saveAndFlush(reservation);
        Long locationId = location.getId();
        // Get all the reservationList where location equals to locationId
        defaultReservationShouldBeFound("locationId.equals=" + locationId);

        // Get all the reservationList where location equals to (locationId + 1)
        defaultReservationShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    @Test
    @Transactional
    void getAllReservationsByClientIsEqualToSomething() throws Exception {
        User client;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            reservationRepository.saveAndFlush(reservation);
            client = UserResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(client);
        em.flush();
        reservation.setClient(client);
        reservationRepository.saveAndFlush(reservation);
        Long clientId = client.getId();
        // Get all the reservationList where client equals to clientId
        defaultReservationShouldBeFound("clientId.equals=" + clientId);

        // Get all the reservationList where client equals to (clientId + 1)
        defaultReservationShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllReservationsByRoomIsEqualToSomething() throws Exception {
        DiningRoom room;
        if (TestUtil.findAll(em, DiningRoom.class).isEmpty()) {
            reservationRepository.saveAndFlush(reservation);
            room = DiningRoomResourceIT.createEntity(em);
        } else {
            room = TestUtil.findAll(em, DiningRoom.class).get(0);
        }
        em.persist(room);
        em.flush();
        reservation.setRoom(room);
        reservationRepository.saveAndFlush(reservation);
        Long roomId = room.getId();
        // Get all the reservationList where room equals to roomId
        defaultReservationShouldBeFound("roomId.equals=" + roomId);

        // Get all the reservationList where room equals to (roomId + 1)
        defaultReservationShouldNotBeFound("roomId.equals=" + (roomId + 1));
    }

    private void defaultReservationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReservationShouldBeFound(shouldBeFound);
        defaultReservationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReservationShouldBeFound(String filter) throws Exception {
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].reservationCode").value(hasItem(DEFAULT_RESERVATION_CODE)))
            .andExpect(jsonPath("$.[*].reservationDate").value(hasItem(DEFAULT_RESERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME)))
            .andExpect(jsonPath("$.[*].partySize").value(hasItem(DEFAULT_PARTY_SIZE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].specialRequests").value(hasItem(DEFAULT_SPECIAL_REQUESTS)))
            .andExpect(jsonPath("$.[*].internalNotes").value(hasItem(DEFAULT_INTERNAL_NOTES)))
            .andExpect(jsonPath("$.[*].reminderSentAt").value(hasItem(DEFAULT_REMINDER_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].cancelledAt").value(hasItem(DEFAULT_CANCELLED_AT.toString())))
            .andExpect(jsonPath("$.[*].cancellationReason").value(hasItem(DEFAULT_CANCELLATION_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReservationShouldNotBeFound(String filter) throws Exception {
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReservation() throws Exception {
        // Get the reservation
        restReservationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReservation() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservation
        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReservation are not directly saved in db
        em.detach(updatedReservation);
        updatedReservation
            .reservationCode(UPDATED_RESERVATION_CODE)
            .reservationDate(UPDATED_RESERVATION_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .partySize(UPDATED_PARTY_SIZE)
            .status(UPDATED_STATUS)
            .specialRequests(UPDATED_SPECIAL_REQUESTS)
            .internalNotes(UPDATED_INTERNAL_NOTES)
            .reminderSentAt(UPDATED_REMINDER_SENT_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancellationReason(UPDATED_CANCELLATION_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ReservationDTO reservationDTO = reservationMapper.toDto(updatedReservation);

        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReservationToMatchAllProperties(updatedReservation);
    }

    @Test
    @Transactional
    void putNonExistingReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReservationWithPatch() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservation using partial update
        Reservation partialUpdatedReservation = new Reservation();
        partialUpdatedReservation.setId(reservation.getId());

        partialUpdatedReservation
            .reservationCode(UPDATED_RESERVATION_CODE)
            .startTime(UPDATED_START_TIME)
            .internalNotes(UPDATED_INTERNAL_NOTES)
            .reminderSentAt(UPDATED_REMINDER_SENT_AT)
            .createdAt(UPDATED_CREATED_AT);

        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReservation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReservation))
            )
            .andExpect(status().isOk());

        // Validate the Reservation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReservationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReservation, reservation),
            getPersistedReservation(reservation)
        );
    }

    @Test
    @Transactional
    void fullUpdateReservationWithPatch() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservation using partial update
        Reservation partialUpdatedReservation = new Reservation();
        partialUpdatedReservation.setId(reservation.getId());

        partialUpdatedReservation
            .reservationCode(UPDATED_RESERVATION_CODE)
            .reservationDate(UPDATED_RESERVATION_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .partySize(UPDATED_PARTY_SIZE)
            .status(UPDATED_STATUS)
            .specialRequests(UPDATED_SPECIAL_REQUESTS)
            .internalNotes(UPDATED_INTERNAL_NOTES)
            .reminderSentAt(UPDATED_REMINDER_SENT_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancellationReason(UPDATED_CANCELLATION_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReservation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReservation))
            )
            .andExpect(status().isOk());

        // Validate the Reservation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReservationUpdatableFieldsEquals(partialUpdatedReservation, getPersistedReservation(partialUpdatedReservation));
    }

    @Test
    @Transactional
    void patchNonExistingReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReservation() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reservation
        restReservationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reservation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reservationRepository.count();
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

    protected Reservation getPersistedReservation(Reservation reservation) {
        return reservationRepository.findById(reservation.getId()).orElseThrow();
    }

    protected void assertPersistedReservationToMatchAllProperties(Reservation expectedReservation) {
        assertReservationAllPropertiesEquals(expectedReservation, getPersistedReservation(expectedReservation));
    }

    protected void assertPersistedReservationToMatchUpdatableProperties(Reservation expectedReservation) {
        assertReservationAllUpdatablePropertiesEquals(expectedReservation, getPersistedReservation(expectedReservation));
    }
}
