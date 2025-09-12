package com.lumi.app.web.rest;

import static com.lumi.app.domain.VoucherRedemptionAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.VoucherRedemption;
import com.lumi.app.repository.VoucherRedemptionRepository;
import com.lumi.app.repository.search.VoucherRedemptionSearchRepository;
import com.lumi.app.service.dto.VoucherRedemptionDTO;
import com.lumi.app.service.mapper.VoucherRedemptionMapper;
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
 * Integration tests for the {@link VoucherRedemptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VoucherRedemptionResourceIT {

    private static final Long DEFAULT_VOUCHER_ID = 1L;
    private static final Long UPDATED_VOUCHER_ID = 2L;

    private static final Long DEFAULT_ORDER_ID = 1L;
    private static final Long UPDATED_ORDER_ID = 2L;

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;

    private static final Instant DEFAULT_REDEEMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REDEEMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/voucher-redemptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/voucher-redemptions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VoucherRedemptionRepository voucherRedemptionRepository;

    @Autowired
    private VoucherRedemptionMapper voucherRedemptionMapper;

    @Autowired
    private VoucherRedemptionSearchRepository voucherRedemptionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVoucherRedemptionMockMvc;

    private VoucherRedemption voucherRedemption;

    private VoucherRedemption insertedVoucherRedemption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VoucherRedemption createEntity() {
        return new VoucherRedemption()
            .voucherId(DEFAULT_VOUCHER_ID)
            .orderId(DEFAULT_ORDER_ID)
            .customerId(DEFAULT_CUSTOMER_ID)
            .redeemedAt(DEFAULT_REDEEMED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VoucherRedemption createUpdatedEntity() {
        return new VoucherRedemption()
            .voucherId(UPDATED_VOUCHER_ID)
            .orderId(UPDATED_ORDER_ID)
            .customerId(UPDATED_CUSTOMER_ID)
            .redeemedAt(UPDATED_REDEEMED_AT);
    }

    @BeforeEach
    void initTest() {
        voucherRedemption = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVoucherRedemption != null) {
            voucherRedemptionRepository.delete(insertedVoucherRedemption);
            voucherRedemptionSearchRepository.delete(insertedVoucherRedemption);
            insertedVoucherRedemption = null;
        }
    }

    @Test
    @Transactional
    void createVoucherRedemption() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);
        var returnedVoucherRedemptionDTO = om.readValue(
            restVoucherRedemptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VoucherRedemptionDTO.class
        );

        // Validate the VoucherRedemption in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVoucherRedemption = voucherRedemptionMapper.toEntity(returnedVoucherRedemptionDTO);
        assertVoucherRedemptionUpdatableFieldsEquals(returnedVoucherRedemption, getPersistedVoucherRedemption(returnedVoucherRedemption));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedVoucherRedemption = returnedVoucherRedemption;
    }

    @Test
    @Transactional
    void createVoucherRedemptionWithExistingId() throws Exception {
        // Create the VoucherRedemption with an existing ID
        voucherRedemption.setId(1L);
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoucherRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkVoucherIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        // set the field null
        voucherRedemption.setVoucherId(null);

        // Create the VoucherRedemption, which fails.
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        restVoucherRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOrderIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        // set the field null
        voucherRedemption.setOrderId(null);

        // Create the VoucherRedemption, which fails.
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        restVoucherRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCustomerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        // set the field null
        voucherRedemption.setCustomerId(null);

        // Create the VoucherRedemption, which fails.
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        restVoucherRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRedeemedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        // set the field null
        voucherRedemption.setRedeemedAt(null);

        // Create the VoucherRedemption, which fails.
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        restVoucherRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllVoucherRedemptions() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);

        // Get all the voucherRedemptionList
        restVoucherRedemptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voucherRedemption.getId().intValue())))
            .andExpect(jsonPath("$.[*].voucherId").value(hasItem(DEFAULT_VOUCHER_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].redeemedAt").value(hasItem(DEFAULT_REDEEMED_AT.toString())));
    }

    @Test
    @Transactional
    void getVoucherRedemption() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);

        // Get the voucherRedemption
        restVoucherRedemptionMockMvc
            .perform(get(ENTITY_API_URL_ID, voucherRedemption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(voucherRedemption.getId().intValue()))
            .andExpect(jsonPath("$.voucherId").value(DEFAULT_VOUCHER_ID.intValue()))
            .andExpect(jsonPath("$.orderId").value(DEFAULT_ORDER_ID.intValue()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID.intValue()))
            .andExpect(jsonPath("$.redeemedAt").value(DEFAULT_REDEEMED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVoucherRedemption() throws Exception {
        // Get the voucherRedemption
        restVoucherRedemptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVoucherRedemption() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucherRedemptionSearchRepository.save(voucherRedemption);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());

        // Update the voucherRedemption
        VoucherRedemption updatedVoucherRedemption = voucherRedemptionRepository.findById(voucherRedemption.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVoucherRedemption are not directly saved in db
        em.detach(updatedVoucherRedemption);
        updatedVoucherRedemption
            .voucherId(UPDATED_VOUCHER_ID)
            .orderId(UPDATED_ORDER_ID)
            .customerId(UPDATED_CUSTOMER_ID)
            .redeemedAt(UPDATED_REDEEMED_AT);
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(updatedVoucherRedemption);

        restVoucherRedemptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voucherRedemptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(voucherRedemptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVoucherRedemptionToMatchAllProperties(updatedVoucherRedemption);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<VoucherRedemption> voucherRedemptionSearchList = Streamable.of(voucherRedemptionSearchRepository.findAll()).toList();
                VoucherRedemption testVoucherRedemptionSearch = voucherRedemptionSearchList.get(searchDatabaseSizeAfter - 1);

                assertVoucherRedemptionAllPropertiesEquals(testVoucherRedemptionSearch, updatedVoucherRedemption);
            });
    }

    @Test
    @Transactional
    void putNonExistingVoucherRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        voucherRedemption.setId(longCount.incrementAndGet());

        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoucherRedemptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voucherRedemptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(voucherRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchVoucherRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        voucherRedemption.setId(longCount.incrementAndGet());

        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherRedemptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(voucherRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVoucherRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        voucherRedemption.setId(longCount.incrementAndGet());

        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherRedemptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateVoucherRedemptionWithPatch() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucherRedemption using partial update
        VoucherRedemption partialUpdatedVoucherRedemption = new VoucherRedemption();
        partialUpdatedVoucherRedemption.setId(voucherRedemption.getId());

        partialUpdatedVoucherRedemption.voucherId(UPDATED_VOUCHER_ID).redeemedAt(UPDATED_REDEEMED_AT);

        restVoucherRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVoucherRedemption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVoucherRedemption))
            )
            .andExpect(status().isOk());

        // Validate the VoucherRedemption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVoucherRedemptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVoucherRedemption, voucherRedemption),
            getPersistedVoucherRedemption(voucherRedemption)
        );
    }

    @Test
    @Transactional
    void fullUpdateVoucherRedemptionWithPatch() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucherRedemption using partial update
        VoucherRedemption partialUpdatedVoucherRedemption = new VoucherRedemption();
        partialUpdatedVoucherRedemption.setId(voucherRedemption.getId());

        partialUpdatedVoucherRedemption
            .voucherId(UPDATED_VOUCHER_ID)
            .orderId(UPDATED_ORDER_ID)
            .customerId(UPDATED_CUSTOMER_ID)
            .redeemedAt(UPDATED_REDEEMED_AT);

        restVoucherRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVoucherRedemption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVoucherRedemption))
            )
            .andExpect(status().isOk());

        // Validate the VoucherRedemption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVoucherRedemptionUpdatableFieldsEquals(
            partialUpdatedVoucherRedemption,
            getPersistedVoucherRedemption(partialUpdatedVoucherRedemption)
        );
    }

    @Test
    @Transactional
    void patchNonExistingVoucherRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        voucherRedemption.setId(longCount.incrementAndGet());

        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoucherRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, voucherRedemptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(voucherRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVoucherRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        voucherRedemption.setId(longCount.incrementAndGet());

        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(voucherRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVoucherRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        voucherRedemption.setId(longCount.incrementAndGet());

        // Create the VoucherRedemption
        VoucherRedemptionDTO voucherRedemptionDTO = voucherRedemptionMapper.toDto(voucherRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherRedemptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(voucherRedemptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VoucherRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteVoucherRedemption() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);
        voucherRedemptionRepository.save(voucherRedemption);
        voucherRedemptionSearchRepository.save(voucherRedemption);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the voucherRedemption
        restVoucherRedemptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, voucherRedemption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherRedemptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchVoucherRedemption() throws Exception {
        // Initialize the database
        insertedVoucherRedemption = voucherRedemptionRepository.saveAndFlush(voucherRedemption);
        voucherRedemptionSearchRepository.save(voucherRedemption);

        // Search the voucherRedemption
        restVoucherRedemptionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + voucherRedemption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voucherRedemption.getId().intValue())))
            .andExpect(jsonPath("$.[*].voucherId").value(hasItem(DEFAULT_VOUCHER_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].redeemedAt").value(hasItem(DEFAULT_REDEEMED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return voucherRedemptionRepository.count();
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

    protected VoucherRedemption getPersistedVoucherRedemption(VoucherRedemption voucherRedemption) {
        return voucherRedemptionRepository.findById(voucherRedemption.getId()).orElseThrow();
    }

    protected void assertPersistedVoucherRedemptionToMatchAllProperties(VoucherRedemption expectedVoucherRedemption) {
        assertVoucherRedemptionAllPropertiesEquals(expectedVoucherRedemption, getPersistedVoucherRedemption(expectedVoucherRedemption));
    }

    protected void assertPersistedVoucherRedemptionToMatchUpdatableProperties(VoucherRedemption expectedVoucherRedemption) {
        assertVoucherRedemptionAllUpdatablePropertiesEquals(
            expectedVoucherRedemption,
            getPersistedVoucherRedemption(expectedVoucherRedemption)
        );
    }
}
