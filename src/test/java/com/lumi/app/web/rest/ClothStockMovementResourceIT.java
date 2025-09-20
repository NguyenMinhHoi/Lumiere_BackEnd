package com.lumi.app.web.rest;

import static com.lumi.app.domain.ClothStockMovementAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ClothStockMovement;
import com.lumi.app.domain.enumeration.StockMovementReason;
import com.lumi.app.repository.ClothStockMovementRepository;
import com.lumi.app.repository.search.ClothStockMovementSearchRepository;
import com.lumi.app.service.dto.ClothStockMovementDTO;
import com.lumi.app.service.mapper.ClothStockMovementMapper;
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
 * Integration tests for the {@link ClothStockMovementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClothStockMovementResourceIT {

    private static final Long DEFAULT_CLOTH_ID = 1L;
    private static final Long UPDATED_CLOTH_ID = 2L;

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

    private static final String ENTITY_API_URL = "/api/cloth-stock-movements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cloth-stock-movements/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClothStockMovementRepository clothStockMovementRepository;

    @Autowired
    private ClothStockMovementMapper clothStockMovementMapper;

    @Autowired
    private ClothStockMovementSearchRepository clothStockMovementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClothStockMovementMockMvc;

    private ClothStockMovement clothStockMovement;

    private ClothStockMovement insertedClothStockMovement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothStockMovement createEntity() {
        return new ClothStockMovement()
            .clothId(DEFAULT_CLOTH_ID)
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
    public static ClothStockMovement createUpdatedEntity() {
        return new ClothStockMovement()
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .delta(UPDATED_DELTA)
            .reason(UPDATED_REASON)
            .refOrderId(UPDATED_REF_ORDER_ID)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        clothStockMovement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClothStockMovement != null) {
            clothStockMovementRepository.delete(insertedClothStockMovement);
            clothStockMovementSearchRepository.delete(insertedClothStockMovement);
            insertedClothStockMovement = null;
        }
    }

    @Test
    @Transactional
    void createClothStockMovement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);
        var returnedClothStockMovementDTO = om.readValue(
            restClothStockMovementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClothStockMovementDTO.class
        );

        // Validate the ClothStockMovement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClothStockMovement = clothStockMovementMapper.toEntity(returnedClothStockMovementDTO);
        assertClothStockMovementUpdatableFieldsEquals(
            returnedClothStockMovement,
            getPersistedClothStockMovement(returnedClothStockMovement)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedClothStockMovement = returnedClothStockMovement;
    }

    @Test
    @Transactional
    void createClothStockMovementWithExistingId() throws Exception {
        // Create the ClothStockMovement with an existing ID
        clothStockMovement.setId(1L);
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClothStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkClothIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        // set the field null
        clothStockMovement.setClothId(null);

        // Create the ClothStockMovement, which fails.
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        restClothStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWarehouseIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        // set the field null
        clothStockMovement.setWarehouseId(null);

        // Create the ClothStockMovement, which fails.
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        restClothStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDeltaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        // set the field null
        clothStockMovement.setDelta(null);

        // Create the ClothStockMovement, which fails.
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        restClothStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        // set the field null
        clothStockMovement.setReason(null);

        // Create the ClothStockMovement, which fails.
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        restClothStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        // set the field null
        clothStockMovement.setCreatedAt(null);

        // Create the ClothStockMovement, which fails.
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        restClothStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllClothStockMovements() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);

        // Get all the clothStockMovementList
        restClothStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothStockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].delta").value(hasItem(DEFAULT_DELTA.intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].refOrderId").value(hasItem(DEFAULT_REF_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getClothStockMovement() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);

        // Get the clothStockMovement
        restClothStockMovementMockMvc
            .perform(get(ENTITY_API_URL_ID, clothStockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clothStockMovement.getId().intValue()))
            .andExpect(jsonPath("$.clothId").value(DEFAULT_CLOTH_ID.intValue()))
            .andExpect(jsonPath("$.warehouseId").value(DEFAULT_WAREHOUSE_ID.intValue()))
            .andExpect(jsonPath("$.delta").value(DEFAULT_DELTA.intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.refOrderId").value(DEFAULT_REF_ORDER_ID.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingClothStockMovement() throws Exception {
        // Get the clothStockMovement
        restClothStockMovementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClothStockMovement() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        clothStockMovementSearchRepository.save(clothStockMovement);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());

        // Update the clothStockMovement
        ClothStockMovement updatedClothStockMovement = clothStockMovementRepository.findById(clothStockMovement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClothStockMovement are not directly saved in db
        em.detach(updatedClothStockMovement);
        updatedClothStockMovement
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .delta(UPDATED_DELTA)
            .reason(UPDATED_REASON)
            .refOrderId(UPDATED_REF_ORDER_ID)
            .createdAt(UPDATED_CREATED_AT);
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(updatedClothStockMovement);

        restClothStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothStockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothStockMovementDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClothStockMovementToMatchAllProperties(updatedClothStockMovement);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ClothStockMovement> clothStockMovementSearchList = Streamable.of(
                    clothStockMovementSearchRepository.findAll()
                ).toList();
                ClothStockMovement testClothStockMovementSearch = clothStockMovementSearchList.get(searchDatabaseSizeAfter - 1);

                assertClothStockMovementAllPropertiesEquals(testClothStockMovementSearch, updatedClothStockMovement);
            });
    }

    @Test
    @Transactional
    void putNonExistingClothStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        clothStockMovement.setId(longCount.incrementAndGet());

        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothStockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothStockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchClothStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        clothStockMovement.setId(longCount.incrementAndGet());

        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothStockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClothStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        clothStockMovement.setId(longCount.incrementAndGet());

        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothStockMovementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateClothStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothStockMovement using partial update
        ClothStockMovement partialUpdatedClothStockMovement = new ClothStockMovement();
        partialUpdatedClothStockMovement.setId(clothStockMovement.getId());

        restClothStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the ClothStockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothStockMovementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClothStockMovement, clothStockMovement),
            getPersistedClothStockMovement(clothStockMovement)
        );
    }

    @Test
    @Transactional
    void fullUpdateClothStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothStockMovement using partial update
        ClothStockMovement partialUpdatedClothStockMovement = new ClothStockMovement();
        partialUpdatedClothStockMovement.setId(clothStockMovement.getId());

        partialUpdatedClothStockMovement
            .clothId(UPDATED_CLOTH_ID)
            .warehouseId(UPDATED_WAREHOUSE_ID)
            .delta(UPDATED_DELTA)
            .reason(UPDATED_REASON)
            .refOrderId(UPDATED_REF_ORDER_ID)
            .createdAt(UPDATED_CREATED_AT);

        restClothStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the ClothStockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothStockMovementUpdatableFieldsEquals(
            partialUpdatedClothStockMovement,
            getPersistedClothStockMovement(partialUpdatedClothStockMovement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingClothStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        clothStockMovement.setId(longCount.incrementAndGet());

        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clothStockMovementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothStockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClothStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        clothStockMovement.setId(longCount.incrementAndGet());

        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothStockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClothStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        clothStockMovement.setId(longCount.incrementAndGet());

        // Create the ClothStockMovement
        ClothStockMovementDTO clothStockMovementDTO = clothStockMovementMapper.toDto(clothStockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothStockMovementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clothStockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothStockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteClothStockMovement() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);
        clothStockMovementRepository.save(clothStockMovement);
        clothStockMovementSearchRepository.save(clothStockMovement);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the clothStockMovement
        restClothStockMovementMockMvc
            .perform(delete(ENTITY_API_URL_ID, clothStockMovement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothStockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchClothStockMovement() throws Exception {
        // Initialize the database
        insertedClothStockMovement = clothStockMovementRepository.saveAndFlush(clothStockMovement);
        clothStockMovementSearchRepository.save(clothStockMovement);

        // Search the clothStockMovement
        restClothStockMovementMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + clothStockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothStockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].warehouseId").value(hasItem(DEFAULT_WAREHOUSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].delta").value(hasItem(DEFAULT_DELTA.intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].refOrderId").value(hasItem(DEFAULT_REF_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return clothStockMovementRepository.count();
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

    protected ClothStockMovement getPersistedClothStockMovement(ClothStockMovement clothStockMovement) {
        return clothStockMovementRepository.findById(clothStockMovement.getId()).orElseThrow();
    }

    protected void assertPersistedClothStockMovementToMatchAllProperties(ClothStockMovement expectedClothStockMovement) {
        assertClothStockMovementAllPropertiesEquals(expectedClothStockMovement, getPersistedClothStockMovement(expectedClothStockMovement));
    }

    protected void assertPersistedClothStockMovementToMatchUpdatableProperties(ClothStockMovement expectedClothStockMovement) {
        assertClothStockMovementAllUpdatablePropertiesEquals(
            expectedClothStockMovement,
            getPersistedClothStockMovement(expectedClothStockMovement)
        );
    }
}
