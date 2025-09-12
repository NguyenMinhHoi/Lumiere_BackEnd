package com.lumi.app.web.rest;

import static com.lumi.app.domain.CompanyConfigAdditionalAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.CompanyConfigAdditional;
import com.lumi.app.repository.CompanyConfigAdditionalRepository;
import com.lumi.app.repository.search.CompanyConfigAdditionalSearchRepository;
import com.lumi.app.service.dto.CompanyConfigAdditionalDTO;
import com.lumi.app.service.mapper.CompanyConfigAdditionalMapper;
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
 * Integration tests for the {@link CompanyConfigAdditionalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompanyConfigAdditionalResourceIT {

    private static final Long DEFAULT_COMPANY_CONFIG_ID = 1L;
    private static final Long UPDATED_COMPANY_CONFIG_ID = 2L;

    private static final String DEFAULT_CONFIG_KEY = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_VALUE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/company-config-additionals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/company-config-additionals/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CompanyConfigAdditionalRepository companyConfigAdditionalRepository;

    @Autowired
    private CompanyConfigAdditionalMapper companyConfigAdditionalMapper;

    @Autowired
    private CompanyConfigAdditionalSearchRepository companyConfigAdditionalSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompanyConfigAdditionalMockMvc;

    private CompanyConfigAdditional companyConfigAdditional;

    private CompanyConfigAdditional insertedCompanyConfigAdditional;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanyConfigAdditional createEntity() {
        return new CompanyConfigAdditional()
            .companyConfigId(DEFAULT_COMPANY_CONFIG_ID)
            .configKey(DEFAULT_CONFIG_KEY)
            .configValue(DEFAULT_CONFIG_VALUE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanyConfigAdditional createUpdatedEntity() {
        return new CompanyConfigAdditional()
            .companyConfigId(UPDATED_COMPANY_CONFIG_ID)
            .configKey(UPDATED_CONFIG_KEY)
            .configValue(UPDATED_CONFIG_VALUE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        companyConfigAdditional = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCompanyConfigAdditional != null) {
            companyConfigAdditionalRepository.delete(insertedCompanyConfigAdditional);
            companyConfigAdditionalSearchRepository.delete(insertedCompanyConfigAdditional);
            insertedCompanyConfigAdditional = null;
        }
    }

    @Test
    @Transactional
    void createCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);
        var returnedCompanyConfigAdditionalDTO = om.readValue(
            restCompanyConfigAdditionalMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigAdditionalDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CompanyConfigAdditionalDTO.class
        );

        // Validate the CompanyConfigAdditional in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCompanyConfigAdditional = companyConfigAdditionalMapper.toEntity(returnedCompanyConfigAdditionalDTO);
        assertCompanyConfigAdditionalUpdatableFieldsEquals(
            returnedCompanyConfigAdditional,
            getPersistedCompanyConfigAdditional(returnedCompanyConfigAdditional)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCompanyConfigAdditional = returnedCompanyConfigAdditional;
    }

    @Test
    @Transactional
    void createCompanyConfigAdditionalWithExistingId() throws Exception {
        // Create the CompanyConfigAdditional with an existing ID
        companyConfigAdditional.setId(1L);
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyConfigAdditionalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigAdditionalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCompanyConfigIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        // set the field null
        companyConfigAdditional.setCompanyConfigId(null);

        // Create the CompanyConfigAdditional, which fails.
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        restCompanyConfigAdditionalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigAdditionalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConfigKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        // set the field null
        companyConfigAdditional.setConfigKey(null);

        // Create the CompanyConfigAdditional, which fails.
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        restCompanyConfigAdditionalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigAdditionalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCompanyConfigAdditionals() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);

        // Get all the companyConfigAdditionalList
        restCompanyConfigAdditionalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyConfigAdditional.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyConfigId").value(hasItem(DEFAULT_COMPANY_CONFIG_ID.intValue())))
            .andExpect(jsonPath("$.[*].configKey").value(hasItem(DEFAULT_CONFIG_KEY)))
            .andExpect(jsonPath("$.[*].configValue").value(hasItem(DEFAULT_CONFIG_VALUE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCompanyConfigAdditional() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);

        // Get the companyConfigAdditional
        restCompanyConfigAdditionalMockMvc
            .perform(get(ENTITY_API_URL_ID, companyConfigAdditional.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(companyConfigAdditional.getId().intValue()))
            .andExpect(jsonPath("$.companyConfigId").value(DEFAULT_COMPANY_CONFIG_ID.intValue()))
            .andExpect(jsonPath("$.configKey").value(DEFAULT_CONFIG_KEY))
            .andExpect(jsonPath("$.configValue").value(DEFAULT_CONFIG_VALUE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCompanyConfigAdditional() throws Exception {
        // Get the companyConfigAdditional
        restCompanyConfigAdditionalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCompanyConfigAdditional() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        companyConfigAdditionalSearchRepository.save(companyConfigAdditional);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());

        // Update the companyConfigAdditional
        CompanyConfigAdditional updatedCompanyConfigAdditional = companyConfigAdditionalRepository
            .findById(companyConfigAdditional.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCompanyConfigAdditional are not directly saved in db
        em.detach(updatedCompanyConfigAdditional);
        updatedCompanyConfigAdditional
            .companyConfigId(UPDATED_COMPANY_CONFIG_ID)
            .configKey(UPDATED_CONFIG_KEY)
            .configValue(UPDATED_CONFIG_VALUE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(updatedCompanyConfigAdditional);

        restCompanyConfigAdditionalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companyConfigAdditionalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyConfigAdditionalDTO))
            )
            .andExpect(status().isOk());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCompanyConfigAdditionalToMatchAllProperties(updatedCompanyConfigAdditional);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CompanyConfigAdditional> companyConfigAdditionalSearchList = Streamable.of(
                    companyConfigAdditionalSearchRepository.findAll()
                ).toList();
                CompanyConfigAdditional testCompanyConfigAdditionalSearch = companyConfigAdditionalSearchList.get(
                    searchDatabaseSizeAfter - 1
                );

                assertCompanyConfigAdditionalAllPropertiesEquals(testCompanyConfigAdditionalSearch, updatedCompanyConfigAdditional);
            });
    }

    @Test
    @Transactional
    void putNonExistingCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        companyConfigAdditional.setId(longCount.incrementAndGet());

        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyConfigAdditionalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companyConfigAdditionalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyConfigAdditionalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        companyConfigAdditional.setId(longCount.incrementAndGet());

        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigAdditionalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyConfigAdditionalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        companyConfigAdditional.setId(longCount.incrementAndGet());

        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigAdditionalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigAdditionalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCompanyConfigAdditionalWithPatch() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companyConfigAdditional using partial update
        CompanyConfigAdditional partialUpdatedCompanyConfigAdditional = new CompanyConfigAdditional();
        partialUpdatedCompanyConfigAdditional.setId(companyConfigAdditional.getId());

        partialUpdatedCompanyConfigAdditional.configValue(UPDATED_CONFIG_VALUE).updatedAt(UPDATED_UPDATED_AT);

        restCompanyConfigAdditionalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanyConfigAdditional.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompanyConfigAdditional))
            )
            .andExpect(status().isOk());

        // Validate the CompanyConfigAdditional in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanyConfigAdditionalUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCompanyConfigAdditional, companyConfigAdditional),
            getPersistedCompanyConfigAdditional(companyConfigAdditional)
        );
    }

    @Test
    @Transactional
    void fullUpdateCompanyConfigAdditionalWithPatch() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companyConfigAdditional using partial update
        CompanyConfigAdditional partialUpdatedCompanyConfigAdditional = new CompanyConfigAdditional();
        partialUpdatedCompanyConfigAdditional.setId(companyConfigAdditional.getId());

        partialUpdatedCompanyConfigAdditional
            .companyConfigId(UPDATED_COMPANY_CONFIG_ID)
            .configKey(UPDATED_CONFIG_KEY)
            .configValue(UPDATED_CONFIG_VALUE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCompanyConfigAdditionalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanyConfigAdditional.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompanyConfigAdditional))
            )
            .andExpect(status().isOk());

        // Validate the CompanyConfigAdditional in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanyConfigAdditionalUpdatableFieldsEquals(
            partialUpdatedCompanyConfigAdditional,
            getPersistedCompanyConfigAdditional(partialUpdatedCompanyConfigAdditional)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        companyConfigAdditional.setId(longCount.incrementAndGet());

        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyConfigAdditionalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, companyConfigAdditionalDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companyConfigAdditionalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        companyConfigAdditional.setId(longCount.incrementAndGet());

        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigAdditionalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companyConfigAdditionalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompanyConfigAdditional() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        companyConfigAdditional.setId(longCount.incrementAndGet());

        // Create the CompanyConfigAdditional
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = companyConfigAdditionalMapper.toDto(companyConfigAdditional);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigAdditionalMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(companyConfigAdditionalDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanyConfigAdditional in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCompanyConfigAdditional() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);
        companyConfigAdditionalRepository.save(companyConfigAdditional);
        companyConfigAdditionalSearchRepository.save(companyConfigAdditional);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the companyConfigAdditional
        restCompanyConfigAdditionalMockMvc
            .perform(delete(ENTITY_API_URL_ID, companyConfigAdditional.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigAdditionalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCompanyConfigAdditional() throws Exception {
        // Initialize the database
        insertedCompanyConfigAdditional = companyConfigAdditionalRepository.saveAndFlush(companyConfigAdditional);
        companyConfigAdditionalSearchRepository.save(companyConfigAdditional);

        // Search the companyConfigAdditional
        restCompanyConfigAdditionalMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + companyConfigAdditional.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyConfigAdditional.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyConfigId").value(hasItem(DEFAULT_COMPANY_CONFIG_ID.intValue())))
            .andExpect(jsonPath("$.[*].configKey").value(hasItem(DEFAULT_CONFIG_KEY)))
            .andExpect(jsonPath("$.[*].configValue").value(hasItem(DEFAULT_CONFIG_VALUE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return companyConfigAdditionalRepository.count();
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

    protected CompanyConfigAdditional getPersistedCompanyConfigAdditional(CompanyConfigAdditional companyConfigAdditional) {
        return companyConfigAdditionalRepository.findById(companyConfigAdditional.getId()).orElseThrow();
    }

    protected void assertPersistedCompanyConfigAdditionalToMatchAllProperties(CompanyConfigAdditional expectedCompanyConfigAdditional) {
        assertCompanyConfigAdditionalAllPropertiesEquals(
            expectedCompanyConfigAdditional,
            getPersistedCompanyConfigAdditional(expectedCompanyConfigAdditional)
        );
    }

    protected void assertPersistedCompanyConfigAdditionalToMatchUpdatableProperties(
        CompanyConfigAdditional expectedCompanyConfigAdditional
    ) {
        assertCompanyConfigAdditionalAllUpdatablePropertiesEquals(
            expectedCompanyConfigAdditional,
            getPersistedCompanyConfigAdditional(expectedCompanyConfigAdditional)
        );
    }
}
