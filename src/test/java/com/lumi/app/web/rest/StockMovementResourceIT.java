package com.lumi.app.web.rest;

import static com.lumi.app.domain.StockMovementAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.StockMovement;
import com.lumi.app.domain.enumeration.StockMovementReason;
import com.lumi.app.repository.StockMovementRepository;
import com.lumi.app.repository.search.StockMovementSearchRepository;
import com.lumi.app.service.dto.StockMovementDTO;
import com.lumi.app.service.mapper.StockMovementMapper;
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
 * Integration tests for the {@link StockMovementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockMovementResourceIT {

    private static final Long DEFAULT_PRODUCT_VARIANT_ID = 1L;
    private static final Long UPDATED_PRODUCT_VARIANT_ID = 2L;

    private static final Long DEFAULT_WAREHOUSE_ID = 1L;
    private static final Long UPDATED_WAREHOUSE_ID = 2L;

    private static final Long DEFAULT_DELTA = 1L;
    private static final Long UPDATED_DELTA = 2L;

    private static final StockMovementReason DEFAULT_REASON = StockMovementReason.PURCHASE;
    private static final StockMovementReason UPDATED_REASON = StockMovementReason.SALE;

    private static final Long DEFAULT_REF_ORDER_ID = 1L;
    private static final Long UPDATED_REF_ORDER_ID = 2L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stock-movements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/stock-movements/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Autowired
    private StockMovementSearchRepository stockMovementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMovementMockMvc;

    private StockMovement stockMovement;

    private StockMovement insertedStockMovement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createEntity() {
        return new StockMovement()
            .productVariantId(DEFAULT_PRODUCT_VARIANT_ID)
            .warehouseId(DEFAULT_WAREHOUSE_ID)
            .delta(DEFAULT_DELTA)
            .reason(DEFAULT_REASON)
            .refOrderId(DEFAULT_REF_ORDER_ID)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createUpdatedEntity() {
        return new StockMovement()
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .delta(UPDATED_DELTA)
            .reason(UPDATED_REASON)
            .refOrderId(UPDATED_REF_ORDER_ID)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        stockMovement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStockMovement != null) {
            stockMovementRepository.delete(insertedStockMovement);
            stockMovementSearchRepository.delete(insertedStockMovement);
            insertedStockMovement = null;
        }
    }

    @Test
    @Transactional
    void createStockMovement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);
        var returnedStockMovementDTO = om.readValue(
            restStockMovementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockMovementDTO.class
        );

        // Validate the StockMovement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockMovement = stockMovementMapper.toEntity(returnedStockMovementDTO);
        assertStockMovementUpdatableFieldsEquals(returnedStockMovement, getPersistedStockMovement(returnedStockMovement));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedStockMovement = returnedStockMovement;
    }

    @Test
    @Transactional
    void createStockMovementWithExistingId() throws Exception {
        // Create the StockMovement with an existing ID
        stockMovement.setId(1L);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProductVariantIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setProductVariantId(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWarehouseIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setWarehouseId(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDeltaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setDelta(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setReason(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setCreatedAt(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStockMovements() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].productVariantId").value(hasItem(DEFAULT_PRODUCT_VARIANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].delta").value(hasItem(DEFAULT_DELTA.intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].refOrderId").value(hasItem(DEFAULT_REF_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get the stockMovement
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL_ID, stockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockMovement.getId().intValue()))
            .andExpect(jsonPath("$.productVariantId").value(DEFAULT_PRODUCT_VARIANT_ID.intValue()))
            .andExpect(jsonPath("$.warehouseId").value(DEFAULT_WAREHOUSE_ID.intValue()))
            .andExpect(jsonPath("$.delta").value(DEFAULT_DELTA.intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.refOrderId").value(DEFAULT_REF_ORDER_ID.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStockMovement() throws Exception {
        // Get the stockMovement
        restStockMovementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovementSearchRepository.save(stockMovement);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());

        // Update the stockMovement
        StockMovement updatedStockMovement = stockMovementRepository.findById(stockMovement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockMovement are not directly saved in db
        em.detach(updatedStockMovement);
        updatedStockMovement
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .delta(UPDATED_DELTA)
            .reason(UPDATED_REASON)
            .refOrderId(UPDATED_REF_ORDER_ID)
            .createdAt(UPDATED_CREATED_AT);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(updatedStockMovement);

        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockMovementToMatchAllProperties(updatedStockMovement);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<StockMovement> stockMovementSearchList = Streamable.of(stockMovementSearchRepository.findAll()).toList();
                StockMovement testStockMovementSearch = stockMovementSearchList.get(searchDatabaseSizeAfter - 1);

                assertStockMovementAllPropertiesEquals(testStockMovementSearch, updatedStockMovement);
            });
    }

    @Test
    @Transactional
    void putNonExistingStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement.productVariantId(UPDATED_PRODUCT_VARIANT_ID).warehouseId(UPDATED_WAREHOUSE_ID).delta(UPDATED_DELTA);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockMovementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockMovement, stockMovement),
            getPersistedStockMovement(stockMovement)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .delta(UPDATED_DELTA)
            .reason(UPDATED_REASON)
            .refOrderId(UPDATED_REF_ORDER_ID)
            .createdAt(UPDATED_CREATED_AT);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockMovementUpdatableFieldsEquals(partialUpdatedStockMovement, getPersistedStockMovement(partialUpdatedStockMovement));
    }

    @Test
    @Transactional
    void patchNonExistingStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);
        stockMovementRepository.save(stockMovement);
        stockMovementSearchRepository.save(stockMovement);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the stockMovement
        restStockMovementMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockMovement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);
        stockMovementSearchRepository.save(stockMovement);

        // Search the stockMovement
        restStockMovementMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].productVariantId").value(hasItem(DEFAULT_PRODUCT_VARIANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].delta").value(hasItem(DEFAULT_DELTA.intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].refOrderId").value(hasItem(DEFAULT_REF_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return stockMovementRepository.count();
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

    protected StockMovement getPersistedStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.findById(stockMovement.getId()).orElseThrow();
    }

    protected void assertPersistedStockMovementToMatchAllProperties(StockMovement expectedStockMovement) {
        assertStockMovementAllPropertiesEquals(expectedStockMovement, getPersistedStockMovement(expectedStockMovement));
    }

    protected void assertPersistedStockMovementToMatchUpdatableProperties(StockMovement expectedStockMovement) {
        assertStockMovementAllUpdatablePropertiesEquals(expectedStockMovement, getPersistedStockMovement(expectedStockMovement));
    }
}
