package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.UserProfileAsserts.*;
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
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.domain.UserProfile;
import md.utm.restaurant.domain.enumeration.UserRole;
import md.utm.restaurant.repository.UserProfileRepository;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.service.UserProfileService;
import md.utm.restaurant.service.dto.UserProfileDTO;
import md.utm.restaurant.service.mapper.UserProfileMapper;
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
 * Integration tests for the {@link UserProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserProfileResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_AVATAR_URL = "AAAAAAAAAA";
    private static final String UPDATED_AVATAR_URL = "BBBBBBBBBB";

    private static final UserRole DEFAULT_ROLE = UserRole.SUPER_ADMIN;
    private static final UserRole UPDATED_ROLE = UserRole.MANAGER;

    private static final String DEFAULT_PREFERRED_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_LANGUAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS = false;
    private static final Boolean UPDATED_RECEIVE_EMAIL_NOTIFICATIONS = true;

    private static final Boolean DEFAULT_RECEIVE_PUSH_NOTIFICATIONS = false;
    private static final Boolean UPDATED_RECEIVE_PUSH_NOTIFICATIONS = true;

    private static final Integer DEFAULT_LOYALTY_POINTS = 0;
    private static final Integer UPDATED_LOYALTY_POINTS = 1;
    private static final Integer SMALLER_LOYALTY_POINTS = 0 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepositoryMock;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Mock
    private UserProfileService userProfileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserProfileMockMvc;

    private UserProfile userProfile;

    private UserProfile insertedUserProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createEntity() {
        return new UserProfile()
            .phone(DEFAULT_PHONE)
            .avatarUrl(DEFAULT_AVATAR_URL)
            .role(DEFAULT_ROLE)
            .preferredLanguage(DEFAULT_PREFERRED_LANGUAGE)
            .receiveEmailNotifications(DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS)
            .receivePushNotifications(DEFAULT_RECEIVE_PUSH_NOTIFICATIONS)
            .loyaltyPoints(DEFAULT_LOYALTY_POINTS)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createUpdatedEntity() {
        return new UserProfile()
            .phone(UPDATED_PHONE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .role(UPDATED_ROLE)
            .preferredLanguage(UPDATED_PREFERRED_LANGUAGE)
            .receiveEmailNotifications(UPDATED_RECEIVE_EMAIL_NOTIFICATIONS)
            .receivePushNotifications(UPDATED_RECEIVE_PUSH_NOTIFICATIONS)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        userProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserProfile != null) {
            userProfileRepository.delete(insertedUserProfile);
            insertedUserProfile = null;
        }
    }

    @Test
    @Transactional
    void createUserProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);
        var returnedUserProfileDTO = om.readValue(
            restUserProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userProfileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserProfileDTO.class
        );

        // Validate the UserProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserProfile = userProfileMapper.toEntity(returnedUserProfileDTO);
        assertUserProfileUpdatableFieldsEquals(returnedUserProfile, getPersistedUserProfile(returnedUserProfile));

        insertedUserProfile = returnedUserProfile;
    }

    @Test
    @Transactional
    void createUserProfileWithExistingId() throws Exception {
        // Create the UserProfile with an existing ID
        userProfile.setId(1L);
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userProfile.setRole(null);

        // Create the UserProfile, which fails.
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        restUserProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userProfile.setCreatedAt(null);

        // Create the UserProfile, which fails.
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        restUserProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserProfiles() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList
        restUserProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].preferredLanguage").value(hasItem(DEFAULT_PREFERRED_LANGUAGE)))
            .andExpect(jsonPath("$.[*].receiveEmailNotifications").value(hasItem(DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS)))
            .andExpect(jsonPath("$.[*].receivePushNotifications").value(hasItem(DEFAULT_RECEIVE_PUSH_NOTIFICATIONS)))
            .andExpect(jsonPath("$.[*].loyaltyPoints").value(hasItem(DEFAULT_LOYALTY_POINTS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(userProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserProfile() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get the userProfile
        restUserProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, userProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userProfile.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.avatarUrl").value(DEFAULT_AVATAR_URL))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.preferredLanguage").value(DEFAULT_PREFERRED_LANGUAGE))
            .andExpect(jsonPath("$.receiveEmailNotifications").value(DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS))
            .andExpect(jsonPath("$.receivePushNotifications").value(DEFAULT_RECEIVE_PUSH_NOTIFICATIONS))
            .andExpect(jsonPath("$.loyaltyPoints").value(DEFAULT_LOYALTY_POINTS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getUserProfilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        Long id = userProfile.getId();

        defaultUserProfileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUserProfileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUserProfileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserProfilesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where phone equals to
        defaultUserProfileFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserProfilesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where phone in
        defaultUserProfileFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserProfilesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where phone is not null
        defaultUserProfileFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where phone contains
        defaultUserProfileFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserProfilesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where phone does not contain
        defaultUserProfileFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllUserProfilesByAvatarUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where avatarUrl equals to
        defaultUserProfileFiltering("avatarUrl.equals=" + DEFAULT_AVATAR_URL, "avatarUrl.equals=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllUserProfilesByAvatarUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where avatarUrl in
        defaultUserProfileFiltering("avatarUrl.in=" + DEFAULT_AVATAR_URL + "," + UPDATED_AVATAR_URL, "avatarUrl.in=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllUserProfilesByAvatarUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where avatarUrl is not null
        defaultUserProfileFiltering("avatarUrl.specified=true", "avatarUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByAvatarUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where avatarUrl contains
        defaultUserProfileFiltering("avatarUrl.contains=" + DEFAULT_AVATAR_URL, "avatarUrl.contains=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllUserProfilesByAvatarUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where avatarUrl does not contain
        defaultUserProfileFiltering("avatarUrl.doesNotContain=" + UPDATED_AVATAR_URL, "avatarUrl.doesNotContain=" + DEFAULT_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllUserProfilesByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where role equals to
        defaultUserProfileFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllUserProfilesByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where role in
        defaultUserProfileFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllUserProfilesByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where role is not null
        defaultUserProfileFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByPreferredLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where preferredLanguage equals to
        defaultUserProfileFiltering(
            "preferredLanguage.equals=" + DEFAULT_PREFERRED_LANGUAGE,
            "preferredLanguage.equals=" + UPDATED_PREFERRED_LANGUAGE
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByPreferredLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where preferredLanguage in
        defaultUserProfileFiltering(
            "preferredLanguage.in=" + DEFAULT_PREFERRED_LANGUAGE + "," + UPDATED_PREFERRED_LANGUAGE,
            "preferredLanguage.in=" + UPDATED_PREFERRED_LANGUAGE
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByPreferredLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where preferredLanguage is not null
        defaultUserProfileFiltering("preferredLanguage.specified=true", "preferredLanguage.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByPreferredLanguageContainsSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where preferredLanguage contains
        defaultUserProfileFiltering(
            "preferredLanguage.contains=" + DEFAULT_PREFERRED_LANGUAGE,
            "preferredLanguage.contains=" + UPDATED_PREFERRED_LANGUAGE
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByPreferredLanguageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where preferredLanguage does not contain
        defaultUserProfileFiltering(
            "preferredLanguage.doesNotContain=" + UPDATED_PREFERRED_LANGUAGE,
            "preferredLanguage.doesNotContain=" + DEFAULT_PREFERRED_LANGUAGE
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByReceiveEmailNotificationsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where receiveEmailNotifications equals to
        defaultUserProfileFiltering(
            "receiveEmailNotifications.equals=" + DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS,
            "receiveEmailNotifications.equals=" + UPDATED_RECEIVE_EMAIL_NOTIFICATIONS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByReceiveEmailNotificationsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where receiveEmailNotifications in
        defaultUserProfileFiltering(
            "receiveEmailNotifications.in=" + DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS + "," + UPDATED_RECEIVE_EMAIL_NOTIFICATIONS,
            "receiveEmailNotifications.in=" + UPDATED_RECEIVE_EMAIL_NOTIFICATIONS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByReceiveEmailNotificationsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where receiveEmailNotifications is not null
        defaultUserProfileFiltering("receiveEmailNotifications.specified=true", "receiveEmailNotifications.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByReceivePushNotificationsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where receivePushNotifications equals to
        defaultUserProfileFiltering(
            "receivePushNotifications.equals=" + DEFAULT_RECEIVE_PUSH_NOTIFICATIONS,
            "receivePushNotifications.equals=" + UPDATED_RECEIVE_PUSH_NOTIFICATIONS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByReceivePushNotificationsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where receivePushNotifications in
        defaultUserProfileFiltering(
            "receivePushNotifications.in=" + DEFAULT_RECEIVE_PUSH_NOTIFICATIONS + "," + UPDATED_RECEIVE_PUSH_NOTIFICATIONS,
            "receivePushNotifications.in=" + UPDATED_RECEIVE_PUSH_NOTIFICATIONS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByReceivePushNotificationsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where receivePushNotifications is not null
        defaultUserProfileFiltering("receivePushNotifications.specified=true", "receivePushNotifications.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints equals to
        defaultUserProfileFiltering("loyaltyPoints.equals=" + DEFAULT_LOYALTY_POINTS, "loyaltyPoints.equals=" + UPDATED_LOYALTY_POINTS);
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints in
        defaultUserProfileFiltering(
            "loyaltyPoints.in=" + DEFAULT_LOYALTY_POINTS + "," + UPDATED_LOYALTY_POINTS,
            "loyaltyPoints.in=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints is not null
        defaultUserProfileFiltering("loyaltyPoints.specified=true", "loyaltyPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints is greater than or equal to
        defaultUserProfileFiltering(
            "loyaltyPoints.greaterThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.greaterThanOrEqual=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints is less than or equal to
        defaultUserProfileFiltering(
            "loyaltyPoints.lessThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.lessThanOrEqual=" + SMALLER_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints is less than
        defaultUserProfileFiltering("loyaltyPoints.lessThan=" + UPDATED_LOYALTY_POINTS, "loyaltyPoints.lessThan=" + DEFAULT_LOYALTY_POINTS);
    }

    @Test
    @Transactional
    void getAllUserProfilesByLoyaltyPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where loyaltyPoints is greater than
        defaultUserProfileFiltering(
            "loyaltyPoints.greaterThan=" + SMALLER_LOYALTY_POINTS,
            "loyaltyPoints.greaterThan=" + DEFAULT_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserProfilesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where createdAt equals to
        defaultUserProfileFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserProfilesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where createdAt in
        defaultUserProfileFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserProfilesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where createdAt is not null
        defaultUserProfileFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserProfilesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userProfileRepository.saveAndFlush(userProfile);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        userProfile.setUser(user);
        userProfileRepository.saveAndFlush(userProfile);
        Long userId = user.getId();
        // Get all the userProfileList where user equals to userId
        defaultUserProfileShouldBeFound("userId.equals=" + userId);

        // Get all the userProfileList where user equals to (userId + 1)
        defaultUserProfileShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllUserProfilesByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            userProfileRepository.saveAndFlush(userProfile);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        userProfile.setLocation(location);
        userProfileRepository.saveAndFlush(userProfile);
        Long locationId = location.getId();
        // Get all the userProfileList where location equals to locationId
        defaultUserProfileShouldBeFound("locationId.equals=" + locationId);

        // Get all the userProfileList where location equals to (locationId + 1)
        defaultUserProfileShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultUserProfileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUserProfileShouldBeFound(shouldBeFound);
        defaultUserProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserProfileShouldBeFound(String filter) throws Exception {
        restUserProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].preferredLanguage").value(hasItem(DEFAULT_PREFERRED_LANGUAGE)))
            .andExpect(jsonPath("$.[*].receiveEmailNotifications").value(hasItem(DEFAULT_RECEIVE_EMAIL_NOTIFICATIONS)))
            .andExpect(jsonPath("$.[*].receivePushNotifications").value(hasItem(DEFAULT_RECEIVE_PUSH_NOTIFICATIONS)))
            .andExpect(jsonPath("$.[*].loyaltyPoints").value(hasItem(DEFAULT_LOYALTY_POINTS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restUserProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserProfileShouldNotBeFound(String filter) throws Exception {
        restUserProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserProfile() throws Exception {
        // Get the userProfile
        restUserProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserProfile() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userProfile
        UserProfile updatedUserProfile = userProfileRepository.findById(userProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserProfile are not directly saved in db
        em.detach(updatedUserProfile);
        updatedUserProfile
            .phone(UPDATED_PHONE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .role(UPDATED_ROLE)
            .preferredLanguage(UPDATED_PREFERRED_LANGUAGE)
            .receiveEmailNotifications(UPDATED_RECEIVE_EMAIL_NOTIFICATIONS)
            .receivePushNotifications(UPDATED_RECEIVE_PUSH_NOTIFICATIONS)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .createdAt(UPDATED_CREATED_AT);
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(updatedUserProfile);

        restUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserProfileToMatchAllProperties(updatedUserProfile);
    }

    @Test
    @Transactional
    void putNonExistingUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(longCount.incrementAndGet());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(longCount.incrementAndGet());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(longCount.incrementAndGet());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserProfileWithPatch() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userProfile using partial update
        UserProfile partialUpdatedUserProfile = new UserProfile();
        partialUpdatedUserProfile.setId(userProfile.getId());

        partialUpdatedUserProfile
            .phone(UPDATED_PHONE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .preferredLanguage(UPDATED_PREFERRED_LANGUAGE)
            .createdAt(UPDATED_CREATED_AT);

        restUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserProfile))
            )
            .andExpect(status().isOk());

        // Validate the UserProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserProfile, userProfile),
            getPersistedUserProfile(userProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserProfileWithPatch() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userProfile using partial update
        UserProfile partialUpdatedUserProfile = new UserProfile();
        partialUpdatedUserProfile.setId(userProfile.getId());

        partialUpdatedUserProfile
            .phone(UPDATED_PHONE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .role(UPDATED_ROLE)
            .preferredLanguage(UPDATED_PREFERRED_LANGUAGE)
            .receiveEmailNotifications(UPDATED_RECEIVE_EMAIL_NOTIFICATIONS)
            .receivePushNotifications(UPDATED_RECEIVE_PUSH_NOTIFICATIONS)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .createdAt(UPDATED_CREATED_AT);

        restUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserProfile))
            )
            .andExpect(status().isOk());

        // Validate the UserProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserProfileUpdatableFieldsEquals(partialUpdatedUserProfile, getPersistedUserProfile(partialUpdatedUserProfile));
    }

    @Test
    @Transactional
    void patchNonExistingUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(longCount.incrementAndGet());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(longCount.incrementAndGet());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(longCount.incrementAndGet());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserProfile() throws Exception {
        // Initialize the database
        insertedUserProfile = userProfileRepository.saveAndFlush(userProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userProfile
        restUserProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, userProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userProfileRepository.count();
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

    protected UserProfile getPersistedUserProfile(UserProfile userProfile) {
        return userProfileRepository.findById(userProfile.getId()).orElseThrow();
    }

    protected void assertPersistedUserProfileToMatchAllProperties(UserProfile expectedUserProfile) {
        assertUserProfileAllPropertiesEquals(expectedUserProfile, getPersistedUserProfile(expectedUserProfile));
    }

    protected void assertPersistedUserProfileToMatchUpdatableProperties(UserProfile expectedUserProfile) {
        assertUserProfileAllUpdatablePropertiesEquals(expectedUserProfile, getPersistedUserProfile(expectedUserProfile));
    }
}
