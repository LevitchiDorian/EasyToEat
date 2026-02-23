package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.MenuItemOptionAsserts.*;
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
import md.utm.restaurant.domain.MenuItem;
import md.utm.restaurant.domain.MenuItemOption;
import md.utm.restaurant.repository.MenuItemOptionRepository;
import md.utm.restaurant.service.MenuItemOptionService;
import md.utm.restaurant.service.dto.MenuItemOptionDTO;
import md.utm.restaurant.service.mapper.MenuItemOptionMapper;
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
 * Integration tests for the {@link MenuItemOptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MenuItemOptionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_REQUIRED = false;
    private static final Boolean UPDATED_IS_REQUIRED = true;

    private static final Integer DEFAULT_MAX_SELECTIONS = 1;
    private static final Integer UPDATED_MAX_SELECTIONS = 2;

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final String ENTITY_API_URL = "/api/menu-item-options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemOptionRepository menuItemOptionRepository;

    @Mock
    private MenuItemOptionRepository menuItemOptionRepositoryMock;

    @Autowired
    private MenuItemOptionMapper menuItemOptionMapper;

    @Mock
    private MenuItemOptionService menuItemOptionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMenuItemOptionMockMvc;

    private MenuItemOption menuItemOption;

    private MenuItemOption insertedMenuItemOption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemOption createEntity(EntityManager em) {
        MenuItemOption menuItemOption = new MenuItemOption()
            .name(DEFAULT_NAME)
            .isRequired(DEFAULT_IS_REQUIRED)
            .maxSelections(DEFAULT_MAX_SELECTIONS)
            .displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        MenuItem menuItem;
        if (TestUtil.findAll(em, MenuItem.class).isEmpty()) {
            menuItem = MenuItemResourceIT.createEntity(em);
            em.persist(menuItem);
            em.flush();
        } else {
            menuItem = TestUtil.findAll(em, MenuItem.class).get(0);
        }
        menuItemOption.setMenuItem(menuItem);
        return menuItemOption;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemOption createUpdatedEntity(EntityManager em) {
        MenuItemOption updatedMenuItemOption = new MenuItemOption()
            .name(UPDATED_NAME)
            .isRequired(UPDATED_IS_REQUIRED)
            .maxSelections(UPDATED_MAX_SELECTIONS)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        MenuItem menuItem;
        if (TestUtil.findAll(em, MenuItem.class).isEmpty()) {
            menuItem = MenuItemResourceIT.createUpdatedEntity(em);
            em.persist(menuItem);
            em.flush();
        } else {
            menuItem = TestUtil.findAll(em, MenuItem.class).get(0);
        }
        updatedMenuItemOption.setMenuItem(menuItem);
        return updatedMenuItemOption;
    }

    @BeforeEach
    void initTest() {
        menuItemOption = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItemOption != null) {
            menuItemOptionRepository.delete(insertedMenuItemOption);
            insertedMenuItemOption = null;
        }
    }

    @Test
    @Transactional
    void createMenuItemOption() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);
        var returnedMenuItemOptionDTO = om.readValue(
            restMenuItemOptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MenuItemOptionDTO.class
        );

        // Validate the MenuItemOption in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItemOption = menuItemOptionMapper.toEntity(returnedMenuItemOptionDTO);
        assertMenuItemOptionUpdatableFieldsEquals(returnedMenuItemOption, getPersistedMenuItemOption(returnedMenuItemOption));

        insertedMenuItemOption = returnedMenuItemOption;
    }

    @Test
    @Transactional
    void createMenuItemOptionWithExistingId() throws Exception {
        // Create the MenuItemOption with an existing ID
        menuItemOption.setId(1L);
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMenuItemOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemOption.setName(null);

        // Create the MenuItemOption, which fails.
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        restMenuItemOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsRequiredIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemOption.setIsRequired(null);

        // Create the MenuItemOption, which fails.
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        restMenuItemOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMenuItemOptions() throws Exception {
        // Initialize the database
        insertedMenuItemOption = menuItemOptionRepository.saveAndFlush(menuItemOption);

        // Get all the menuItemOptionList
        restMenuItemOptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuItemOption.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].isRequired").value(hasItem(DEFAULT_IS_REQUIRED)))
            .andExpect(jsonPath("$.[*].maxSelections").value(hasItem(DEFAULT_MAX_SELECTIONS)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemOptionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(menuItemOptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemOptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(menuItemOptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemOptionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(menuItemOptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemOptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(menuItemOptionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMenuItemOption() throws Exception {
        // Initialize the database
        insertedMenuItemOption = menuItemOptionRepository.saveAndFlush(menuItemOption);

        // Get the menuItemOption
        restMenuItemOptionMockMvc
            .perform(get(ENTITY_API_URL_ID, menuItemOption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(menuItemOption.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.isRequired").value(DEFAULT_IS_REQUIRED))
            .andExpect(jsonPath("$.maxSelections").value(DEFAULT_MAX_SELECTIONS))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingMenuItemOption() throws Exception {
        // Get the menuItemOption
        restMenuItemOptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMenuItemOption() throws Exception {
        // Initialize the database
        insertedMenuItemOption = menuItemOptionRepository.saveAndFlush(menuItemOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemOption
        MenuItemOption updatedMenuItemOption = menuItemOptionRepository.findById(menuItemOption.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMenuItemOption are not directly saved in db
        em.detach(updatedMenuItemOption);
        updatedMenuItemOption
            .name(UPDATED_NAME)
            .isRequired(UPDATED_IS_REQUIRED)
            .maxSelections(UPDATED_MAX_SELECTIONS)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(updatedMenuItemOption);

        restMenuItemOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemOptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemOptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemOptionToMatchAllProperties(updatedMenuItemOption);
    }

    @Test
    @Transactional
    void putNonExistingMenuItemOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOption.setId(longCount.incrementAndGet());

        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemOptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMenuItemOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOption.setId(longCount.incrementAndGet());

        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMenuItemOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOption.setId(longCount.incrementAndGet());

        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemOptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMenuItemOptionWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItemOption = menuItemOptionRepository.saveAndFlush(menuItemOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemOption using partial update
        MenuItemOption partialUpdatedMenuItemOption = new MenuItemOption();
        partialUpdatedMenuItemOption.setId(menuItemOption.getId());

        partialUpdatedMenuItemOption.name(UPDATED_NAME).isRequired(UPDATED_IS_REQUIRED);

        restMenuItemOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItemOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItemOption))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemOptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuItemOption, menuItemOption),
            getPersistedMenuItemOption(menuItemOption)
        );
    }

    @Test
    @Transactional
    void fullUpdateMenuItemOptionWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItemOption = menuItemOptionRepository.saveAndFlush(menuItemOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemOption using partial update
        MenuItemOption partialUpdatedMenuItemOption = new MenuItemOption();
        partialUpdatedMenuItemOption.setId(menuItemOption.getId());

        partialUpdatedMenuItemOption
            .name(UPDATED_NAME)
            .isRequired(UPDATED_IS_REQUIRED)
            .maxSelections(UPDATED_MAX_SELECTIONS)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restMenuItemOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItemOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItemOption))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemOptionUpdatableFieldsEquals(partialUpdatedMenuItemOption, getPersistedMenuItemOption(partialUpdatedMenuItemOption));
    }

    @Test
    @Transactional
    void patchNonExistingMenuItemOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOption.setId(longCount.incrementAndGet());

        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, menuItemOptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMenuItemOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOption.setId(longCount.incrementAndGet());

        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMenuItemOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemOption.setId(longCount.incrementAndGet());

        // Create the MenuItemOption
        MenuItemOptionDTO menuItemOptionDTO = menuItemOptionMapper.toDto(menuItemOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemOptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(menuItemOptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItemOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMenuItemOption() throws Exception {
        // Initialize the database
        insertedMenuItemOption = menuItemOptionRepository.saveAndFlush(menuItemOption);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuItemOption
        restMenuItemOptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, menuItemOption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuItemOptionRepository.count();
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

    protected MenuItemOption getPersistedMenuItemOption(MenuItemOption menuItemOption) {
        return menuItemOptionRepository.findById(menuItemOption.getId()).orElseThrow();
    }

    protected void assertPersistedMenuItemOptionToMatchAllProperties(MenuItemOption expectedMenuItemOption) {
        assertMenuItemOptionAllPropertiesEquals(expectedMenuItemOption, getPersistedMenuItemOption(expectedMenuItemOption));
    }

    protected void assertPersistedMenuItemOptionToMatchUpdatableProperties(MenuItemOption expectedMenuItemOption) {
        assertMenuItemOptionAllUpdatablePropertiesEquals(expectedMenuItemOption, getPersistedMenuItemOption(expectedMenuItemOption));
    }
}
