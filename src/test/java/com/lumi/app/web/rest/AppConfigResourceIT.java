package com.lumi.app.web.rest;

import static com.lumi.app.domain.AppConfigAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.AppConfig;
import com.lumi.app.repository.AppConfigRepository;
import com.lumi.app.repository.search.AppConfigSearchRepository;
import com.lumi.app.service.dto.AppConfigDTO;
import com.lumi.app.service.mapper.AppConfigMapper;
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
 * Integration tests for the {@link AppConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppConfigResourceIT {

    private static final String DEFAULT_APP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_APP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG_KEY = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_VALUE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/app-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/app-configs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Autowired
    private AppConfigMapper appConfigMapper;

    @Autowired
    private AppConfigSearchRepository appConfigSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppConfigMockMvc;

    private AppConfig appConfig;

    private AppConfig insertedAppConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppConfig createEntity() {
        return new AppConfig()
            .appCode(DEFAULT_APP_CODE)
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
    public static AppConfig createUpdatedEntity() {
        return new AppConfig()
            .appCode(UPDATED_APP_CODE)
            .configKey(UPDATED_CONFIG_KEY)
            .configValue(UPDATED_CONFIG_VALUE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        appConfig = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppConfig != null) {
            appConfigRepository.delete(insertedAppConfig);
            appConfigSearchRepository.delete(insertedAppConfig);
            insertedAppConfig = null;
        }
    }

    @Test
    @Transactional
    void createAppConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);
        var returnedAppConfigDTO = om.readValue(
            restAppConfigMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appConfigDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppConfigDTO.class
        );

        // Validate the AppConfig in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppConfig = appConfigMapper.toEntity(returnedAppConfigDTO);
        assertAppConfigUpdatableFieldsEquals(returnedAppConfig, getPersistedAppConfig(returnedAppConfig));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedAppConfig = returnedAppConfig;
    }

    @Test
    @Transactional
    void createAppConfigWithExistingId() throws Exception {
        // Create the AppConfig with an existing ID
        appConfig.setId(1L);
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appConfigDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAppCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        // set the field null
        appConfig.setAppCode(null);

        // Create the AppConfig, which fails.
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        restAppConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConfigKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        // set the field null
        appConfig.setConfigKey(null);

        // Create the AppConfig, which fails.
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        restAppConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAppConfigs() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);

        // Get all the appConfigList
        restAppConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].appCode").value(hasItem(DEFAULT_APP_CODE)))
            .andExpect(jsonPath("$.[*].configKey").value(hasItem(DEFAULT_CONFIG_KEY)))
            .andExpect(jsonPath("$.[*].configValue").value(hasItem(DEFAULT_CONFIG_VALUE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAppConfig() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);

        // Get the appConfig
        restAppConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, appConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appConfig.getId().intValue()))
            .andExpect(jsonPath("$.appCode").value(DEFAULT_APP_CODE))
            .andExpect(jsonPath("$.configKey").value(DEFAULT_CONFIG_KEY))
            .andExpect(jsonPath("$.configValue").value(DEFAULT_CONFIG_VALUE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAppConfig() throws Exception {
        // Get the appConfig
        restAppConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppConfig() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        appConfigSearchRepository.save(appConfig);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());

        // Update the appConfig
        AppConfig updatedAppConfig = appConfigRepository.findById(appConfig.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppConfig are not directly saved in db
        em.detach(updatedAppConfig);
        updatedAppConfig
            .appCode(UPDATED_APP_CODE)
            .configKey(UPDATED_CONFIG_KEY)
            .configValue(UPDATED_CONFIG_VALUE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(updatedAppConfig);

        restAppConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appConfigDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppConfigToMatchAllProperties(updatedAppConfig);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AppConfig> appConfigSearchList = Streamable.of(appConfigSearchRepository.findAll()).toList();
                AppConfig testAppConfigSearch = appConfigSearchList.get(searchDatabaseSizeAfter - 1);

                assertAppConfigAllPropertiesEquals(testAppConfigSearch, updatedAppConfig);
            });
    }

    @Test
    @Transactional
    void putNonExistingAppConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        appConfig.setId(longCount.incrementAndGet());

        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        appConfig.setId(longCount.incrementAndGet());

        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        appConfig.setId(longCount.incrementAndGet());

        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAppConfigWithPatch() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appConfig using partial update
        AppConfig partialUpdatedAppConfig = new AppConfig();
        partialUpdatedAppConfig.setId(appConfig.getId());

        partialUpdatedAppConfig.appCode(UPDATED_APP_CODE).configKey(UPDATED_CONFIG_KEY).updatedAt(UPDATED_UPDATED_AT);

        restAppConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppConfig))
            )
            .andExpect(status().isOk());

        // Validate the AppConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppConfigUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppConfig, appConfig),
            getPersistedAppConfig(appConfig)
        );
    }

    @Test
    @Transactional
    void fullUpdateAppConfigWithPatch() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appConfig using partial update
        AppConfig partialUpdatedAppConfig = new AppConfig();
        partialUpdatedAppConfig.setId(appConfig.getId());

        partialUpdatedAppConfig
            .appCode(UPDATED_APP_CODE)
            .configKey(UPDATED_CONFIG_KEY)
            .configValue(UPDATED_CONFIG_VALUE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restAppConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppConfig))
            )
            .andExpect(status().isOk());

        // Validate the AppConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppConfigUpdatableFieldsEquals(partialUpdatedAppConfig, getPersistedAppConfig(partialUpdatedAppConfig));
    }

    @Test
    @Transactional
    void patchNonExistingAppConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        appConfig.setId(longCount.incrementAndGet());

        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appConfigDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        appConfig.setId(longCount.incrementAndGet());

        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        appConfig.setId(longCount.incrementAndGet());

        // Create the AppConfig
        AppConfigDTO appConfigDTO = appConfigMapper.toDto(appConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAppConfig() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);
        appConfigRepository.save(appConfig);
        appConfigSearchRepository.save(appConfig);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the appConfig
        restAppConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, appConfig.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAppConfig() throws Exception {
        // Initialize the database
        insertedAppConfig = appConfigRepository.saveAndFlush(appConfig);
        appConfigSearchRepository.save(appConfig);

        // Search the appConfig
        restAppConfigMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + appConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].appCode").value(hasItem(DEFAULT_APP_CODE)))
            .andExpect(jsonPath("$.[*].configKey").value(hasItem(DEFAULT_CONFIG_KEY)))
            .andExpect(jsonPath("$.[*].configValue").value(hasItem(DEFAULT_CONFIG_VALUE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return appConfigRepository.count();
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

    protected AppConfig getPersistedAppConfig(AppConfig appConfig) {
        return appConfigRepository.findById(appConfig.getId()).orElseThrow();
    }

    protected void assertPersistedAppConfigToMatchAllProperties(AppConfig expectedAppConfig) {
        assertAppConfigAllPropertiesEquals(expectedAppConfig, getPersistedAppConfig(expectedAppConfig));
    }

    protected void assertPersistedAppConfigToMatchUpdatableProperties(AppConfig expectedAppConfig) {
        assertAppConfigAllUpdatablePropertiesEquals(expectedAppConfig, getPersistedAppConfig(expectedAppConfig));
    }
}
