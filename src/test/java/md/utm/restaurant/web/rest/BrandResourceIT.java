package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.BrandAsserts.*;
import static md.utm.restaurant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import md.utm.restaurant.IntegrationTest;
import md.utm.restaurant.domain.Brand;
import md.utm.restaurant.repository.BrandRepository;
import md.utm.restaurant.service.dto.BrandDTO;
import md.utm.restaurant.service.mapper.BrandMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BrandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BrandResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMARY_COLOR = "AAAAAAA";
    private static final String UPDATED_PRIMARY_COLOR = "BBBBBBB";

    private static final String DEFAULT_SECONDARY_COLOR = "AAAAAAA";
    private static final String UPDATED_SECONDARY_COLOR = "BBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PHONE = "BBBBBBBBBB";

    private static final Integer DEFAULT_DEFAULT_RESERVATION_DURATION = 15;
    private static final Integer UPDATED_DEFAULT_RESERVATION_DURATION = 16;
    private static final Integer SMALLER_DEFAULT_RESERVATION_DURATION = 15 - 1;

    private static final Integer DEFAULT_MAX_ADVANCE_BOOKING_DAYS = 1;
    private static final Integer UPDATED_MAX_ADVANCE_BOOKING_DAYS = 2;
    private static final Integer SMALLER_MAX_ADVANCE_BOOKING_DAYS = 1 - 1;

    private static final Integer DEFAULT_CANCELLATION_DEADLINE_HOURS = 0;
    private static final Integer UPDATED_CANCELLATION_DEADLINE_HOURS = 1;
    private static final Integer SMALLER_CANCELLATION_DEADLINE_HOURS = 0 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/brands";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBrandMockMvc;

    private Brand brand;

    private Brand insertedBrand;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createEntity() {
        return new Brand()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .logoUrl(DEFAULT_LOGO_URL)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .primaryColor(DEFAULT_PRIMARY_COLOR)
            .secondaryColor(DEFAULT_SECONDARY_COLOR)
            .website(DEFAULT_WEBSITE)
            .contactEmail(DEFAULT_CONTACT_EMAIL)
            .contactPhone(DEFAULT_CONTACT_PHONE)
            .defaultReservationDuration(DEFAULT_DEFAULT_RESERVATION_DURATION)
            .maxAdvanceBookingDays(DEFAULT_MAX_ADVANCE_BOOKING_DAYS)
            .cancellationDeadlineHours(DEFAULT_CANCELLATION_DEADLINE_HOURS)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createUpdatedEntity() {
        return new Brand()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .logoUrl(UPDATED_LOGO_URL)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .primaryColor(UPDATED_PRIMARY_COLOR)
            .secondaryColor(UPDATED_SECONDARY_COLOR)
            .website(UPDATED_WEBSITE)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .defaultReservationDuration(UPDATED_DEFAULT_RESERVATION_DURATION)
            .maxAdvanceBookingDays(UPDATED_MAX_ADVANCE_BOOKING_DAYS)
            .cancellationDeadlineHours(UPDATED_CANCELLATION_DEADLINE_HOURS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        brand = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBrand != null) {
            brandRepository.delete(insertedBrand);
            insertedBrand = null;
        }
    }

    @Test
    @Transactional
    void createBrand() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);
        var returnedBrandDTO = om.readValue(
            restBrandMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BrandDTO.class
        );

        // Validate the Brand in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBrand = brandMapper.toEntity(returnedBrandDTO);
        assertBrandUpdatableFieldsEquals(returnedBrand, getPersistedBrand(returnedBrand));

        insertedBrand = returnedBrand;
    }

    @Test
    @Transactional
    void createBrandWithExistingId() throws Exception {
        // Create the Brand with an existing ID
        brand.setId(1L);
        BrandDTO brandDTO = brandMapper.toDto(brand);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setName(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContactEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setContactEmail(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContactPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setContactPhone(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDefaultReservationDurationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setDefaultReservationDuration(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxAdvanceBookingDaysIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setMaxAdvanceBookingDays(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCancellationDeadlineHoursIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setCancellationDeadlineHours(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setIsActive(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        brand.setCreatedAt(null);

        // Create the Brand, which fails.
        BrandDTO brandDTO = brandMapper.toDto(brand);

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBrands() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList
        restBrandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].primaryColor").value(hasItem(DEFAULT_PRIMARY_COLOR)))
            .andExpect(jsonPath("$.[*].secondaryColor").value(hasItem(DEFAULT_SECONDARY_COLOR)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].contactEmail").value(hasItem(DEFAULT_CONTACT_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPhone").value(hasItem(DEFAULT_CONTACT_PHONE)))
            .andExpect(jsonPath("$.[*].defaultReservationDuration").value(hasItem(DEFAULT_DEFAULT_RESERVATION_DURATION)))
            .andExpect(jsonPath("$.[*].maxAdvanceBookingDays").value(hasItem(DEFAULT_MAX_ADVANCE_BOOKING_DAYS)))
            .andExpect(jsonPath("$.[*].cancellationDeadlineHours").value(hasItem(DEFAULT_CANCELLATION_DEADLINE_HOURS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getBrand() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get the brand
        restBrandMockMvc
            .perform(get(ENTITY_API_URL_ID, brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(brand.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.logoUrl").value(DEFAULT_LOGO_URL))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.primaryColor").value(DEFAULT_PRIMARY_COLOR))
            .andExpect(jsonPath("$.secondaryColor").value(DEFAULT_SECONDARY_COLOR))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE))
            .andExpect(jsonPath("$.contactEmail").value(DEFAULT_CONTACT_EMAIL))
            .andExpect(jsonPath("$.contactPhone").value(DEFAULT_CONTACT_PHONE))
            .andExpect(jsonPath("$.defaultReservationDuration").value(DEFAULT_DEFAULT_RESERVATION_DURATION))
            .andExpect(jsonPath("$.maxAdvanceBookingDays").value(DEFAULT_MAX_ADVANCE_BOOKING_DAYS))
            .andExpect(jsonPath("$.cancellationDeadlineHours").value(DEFAULT_CANCELLATION_DEADLINE_HOURS))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getBrandsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        Long id = brand.getId();

        defaultBrandFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBrandFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBrandFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBrandsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where name equals to
        defaultBrandFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBrandsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where name in
        defaultBrandFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBrandsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where name is not null
        defaultBrandFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where name contains
        defaultBrandFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBrandsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where name does not contain
        defaultBrandFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllBrandsByLogoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where logoUrl equals to
        defaultBrandFiltering("logoUrl.equals=" + DEFAULT_LOGO_URL, "logoUrl.equals=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllBrandsByLogoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where logoUrl in
        defaultBrandFiltering("logoUrl.in=" + DEFAULT_LOGO_URL + "," + UPDATED_LOGO_URL, "logoUrl.in=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllBrandsByLogoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where logoUrl is not null
        defaultBrandFiltering("logoUrl.specified=true", "logoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByLogoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where logoUrl contains
        defaultBrandFiltering("logoUrl.contains=" + DEFAULT_LOGO_URL, "logoUrl.contains=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllBrandsByLogoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where logoUrl does not contain
        defaultBrandFiltering("logoUrl.doesNotContain=" + UPDATED_LOGO_URL, "logoUrl.doesNotContain=" + DEFAULT_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllBrandsByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where coverImageUrl equals to
        defaultBrandFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBrandsByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where coverImageUrl in
        defaultBrandFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where coverImageUrl is not null
        defaultBrandFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where coverImageUrl contains
        defaultBrandFiltering("coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBrandsByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where coverImageUrl does not contain
        defaultBrandFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBrandsByPrimaryColorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where primaryColor equals to
        defaultBrandFiltering("primaryColor.equals=" + DEFAULT_PRIMARY_COLOR, "primaryColor.equals=" + UPDATED_PRIMARY_COLOR);
    }

    @Test
    @Transactional
    void getAllBrandsByPrimaryColorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where primaryColor in
        defaultBrandFiltering(
            "primaryColor.in=" + DEFAULT_PRIMARY_COLOR + "," + UPDATED_PRIMARY_COLOR,
            "primaryColor.in=" + UPDATED_PRIMARY_COLOR
        );
    }

    @Test
    @Transactional
    void getAllBrandsByPrimaryColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where primaryColor is not null
        defaultBrandFiltering("primaryColor.specified=true", "primaryColor.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByPrimaryColorContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where primaryColor contains
        defaultBrandFiltering("primaryColor.contains=" + DEFAULT_PRIMARY_COLOR, "primaryColor.contains=" + UPDATED_PRIMARY_COLOR);
    }

    @Test
    @Transactional
    void getAllBrandsByPrimaryColorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where primaryColor does not contain
        defaultBrandFiltering(
            "primaryColor.doesNotContain=" + UPDATED_PRIMARY_COLOR,
            "primaryColor.doesNotContain=" + DEFAULT_PRIMARY_COLOR
        );
    }

    @Test
    @Transactional
    void getAllBrandsBySecondaryColorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where secondaryColor equals to
        defaultBrandFiltering("secondaryColor.equals=" + DEFAULT_SECONDARY_COLOR, "secondaryColor.equals=" + UPDATED_SECONDARY_COLOR);
    }

    @Test
    @Transactional
    void getAllBrandsBySecondaryColorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where secondaryColor in
        defaultBrandFiltering(
            "secondaryColor.in=" + DEFAULT_SECONDARY_COLOR + "," + UPDATED_SECONDARY_COLOR,
            "secondaryColor.in=" + UPDATED_SECONDARY_COLOR
        );
    }

    @Test
    @Transactional
    void getAllBrandsBySecondaryColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where secondaryColor is not null
        defaultBrandFiltering("secondaryColor.specified=true", "secondaryColor.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsBySecondaryColorContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where secondaryColor contains
        defaultBrandFiltering("secondaryColor.contains=" + DEFAULT_SECONDARY_COLOR, "secondaryColor.contains=" + UPDATED_SECONDARY_COLOR);
    }

    @Test
    @Transactional
    void getAllBrandsBySecondaryColorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where secondaryColor does not contain
        defaultBrandFiltering(
            "secondaryColor.doesNotContain=" + UPDATED_SECONDARY_COLOR,
            "secondaryColor.doesNotContain=" + DEFAULT_SECONDARY_COLOR
        );
    }

    @Test
    @Transactional
    void getAllBrandsByWebsiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where website equals to
        defaultBrandFiltering("website.equals=" + DEFAULT_WEBSITE, "website.equals=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllBrandsByWebsiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where website in
        defaultBrandFiltering("website.in=" + DEFAULT_WEBSITE + "," + UPDATED_WEBSITE, "website.in=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllBrandsByWebsiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where website is not null
        defaultBrandFiltering("website.specified=true", "website.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByWebsiteContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where website contains
        defaultBrandFiltering("website.contains=" + DEFAULT_WEBSITE, "website.contains=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllBrandsByWebsiteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where website does not contain
        defaultBrandFiltering("website.doesNotContain=" + UPDATED_WEBSITE, "website.doesNotContain=" + DEFAULT_WEBSITE);
    }

    @Test
    @Transactional
    void getAllBrandsByContactEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactEmail equals to
        defaultBrandFiltering("contactEmail.equals=" + DEFAULT_CONTACT_EMAIL, "contactEmail.equals=" + UPDATED_CONTACT_EMAIL);
    }

    @Test
    @Transactional
    void getAllBrandsByContactEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactEmail in
        defaultBrandFiltering(
            "contactEmail.in=" + DEFAULT_CONTACT_EMAIL + "," + UPDATED_CONTACT_EMAIL,
            "contactEmail.in=" + UPDATED_CONTACT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllBrandsByContactEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactEmail is not null
        defaultBrandFiltering("contactEmail.specified=true", "contactEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByContactEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactEmail contains
        defaultBrandFiltering("contactEmail.contains=" + DEFAULT_CONTACT_EMAIL, "contactEmail.contains=" + UPDATED_CONTACT_EMAIL);
    }

    @Test
    @Transactional
    void getAllBrandsByContactEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactEmail does not contain
        defaultBrandFiltering(
            "contactEmail.doesNotContain=" + UPDATED_CONTACT_EMAIL,
            "contactEmail.doesNotContain=" + DEFAULT_CONTACT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllBrandsByContactPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactPhone equals to
        defaultBrandFiltering("contactPhone.equals=" + DEFAULT_CONTACT_PHONE, "contactPhone.equals=" + UPDATED_CONTACT_PHONE);
    }

    @Test
    @Transactional
    void getAllBrandsByContactPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactPhone in
        defaultBrandFiltering(
            "contactPhone.in=" + DEFAULT_CONTACT_PHONE + "," + UPDATED_CONTACT_PHONE,
            "contactPhone.in=" + UPDATED_CONTACT_PHONE
        );
    }

    @Test
    @Transactional
    void getAllBrandsByContactPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactPhone is not null
        defaultBrandFiltering("contactPhone.specified=true", "contactPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByContactPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactPhone contains
        defaultBrandFiltering("contactPhone.contains=" + DEFAULT_CONTACT_PHONE, "contactPhone.contains=" + UPDATED_CONTACT_PHONE);
    }

    @Test
    @Transactional
    void getAllBrandsByContactPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where contactPhone does not contain
        defaultBrandFiltering(
            "contactPhone.doesNotContain=" + UPDATED_CONTACT_PHONE,
            "contactPhone.doesNotContain=" + DEFAULT_CONTACT_PHONE
        );
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration equals to
        defaultBrandFiltering(
            "defaultReservationDuration.equals=" + DEFAULT_DEFAULT_RESERVATION_DURATION,
            "defaultReservationDuration.equals=" + UPDATED_DEFAULT_RESERVATION_DURATION
        );
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration in
        defaultBrandFiltering(
            "defaultReservationDuration.in=" + DEFAULT_DEFAULT_RESERVATION_DURATION + "," + UPDATED_DEFAULT_RESERVATION_DURATION,
            "defaultReservationDuration.in=" + UPDATED_DEFAULT_RESERVATION_DURATION
        );
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration is not null
        defaultBrandFiltering("defaultReservationDuration.specified=true", "defaultReservationDuration.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration is greater than or equal to
        defaultBrandFiltering(
            "defaultReservationDuration.greaterThanOrEqual=" + DEFAULT_DEFAULT_RESERVATION_DURATION,
            "defaultReservationDuration.greaterThanOrEqual=" + (DEFAULT_DEFAULT_RESERVATION_DURATION + 1)
        );
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration is less than or equal to
        defaultBrandFiltering(
            "defaultReservationDuration.lessThanOrEqual=" + DEFAULT_DEFAULT_RESERVATION_DURATION,
            "defaultReservationDuration.lessThanOrEqual=" + SMALLER_DEFAULT_RESERVATION_DURATION
        );
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration is less than
        defaultBrandFiltering(
            "defaultReservationDuration.lessThan=" + (DEFAULT_DEFAULT_RESERVATION_DURATION + 1),
            "defaultReservationDuration.lessThan=" + DEFAULT_DEFAULT_RESERVATION_DURATION
        );
    }

    @Test
    @Transactional
    void getAllBrandsByDefaultReservationDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where defaultReservationDuration is greater than
        defaultBrandFiltering(
            "defaultReservationDuration.greaterThan=" + SMALLER_DEFAULT_RESERVATION_DURATION,
            "defaultReservationDuration.greaterThan=" + DEFAULT_DEFAULT_RESERVATION_DURATION
        );
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays equals to
        defaultBrandFiltering(
            "maxAdvanceBookingDays.equals=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS,
            "maxAdvanceBookingDays.equals=" + UPDATED_MAX_ADVANCE_BOOKING_DAYS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays in
        defaultBrandFiltering(
            "maxAdvanceBookingDays.in=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS + "," + UPDATED_MAX_ADVANCE_BOOKING_DAYS,
            "maxAdvanceBookingDays.in=" + UPDATED_MAX_ADVANCE_BOOKING_DAYS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays is not null
        defaultBrandFiltering("maxAdvanceBookingDays.specified=true", "maxAdvanceBookingDays.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays is greater than or equal to
        defaultBrandFiltering(
            "maxAdvanceBookingDays.greaterThanOrEqual=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS,
            "maxAdvanceBookingDays.greaterThanOrEqual=" + (DEFAULT_MAX_ADVANCE_BOOKING_DAYS + 1)
        );
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays is less than or equal to
        defaultBrandFiltering(
            "maxAdvanceBookingDays.lessThanOrEqual=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS,
            "maxAdvanceBookingDays.lessThanOrEqual=" + SMALLER_MAX_ADVANCE_BOOKING_DAYS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays is less than
        defaultBrandFiltering(
            "maxAdvanceBookingDays.lessThan=" + (DEFAULT_MAX_ADVANCE_BOOKING_DAYS + 1),
            "maxAdvanceBookingDays.lessThan=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByMaxAdvanceBookingDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where maxAdvanceBookingDays is greater than
        defaultBrandFiltering(
            "maxAdvanceBookingDays.greaterThan=" + SMALLER_MAX_ADVANCE_BOOKING_DAYS,
            "maxAdvanceBookingDays.greaterThan=" + DEFAULT_MAX_ADVANCE_BOOKING_DAYS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours equals to
        defaultBrandFiltering(
            "cancellationDeadlineHours.equals=" + DEFAULT_CANCELLATION_DEADLINE_HOURS,
            "cancellationDeadlineHours.equals=" + UPDATED_CANCELLATION_DEADLINE_HOURS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours in
        defaultBrandFiltering(
            "cancellationDeadlineHours.in=" + DEFAULT_CANCELLATION_DEADLINE_HOURS + "," + UPDATED_CANCELLATION_DEADLINE_HOURS,
            "cancellationDeadlineHours.in=" + UPDATED_CANCELLATION_DEADLINE_HOURS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours is not null
        defaultBrandFiltering("cancellationDeadlineHours.specified=true", "cancellationDeadlineHours.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours is greater than or equal to
        defaultBrandFiltering(
            "cancellationDeadlineHours.greaterThanOrEqual=" + DEFAULT_CANCELLATION_DEADLINE_HOURS,
            "cancellationDeadlineHours.greaterThanOrEqual=" + (DEFAULT_CANCELLATION_DEADLINE_HOURS + 1)
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours is less than or equal to
        defaultBrandFiltering(
            "cancellationDeadlineHours.lessThanOrEqual=" + DEFAULT_CANCELLATION_DEADLINE_HOURS,
            "cancellationDeadlineHours.lessThanOrEqual=" + SMALLER_CANCELLATION_DEADLINE_HOURS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours is less than
        defaultBrandFiltering(
            "cancellationDeadlineHours.lessThan=" + (DEFAULT_CANCELLATION_DEADLINE_HOURS + 1),
            "cancellationDeadlineHours.lessThan=" + DEFAULT_CANCELLATION_DEADLINE_HOURS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByCancellationDeadlineHoursIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where cancellationDeadlineHours is greater than
        defaultBrandFiltering(
            "cancellationDeadlineHours.greaterThan=" + SMALLER_CANCELLATION_DEADLINE_HOURS,
            "cancellationDeadlineHours.greaterThan=" + DEFAULT_CANCELLATION_DEADLINE_HOURS
        );
    }

    @Test
    @Transactional
    void getAllBrandsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where isActive equals to
        defaultBrandFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllBrandsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where isActive in
        defaultBrandFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllBrandsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where isActive is not null
        defaultBrandFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllBrandsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where createdAt equals to
        defaultBrandFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBrandsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where createdAt in
        defaultBrandFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBrandsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        // Get all the brandList where createdAt is not null
        defaultBrandFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultBrandFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBrandShouldBeFound(shouldBeFound);
        defaultBrandShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBrandShouldBeFound(String filter) throws Exception {
        restBrandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].primaryColor").value(hasItem(DEFAULT_PRIMARY_COLOR)))
            .andExpect(jsonPath("$.[*].secondaryColor").value(hasItem(DEFAULT_SECONDARY_COLOR)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].contactEmail").value(hasItem(DEFAULT_CONTACT_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPhone").value(hasItem(DEFAULT_CONTACT_PHONE)))
            .andExpect(jsonPath("$.[*].defaultReservationDuration").value(hasItem(DEFAULT_DEFAULT_RESERVATION_DURATION)))
            .andExpect(jsonPath("$.[*].maxAdvanceBookingDays").value(hasItem(DEFAULT_MAX_ADVANCE_BOOKING_DAYS)))
            .andExpect(jsonPath("$.[*].cancellationDeadlineHours").value(hasItem(DEFAULT_CANCELLATION_DEADLINE_HOURS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restBrandMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBrandShouldNotBeFound(String filter) throws Exception {
        restBrandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBrandMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBrand() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the brand
        Brand updatedBrand = brandRepository.findById(brand.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBrand are not directly saved in db
        em.detach(updatedBrand);
        updatedBrand
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .logoUrl(UPDATED_LOGO_URL)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .primaryColor(UPDATED_PRIMARY_COLOR)
            .secondaryColor(UPDATED_SECONDARY_COLOR)
            .website(UPDATED_WEBSITE)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .defaultReservationDuration(UPDATED_DEFAULT_RESERVATION_DURATION)
            .maxAdvanceBookingDays(UPDATED_MAX_ADVANCE_BOOKING_DAYS)
            .cancellationDeadlineHours(UPDATED_CANCELLATION_DEADLINE_HOURS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
        BrandDTO brandDTO = brandMapper.toDto(updatedBrand);

        restBrandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brandDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO))
            )
            .andExpect(status().isOk());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBrandToMatchAllProperties(updatedBrand);
    }

    @Test
    @Transactional
    void putNonExistingBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        brand.setId(longCount.incrementAndGet());

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brandDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        brand.setId(longCount.incrementAndGet());

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(brandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        brand.setId(longCount.incrementAndGet());

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBrandWithPatch() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the brand using partial update
        Brand partialUpdatedBrand = new Brand();
        partialUpdatedBrand.setId(brand.getId());

        partialUpdatedBrand
            .name(UPDATED_NAME)
            .logoUrl(UPDATED_LOGO_URL)
            .primaryColor(UPDATED_PRIMARY_COLOR)
            .secondaryColor(UPDATED_SECONDARY_COLOR)
            .website(UPDATED_WEBSITE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);

        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrand.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBrand))
            )
            .andExpect(status().isOk());

        // Validate the Brand in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBrandUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBrand, brand), getPersistedBrand(brand));
    }

    @Test
    @Transactional
    void fullUpdateBrandWithPatch() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the brand using partial update
        Brand partialUpdatedBrand = new Brand();
        partialUpdatedBrand.setId(brand.getId());

        partialUpdatedBrand
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .logoUrl(UPDATED_LOGO_URL)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .primaryColor(UPDATED_PRIMARY_COLOR)
            .secondaryColor(UPDATED_SECONDARY_COLOR)
            .website(UPDATED_WEBSITE)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .defaultReservationDuration(UPDATED_DEFAULT_RESERVATION_DURATION)
            .maxAdvanceBookingDays(UPDATED_MAX_ADVANCE_BOOKING_DAYS)
            .cancellationDeadlineHours(UPDATED_CANCELLATION_DEADLINE_HOURS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);

        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrand.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBrand))
            )
            .andExpect(status().isOk());

        // Validate the Brand in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBrandUpdatableFieldsEquals(partialUpdatedBrand, getPersistedBrand(partialUpdatedBrand));
    }

    @Test
    @Transactional
    void patchNonExistingBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        brand.setId(longCount.incrementAndGet());

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, brandDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(brandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        brand.setId(longCount.incrementAndGet());

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(brandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        brand.setId(longCount.incrementAndGet());

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(brandDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Brand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBrand() throws Exception {
        // Initialize the database
        insertedBrand = brandRepository.saveAndFlush(brand);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the brand
        restBrandMockMvc
            .perform(delete(ENTITY_API_URL_ID, brand.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return brandRepository.count();
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

    protected Brand getPersistedBrand(Brand brand) {
        return brandRepository.findById(brand.getId()).orElseThrow();
    }

    protected void assertPersistedBrandToMatchAllProperties(Brand expectedBrand) {
        assertBrandAllPropertiesEquals(expectedBrand, getPersistedBrand(expectedBrand));
    }

    protected void assertPersistedBrandToMatchUpdatableProperties(Brand expectedBrand) {
        assertBrandAllUpdatablePropertiesEquals(expectedBrand, getPersistedBrand(expectedBrand));
    }
}
