package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.ReviewAsserts.*;
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
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.Review;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.repository.ReviewRepository;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.service.ReviewService;
import md.utm.restaurant.service.dto.ReviewDTO;
import md.utm.restaurant.service.mapper.ReviewMapper;
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
 * Integration tests for the {@link ReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReviewResourceIT {

    private static final Integer DEFAULT_OVERALL_RATING = 1;
    private static final Integer UPDATED_OVERALL_RATING = 2;
    private static final Integer SMALLER_OVERALL_RATING = 1 - 1;

    private static final Integer DEFAULT_FOOD_RATING = 1;
    private static final Integer UPDATED_FOOD_RATING = 2;
    private static final Integer SMALLER_FOOD_RATING = 1 - 1;

    private static final Integer DEFAULT_SERVICE_RATING = 1;
    private static final Integer UPDATED_SERVICE_RATING = 2;
    private static final Integer SMALLER_SERVICE_RATING = 1 - 1;

    private static final Integer DEFAULT_AMBIENCE_RATING = 1;
    private static final Integer UPDATED_AMBIENCE_RATING = 2;
    private static final Integer SMALLER_AMBIENCE_RATING = 1 - 1;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_APPROVED = false;
    private static final Boolean UPDATED_IS_APPROVED = true;

    private static final Boolean DEFAULT_IS_ANONYMOUS = false;
    private static final Boolean UPDATED_IS_ANONYMOUS = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepositoryMock;

    @Autowired
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewService reviewServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReviewMockMvc;

    private Review review;

    private Review insertedReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createEntity() {
        return new Review()
            .overallRating(DEFAULT_OVERALL_RATING)
            .foodRating(DEFAULT_FOOD_RATING)
            .serviceRating(DEFAULT_SERVICE_RATING)
            .ambienceRating(DEFAULT_AMBIENCE_RATING)
            .comment(DEFAULT_COMMENT)
            .isApproved(DEFAULT_IS_APPROVED)
            .isAnonymous(DEFAULT_IS_ANONYMOUS)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createUpdatedEntity() {
        return new Review()
            .overallRating(UPDATED_OVERALL_RATING)
            .foodRating(UPDATED_FOOD_RATING)
            .serviceRating(UPDATED_SERVICE_RATING)
            .ambienceRating(UPDATED_AMBIENCE_RATING)
            .comment(UPDATED_COMMENT)
            .isApproved(UPDATED_IS_APPROVED)
            .isAnonymous(UPDATED_IS_ANONYMOUS)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        review = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReview != null) {
            reviewRepository.delete(insertedReview);
            insertedReview = null;
        }
    }

    @Test
    @Transactional
    void createReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);
        var returnedReviewDTO = om.readValue(
            restReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReviewDTO.class
        );

        // Validate the Review in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReview = reviewMapper.toEntity(returnedReviewDTO);
        assertReviewUpdatableFieldsEquals(returnedReview, getPersistedReview(returnedReview));

        insertedReview = returnedReview;
    }

    @Test
    @Transactional
    void createReviewWithExistingId() throws Exception {
        // Create the Review with an existing ID
        review.setId(1L);
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOverallRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        review.setOverallRating(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsApprovedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        review.setIsApproved(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsAnonymousIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        review.setIsAnonymous(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        review.setCreatedAt(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReviews() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].overallRating").value(hasItem(DEFAULT_OVERALL_RATING)))
            .andExpect(jsonPath("$.[*].foodRating").value(hasItem(DEFAULT_FOOD_RATING)))
            .andExpect(jsonPath("$.[*].serviceRating").value(hasItem(DEFAULT_SERVICE_RATING)))
            .andExpect(jsonPath("$.[*].ambienceRating").value(hasItem(DEFAULT_AMBIENCE_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].isApproved").value(hasItem(DEFAULT_IS_APPROVED)))
            .andExpect(jsonPath("$.[*].isAnonymous").value(hasItem(DEFAULT_IS_ANONYMOUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReviewsWithEagerRelationshipsIsEnabled() throws Exception {
        when(reviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reviewServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReviewsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reviewRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, review.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(review.getId().intValue()))
            .andExpect(jsonPath("$.overallRating").value(DEFAULT_OVERALL_RATING))
            .andExpect(jsonPath("$.foodRating").value(DEFAULT_FOOD_RATING))
            .andExpect(jsonPath("$.serviceRating").value(DEFAULT_SERVICE_RATING))
            .andExpect(jsonPath("$.ambienceRating").value(DEFAULT_AMBIENCE_RATING))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.isApproved").value(DEFAULT_IS_APPROVED))
            .andExpect(jsonPath("$.isAnonymous").value(DEFAULT_IS_ANONYMOUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getReviewsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        Long id = review.getId();

        defaultReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating equals to
        defaultReviewFiltering("overallRating.equals=" + DEFAULT_OVERALL_RATING, "overallRating.equals=" + UPDATED_OVERALL_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating in
        defaultReviewFiltering(
            "overallRating.in=" + DEFAULT_OVERALL_RATING + "," + UPDATED_OVERALL_RATING,
            "overallRating.in=" + UPDATED_OVERALL_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating is not null
        defaultReviewFiltering("overallRating.specified=true", "overallRating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating is greater than or equal to
        defaultReviewFiltering(
            "overallRating.greaterThanOrEqual=" + DEFAULT_OVERALL_RATING,
            "overallRating.greaterThanOrEqual=" + (DEFAULT_OVERALL_RATING + 1)
        );
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating is less than or equal to
        defaultReviewFiltering(
            "overallRating.lessThanOrEqual=" + DEFAULT_OVERALL_RATING,
            "overallRating.lessThanOrEqual=" + SMALLER_OVERALL_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating is less than
        defaultReviewFiltering(
            "overallRating.lessThan=" + (DEFAULT_OVERALL_RATING + 1),
            "overallRating.lessThan=" + DEFAULT_OVERALL_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByOverallRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where overallRating is greater than
        defaultReviewFiltering(
            "overallRating.greaterThan=" + SMALLER_OVERALL_RATING,
            "overallRating.greaterThan=" + DEFAULT_OVERALL_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating equals to
        defaultReviewFiltering("foodRating.equals=" + DEFAULT_FOOD_RATING, "foodRating.equals=" + UPDATED_FOOD_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating in
        defaultReviewFiltering("foodRating.in=" + DEFAULT_FOOD_RATING + "," + UPDATED_FOOD_RATING, "foodRating.in=" + UPDATED_FOOD_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating is not null
        defaultReviewFiltering("foodRating.specified=true", "foodRating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating is greater than or equal to
        defaultReviewFiltering(
            "foodRating.greaterThanOrEqual=" + DEFAULT_FOOD_RATING,
            "foodRating.greaterThanOrEqual=" + (DEFAULT_FOOD_RATING + 1)
        );
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating is less than or equal to
        defaultReviewFiltering("foodRating.lessThanOrEqual=" + DEFAULT_FOOD_RATING, "foodRating.lessThanOrEqual=" + SMALLER_FOOD_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating is less than
        defaultReviewFiltering("foodRating.lessThan=" + (DEFAULT_FOOD_RATING + 1), "foodRating.lessThan=" + DEFAULT_FOOD_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByFoodRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where foodRating is greater than
        defaultReviewFiltering("foodRating.greaterThan=" + SMALLER_FOOD_RATING, "foodRating.greaterThan=" + DEFAULT_FOOD_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating equals to
        defaultReviewFiltering("serviceRating.equals=" + DEFAULT_SERVICE_RATING, "serviceRating.equals=" + UPDATED_SERVICE_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating in
        defaultReviewFiltering(
            "serviceRating.in=" + DEFAULT_SERVICE_RATING + "," + UPDATED_SERVICE_RATING,
            "serviceRating.in=" + UPDATED_SERVICE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating is not null
        defaultReviewFiltering("serviceRating.specified=true", "serviceRating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating is greater than or equal to
        defaultReviewFiltering(
            "serviceRating.greaterThanOrEqual=" + DEFAULT_SERVICE_RATING,
            "serviceRating.greaterThanOrEqual=" + (DEFAULT_SERVICE_RATING + 1)
        );
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating is less than or equal to
        defaultReviewFiltering(
            "serviceRating.lessThanOrEqual=" + DEFAULT_SERVICE_RATING,
            "serviceRating.lessThanOrEqual=" + SMALLER_SERVICE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating is less than
        defaultReviewFiltering(
            "serviceRating.lessThan=" + (DEFAULT_SERVICE_RATING + 1),
            "serviceRating.lessThan=" + DEFAULT_SERVICE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByServiceRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where serviceRating is greater than
        defaultReviewFiltering(
            "serviceRating.greaterThan=" + SMALLER_SERVICE_RATING,
            "serviceRating.greaterThan=" + DEFAULT_SERVICE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating equals to
        defaultReviewFiltering("ambienceRating.equals=" + DEFAULT_AMBIENCE_RATING, "ambienceRating.equals=" + UPDATED_AMBIENCE_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating in
        defaultReviewFiltering(
            "ambienceRating.in=" + DEFAULT_AMBIENCE_RATING + "," + UPDATED_AMBIENCE_RATING,
            "ambienceRating.in=" + UPDATED_AMBIENCE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating is not null
        defaultReviewFiltering("ambienceRating.specified=true", "ambienceRating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating is greater than or equal to
        defaultReviewFiltering(
            "ambienceRating.greaterThanOrEqual=" + DEFAULT_AMBIENCE_RATING,
            "ambienceRating.greaterThanOrEqual=" + (DEFAULT_AMBIENCE_RATING + 1)
        );
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating is less than or equal to
        defaultReviewFiltering(
            "ambienceRating.lessThanOrEqual=" + DEFAULT_AMBIENCE_RATING,
            "ambienceRating.lessThanOrEqual=" + SMALLER_AMBIENCE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating is less than
        defaultReviewFiltering(
            "ambienceRating.lessThan=" + (DEFAULT_AMBIENCE_RATING + 1),
            "ambienceRating.lessThan=" + DEFAULT_AMBIENCE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByAmbienceRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where ambienceRating is greater than
        defaultReviewFiltering(
            "ambienceRating.greaterThan=" + SMALLER_AMBIENCE_RATING,
            "ambienceRating.greaterThan=" + DEFAULT_AMBIENCE_RATING
        );
    }

    @Test
    @Transactional
    void getAllReviewsByIsApprovedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where isApproved equals to
        defaultReviewFiltering("isApproved.equals=" + DEFAULT_IS_APPROVED, "isApproved.equals=" + UPDATED_IS_APPROVED);
    }

    @Test
    @Transactional
    void getAllReviewsByIsApprovedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where isApproved in
        defaultReviewFiltering("isApproved.in=" + DEFAULT_IS_APPROVED + "," + UPDATED_IS_APPROVED, "isApproved.in=" + UPDATED_IS_APPROVED);
    }

    @Test
    @Transactional
    void getAllReviewsByIsApprovedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where isApproved is not null
        defaultReviewFiltering("isApproved.specified=true", "isApproved.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByIsAnonymousIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where isAnonymous equals to
        defaultReviewFiltering("isAnonymous.equals=" + DEFAULT_IS_ANONYMOUS, "isAnonymous.equals=" + UPDATED_IS_ANONYMOUS);
    }

    @Test
    @Transactional
    void getAllReviewsByIsAnonymousIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where isAnonymous in
        defaultReviewFiltering(
            "isAnonymous.in=" + DEFAULT_IS_ANONYMOUS + "," + UPDATED_IS_ANONYMOUS,
            "isAnonymous.in=" + UPDATED_IS_ANONYMOUS
        );
    }

    @Test
    @Transactional
    void getAllReviewsByIsAnonymousIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where isAnonymous is not null
        defaultReviewFiltering("isAnonymous.specified=true", "isAnonymous.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where createdAt equals to
        defaultReviewFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where createdAt in
        defaultReviewFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where createdAt is not null
        defaultReviewFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            location = LocationResourceIT.createEntity(em);
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        review.setLocation(location);
        reviewRepository.saveAndFlush(review);
        Long locationId = location.getId();
        // Get all the reviewList where location equals to locationId
        defaultReviewShouldBeFound("locationId.equals=" + locationId);

        // Get all the reviewList where location equals to (locationId + 1)
        defaultReviewShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByReservationIsEqualToSomething() throws Exception {
        Reservation reservation;
        if (TestUtil.findAll(em, Reservation.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            reservation = ReservationResourceIT.createEntity();
        } else {
            reservation = TestUtil.findAll(em, Reservation.class).get(0);
        }
        em.persist(reservation);
        em.flush();
        review.setReservation(reservation);
        reviewRepository.saveAndFlush(review);
        Long reservationId = reservation.getId();
        // Get all the reviewList where reservation equals to reservationId
        defaultReviewShouldBeFound("reservationId.equals=" + reservationId);

        // Get all the reviewList where reservation equals to (reservationId + 1)
        defaultReviewShouldNotBeFound("reservationId.equals=" + (reservationId + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByClientIsEqualToSomething() throws Exception {
        User client;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            client = UserResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(client);
        em.flush();
        review.setClient(client);
        reviewRepository.saveAndFlush(review);
        Long clientId = client.getId();
        // Get all the reviewList where client equals to clientId
        defaultReviewShouldBeFound("clientId.equals=" + clientId);

        // Get all the reviewList where client equals to (clientId + 1)
        defaultReviewShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    private void defaultReviewFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReviewShouldBeFound(shouldBeFound);
        defaultReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReviewShouldBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].overallRating").value(hasItem(DEFAULT_OVERALL_RATING)))
            .andExpect(jsonPath("$.[*].foodRating").value(hasItem(DEFAULT_FOOD_RATING)))
            .andExpect(jsonPath("$.[*].serviceRating").value(hasItem(DEFAULT_SERVICE_RATING)))
            .andExpect(jsonPath("$.[*].ambienceRating").value(hasItem(DEFAULT_AMBIENCE_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].isApproved").value(hasItem(DEFAULT_IS_APPROVED)))
            .andExpect(jsonPath("$.[*].isAnonymous").value(hasItem(DEFAULT_IS_ANONYMOUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReviewShouldNotBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review
        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReview are not directly saved in db
        em.detach(updatedReview);
        updatedReview
            .overallRating(UPDATED_OVERALL_RATING)
            .foodRating(UPDATED_FOOD_RATING)
            .serviceRating(UPDATED_SERVICE_RATING)
            .ambienceRating(UPDATED_AMBIENCE_RATING)
            .comment(UPDATED_COMMENT)
            .isApproved(UPDATED_IS_APPROVED)
            .isAnonymous(UPDATED_IS_ANONYMOUS)
            .createdAt(UPDATED_CREATED_AT);
        ReviewDTO reviewDTO = reviewMapper.toDto(updatedReview);

        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReviewToMatchAllProperties(updatedReview);
    }

    @Test
    @Transactional
    void putNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview
            .serviceRating(UPDATED_SERVICE_RATING)
            .ambienceRating(UPDATED_AMBIENCE_RATING)
            .comment(UPDATED_COMMENT)
            .createdAt(UPDATED_CREATED_AT);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReview))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReview, review), getPersistedReview(review));
    }

    @Test
    @Transactional
    void fullUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview
            .overallRating(UPDATED_OVERALL_RATING)
            .foodRating(UPDATED_FOOD_RATING)
            .serviceRating(UPDATED_SERVICE_RATING)
            .ambienceRating(UPDATED_AMBIENCE_RATING)
            .comment(UPDATED_COMMENT)
            .isApproved(UPDATED_IS_APPROVED)
            .isAnonymous(UPDATED_IS_ANONYMOUS)
            .createdAt(UPDATED_CREATED_AT);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReview))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewUpdatableFieldsEquals(partialUpdatedReview, getPersistedReview(partialUpdatedReview));
    }

    @Test
    @Transactional
    void patchNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the review
        restReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, review.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reviewRepository.count();
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

    protected Review getPersistedReview(Review review) {
        return reviewRepository.findById(review.getId()).orElseThrow();
    }

    protected void assertPersistedReviewToMatchAllProperties(Review expectedReview) {
        assertReviewAllPropertiesEquals(expectedReview, getPersistedReview(expectedReview));
    }

    protected void assertPersistedReviewToMatchUpdatableProperties(Review expectedReview) {
        assertReviewAllUpdatablePropertiesEquals(expectedReview, getPersistedReview(expectedReview));
    }
}
