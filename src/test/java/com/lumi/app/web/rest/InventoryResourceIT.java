package com.lumi.app.web.rest;

import static com.lumi.app.domain.InventoryAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Inventory;
import com.lumi.app.repository.InventoryRepository;
import com.lumi.app.repository.search.InventorySearchRepository;
import com.lumi.app.service.dto.InventoryDTO;
import com.lumi.app.service.mapper.InventoryMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InventoryResourceIT {

    private static final Long DEFAULT_PRODUCT_VARIANT_ID = 1L;
    private static final Long UPDATED_PRODUCT_VARIANT_ID = 2L;

    private static final Long DEFAULT_WAREHOUSE_ID = 1L;
    private static final Long UPDATED_WAREHOUSE_ID = 2L;

    private static final Long DEFAULT_QUANTITY = 0L;
    private static final Long UPDATED_QUANTITY = 1L;

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/inventories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private InventorySearchRepository inventorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryMockMvc;

    private Inventory inventory;

    private Inventory insertedInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createEntity() {
        return new Inventory()
            .productVariantId(DEFAULT_PRODUCT_VARIANT_ID)
            .warehouseId(DEFAULT_WAREHOUSE_ID)
            .quantity(DEFAULT_QUANTITY)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createUpdatedEntity() {
        return new Inventory()
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        inventory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInventory != null) {
            inventoryRepository.delete(insertedInventory);
            inventorySearchRepository.delete(insertedInventory);
            insertedInventory = null;
        }
    }

    @Test
    @Transactional
    void createInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
        var returnedInventoryDTO = om.readValue(
            restInventoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventoryDTO.class
        );

        // Validate the Inventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventory = inventoryMapper.toEntity(returnedInventoryDTO);
        assertInventoryUpdatableFieldsEquals(returnedInventory, getPersistedInventory(returnedInventory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInventory = returnedInventory;
    }

    @Test
    @Transactional
    void createInventoryWithExistingId() throws Exception {
        // Create the Inventory with an existing ID
        inventory.setId(1L);
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProductVariantIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        // set the field null
        inventory.setProductVariantId(null);

        // Create the Inventory, which fails.
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWarehouseIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        // set the field null
        inventory.setWarehouseId(null);

        // Create the Inventory, which fails.
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        // set the field null
        inventory.setQuantity(null);

        // Create the Inventory, which fails.
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        // set the field null
        inventory.setUpdatedAt(null);

        // Create the Inventory, which fails.
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInventories() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get all the inventoryList
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].productVariantId").value(hasItem(DEFAULT_PRODUCT_VARIANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get the inventory
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, inventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.productVariantId").value(DEFAULT_PRODUCT_VARIANT_ID.intValue()))
            .andExpect(jsonPath("$.warehouseId").value(DEFAULT_WAREHOUSE_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.intValue()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingInventory() throws Exception {
        // Get the inventory
        restInventoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventorySearchRepository.save(inventory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());

        // Update the inventory
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventory are not directly saved in db
        em.detach(updatedInventory);
        updatedInventory
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);
        InventoryDTO inventoryDTO = inventoryMapper.toDto(updatedInventory);

        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryToMatchAllProperties(updatedInventory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Inventory> inventorySearchList = Streamable.of(inventorySearchRepository.findAll()).toList();
                Inventory testInventorySearch = inventorySearchList.get(searchDatabaseSizeAfter - 1);

                assertInventoryAllPropertiesEquals(testInventorySearch, updatedInventory);
            });
    }

    @Test
    @Transactional
    void putNonExistingInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        inventory.setId(longCount.incrementAndGet());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        inventory.setId(longCount.incrementAndGet());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        inventory.setId(longCount.incrementAndGet());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory using partial update
        Inventory partialUpdatedInventory = new Inventory();
        partialUpdatedInventory.setId(inventory.getId());

        partialUpdatedInventory.productVariantId(UPDATED_PRODUCT_VARIANT_ID).warehouseId(UPDATED_WAREHOUSE_ID).quantity(UPDATED_QUANTITY);

        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventory))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventory, inventory),
            getPersistedInventory(inventory)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory using partial update
        Inventory partialUpdatedInventory = new Inventory();
        partialUpdatedInventory.setId(inventory.getId());

        partialUpdatedInventory
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);

        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventory))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryUpdatableFieldsEquals(partialUpdatedInventory, getPersistedInventory(partialUpdatedInventory));
    }

    @Test
    @Transactional
    void patchNonExistingInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        inventory.setId(longCount.incrementAndGet());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        inventory.setId(longCount.incrementAndGet());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        inventory.setId(longCount.incrementAndGet());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);
        inventoryRepository.save(inventory);
        inventorySearchRepository.save(inventory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the inventory
        restInventoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(inventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);
        inventorySearchRepository.save(inventory);

        // Search the inventory
        restInventoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + inventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].productVariantId").value(hasItem(DEFAULT_PRODUCT_VARIANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return inventoryRepository.count();
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

    protected Inventory getPersistedInventory(Inventory inventory) {
        return inventoryRepository.findById(inventory.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryToMatchAllProperties(Inventory expectedInventory) {
        assertInventoryAllPropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }

    protected void assertPersistedInventoryToMatchUpdatableProperties(Inventory expectedInventory) {
        assertInventoryAllUpdatablePropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }
}
