package com.lumi.app.web.rest;

import static com.lumi.app.domain.ClothInventoryAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ClothInventory;
import com.lumi.app.repository.ClothInventoryRepository;
import com.lumi.app.repository.search.ClothInventorySearchRepository;
import com.lumi.app.service.dto.ClothInventoryDTO;
import com.lumi.app.service.mapper.ClothInventoryMapper;
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
 * Integration tests for the {@link ClothInventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClothInventoryResourceIT {

    private static final Long DEFAULT_CLOTH_ID = 1L;
    private static final Long UPDATED_CLOTH_ID = 2L;

    private static final Long DEFAULT_WAREHOUSE_ID = 1L;
    private static final Long UPDATED_WAREHOUSE_ID = 2L;

    private static final Long DEFAULT_QUANTITY = 0L;
    private static final Long UPDATED_QUANTITY = 1L;

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cloth-inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cloth-inventories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClothInventoryRepository clothInventoryRepository;

    @Autowired
    private ClothInventoryMapper clothInventoryMapper;

    @Autowired
    private ClothInventorySearchRepository clothInventorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClothInventoryMockMvc;

    private ClothInventory clothInventory;

    private ClothInventory insertedClothInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothInventory createEntity() {
        return new ClothInventory()
            .clothId(DEFAULT_CLOTH_ID)
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
    public static ClothInventory createUpdatedEntity() {
        return new ClothInventory()
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        clothInventory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClothInventory != null) {
            clothInventoryRepository.delete(insertedClothInventory);
            clothInventorySearchRepository.delete(insertedClothInventory);
            insertedClothInventory = null;
        }
    }

    @Test
    @Transactional
    void createClothInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);
        var returnedClothInventoryDTO = om.readValue(
            restClothInventoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClothInventoryDTO.class
        );

        // Validate the ClothInventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClothInventory = clothInventoryMapper.toEntity(returnedClothInventoryDTO);
        assertClothInventoryUpdatableFieldsEquals(returnedClothInventory, getPersistedClothInventory(returnedClothInventory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedClothInventory = returnedClothInventory;
    }

    @Test
    @Transactional
    void createClothInventoryWithExistingId() throws Exception {
        // Create the ClothInventory with an existing ID
        clothInventory.setId(1L);
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClothInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkClothIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        // set the field null
        clothInventory.setClothId(null);

        // Create the ClothInventory, which fails.
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        restClothInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWarehouseIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        // set the field null
        clothInventory.setWarehouseId(null);

        // Create the ClothInventory, which fails.
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        restClothInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        // set the field null
        clothInventory.setQuantity(null);

        // Create the ClothInventory, which fails.
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        restClothInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        // set the field null
        clothInventory.setUpdatedAt(null);

        // Create the ClothInventory, which fails.
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        restClothInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllClothInventories() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);

        // Get all the clothInventoryList
        restClothInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothInventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getClothInventory() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);

        // Get the clothInventory
        restClothInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, clothInventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clothInventory.getId().intValue()))
            .andExpect(jsonPath("$.clothId").value(DEFAULT_CLOTH_ID.intValue()))
            .andExpect(jsonPath("$.warehouseId").value(DEFAULT_WAREHOUSE_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.intValue()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingClothInventory() throws Exception {
        // Get the clothInventory
        restClothInventoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClothInventory() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        clothInventorySearchRepository.save(clothInventory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());

        // Update the clothInventory
        ClothInventory updatedClothInventory = clothInventoryRepository.findById(clothInventory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClothInventory are not directly saved in db
        em.detach(updatedClothInventory);
        updatedClothInventory
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(updatedClothInventory);

        restClothInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothInventoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothInventoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClothInventoryToMatchAllProperties(updatedClothInventory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ClothInventory> clothInventorySearchList = Streamable.of(clothInventorySearchRepository.findAll()).toList();
                ClothInventory testClothInventorySearch = clothInventorySearchList.get(searchDatabaseSizeAfter - 1);

                assertClothInventoryAllPropertiesEquals(testClothInventorySearch, updatedClothInventory);
            });
    }

    @Test
    @Transactional
    void putNonExistingClothInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        clothInventory.setId(longCount.incrementAndGet());

        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothInventoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchClothInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        clothInventory.setId(longCount.incrementAndGet());

        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClothInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        clothInventory.setId(longCount.incrementAndGet());

        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothInventoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateClothInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothInventory using partial update
        ClothInventory partialUpdatedClothInventory = new ClothInventory();
        partialUpdatedClothInventory.setId(clothInventory.getId());

        partialUpdatedClothInventory
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);

        restClothInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothInventory))
            )
            .andExpect(status().isOk());

        // Validate the ClothInventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothInventoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClothInventory, clothInventory),
            getPersistedClothInventory(clothInventory)
        );
    }

    @Test
    @Transactional
    void fullUpdateClothInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothInventory using partial update
        ClothInventory partialUpdatedClothInventory = new ClothInventory();
        partialUpdatedClothInventory.setId(clothInventory.getId());

        partialUpdatedClothInventory
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .quantity(UPDATED_QUANTITY)
            .updatedAt(UPDATED_UPDATED_AT);

        restClothInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothInventory))
            )
            .andExpect(status().isOk());

        // Validate the ClothInventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothInventoryUpdatableFieldsEquals(partialUpdatedClothInventory, getPersistedClothInventory(partialUpdatedClothInventory));
    }

    @Test
    @Transactional
    void patchNonExistingClothInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        clothInventory.setId(longCount.incrementAndGet());

        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clothInventoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClothInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        clothInventory.setId(longCount.incrementAndGet());

        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClothInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        clothInventory.setId(longCount.incrementAndGet());

        // Create the ClothInventory
        ClothInventoryDTO clothInventoryDTO = clothInventoryMapper.toDto(clothInventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothInventoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clothInventoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteClothInventory() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);
        clothInventoryRepository.save(clothInventory);
        clothInventorySearchRepository.save(clothInventory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the clothInventory
        restClothInventoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, clothInventory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothInventorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchClothInventory() throws Exception {
        // Initialize the database
        insertedClothInventory = clothInventoryRepository.saveAndFlush(clothInventory);
        clothInventorySearchRepository.save(clothInventory);

        // Search the clothInventory
        restClothInventoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + clothInventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothInventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return clothInventoryRepository.count();
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

    protected ClothInventory getPersistedClothInventory(ClothInventory clothInventory) {
        return clothInventoryRepository.findById(clothInventory.getId()).orElseThrow();
    }

    protected void assertPersistedClothInventoryToMatchAllProperties(ClothInventory expectedClothInventory) {
        assertClothInventoryAllPropertiesEquals(expectedClothInventory, getPersistedClothInventory(expectedClothInventory));
    }

    protected void assertPersistedClothInventoryToMatchUpdatableProperties(ClothInventory expectedClothInventory) {
        assertClothInventoryAllUpdatablePropertiesEquals(expectedClothInventory, getPersistedClothInventory(expectedClothInventory));
    }
}
