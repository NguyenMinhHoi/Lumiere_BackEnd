package com.lumi.app.web.rest;

import static com.lumi.app.domain.CompanyConfigAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.CompanyConfig;
import com.lumi.app.repository.CompanyConfigRepository;
import com.lumi.app.repository.search.CompanyConfigSearchRepository;
import com.lumi.app.service.dto.CompanyConfigDTO;
import com.lumi.app.service.mapper.CompanyConfigMapper;
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
 * Integration tests for the {@link CompanyConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompanyConfigResourceIT {

    private static final Long DEFAULT_COMPANY_ID = 1L;
    private static final Long UPDATED_COMPANY_ID = 2L;

    private static final Long DEFAULT_APP_ID = 1L;
    private static final Long UPDATED_APP_ID = 2L;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/company-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/company-configs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CompanyConfigRepository companyConfigRepository;

    @Autowired
    private CompanyConfigMapper companyConfigMapper;

    @Autowired
    private CompanyConfigSearchRepository companyConfigSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompanyConfigMockMvc;

    private CompanyConfig companyConfig;

    private CompanyConfig insertedCompanyConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanyConfig createEntity() {
        return new CompanyConfig()
            .companyId(DEFAULT_COMPANY_ID)
            .appId(DEFAULT_APP_ID)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanyConfig createUpdatedEntity() {
        return new CompanyConfig()
            .companyId(UPDATED_COMPANY_ID)
            .appId(UPDATED_APP_ID)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        companyConfig = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCompanyConfig != null) {
            companyConfigRepository.delete(insertedCompanyConfig);
            companyConfigSearchRepository.delete(insertedCompanyConfig);
            insertedCompanyConfig = null;
        }
    }

    @Test
    @Transactional
    void createCompanyConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);
        var returnedCompanyConfigDTO = om.readValue(
            restCompanyConfigMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CompanyConfigDTO.class
        );

        // Validate the CompanyConfig in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCompanyConfig = companyConfigMapper.toEntity(returnedCompanyConfigDTO);
        assertCompanyConfigUpdatableFieldsEquals(returnedCompanyConfig, getPersistedCompanyConfig(returnedCompanyConfig));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCompanyConfig = returnedCompanyConfig;
    }

    @Test
    @Transactional
    void createCompanyConfigWithExistingId() throws Exception {
        // Create the CompanyConfig with an existing ID
        companyConfig.setId(1L);
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCompanyIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        // set the field null
        companyConfig.setCompanyId(null);

        // Create the CompanyConfig, which fails.
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        restCompanyConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAppIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        // set the field null
        companyConfig.setAppId(null);

        // Create the CompanyConfig, which fails.
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        restCompanyConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCompanyConfigs() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);

        // Get all the companyConfigList
        restCompanyConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyId").value(hasItem(DEFAULT_COMPANY_ID.intValue())))
            .andExpect(jsonPath("$.[*].appId").value(hasItem(DEFAULT_APP_ID.intValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCompanyConfig() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);

        // Get the companyConfig
        restCompanyConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, companyConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(companyConfig.getId().intValue()))
            .andExpect(jsonPath("$.companyId").value(DEFAULT_COMPANY_ID.intValue()))
            .andExpect(jsonPath("$.appId").value(DEFAULT_APP_ID.intValue()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCompanyConfig() throws Exception {
        // Get the companyConfig
        restCompanyConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCompanyConfig() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        companyConfigSearchRepository.save(companyConfig);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());

        // Update the companyConfig
        CompanyConfig updatedCompanyConfig = companyConfigRepository.findById(companyConfig.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCompanyConfig are not directly saved in db
        em.detach(updatedCompanyConfig);
        updatedCompanyConfig
            .companyId(UPDATED_COMPANY_ID)
            .appId(UPDATED_APP_ID)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(updatedCompanyConfig);

        restCompanyConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companyConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyConfigDTO))
            )
            .andExpect(status().isOk());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCompanyConfigToMatchAllProperties(updatedCompanyConfig);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CompanyConfig> companyConfigSearchList = Streamable.of(companyConfigSearchRepository.findAll()).toList();
                CompanyConfig testCompanyConfigSearch = companyConfigSearchList.get(searchDatabaseSizeAfter - 1);

                assertCompanyConfigAllPropertiesEquals(testCompanyConfigSearch, updatedCompanyConfig);
            });
    }

    @Test
    @Transactional
    void putNonExistingCompanyConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        companyConfig.setId(longCount.incrementAndGet());

        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companyConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompanyConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        companyConfig.setId(longCount.incrementAndGet());

        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompanyConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        companyConfig.setId(longCount.incrementAndGet());

        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCompanyConfigWithPatch() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companyConfig using partial update
        CompanyConfig partialUpdatedCompanyConfig = new CompanyConfig();
        partialUpdatedCompanyConfig.setId(companyConfig.getId());

        partialUpdatedCompanyConfig.companyId(UPDATED_COMPANY_ID);

        restCompanyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanyConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompanyConfig))
            )
            .andExpect(status().isOk());

        // Validate the CompanyConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanyConfigUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCompanyConfig, companyConfig),
            getPersistedCompanyConfig(companyConfig)
        );
    }

    @Test
    @Transactional
    void fullUpdateCompanyConfigWithPatch() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companyConfig using partial update
        CompanyConfig partialUpdatedCompanyConfig = new CompanyConfig();
        partialUpdatedCompanyConfig.setId(companyConfig.getId());

        partialUpdatedCompanyConfig
            .companyId(UPDATED_COMPANY_ID)
            .appId(UPDATED_APP_ID)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCompanyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanyConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompanyConfig))
            )
            .andExpect(status().isOk());

        // Validate the CompanyConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanyConfigUpdatableFieldsEquals(partialUpdatedCompanyConfig, getPersistedCompanyConfig(partialUpdatedCompanyConfig));
    }

    @Test
    @Transactional
    void patchNonExistingCompanyConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        companyConfig.setId(longCount.incrementAndGet());

        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, companyConfigDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companyConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompanyConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        companyConfig.setId(longCount.incrementAndGet());

        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companyConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompanyConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        companyConfig.setId(longCount.incrementAndGet());

        // Create the CompanyConfig
        CompanyConfigDTO companyConfigDTO = companyConfigMapper.toDto(companyConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyConfigMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(companyConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanyConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCompanyConfig() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);
        companyConfigRepository.save(companyConfig);
        companyConfigSearchRepository.save(companyConfig);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the companyConfig
        restCompanyConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, companyConfig.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companyConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCompanyConfig() throws Exception {
        // Initialize the database
        insertedCompanyConfig = companyConfigRepository.saveAndFlush(companyConfig);
        companyConfigSearchRepository.save(companyConfig);

        // Search the companyConfig
        restCompanyConfigMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + companyConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyId").value(hasItem(DEFAULT_COMPANY_ID.intValue())))
            .andExpect(jsonPath("$.[*].appId").value(hasItem(DEFAULT_APP_ID.intValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return companyConfigRepository.count();
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

    protected CompanyConfig getPersistedCompanyConfig(CompanyConfig companyConfig) {
        return companyConfigRepository.findById(companyConfig.getId()).orElseThrow();
    }

    protected void assertPersistedCompanyConfigToMatchAllProperties(CompanyConfig expectedCompanyConfig) {
        assertCompanyConfigAllPropertiesEquals(expectedCompanyConfig, getPersistedCompanyConfig(expectedCompanyConfig));
    }

    protected void assertPersistedCompanyConfigToMatchUpdatableProperties(CompanyConfig expectedCompanyConfig) {
        assertCompanyConfigAllUpdatablePropertiesEquals(expectedCompanyConfig, getPersistedCompanyConfig(expectedCompanyConfig));
    }
}
