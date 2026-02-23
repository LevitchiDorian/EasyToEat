package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.MenuItemAllergenAsserts.*;
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
import md.utm.restaurant.domain.MenuItemAllergen;
import md.utm.restaurant.domain.enumeration.AllergenType;
import md.utm.restaurant.repository.MenuItemAllergenRepository;
import md.utm.restaurant.service.MenuItemAllergenService;
import md.utm.restaurant.service.dto.MenuItemAllergenDTO;
import md.utm.restaurant.service.mapper.MenuItemAllergenMapper;
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
 * Integration tests for the {@link MenuItemAllergenResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MenuItemAllergenResourceIT {

    private static final AllergenType DEFAULT_ALLERGEN = AllergenType.GLUTEN;
    private static final AllergenType UPDATED_ALLERGEN = AllergenType.DAIRY;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/menu-item-allergens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemAllergenRepository menuItemAllergenRepository;

    @Mock
    private MenuItemAllergenRepository menuItemAllergenRepositoryMock;

    @Autowired
    private MenuItemAllergenMapper menuItemAllergenMapper;

    @Mock
    private MenuItemAllergenService menuItemAllergenServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMenuItemAllergenMockMvc;

    private MenuItemAllergen menuItemAllergen;

    private MenuItemAllergen insertedMenuItemAllergen;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemAllergen createEntity(EntityManager em) {
        MenuItemAllergen menuItemAllergen = new MenuItemAllergen().allergen(DEFAULT_ALLERGEN).notes(DEFAULT_NOTES);
        // Add required entity
        MenuItem menuItem;
        if (TestUtil.findAll(em, MenuItem.class).isEmpty()) {
            menuItem = MenuItemResourceIT.createEntity(em);
            em.persist(menuItem);
            em.flush();
        } else {
            menuItem = TestUtil.findAll(em, MenuItem.class).get(0);
        }
        menuItemAllergen.setMenuItem(menuItem);
        return menuItemAllergen;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemAllergen createUpdatedEntity(EntityManager em) {
        MenuItemAllergen updatedMenuItemAllergen = new MenuItemAllergen().allergen(UPDATED_ALLERGEN).notes(UPDATED_NOTES);
        // Add required entity
        MenuItem menuItem;
        if (TestUtil.findAll(em, MenuItem.class).isEmpty()) {
            menuItem = MenuItemResourceIT.createUpdatedEntity(em);
            em.persist(menuItem);
            em.flush();
        } else {
            menuItem = TestUtil.findAll(em, MenuItem.class).get(0);
        }
        updatedMenuItemAllergen.setMenuItem(menuItem);
        return updatedMenuItemAllergen;
    }

    @BeforeEach
    void initTest() {
        menuItemAllergen = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItemAllergen != null) {
            menuItemAllergenRepository.delete(insertedMenuItemAllergen);
            insertedMenuItemAllergen = null;
        }
    }

    @Test
    @Transactional
    void createMenuItemAllergen() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);
        var returnedMenuItemAllergenDTO = om.readValue(
            restMenuItemAllergenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemAllergenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MenuItemAllergenDTO.class
        );

        // Validate the MenuItemAllergen in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItemAllergen = menuItemAllergenMapper.toEntity(returnedMenuItemAllergenDTO);
        assertMenuItemAllergenUpdatableFieldsEquals(returnedMenuItemAllergen, getPersistedMenuItemAllergen(returnedMenuItemAllergen));

        insertedMenuItemAllergen = returnedMenuItemAllergen;
    }

    @Test
    @Transactional
    void createMenuItemAllergenWithExistingId() throws Exception {
        // Create the MenuItemAllergen with an existing ID
        menuItemAllergen.setId(1L);
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMenuItemAllergenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemAllergenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAllergenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemAllergen.setAllergen(null);

        // Create the MenuItemAllergen, which fails.
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        restMenuItemAllergenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemAllergenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMenuItemAllergens() throws Exception {
        // Initialize the database
        insertedMenuItemAllergen = menuItemAllergenRepository.saveAndFlush(menuItemAllergen);

        // Get all the menuItemAllergenList
        restMenuItemAllergenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuItemAllergen.getId().intValue())))
            .andExpect(jsonPath("$.[*].allergen").value(hasItem(DEFAULT_ALLERGEN.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemAllergensWithEagerRelationshipsIsEnabled() throws Exception {
        when(menuItemAllergenServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemAllergenMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(menuItemAllergenServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenuItemAllergensWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(menuItemAllergenServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuItemAllergenMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(menuItemAllergenRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMenuItemAllergen() throws Exception {
        // Initialize the database
        insertedMenuItemAllergen = menuItemAllergenRepository.saveAndFlush(menuItemAllergen);

        // Get the menuItemAllergen
        restMenuItemAllergenMockMvc
            .perform(get(ENTITY_API_URL_ID, menuItemAllergen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(menuItemAllergen.getId().intValue()))
            .andExpect(jsonPath("$.allergen").value(DEFAULT_ALLERGEN.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingMenuItemAllergen() throws Exception {
        // Get the menuItemAllergen
        restMenuItemAllergenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMenuItemAllergen() throws Exception {
        // Initialize the database
        insertedMenuItemAllergen = menuItemAllergenRepository.saveAndFlush(menuItemAllergen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemAllergen
        MenuItemAllergen updatedMenuItemAllergen = menuItemAllergenRepository.findById(menuItemAllergen.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMenuItemAllergen are not directly saved in db
        em.detach(updatedMenuItemAllergen);
        updatedMenuItemAllergen.allergen(UPDATED_ALLERGEN).notes(UPDATED_NOTES);
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(updatedMenuItemAllergen);

        restMenuItemAllergenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemAllergenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemAllergenDTO))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemAllergenToMatchAllProperties(updatedMenuItemAllergen);
    }

    @Test
    @Transactional
    void putNonExistingMenuItemAllergen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAllergen.setId(longCount.incrementAndGet());

        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemAllergenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuItemAllergenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemAllergenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMenuItemAllergen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAllergen.setId(longCount.incrementAndGet());

        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemAllergenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(menuItemAllergenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMenuItemAllergen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAllergen.setId(longCount.incrementAndGet());

        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemAllergenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(menuItemAllergenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMenuItemAllergenWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItemAllergen = menuItemAllergenRepository.saveAndFlush(menuItemAllergen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemAllergen using partial update
        MenuItemAllergen partialUpdatedMenuItemAllergen = new MenuItemAllergen();
        partialUpdatedMenuItemAllergen.setId(menuItemAllergen.getId());

        partialUpdatedMenuItemAllergen.allergen(UPDATED_ALLERGEN).notes(UPDATED_NOTES);

        restMenuItemAllergenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItemAllergen.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItemAllergen))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemAllergen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemAllergenUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuItemAllergen, menuItemAllergen),
            getPersistedMenuItemAllergen(menuItemAllergen)
        );
    }

    @Test
    @Transactional
    void fullUpdateMenuItemAllergenWithPatch() throws Exception {
        // Initialize the database
        insertedMenuItemAllergen = menuItemAllergenRepository.saveAndFlush(menuItemAllergen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemAllergen using partial update
        MenuItemAllergen partialUpdatedMenuItemAllergen = new MenuItemAllergen();
        partialUpdatedMenuItemAllergen.setId(menuItemAllergen.getId());

        partialUpdatedMenuItemAllergen.allergen(UPDATED_ALLERGEN).notes(UPDATED_NOTES);

        restMenuItemAllergenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuItemAllergen.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMenuItemAllergen))
            )
            .andExpect(status().isOk());

        // Validate the MenuItemAllergen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemAllergenUpdatableFieldsEquals(
            partialUpdatedMenuItemAllergen,
            getPersistedMenuItemAllergen(partialUpdatedMenuItemAllergen)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMenuItemAllergen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAllergen.setId(longCount.incrementAndGet());

        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuItemAllergenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, menuItemAllergenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemAllergenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMenuItemAllergen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAllergen.setId(longCount.incrementAndGet());

        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemAllergenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(menuItemAllergenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMenuItemAllergen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAllergen.setId(longCount.incrementAndGet());

        // Create the MenuItemAllergen
        MenuItemAllergenDTO menuItemAllergenDTO = menuItemAllergenMapper.toDto(menuItemAllergen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuItemAllergenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(menuItemAllergenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MenuItemAllergen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMenuItemAllergen() throws Exception {
        // Initialize the database
        insertedMenuItemAllergen = menuItemAllergenRepository.saveAndFlush(menuItemAllergen);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuItemAllergen
        restMenuItemAllergenMockMvc
            .perform(delete(ENTITY_API_URL_ID, menuItemAllergen.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuItemAllergenRepository.count();
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

    protected MenuItemAllergen getPersistedMenuItemAllergen(MenuItemAllergen menuItemAllergen) {
        return menuItemAllergenRepository.findById(menuItemAllergen.getId()).orElseThrow();
    }

    protected void assertPersistedMenuItemAllergenToMatchAllProperties(MenuItemAllergen expectedMenuItemAllergen) {
        assertMenuItemAllergenAllPropertiesEquals(expectedMenuItemAllergen, getPersistedMenuItemAllergen(expectedMenuItemAllergen));
    }

    protected void assertPersistedMenuItemAllergenToMatchUpdatableProperties(MenuItemAllergen expectedMenuItemAllergen) {
        assertMenuItemAllergenAllUpdatablePropertiesEquals(
            expectedMenuItemAllergen,
            getPersistedMenuItemAllergen(expectedMenuItemAllergen)
        );
    }
}
