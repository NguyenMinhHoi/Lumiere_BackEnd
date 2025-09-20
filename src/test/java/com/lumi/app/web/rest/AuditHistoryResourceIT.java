package com.lumi.app.web.rest;

import static com.lumi.app.domain.AuditHistoryAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.AuditHistory;
import com.lumi.app.domain.enumeration.AuditAction;
import com.lumi.app.repository.AuditHistoryRepository;
import com.lumi.app.repository.search.AuditHistorySearchRepository;
import com.lumi.app.service.dto.AuditHistoryDTO;
import com.lumi.app.service.mapper.AuditHistoryMapper;
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
 * Integration tests for the {@link AuditHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuditHistoryResourceIT {

    private static final String DEFAULT_ENTITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITY_ID = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_ID = "BBBBBBBBBB";

    private static final AuditAction DEFAULT_ACTION = AuditAction.CREATE;
    private static final AuditAction UPDATED_ACTION = AuditAction.UPDATE;

    private static final String DEFAULT_OLD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_OLD_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_NEW_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_PERFORMED_BY = "AAAAAAAAAA";
    private static final String UPDATED_PERFORMED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_PERFORMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERFORMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/audit-histories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditHistoryRepository auditHistoryRepository;

    @Autowired
    private AuditHistoryMapper auditHistoryMapper;

    @Autowired
    private AuditHistorySearchRepository auditHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditHistoryMockMvc;

    private AuditHistory auditHistory;

    private AuditHistory insertedAuditHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditHistory createEntity() {
        return new AuditHistory()
            .entityName(DEFAULT_ENTITY_NAME)
            .entityId(DEFAULT_ENTITY_ID)
            .action(DEFAULT_ACTION)
            .oldValue(DEFAULT_OLD_VALUE)
            .newValue(DEFAULT_NEW_VALUE)
            .performedBy(DEFAULT_PERFORMED_BY)
            .performedAt(DEFAULT_PERFORMED_AT)
            .ipAddress(DEFAULT_IP_ADDRESS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditHistory createUpdatedEntity() {
        return new AuditHistory()
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .action(UPDATED_ACTION)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedAt(UPDATED_PERFORMED_AT)
            .ipAddress(UPDATED_IP_ADDRESS);
    }

    @BeforeEach
    void initTest() {
        auditHistory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditHistory != null) {
            auditHistoryRepository.delete(insertedAuditHistory);
            auditHistorySearchRepository.delete(insertedAuditHistory);
            insertedAuditHistory = null;
        }
    }

    @Test
    @Transactional
    void createAuditHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);
        var returnedAuditHistoryDTO = om.readValue(
            restAuditHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuditHistoryDTO.class
        );

        // Validate the AuditHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditHistory = auditHistoryMapper.toEntity(returnedAuditHistoryDTO);
        assertAuditHistoryUpdatableFieldsEquals(returnedAuditHistory, getPersistedAuditHistory(returnedAuditHistory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedAuditHistory = returnedAuditHistory;
    }

    @Test
    @Transactional
    void createAuditHistoryWithExistingId() throws Exception {
        // Create the AuditHistory with an existing ID
        auditHistory.setId(1L);
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEntityNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        // set the field null
        auditHistory.setEntityName(null);

        // Create the AuditHistory, which fails.
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        restAuditHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEntityIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        // set the field null
        auditHistory.setEntityId(null);

        // Create the AuditHistory, which fails.
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        restAuditHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        // set the field null
        auditHistory.setAction(null);

        // Create the AuditHistory, which fails.
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        restAuditHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPerformedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        // set the field null
        auditHistory.setPerformedAt(null);

        // Create the AuditHistory, which fails.
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        restAuditHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAuditHistories() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList
        restAuditHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)))
            .andExpect(jsonPath("$.[*].performedBy").value(hasItem(DEFAULT_PERFORMED_BY)))
            .andExpect(jsonPath("$.[*].performedAt").value(hasItem(DEFAULT_PERFORMED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)));
    }

    @Test
    @Transactional
    void getAuditHistory() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get the auditHistory
        restAuditHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, auditHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditHistory.getId().intValue()))
            .andExpect(jsonPath("$.entityName").value(DEFAULT_ENTITY_NAME))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.oldValue").value(DEFAULT_OLD_VALUE))
            .andExpect(jsonPath("$.newValue").value(DEFAULT_NEW_VALUE))
            .andExpect(jsonPath("$.performedBy").value(DEFAULT_PERFORMED_BY))
            .andExpect(jsonPath("$.performedAt").value(DEFAULT_PERFORMED_AT.toString()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS));
    }

    @Test
    @Transactional
    void getAuditHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        Long id = auditHistory.getId();

        defaultAuditHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuditHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuditHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityName equals to
        defaultAuditHistoryFiltering("entityName.equals=" + DEFAULT_ENTITY_NAME, "entityName.equals=" + UPDATED_ENTITY_NAME);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityName in
        defaultAuditHistoryFiltering(
            "entityName.in=" + DEFAULT_ENTITY_NAME + "," + UPDATED_ENTITY_NAME,
            "entityName.in=" + UPDATED_ENTITY_NAME
        );
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityName is not null
        defaultAuditHistoryFiltering("entityName.specified=true", "entityName.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityName contains
        defaultAuditHistoryFiltering("entityName.contains=" + DEFAULT_ENTITY_NAME, "entityName.contains=" + UPDATED_ENTITY_NAME);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityName does not contain
        defaultAuditHistoryFiltering(
            "entityName.doesNotContain=" + UPDATED_ENTITY_NAME,
            "entityName.doesNotContain=" + DEFAULT_ENTITY_NAME
        );
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityId equals to
        defaultAuditHistoryFiltering("entityId.equals=" + DEFAULT_ENTITY_ID, "entityId.equals=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityId in
        defaultAuditHistoryFiltering("entityId.in=" + DEFAULT_ENTITY_ID + "," + UPDATED_ENTITY_ID, "entityId.in=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityId is not null
        defaultAuditHistoryFiltering("entityId.specified=true", "entityId.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityIdContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityId contains
        defaultAuditHistoryFiltering("entityId.contains=" + DEFAULT_ENTITY_ID, "entityId.contains=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByEntityIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where entityId does not contain
        defaultAuditHistoryFiltering("entityId.doesNotContain=" + UPDATED_ENTITY_ID, "entityId.doesNotContain=" + DEFAULT_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where action equals to
        defaultAuditHistoryFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where action in
        defaultAuditHistoryFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where action is not null
        defaultAuditHistoryFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedBy equals to
        defaultAuditHistoryFiltering("performedBy.equals=" + DEFAULT_PERFORMED_BY, "performedBy.equals=" + UPDATED_PERFORMED_BY);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedBy in
        defaultAuditHistoryFiltering(
            "performedBy.in=" + DEFAULT_PERFORMED_BY + "," + UPDATED_PERFORMED_BY,
            "performedBy.in=" + UPDATED_PERFORMED_BY
        );
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedBy is not null
        defaultAuditHistoryFiltering("performedBy.specified=true", "performedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedByContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedBy contains
        defaultAuditHistoryFiltering("performedBy.contains=" + DEFAULT_PERFORMED_BY, "performedBy.contains=" + UPDATED_PERFORMED_BY);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedBy does not contain
        defaultAuditHistoryFiltering(
            "performedBy.doesNotContain=" + UPDATED_PERFORMED_BY,
            "performedBy.doesNotContain=" + DEFAULT_PERFORMED_BY
        );
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedAt equals to
        defaultAuditHistoryFiltering("performedAt.equals=" + DEFAULT_PERFORMED_AT, "performedAt.equals=" + UPDATED_PERFORMED_AT);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedAt in
        defaultAuditHistoryFiltering(
            "performedAt.in=" + DEFAULT_PERFORMED_AT + "," + UPDATED_PERFORMED_AT,
            "performedAt.in=" + UPDATED_PERFORMED_AT
        );
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByPerformedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where performedAt is not null
        defaultAuditHistoryFiltering("performedAt.specified=true", "performedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where ipAddress equals to
        defaultAuditHistoryFiltering("ipAddress.equals=" + DEFAULT_IP_ADDRESS, "ipAddress.equals=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where ipAddress in
        defaultAuditHistoryFiltering("ipAddress.in=" + DEFAULT_IP_ADDRESS + "," + UPDATED_IP_ADDRESS, "ipAddress.in=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where ipAddress is not null
        defaultAuditHistoryFiltering("ipAddress.specified=true", "ipAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where ipAddress contains
        defaultAuditHistoryFiltering("ipAddress.contains=" + DEFAULT_IP_ADDRESS, "ipAddress.contains=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditHistoriesByIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        // Get all the auditHistoryList where ipAddress does not contain
        defaultAuditHistoryFiltering("ipAddress.doesNotContain=" + UPDATED_IP_ADDRESS, "ipAddress.doesNotContain=" + DEFAULT_IP_ADDRESS);
    }

    private void defaultAuditHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuditHistoryShouldBeFound(shouldBeFound);
        defaultAuditHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuditHistoryShouldBeFound(String filter) throws Exception {
        restAuditHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)))
            .andExpect(jsonPath("$.[*].performedBy").value(hasItem(DEFAULT_PERFORMED_BY)))
            .andExpect(jsonPath("$.[*].performedAt").value(hasItem(DEFAULT_PERFORMED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)));

        // Check, that the count call also returns 1
        restAuditHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuditHistoryShouldNotBeFound(String filter) throws Exception {
        restAuditHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuditHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuditHistory() throws Exception {
        // Get the auditHistory
        restAuditHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuditHistory() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditHistorySearchRepository.save(auditHistory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());

        // Update the auditHistory
        AuditHistory updatedAuditHistory = auditHistoryRepository.findById(auditHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuditHistory are not directly saved in db
        em.detach(updatedAuditHistory);
        updatedAuditHistory
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .action(UPDATED_ACTION)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedAt(UPDATED_PERFORMED_AT)
            .ipAddress(UPDATED_IP_ADDRESS);
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(updatedAuditHistory);

        restAuditHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditHistoryToMatchAllProperties(updatedAuditHistory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AuditHistory> auditHistorySearchList = Streamable.of(auditHistorySearchRepository.findAll()).toList();
                AuditHistory testAuditHistorySearch = auditHistorySearchList.get(searchDatabaseSizeAfter - 1);

                assertAuditHistoryAllPropertiesEquals(testAuditHistorySearch, updatedAuditHistory);
            });
    }

    @Test
    @Transactional
    void putNonExistingAuditHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        auditHistory.setId(longCount.incrementAndGet());

        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        auditHistory.setId(longCount.incrementAndGet());

        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        auditHistory.setId(longCount.incrementAndGet());

        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAuditHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditHistory using partial update
        AuditHistory partialUpdatedAuditHistory = new AuditHistory();
        partialUpdatedAuditHistory.setId(auditHistory.getId());

        partialUpdatedAuditHistory.entityId(UPDATED_ENTITY_ID).performedBy(UPDATED_PERFORMED_BY).ipAddress(UPDATED_IP_ADDRESS);

        restAuditHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditHistory))
            )
            .andExpect(status().isOk());

        // Validate the AuditHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditHistory, auditHistory),
            getPersistedAuditHistory(auditHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateAuditHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditHistory using partial update
        AuditHistory partialUpdatedAuditHistory = new AuditHistory();
        partialUpdatedAuditHistory.setId(auditHistory.getId());

        partialUpdatedAuditHistory
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .action(UPDATED_ACTION)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedAt(UPDATED_PERFORMED_AT)
            .ipAddress(UPDATED_IP_ADDRESS);

        restAuditHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditHistory))
            )
            .andExpect(status().isOk());

        // Validate the AuditHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditHistoryUpdatableFieldsEquals(partialUpdatedAuditHistory, getPersistedAuditHistory(partialUpdatedAuditHistory));
    }

    @Test
    @Transactional
    void patchNonExistingAuditHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        auditHistory.setId(longCount.incrementAndGet());

        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        auditHistory.setId(longCount.incrementAndGet());

        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        auditHistory.setId(longCount.incrementAndGet());

        // Create the AuditHistory
        AuditHistoryDTO auditHistoryDTO = auditHistoryMapper.toDto(auditHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(auditHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAuditHistory() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);
        auditHistoryRepository.save(auditHistory);
        auditHistorySearchRepository.save(auditHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the auditHistory
        restAuditHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(auditHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAuditHistory() throws Exception {
        // Initialize the database
        insertedAuditHistory = auditHistoryRepository.saveAndFlush(auditHistory);
        auditHistorySearchRepository.save(auditHistory);

        // Search the auditHistory
        restAuditHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + auditHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE.toString())))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE.toString())))
            .andExpect(jsonPath("$.[*].performedBy").value(hasItem(DEFAULT_PERFORMED_BY)))
            .andExpect(jsonPath("$.[*].performedAt").value(hasItem(DEFAULT_PERFORMED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)));
    }

    protected long getRepositoryCount() {
        return auditHistoryRepository.count();
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

    protected AuditHistory getPersistedAuditHistory(AuditHistory auditHistory) {
        return auditHistoryRepository.findById(auditHistory.getId()).orElseThrow();
    }

    protected void assertPersistedAuditHistoryToMatchAllProperties(AuditHistory expectedAuditHistory) {
        assertAuditHistoryAllPropertiesEquals(expectedAuditHistory, getPersistedAuditHistory(expectedAuditHistory));
    }

    protected void assertPersistedAuditHistoryToMatchUpdatableProperties(AuditHistory expectedAuditHistory) {
        assertAuditHistoryAllUpdatablePropertiesEquals(expectedAuditHistory, getPersistedAuditHistory(expectedAuditHistory));
    }
}
