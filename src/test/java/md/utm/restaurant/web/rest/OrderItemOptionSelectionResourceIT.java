package md.utm.restaurant.web.rest;

import static md.utm.restaurant.domain.OrderItemOptionSelectionAsserts.*;
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
import md.utm.restaurant.domain.OrderItem;
import md.utm.restaurant.domain.OrderItemOptionSelection;
import md.utm.restaurant.repository.OrderItemOptionSelectionRepository;
import md.utm.restaurant.service.OrderItemOptionSelectionService;
import md.utm.restaurant.service.dto.OrderItemOptionSelectionDTO;
import md.utm.restaurant.service.mapper.OrderItemOptionSelectionMapper;
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
 * Integration tests for the {@link OrderItemOptionSelectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderItemOptionSelectionResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/order-item-option-selections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemOptionSelectionRepository orderItemOptionSelectionRepository;

    @Mock
    private OrderItemOptionSelectionRepository orderItemOptionSelectionRepositoryMock;

    @Autowired
    private OrderItemOptionSelectionMapper orderItemOptionSelectionMapper;

    @Mock
    private OrderItemOptionSelectionService orderItemOptionSelectionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemOptionSelectionMockMvc;

    private OrderItemOptionSelection orderItemOptionSelection;

    private OrderItemOptionSelection insertedOrderItemOptionSelection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItemOptionSelection createEntity(EntityManager em) {
        OrderItemOptionSelection orderItemOptionSelection = new OrderItemOptionSelection()
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE);
        // Add required entity
        OrderItem orderItem;
        if (TestUtil.findAll(em, OrderItem.class).isEmpty()) {
            orderItem = OrderItemResourceIT.createEntity(em);
            em.persist(orderItem);
            em.flush();
        } else {
            orderItem = TestUtil.findAll(em, OrderItem.class).get(0);
        }
        orderItemOptionSelection.setOrderItem(orderItem);
        return orderItemOptionSelection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItemOptionSelection createUpdatedEntity(EntityManager em) {
        OrderItemOptionSelection updatedOrderItemOptionSelection = new OrderItemOptionSelection()
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE);
        // Add required entity
        OrderItem orderItem;
        if (TestUtil.findAll(em, OrderItem.class).isEmpty()) {
            orderItem = OrderItemResourceIT.createUpdatedEntity(em);
            em.persist(orderItem);
            em.flush();
        } else {
            orderItem = TestUtil.findAll(em, OrderItem.class).get(0);
        }
        updatedOrderItemOptionSelection.setOrderItem(orderItem);
        return updatedOrderItemOptionSelection;
    }

    @BeforeEach
    void initTest() {
        orderItemOptionSelection = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderItemOptionSelection != null) {
            orderItemOptionSelectionRepository.delete(insertedOrderItemOptionSelection);
            insertedOrderItemOptionSelection = null;
        }
    }

    @Test
    @Transactional
    void createOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);
        var returnedOrderItemOptionSelectionDTO = om.readValue(
            restOrderItemOptionSelectionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderItemOptionSelectionDTO.class
        );

        // Validate the OrderItemOptionSelection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderItemOptionSelection = orderItemOptionSelectionMapper.toEntity(returnedOrderItemOptionSelectionDTO);
        assertOrderItemOptionSelectionUpdatableFieldsEquals(
            returnedOrderItemOptionSelection,
            getPersistedOrderItemOptionSelection(returnedOrderItemOptionSelection)
        );

        insertedOrderItemOptionSelection = returnedOrderItemOptionSelection;
    }

    @Test
    @Transactional
    void createOrderItemOptionSelectionWithExistingId() throws Exception {
        // Create the OrderItemOptionSelection with an existing ID
        orderItemOptionSelection.setId(1L);
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemOptionSelectionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItemOptionSelection.setUnitPrice(null);

        // Create the OrderItemOptionSelection, which fails.
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        restOrderItemOptionSelectionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderItemOptionSelections() throws Exception {
        // Initialize the database
        insertedOrderItemOptionSelection = orderItemOptionSelectionRepository.saveAndFlush(orderItemOptionSelection);

        // Get all the orderItemOptionSelectionList
        restOrderItemOptionSelectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItemOptionSelection.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderItemOptionSelectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderItemOptionSelectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderItemOptionSelectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderItemOptionSelectionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderItemOptionSelectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderItemOptionSelectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderItemOptionSelectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderItemOptionSelectionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrderItemOptionSelection() throws Exception {
        // Initialize the database
        insertedOrderItemOptionSelection = orderItemOptionSelectionRepository.saveAndFlush(orderItemOptionSelection);

        // Get the orderItemOptionSelection
        restOrderItemOptionSelectionMockMvc
            .perform(get(ENTITY_API_URL_ID, orderItemOptionSelection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItemOptionSelection.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingOrderItemOptionSelection() throws Exception {
        // Get the orderItemOptionSelection
        restOrderItemOptionSelectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderItemOptionSelection() throws Exception {
        // Initialize the database
        insertedOrderItemOptionSelection = orderItemOptionSelectionRepository.saveAndFlush(orderItemOptionSelection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItemOptionSelection
        OrderItemOptionSelection updatedOrderItemOptionSelection = orderItemOptionSelectionRepository
            .findById(orderItemOptionSelection.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedOrderItemOptionSelection are not directly saved in db
        em.detach(updatedOrderItemOptionSelection);
        updatedOrderItemOptionSelection.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE);
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(updatedOrderItemOptionSelection);

        restOrderItemOptionSelectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemOptionSelectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemOptionSelectionToMatchAllProperties(updatedOrderItemOptionSelection);
    }

    @Test
    @Transactional
    void putNonExistingOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemOptionSelection.setId(longCount.incrementAndGet());

        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemOptionSelectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemOptionSelectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemOptionSelection.setId(longCount.incrementAndGet());

        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemOptionSelectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemOptionSelection.setId(longCount.incrementAndGet());

        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemOptionSelectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemOptionSelectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderItemOptionSelectionWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItemOptionSelection = orderItemOptionSelectionRepository.saveAndFlush(orderItemOptionSelection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItemOptionSelection using partial update
        OrderItemOptionSelection partialUpdatedOrderItemOptionSelection = new OrderItemOptionSelection();
        partialUpdatedOrderItemOptionSelection.setId(orderItemOptionSelection.getId());

        partialUpdatedOrderItemOptionSelection.unitPrice(UPDATED_UNIT_PRICE);

        restOrderItemOptionSelectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItemOptionSelection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItemOptionSelection))
            )
            .andExpect(status().isOk());

        // Validate the OrderItemOptionSelection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemOptionSelectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItemOptionSelection, orderItemOptionSelection),
            getPersistedOrderItemOptionSelection(orderItemOptionSelection)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderItemOptionSelectionWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItemOptionSelection = orderItemOptionSelectionRepository.saveAndFlush(orderItemOptionSelection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItemOptionSelection using partial update
        OrderItemOptionSelection partialUpdatedOrderItemOptionSelection = new OrderItemOptionSelection();
        partialUpdatedOrderItemOptionSelection.setId(orderItemOptionSelection.getId());

        partialUpdatedOrderItemOptionSelection.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE);

        restOrderItemOptionSelectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItemOptionSelection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItemOptionSelection))
            )
            .andExpect(status().isOk());

        // Validate the OrderItemOptionSelection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemOptionSelectionUpdatableFieldsEquals(
            partialUpdatedOrderItemOptionSelection,
            getPersistedOrderItemOptionSelection(partialUpdatedOrderItemOptionSelection)
        );
    }

    @Test
    @Transactional
    void patchNonExistingOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemOptionSelection.setId(longCount.incrementAndGet());

        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemOptionSelectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderItemOptionSelectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemOptionSelection.setId(longCount.incrementAndGet());

        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemOptionSelectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderItemOptionSelection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemOptionSelection.setId(longCount.incrementAndGet());

        // Create the OrderItemOptionSelection
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = orderItemOptionSelectionMapper.toDto(orderItemOptionSelection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemOptionSelectionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderItemOptionSelectionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItemOptionSelection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderItemOptionSelection() throws Exception {
        // Initialize the database
        insertedOrderItemOptionSelection = orderItemOptionSelectionRepository.saveAndFlush(orderItemOptionSelection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderItemOptionSelection
        restOrderItemOptionSelectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderItemOptionSelection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderItemOptionSelectionRepository.count();
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

    protected OrderItemOptionSelection getPersistedOrderItemOptionSelection(OrderItemOptionSelection orderItemOptionSelection) {
        return orderItemOptionSelectionRepository.findById(orderItemOptionSelection.getId()).orElseThrow();
    }

    protected void assertPersistedOrderItemOptionSelectionToMatchAllProperties(OrderItemOptionSelection expectedOrderItemOptionSelection) {
        assertOrderItemOptionSelectionAllPropertiesEquals(
            expectedOrderItemOptionSelection,
            getPersistedOrderItemOptionSelection(expectedOrderItemOptionSelection)
        );
    }

    protected void assertPersistedOrderItemOptionSelectionToMatchUpdatableProperties(
        OrderItemOptionSelection expectedOrderItemOptionSelection
    ) {
        assertOrderItemOptionSelectionAllUpdatablePropertiesEquals(
            expectedOrderItemOptionSelection,
            getPersistedOrderItemOptionSelection(expectedOrderItemOptionSelection)
        );
    }
}
