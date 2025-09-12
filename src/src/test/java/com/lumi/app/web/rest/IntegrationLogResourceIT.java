package com.lumi.app.web.rest;

import static com.lumi.app.domain.IntegrationLogAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.IntegrationLog;
import com.lumi.app.domain.enumeration.AppType;
import com.lumi.app.domain.enumeration.AppType;
import com.lumi.app.domain.enumeration.IntegrationStatus;
import com.lumi.app.repository.IntegrationLogRepository;
import com.lumi.app.repository.search.IntegrationLogSearchRepository;
import com.lumi.app.service.dto.IntegrationLogDTO;
import com.lumi.app.service.mapper.IntegrationLogMapper;
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
 * Integration tests for the {@link IntegrationLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IntegrationLogResourceIT {

    private static final AppType DEFAULT_SOURCE_APP = AppType.SHOPEE;
    private static final AppType UPDATED_SOURCE_APP = AppType.TIKTOK;

    private static final AppType DEFAULT_TARGET_APP = AppType.SHOPEE;
    private static final AppType UPDATED_TARGET_APP = AppType.TIKTOK;

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE = "BBBBBBBBBB";

    private static final IntegrationStatus DEFAULT_STATUS = IntegrationStatus.PENDING;
    private static final IntegrationStatus UPDATED_STATUS = IntegrationStatus.SUCCESS;

    private static final Integer DEFAULT_RETRIES = 0;
    private static final Integer UPDATED_RETRIES = 1;
    private static final Integer SMALLER_RETRIES = 0 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/integration-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/integration-logs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IntegrationLogRepository integrationLogRepository;

    @Autowired
    private IntegrationLogMapper integrationLogMapper;

    @Autowired
    private IntegrationLogSearchRepository integrationLogSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIntegrationLogMockMvc;

    private IntegrationLog integrationLog;

    private IntegrationLog insertedIntegrationLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntegrationLog createEntity() {
        return new IntegrationLog()
            .sourceApp(DEFAULT_SOURCE_APP)
            .targetApp(DEFAULT_TARGET_APP)
            .payload(DEFAULT_PAYLOAD)
            .response(DEFAULT_RESPONSE)
            .status(DEFAULT_STATUS)
            .retries(DEFAULT_RETRIES)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntegrationLog createUpdatedEntity() {
        return new IntegrationLog()
            .sourceApp(UPDATED_SOURCE_APP)
            .targetApp(UPDATED_TARGET_APP)
            .payload(UPDATED_PAYLOAD)
            .response(UPDATED_RESPONSE)
            .status(UPDATED_STATUS)
            .retries(UPDATED_RETRIES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        integrationLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIntegrationLog != null) {
            integrationLogRepository.delete(insertedIntegrationLog);
            integrationLogSearchRepository.delete(insertedIntegrationLog);
            insertedIntegrationLog = null;
        }
    }

    @Test
    @Transactional
    void createIntegrationLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);
        var returnedIntegrationLogDTO = om.readValue(
            restIntegrationLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IntegrationLogDTO.class
        );

        // Validate the IntegrationLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIntegrationLog = integrationLogMapper.toEntity(returnedIntegrationLogDTO);
        assertIntegrationLogUpdatableFieldsEquals(returnedIntegrationLog, getPersistedIntegrationLog(returnedIntegrationLog));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedIntegrationLog = returnedIntegrationLog;
    }

    @Test
    @Transactional
    void createIntegrationLogWithExistingId() throws Exception {
        // Create the IntegrationLog with an existing ID
        integrationLog.setId(1L);
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntegrationLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSourceAppIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        // set the field null
        integrationLog.setSourceApp(null);

        // Create the IntegrationLog, which fails.
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        restIntegrationLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTargetAppIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        // set the field null
        integrationLog.setTargetApp(null);

        // Create the IntegrationLog, which fails.
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        restIntegrationLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        // set the field null
        integrationLog.setStatus(null);

        // Create the IntegrationLog, which fails.
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        restIntegrationLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRetriesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        // set the field null
        integrationLog.setRetries(null);

        // Create the IntegrationLog, which fails.
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        restIntegrationLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        // set the field null
        integrationLog.setCreatedAt(null);

        // Create the IntegrationLog, which fails.
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        restIntegrationLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllIntegrationLogs() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList
        restIntegrationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integrationLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceApp").value(hasItem(DEFAULT_SOURCE_APP.toString())))
            .andExpect(jsonPath("$.[*].targetApp").value(hasItem(DEFAULT_TARGET_APP.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].response").value(hasItem(DEFAULT_RESPONSE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].retries").value(hasItem(DEFAULT_RETRIES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getIntegrationLog() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get the integrationLog
        restIntegrationLogMockMvc
            .perform(get(ENTITY_API_URL_ID, integrationLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(integrationLog.getId().intValue()))
            .andExpect(jsonPath("$.sourceApp").value(DEFAULT_SOURCE_APP.toString()))
            .andExpect(jsonPath("$.targetApp").value(DEFAULT_TARGET_APP.toString()))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.response").value(DEFAULT_RESPONSE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.retries").value(DEFAULT_RETRIES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getIntegrationLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        Long id = integrationLog.getId();

        defaultIntegrationLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultIntegrationLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultIntegrationLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsBySourceAppIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where sourceApp equals to
        defaultIntegrationLogFiltering("sourceApp.equals=" + DEFAULT_SOURCE_APP, "sourceApp.equals=" + UPDATED_SOURCE_APP);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsBySourceAppIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where sourceApp in
        defaultIntegrationLogFiltering(
            "sourceApp.in=" + DEFAULT_SOURCE_APP + "," + UPDATED_SOURCE_APP,
            "sourceApp.in=" + UPDATED_SOURCE_APP
        );
    }

    @Test
    @Transactional
    void getAllIntegrationLogsBySourceAppIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where sourceApp is not null
        defaultIntegrationLogFiltering("sourceApp.specified=true", "sourceApp.specified=false");
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByTargetAppIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where targetApp equals to
        defaultIntegrationLogFiltering("targetApp.equals=" + DEFAULT_TARGET_APP, "targetApp.equals=" + UPDATED_TARGET_APP);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByTargetAppIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where targetApp in
        defaultIntegrationLogFiltering(
            "targetApp.in=" + DEFAULT_TARGET_APP + "," + UPDATED_TARGET_APP,
            "targetApp.in=" + UPDATED_TARGET_APP
        );
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByTargetAppIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where targetApp is not null
        defaultIntegrationLogFiltering("targetApp.specified=true", "targetApp.specified=false");
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where status equals to
        defaultIntegrationLogFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where status in
        defaultIntegrationLogFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where status is not null
        defaultIntegrationLogFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries equals to
        defaultIntegrationLogFiltering("retries.equals=" + DEFAULT_RETRIES, "retries.equals=" + UPDATED_RETRIES);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries in
        defaultIntegrationLogFiltering("retries.in=" + DEFAULT_RETRIES + "," + UPDATED_RETRIES, "retries.in=" + UPDATED_RETRIES);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries is not null
        defaultIntegrationLogFiltering("retries.specified=true", "retries.specified=false");
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries is greater than or equal to
        defaultIntegrationLogFiltering("retries.greaterThanOrEqual=" + DEFAULT_RETRIES, "retries.greaterThanOrEqual=" + UPDATED_RETRIES);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries is less than or equal to
        defaultIntegrationLogFiltering("retries.lessThanOrEqual=" + DEFAULT_RETRIES, "retries.lessThanOrEqual=" + SMALLER_RETRIES);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries is less than
        defaultIntegrationLogFiltering("retries.lessThan=" + UPDATED_RETRIES, "retries.lessThan=" + DEFAULT_RETRIES);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByRetriesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where retries is greater than
        defaultIntegrationLogFiltering("retries.greaterThan=" + SMALLER_RETRIES, "retries.greaterThan=" + DEFAULT_RETRIES);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where createdAt equals to
        defaultIntegrationLogFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where createdAt in
        defaultIntegrationLogFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where createdAt is not null
        defaultIntegrationLogFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where updatedAt equals to
        defaultIntegrationLogFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where updatedAt in
        defaultIntegrationLogFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllIntegrationLogsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        // Get all the integrationLogList where updatedAt is not null
        defaultIntegrationLogFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultIntegrationLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultIntegrationLogShouldBeFound(shouldBeFound);
        defaultIntegrationLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIntegrationLogShouldBeFound(String filter) throws Exception {
        restIntegrationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integrationLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceApp").value(hasItem(DEFAULT_SOURCE_APP.toString())))
            .andExpect(jsonPath("$.[*].targetApp").value(hasItem(DEFAULT_TARGET_APP.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].response").value(hasItem(DEFAULT_RESPONSE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].retries").value(hasItem(DEFAULT_RETRIES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restIntegrationLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIntegrationLogShouldNotBeFound(String filter) throws Exception {
        restIntegrationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIntegrationLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIntegrationLog() throws Exception {
        // Get the integrationLog
        restIntegrationLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIntegrationLog() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrationLogSearchRepository.save(integrationLog);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());

        // Update the integrationLog
        IntegrationLog updatedIntegrationLog = integrationLogRepository.findById(integrationLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIntegrationLog are not directly saved in db
        em.detach(updatedIntegrationLog);
        updatedIntegrationLog
            .sourceApp(UPDATED_SOURCE_APP)
            .targetApp(UPDATED_TARGET_APP)
            .payload(UPDATED_PAYLOAD)
            .response(UPDATED_RESPONSE)
            .status(UPDATED_STATUS)
            .retries(UPDATED_RETRIES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(updatedIntegrationLog);

        restIntegrationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, integrationLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(integrationLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIntegrationLogToMatchAllProperties(updatedIntegrationLog);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<IntegrationLog> integrationLogSearchList = Streamable.of(integrationLogSearchRepository.findAll()).toList();
                IntegrationLog testIntegrationLogSearch = integrationLogSearchList.get(searchDatabaseSizeAfter - 1);

                assertIntegrationLogAllPropertiesEquals(testIntegrationLogSearch, updatedIntegrationLog);
            });
    }

    @Test
    @Transactional
    void putNonExistingIntegrationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        integrationLog.setId(longCount.incrementAndGet());

        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntegrationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, integrationLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(integrationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchIntegrationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        integrationLog.setId(longCount.incrementAndGet());

        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(integrationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIntegrationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        integrationLog.setId(longCount.incrementAndGet());

        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateIntegrationLogWithPatch() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrationLog using partial update
        IntegrationLog partialUpdatedIntegrationLog = new IntegrationLog();
        partialUpdatedIntegrationLog.setId(integrationLog.getId());

        partialUpdatedIntegrationLog.targetApp(UPDATED_TARGET_APP).retries(UPDATED_RETRIES).createdAt(UPDATED_CREATED_AT);

        restIntegrationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntegrationLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntegrationLog))
            )
            .andExpect(status().isOk());

        // Validate the IntegrationLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntegrationLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIntegrationLog, integrationLog),
            getPersistedIntegrationLog(integrationLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateIntegrationLogWithPatch() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrationLog using partial update
        IntegrationLog partialUpdatedIntegrationLog = new IntegrationLog();
        partialUpdatedIntegrationLog.setId(integrationLog.getId());

        partialUpdatedIntegrationLog
            .sourceApp(UPDATED_SOURCE_APP)
            .targetApp(UPDATED_TARGET_APP)
            .payload(UPDATED_PAYLOAD)
            .response(UPDATED_RESPONSE)
            .status(UPDATED_STATUS)
            .retries(UPDATED_RETRIES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restIntegrationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntegrationLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntegrationLog))
            )
            .andExpect(status().isOk());

        // Validate the IntegrationLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntegrationLogUpdatableFieldsEquals(partialUpdatedIntegrationLog, getPersistedIntegrationLog(partialUpdatedIntegrationLog));
    }

    @Test
    @Transactional
    void patchNonExistingIntegrationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        integrationLog.setId(longCount.incrementAndGet());

        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntegrationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, integrationLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(integrationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIntegrationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        integrationLog.setId(longCount.incrementAndGet());

        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(integrationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIntegrationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        integrationLog.setId(longCount.incrementAndGet());

        // Create the IntegrationLog
        IntegrationLogDTO integrationLogDTO = integrationLogMapper.toDto(integrationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(integrationLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntegrationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteIntegrationLog() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);
        integrationLogRepository.save(integrationLog);
        integrationLogSearchRepository.save(integrationLog);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the integrationLog
        restIntegrationLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, integrationLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchIntegrationLog() throws Exception {
        // Initialize the database
        insertedIntegrationLog = integrationLogRepository.saveAndFlush(integrationLog);
        integrationLogSearchRepository.save(integrationLog);

        // Search the integrationLog
        restIntegrationLogMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + integrationLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integrationLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceApp").value(hasItem(DEFAULT_SOURCE_APP.toString())))
            .andExpect(jsonPath("$.[*].targetApp").value(hasItem(DEFAULT_TARGET_APP.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD.toString())))
            .andExpect(jsonPath("$.[*].response").value(hasItem(DEFAULT_RESPONSE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].retries").value(hasItem(DEFAULT_RETRIES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return integrationLogRepository.count();
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

    protected IntegrationLog getPersistedIntegrationLog(IntegrationLog integrationLog) {
        return integrationLogRepository.findById(integrationLog.getId()).orElseThrow();
    }

    protected void assertPersistedIntegrationLogToMatchAllProperties(IntegrationLog expectedIntegrationLog) {
        assertIntegrationLogAllPropertiesEquals(expectedIntegrationLog, getPersistedIntegrationLog(expectedIntegrationLog));
    }

    protected void assertPersistedIntegrationLogToMatchUpdatableProperties(IntegrationLog expectedIntegrationLog) {
        assertIntegrationLogAllUpdatablePropertiesEquals(expectedIntegrationLog, getPersistedIntegrationLog(expectedIntegrationLog));
    }
}
