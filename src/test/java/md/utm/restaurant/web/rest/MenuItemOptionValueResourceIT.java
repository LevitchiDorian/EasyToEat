package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.MenuItemOptionValueAsserts.*;
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
import md.utm.restaurant.domain.MenuItemOption;
import md.utm.restaurant.domain.MenuItemOptionValue;
import md.utm.restaurant.repository.MenuItemOptionValueRepository;
import md.utm.restaurant.service.MenuItemOptionValueService;
import md.utm.restaurant.service.dto.MenuItemOptionValueDTO;
import md.utm.restaurant.service.mapper.MenuItemOptionValueMapper;
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
 * Integration tests for the {@link MenuItemOptionValueResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MenuItemOptionValueResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_ADJUSTMENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_ADJUSTMENT = new BigDecimal(2);

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final Boolean DEFAULT_IS_AVAILABLE = false;
    private static final Boolean UPDATED_IS_AVAILABLE = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final String ENTITY_API_URL = "/api/menu-item-option-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemOptionValueRepository menuItemOptionValueRepository;

    @Mock
    private MenuItemOptionValueRepository menuItemOptionValueRepositoryMock;

    @Autowired
    private MenuItemOptionValueMapper menuItemOptionValueMapper;

    @Mock
    private MenuItemOptionValueService menuItemOptionValueServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMenuItemOptionValueMockMvc;

    private MenuItemOptionValue menuItemOptionValue;

    private MenuItemOptionValue insertedMenuItemOptionValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemOptionValue createEntity(EntityManager em) {
        MenuItemOptionValue menuItemOptionValue = new MenuItemOptionValue()
            .label(DEFAULT_LABEL)
            .priceAdjustment(DEFAULT_PRICE_ADJUSTMENT)
            .isDefault(DEFAULT_IS_DEFAULT)
            .isAvailable(DEFAULT_IS_AVAILABLE)
            .displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        MenuItemOption menuItemOption;
        if (TestUtil.findAll(em, MenuItemOption.class).isEmpty()) {
            menuItemOption = MenuItemOptionResourceIT.createEntity(em);
            em.persist(menuItemOption);
            em.flush();
        } else {
            menuItemOption = TestUtil.findAll(em, MenuItemOption.class).get(0);
        }
        menuItemOptionValue.setOption(menuItemOption);
        return menuItemOptionValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemOptionValue createUpdatedEntity(EntityManager em) {
        MenuItemOptionValue updatedMenuItemOptionValue = new MenuItemOptionValue()
            .label(UPDATED_LABEL)
            .priceAdjustment(UPDATED_PRICE_ADJUSTMENT)
            .isDefault(UPDATED_IS_DEFAULT)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        MenuItemOption menuItemOption;
        if (TestUtil.findAll(em, MenuItemOption.class).isEmpty()) {
            menuItemOption = MenuItemOptionResourceIT.createUpdatedEntity(em);
            em.persist(menuItemOption);
            em.flush();
        } else {
            menuItemOption = TestUtil.findAll(em, MenuItemOption.class).get(0);
        }
        updatedMenuItemOptionValue.setOption(menuItemOption);
        return updatedMenuItemOptionValue;
    }

    @BeforeEach
    void initTest() {
        menuItemOptionValue = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItemOptionValue != null) {
            menuItemOptionValueRepository.delete(insertedMenuItemOptionValue);
            insertedMenuItemOptionValue = null;
        }
    }

    @Test
    @Transactional
    void createMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);
        var returnedMenuItemOptionValueDTO = om.readValue(
            restMenuItemOptionValueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MenuItemOptionValueDTO.class
        );

        // Validate the MenuItemOptionValue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItemOptionValue = menuItemOptionValueMapper.toEntity(returnedMenuItemOptionValueDTO);
        assertMenuItemOptionValueUpdatableFieldsEquals(
            returnedMenuItemOptionValue,
            getPersistedMenuItemOptionValue(returnedMenuItemOptionValue)
        );

        insertedMenuItemOptionValue = returnedMenuItemOptionValue;
    }

    @Test
    @Transactional
    void createMenuItemOptionValueWithExistingId() throws Exception {
        // Create the MenuItemOptionValue with an existing ID
        menuItemOptionValue.setId(1L);
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMenuItemOptionValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemOptionValue.setLabel(null);

        // Create the MenuItemOptionValue, which fails.
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        restMenuItemOptionValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceAdjustmentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemOptionValue.setPriceAdjustment(null);

        // Create the MenuItemOptionValue, which fails.
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        restMenuItemOptionValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDefaultIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemOptionValue.setIsDefault(null);

        // Create the MenuItemOptionValue, which fails.
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        restMenuItemOptionValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemOptionValue.setIsAvailable(null);

        // Create the MenuItemOptionValue, which fails.
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        restMenuItemOptionValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMenuItemOptionValues() throws Exception {
        // Initialize the database
        insertedMenuItemOptionValue = menuItemOptionValueRepository.saveAndFlush(menuItemOptionValue);

        // Get all the menuItemOptionValueList
        restMenuItemOptionValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuItemOptionValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].priceAdjustment").value(hasItem(sameNumber(DEFAULT_PRICE_ADJUSTMENT))))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].isAvailable").value(hasItem(DEFAULT_IS_AVAILABLE)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemOptionValuesWithEagerRelationshipsIsEnabled() throws Exception {
        when(menuItemOptionValueServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemOptionValueMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(menuItemOptionValueServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemOptionValuesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(menuItemOptionValueServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemOptionValueMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(menuItemOptionValueRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMenuItemOptionValue() throws Exception {
        // Initialize the database
        insertedMenuItemOptionValue = menuItemOptionValueRepository.saveAndFlush(menuItemOptionValue);

        // Get the menuItemOptionValue
        restMenuItemOptionValueMockMvc
            .perform(get(ENTITY_API_URL_ID, menuItemOptionValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(menuItemOptionValue.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.priceAdjustment").value(sameNumber(DEFAULT_PRICE_ADJUSTMENT)))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT))
            .andExpect(jsonPath("$.isAvailable").value(DEFAULT_IS_AVAILABLE))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingMenuItemOptionValue() throws Exception {
        // Get the menuItemOptionValue
        restMenuItemOptionValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMenuItemOptionValue() throws Exception {
        // Initialize the database
        insertedMenuItemOptionValue = menuItemOptionValueRepository.saveAndFlush(menuItemOptionValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemOptionValue
        MenuItemOptionValue updatedMenuItemOptionValue = menuItemOptionValueRepository.findById(menuItemOptionValue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMenuItemOptionValue are not directly saved in db
        em.detach(updatedMenuItemOptionValue);
        updatedMenuItemOptionValue
            .label(UPDATED_LABEL)
            .priceAdjustment(UPDATED_PRICE_ADJUSTMENT)
            .isDefault(UPDATED_IS_DEFAULT)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(updatedMenuItemOptionValue);

        restMenuItemOptionValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemOptionValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemOptionValueDTO))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemOptionValueToMatchAllProperties(updatedMenuItemOptionValue);
    }

    @Test
    @Transactional
    void putNonExistingMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOptionValue.setId(longCount.incrementAndGet());

        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemOptionValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemOptionValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemOptionValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOptionValue.setId(longCount.incrementAndGet());

        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemOptionValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOptionValue.setId(longCount.incrementAndGet());

        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionValueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMenuItemOptionValueWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItemOptionValue = menuItemOptionValueRepository.saveAndFlush(menuItemOptionValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemOptionValue using partial update
        MenuItemOptionValue partialUpdatedMenuItemOptionValue = new MenuItemOptionValue();
        partialUpdatedMenuItemOptionValue.setId(menuItemOptionValue.getId());

        partialUpdatedMenuItemOptionValue.isDefault(UPDATED_IS_DEFAULT).isAvailable(UPDATED_IS_AVAILABLE);

        restMenuItemOptionValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItemOptionValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItemOptionValue))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemOptionValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemOptionValueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuItemOptionValue, menuItemOptionValue),
            getPersistedMenuItemOptionValue(menuItemOptionValue)
        );
    }

    @Test
    @Transactional
    void fullUpdateMenuItemOptionValueWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItemOptionValue = menuItemOptionValueRepository.saveAndFlush(menuItemOptionValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemOptionValue using partial update
        MenuItemOptionValue partialUpdatedMenuItemOptionValue = new MenuItemOptionValue();
        partialUpdatedMenuItemOptionValue.setId(menuItemOptionValue.getId());

        partialUpdatedMenuItemOptionValue
            .label(UPDATED_LABEL)
            .priceAdjustment(UPDATED_PRICE_ADJUSTMENT)
            .isDefault(UPDATED_IS_DEFAULT)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restMenuItemOptionValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItemOptionValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItemOptionValue))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemOptionValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemOptionValueUpdatableFieldsEquals(
            partialUpdatedMenuItemOptionValue,
            getPersistedMenuItemOptionValue(partialUpdatedMenuItemOptionValue)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOptionValue.setId(longCount.incrementAndGet());

        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemOptionValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, menuItemOptionValueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemOptionValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOptionValue.setId(longCount.incrementAndGet());

        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemOptionValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMenuItemOptionValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOptionValue.setId(longCount.incrementAndGet());

        // Create the MenuItemOptionValue
        MenuItemOptionValueDTO menuItemOptionValueDTO = menuItemOptionValueMapper.toDto(menuItemOptionValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionValueMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(menuItemOptionValueDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItemOptionValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMenuItemOptionValue() throws Exception {
        // Initialize the database
        insertedMenuItemOptionValue = menuItemOptionValueRepository.saveAndFlush(menuItemOptionValue);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuItemOptionValue
        restMenuItemOptionValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, menuItemOptionValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuItemOptionValueRepository.count();
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

    protected MenuItemOptionValue getPersistedMenuItemOptionValue(MenuItemOptionValue menuItemOptionValue) {
        return menuItemOptionValueRepository.findById(menuItemOptionValue.getId()).orElseThrow();
    }

    protected void assertPersistedMenuItemOptionValueToMatchAllProperties(MenuItemOptionValue expectedMenuItemOptionValue) {
        assertMenuItemOptionValueAllPropertiesEquals(
            expectedMenuItemOptionValue,
            getPersistedMenuItemOptionValue(expectedMenuItemOptionValue)
        );
    }

    protected void assertPersistedMenuItemOptionValueToMatchUpdatableProperties(MenuItemOptionValue expectedMenuItemOptionValue) {
        assertMenuItemOptionValueAllUpdatablePropertiesEquals(
            expectedMenuItemOptionValue,
            getPersistedMenuItemOptionValue(expectedMenuItemOptionValue)
        );
    }
}
