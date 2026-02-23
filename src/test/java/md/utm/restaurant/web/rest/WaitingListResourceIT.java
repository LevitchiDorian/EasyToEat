package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.WaitingListAsserts.*;
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
import md.utm.restaurant.domain.User;
import md.utm.restaurant.domain.WaitingList;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.repository.WaitingListRepository;
import md.utm.restaurant.service.WaitingListService;
import md.utm.restaurant.service.dto.WaitingListDTO;
import md.utm.restaurant.service.mapper.WaitingListMapper;
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
 * Integration tests for the {@link WaitingListResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WaitingListResourceIT {

    private static final LocalDate DEFAULT_REQUESTED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REQUESTED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_REQUESTED_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_REQUESTED_TIME = "AAAAA";
    private static final String UPDATED_REQUESTED_TIME = "BBBBB";

    private static final Integer DEFAULT_PARTY_SIZE = 1;
    private static final Integer UPDATED_PARTY_SIZE = 2;
    private static final Integer SMALLER_PARTY_SIZE = 1 - 1;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_NOTIFIED = false;
    private static final Boolean UPDATED_IS_NOTIFIED = true;

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/waiting-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private WaitingListRepository waitingListRepositoryMock;

    @Autowired
    private WaitingListMapper waitingListMapper;

    @Mock
    private WaitingListService waitingListServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWaitingListMockMvc;

    private WaitingList waitingList;

    private WaitingList insertedWaitingList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WaitingList createEntity() {
        return new WaitingList()
            .requestedDate(DEFAULT_REQUESTED_DATE)
            .requestedTime(DEFAULT_REQUESTED_TIME)
            .partySize(DEFAULT_PARTY_SIZE)
            .notes(DEFAULT_NOTES)
            .isNotified(DEFAULT_IS_NOTIFIED)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WaitingList createUpdatedEntity() {
        return new WaitingList()
            .requestedDate(UPDATED_REQUESTED_DATE)
            .requestedTime(UPDATED_REQUESTED_TIME)
            .partySize(UPDATED_PARTY_SIZE)
            .notes(UPDATED_NOTES)
            .isNotified(UPDATED_IS_NOTIFIED)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        waitingList = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWaitingList != null) {
            waitingListRepository.delete(insertedWaitingList);
            insertedWaitingList = null;
        }
    }

    @Test
    @Transactional
    void createWaitingList() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);
        var returnedWaitingListDTO = om.readValue(
            restWaitingListMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WaitingListDTO.class
        );

        // Validate the WaitingList in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWaitingList = waitingListMapper.toEntity(returnedWaitingListDTO);
        assertWaitingListUpdatableFieldsEquals(returnedWaitingList, getPersistedWaitingList(returnedWaitingList));

        insertedWaitingList = returnedWaitingList;
    }

    @Test
    @Transactional
    void createWaitingListWithExistingId() throws Exception {
        // Create the WaitingList with an existing ID
        waitingList.setId(1L);
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWaitingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRequestedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitingList.setRequestedDate(null);

        // Create the WaitingList, which fails.
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        restWaitingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequestedTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitingList.setRequestedTime(null);

        // Create the WaitingList, which fails.
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        restWaitingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPartySizeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitingList.setPartySize(null);

        // Create the WaitingList, which fails.
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        restWaitingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsNotifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitingList.setIsNotified(null);

        // Create the WaitingList, which fails.
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        restWaitingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitingList.setCreatedAt(null);

        // Create the WaitingList, which fails.
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        restWaitingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWaitingLists() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList
        restWaitingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waitingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].requestedDate").value(hasItem(DEFAULT_REQUESTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].requestedTime").value(hasItem(DEFAULT_REQUESTED_TIME)))
            .andExpect(jsonPath("$.[*].partySize").value(hasItem(DEFAULT_PARTY_SIZE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].isNotified").value(hasItem(DEFAULT_IS_NOTIFIED)))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWaitingListsWithEagerRelationshipsIsEnabled() throws Exception {
        when(waitingListServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWaitingListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(waitingListServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWaitingListsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(waitingListServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWaitingListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(waitingListRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWaitingList() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get the waitingList
        restWaitingListMockMvc
            .perform(get(ENTITY_API_URL_ID, waitingList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(waitingList.getId().intValue()))
            .andExpect(jsonPath("$.requestedDate").value(DEFAULT_REQUESTED_DATE.toString()))
            .andExpect(jsonPath("$.requestedTime").value(DEFAULT_REQUESTED_TIME))
            .andExpect(jsonPath("$.partySize").value(DEFAULT_PARTY_SIZE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.isNotified").value(DEFAULT_IS_NOTIFIED))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getWaitingListsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        Long id = waitingList.getId();

        defaultWaitingListFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWaitingListFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWaitingListFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate equals to
        defaultWaitingListFiltering("requestedDate.equals=" + DEFAULT_REQUESTED_DATE, "requestedDate.equals=" + UPDATED_REQUESTED_DATE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate in
        defaultWaitingListFiltering(
            "requestedDate.in=" + DEFAULT_REQUESTED_DATE + "," + UPDATED_REQUESTED_DATE,
            "requestedDate.in=" + UPDATED_REQUESTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate is not null
        defaultWaitingListFiltering("requestedDate.specified=true", "requestedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate is greater than or equal to
        defaultWaitingListFiltering(
            "requestedDate.greaterThanOrEqual=" + DEFAULT_REQUESTED_DATE,
            "requestedDate.greaterThanOrEqual=" + UPDATED_REQUESTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate is less than or equal to
        defaultWaitingListFiltering(
            "requestedDate.lessThanOrEqual=" + DEFAULT_REQUESTED_DATE,
            "requestedDate.lessThanOrEqual=" + SMALLER_REQUESTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate is less than
        defaultWaitingListFiltering("requestedDate.lessThan=" + UPDATED_REQUESTED_DATE, "requestedDate.lessThan=" + DEFAULT_REQUESTED_DATE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedDate is greater than
        defaultWaitingListFiltering(
            "requestedDate.greaterThan=" + SMALLER_REQUESTED_DATE,
            "requestedDate.greaterThan=" + DEFAULT_REQUESTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedTime equals to
        defaultWaitingListFiltering("requestedTime.equals=" + DEFAULT_REQUESTED_TIME, "requestedTime.equals=" + UPDATED_REQUESTED_TIME);
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedTime in
        defaultWaitingListFiltering(
            "requestedTime.in=" + DEFAULT_REQUESTED_TIME + "," + UPDATED_REQUESTED_TIME,
            "requestedTime.in=" + UPDATED_REQUESTED_TIME
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedTime is not null
        defaultWaitingListFiltering("requestedTime.specified=true", "requestedTime.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedTime contains
        defaultWaitingListFiltering("requestedTime.contains=" + DEFAULT_REQUESTED_TIME, "requestedTime.contains=" + UPDATED_REQUESTED_TIME);
    }

    @Test
    @Transactional
    void getAllWaitingListsByRequestedTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where requestedTime does not contain
        defaultWaitingListFiltering(
            "requestedTime.doesNotContain=" + UPDATED_REQUESTED_TIME,
            "requestedTime.doesNotContain=" + DEFAULT_REQUESTED_TIME
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize equals to
        defaultWaitingListFiltering("partySize.equals=" + DEFAULT_PARTY_SIZE, "partySize.equals=" + UPDATED_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize in
        defaultWaitingListFiltering("partySize.in=" + DEFAULT_PARTY_SIZE + "," + UPDATED_PARTY_SIZE, "partySize.in=" + UPDATED_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize is not null
        defaultWaitingListFiltering("partySize.specified=true", "partySize.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize is greater than or equal to
        defaultWaitingListFiltering(
            "partySize.greaterThanOrEqual=" + DEFAULT_PARTY_SIZE,
            "partySize.greaterThanOrEqual=" + UPDATED_PARTY_SIZE
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize is less than or equal to
        defaultWaitingListFiltering("partySize.lessThanOrEqual=" + DEFAULT_PARTY_SIZE, "partySize.lessThanOrEqual=" + SMALLER_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize is less than
        defaultWaitingListFiltering("partySize.lessThan=" + UPDATED_PARTY_SIZE, "partySize.lessThan=" + DEFAULT_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByPartySizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where partySize is greater than
        defaultWaitingListFiltering("partySize.greaterThan=" + SMALLER_PARTY_SIZE, "partySize.greaterThan=" + DEFAULT_PARTY_SIZE);
    }

    @Test
    @Transactional
    void getAllWaitingListsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where notes equals to
        defaultWaitingListFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllWaitingListsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where notes in
        defaultWaitingListFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllWaitingListsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where notes is not null
        defaultWaitingListFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where notes contains
        defaultWaitingListFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllWaitingListsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where notes does not contain
        defaultWaitingListFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllWaitingListsByIsNotifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where isNotified equals to
        defaultWaitingListFiltering("isNotified.equals=" + DEFAULT_IS_NOTIFIED, "isNotified.equals=" + UPDATED_IS_NOTIFIED);
    }

    @Test
    @Transactional
    void getAllWaitingListsByIsNotifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where isNotified in
        defaultWaitingListFiltering(
            "isNotified.in=" + DEFAULT_IS_NOTIFIED + "," + UPDATED_IS_NOTIFIED,
            "isNotified.in=" + UPDATED_IS_NOTIFIED
        );
    }

    @Test
    @Transactional
    void getAllWaitingListsByIsNotifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where isNotified is not null
        defaultWaitingListFiltering("isNotified.specified=true", "isNotified.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where expiresAt equals to
        defaultWaitingListFiltering("expiresAt.equals=" + DEFAULT_EXPIRES_AT, "expiresAt.equals=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllWaitingListsByExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where expiresAt in
        defaultWaitingListFiltering("expiresAt.in=" + DEFAULT_EXPIRES_AT + "," + UPDATED_EXPIRES_AT, "expiresAt.in=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllWaitingListsByExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where expiresAt is not null
        defaultWaitingListFiltering("expiresAt.specified=true", "expiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where createdAt equals to
        defaultWaitingListFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWaitingListsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where createdAt in
        defaultWaitingListFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWaitingListsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        // Get all the waitingListList where createdAt is not null
        defaultWaitingListFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitingListsByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            waitingListRepository.saveAndFlush(waitingList);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        waitingList.setLocation(location);
        waitingListRepository.saveAndFlush(waitingList);
        Long locationId = location.getId();
        // Get all the waitingListList where location equals to locationId
        defaultWaitingListShouldBeFound("locationId.equals=" + locationId);

        // Get all the waitingListList where location equals to (locationId + 1)
        defaultWaitingListShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    @Test
    @Transactional
    void getAllWaitingListsByClientIsEqualToSomething() throws Exception {
        User client;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            waitingListRepository.saveAndFlush(waitingList);
            client = UserResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(client);
        em.flush();
        waitingList.setClient(client);
        waitingListRepository.saveAndFlush(waitingList);
        Long clientId = client.getId();
        // Get all the waitingListList where client equals to clientId
        defaultWaitingListShouldBeFound("clientId.equals=" + clientId);

        // Get all the waitingListList where client equals to (clientId + 1)
        defaultWaitingListShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllWaitingListsByRoomIsEqualToSomething() throws Exception {
        DiningRoom room;
        if (TestUtil.findAll(em, DiningRoom.class).isEmpty()) {
            waitingListRepository.saveAndFlush(waitingList);
            room = DiningRoomResourceIT.createEntity(em);
        } else {
            room = TestUtil.findAll(em, DiningRoom.class).get(0);
        }
        em.persist(room);
        em.flush();
        waitingList.setRoom(room);
        waitingListRepository.saveAndFlush(waitingList);
        Long roomId = room.getId();
        // Get all the waitingListList where room equals to roomId
        defaultWaitingListShouldBeFound("roomId.equals=" + roomId);

        // Get all the waitingListList where room equals to (roomId + 1)
        defaultWaitingListShouldNotBeFound("roomId.equals=" + (roomId + 1));
    }

    private void defaultWaitingListFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWaitingListShouldBeFound(shouldBeFound);
        defaultWaitingListShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWaitingListShouldBeFound(String filter) throws Exception {
        restWaitingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waitingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].requestedDate").value(hasItem(DEFAULT_REQUESTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].requestedTime").value(hasItem(DEFAULT_REQUESTED_TIME)))
            .andExpect(jsonPath("$.[*].partySize").value(hasItem(DEFAULT_PARTY_SIZE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].isNotified").value(hasItem(DEFAULT_IS_NOTIFIED)))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restWaitingListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWaitingListShouldNotBeFound(String filter) throws Exception {
        restWaitingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWaitingListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWaitingList() throws Exception {
        // Get the waitingList
        restWaitingListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWaitingList() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waitingList
        WaitingList updatedWaitingList = waitingListRepository.findById(waitingList.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWaitingList are not directly saved in db
        em.detach(updatedWaitingList);
        updatedWaitingList
            .requestedDate(UPDATED_REQUESTED_DATE)
            .requestedTime(UPDATED_REQUESTED_TIME)
            .partySize(UPDATED_PARTY_SIZE)
            .notes(UPDATED_NOTES)
            .isNotified(UPDATED_IS_NOTIFIED)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdAt(UPDATED_CREATED_AT);
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(updatedWaitingList);

        restWaitingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, waitingListDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waitingListDTO))
            )
            .andExpect(status().isOk());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWaitingListToMatchAllProperties(updatedWaitingList);
    }

    @Test
    @Transactional
    void putNonExistingWaitingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitingList.setId(longCount.incrementAndGet());

        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaitingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, waitingListDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waitingListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWaitingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitingList.setId(longCount.incrementAndGet());

        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waitingListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWaitingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitingList.setId(longCount.incrementAndGet());

        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitingListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWaitingListWithPatch() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waitingList using partial update
        WaitingList partialUpdatedWaitingList = new WaitingList();
        partialUpdatedWaitingList.setId(waitingList.getId());

        partialUpdatedWaitingList.partySize(UPDATED_PARTY_SIZE).createdAt(UPDATED_CREATED_AT);

        restWaitingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaitingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWaitingList))
            )
            .andExpect(status().isOk());

        // Validate the WaitingList in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWaitingListUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWaitingList, waitingList),
            getPersistedWaitingList(waitingList)
        );
    }

    @Test
    @Transactional
    void fullUpdateWaitingListWithPatch() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waitingList using partial update
        WaitingList partialUpdatedWaitingList = new WaitingList();
        partialUpdatedWaitingList.setId(waitingList.getId());

        partialUpdatedWaitingList
            .requestedDate(UPDATED_REQUESTED_DATE)
            .requestedTime(UPDATED_REQUESTED_TIME)
            .partySize(UPDATED_PARTY_SIZE)
            .notes(UPDATED_NOTES)
            .isNotified(UPDATED_IS_NOTIFIED)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdAt(UPDATED_CREATED_AT);

        restWaitingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaitingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWaitingList))
            )
            .andExpect(status().isOk());

        // Validate the WaitingList in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWaitingListUpdatableFieldsEquals(partialUpdatedWaitingList, getPersistedWaitingList(partialUpdatedWaitingList));
    }

    @Test
    @Transactional
    void patchNonExistingWaitingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitingList.setId(longCount.incrementAndGet());

        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaitingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, waitingListDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(waitingListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWaitingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitingList.setId(longCount.incrementAndGet());

        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(waitingListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWaitingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitingList.setId(longCount.incrementAndGet());

        // Create the WaitingList
        WaitingListDTO waitingListDTO = waitingListMapper.toDto(waitingList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitingListMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(waitingListDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WaitingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWaitingList() throws Exception {
        // Initialize the database
        insertedWaitingList = waitingListRepository.saveAndFlush(waitingList);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the waitingList
        restWaitingListMockMvc
            .perform(delete(ENTITY_API_URL_ID, waitingList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return waitingListRepository.count();
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

    protected WaitingList getPersistedWaitingList(WaitingList waitingList) {
        return waitingListRepository.findById(waitingList.getId()).orElseThrow();
    }

    protected void assertPersistedWaitingListToMatchAllProperties(WaitingList expectedWaitingList) {
        assertWaitingListAllPropertiesEquals(expectedWaitingList, getPersistedWaitingList(expectedWaitingList));
    }

    protected void assertPersistedWaitingListToMatchUpdatableProperties(WaitingList expectedWaitingList) {
        assertWaitingListAllUpdatablePropertiesEquals(expectedWaitingList, getPersistedWaitingList(expectedWaitingList));
    }
}
