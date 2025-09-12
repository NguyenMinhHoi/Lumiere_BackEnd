package com.lumi.app.web.rest;

import static com.lumi.app.domain.WarehouseAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Warehouse;
import com.lumi.app.repository.WarehouseRepository;
import com.lumi.app.repository.search.WarehouseSearchRepository;
import com.lumi.app.service.dto.WarehouseDTO;
import com.lumi.app.service.mapper.WarehouseMapper;
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
 * Integration tests for the {@link WarehouseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/warehouses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/warehouses/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private WarehouseSearchRepository warehouseSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseMockMvc;

    private Warehouse warehouse;

    private Warehouse insertedWarehouse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createEntity() {
        return new Warehouse()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .address(DEFAULT_ADDRESS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createUpdatedEntity() {
        return new Warehouse()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        warehouse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWarehouse != null) {
            warehouseRepository.delete(insertedWarehouse);
            warehouseSearchRepository.delete(insertedWarehouse);
            insertedWarehouse = null;
        }
    }

    @Test
    @Transactional
    void createWarehouse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);
        var returnedWarehouseDTO = om.readValue(
            restWarehouseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseDTO.class
        );

        // Validate the Warehouse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouse = warehouseMapper.toEntity(returnedWarehouseDTO);
        assertWarehouseUpdatableFieldsEquals(returnedWarehouse, getPersistedWarehouse(returnedWarehouse));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedWarehouse = returnedWarehouse;
    }

    @Test
    @Transactional
    void createWarehouseWithExistingId() throws Exception {
        // Create the Warehouse with an existing ID
        warehouse.setId(1L);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        // set the field null
        warehouse.setCode(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        // set the field null
        warehouse.setName(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        // set the field null
        warehouse.setCreatedAt(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllWarehouses() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get the warehouse
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(warehouse.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingWarehouse() throws Exception {
        // Get the warehouse
        restWarehouseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseSearchRepository.save(warehouse);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());

        // Update the warehouse
        Warehouse updatedWarehouse = warehouseRepository.findById(warehouse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWarehouse are not directly saved in db
        em.detach(updatedWarehouse);
        updatedWarehouse
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(updatedWarehouse);

        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseToMatchAllProperties(updatedWarehouse);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Warehouse> warehouseSearchList = Streamable.of(warehouseSearchRepository.findAll()).toList();
                Warehouse testWarehouseSearch = warehouseSearchList.get(searchDatabaseSizeAfter - 1);

                assertWarehouseAllPropertiesEquals(testWarehouseSearch, updatedWarehouse);
            });
    }

    @Test
    @Transactional
    void putNonExistingWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse.createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWarehouse, warehouse),
            getPersistedWarehouse(warehouse)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseUpdatableFieldsEquals(partialUpdatedWarehouse, getPersistedWarehouse(partialUpdatedWarehouse));
    }

    @Test
    @Transactional
    void patchNonExistingWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);
        warehouseRepository.save(warehouse);
        warehouseSearchRepository.save(warehouse);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the warehouse
        restWarehouseMockMvc
            .perform(delete(ENTITY_API_URL_ID, warehouse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(warehouseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);
        warehouseSearchRepository.save(warehouse);

        // Search the warehouse
        restWarehouseMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + warehouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return warehouseRepository.count();
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

    protected Warehouse getPersistedWarehouse(Warehouse warehouse) {
        return warehouseRepository.findById(warehouse.getId()).orElseThrow();
    }

    protected void assertPersistedWarehouseToMatchAllProperties(Warehouse expectedWarehouse) {
        assertWarehouseAllPropertiesEquals(expectedWarehouse, getPersistedWarehouse(expectedWarehouse));
    }

    protected void assertPersistedWarehouseToMatchUpdatableProperties(Warehouse expectedWarehouse) {
        assertWarehouseAllUpdatablePropertiesEquals(expectedWarehouse, getPersistedWarehouse(expectedWarehouse));
    }
}
