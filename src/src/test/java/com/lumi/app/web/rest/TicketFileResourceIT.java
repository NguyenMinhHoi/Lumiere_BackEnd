package com.lumi.app.web.rest;

import static com.lumi.app.domain.TicketFileAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.TicketFile;
import com.lumi.app.domain.enumeration.FileStatus;
import com.lumi.app.domain.enumeration.StorageType;
import com.lumi.app.repository.TicketFileRepository;
import com.lumi.app.repository.search.TicketFileSearchRepository;
import com.lumi.app.service.dto.TicketFileDTO;
import com.lumi.app.service.mapper.TicketFileMapper;
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
 * Integration tests for the {@link TicketFileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketFileResourceIT {

    private static final Long DEFAULT_TICKET_ID = 1L;
    private static final Long UPDATED_TICKET_ID = 2L;
    private static final Long SMALLER_TICKET_ID = 1L - 1L;

    private static final Long DEFAULT_UPLOADER_ID = 1L;
    private static final Long UPDATED_UPLOADER_ID = 2L;
    private static final Long SMALLER_UPLOADER_ID = 1L - 1L;

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_CAPACITY = 0L;
    private static final Long UPDATED_CAPACITY = 1L;
    private static final Long SMALLER_CAPACITY = 0L - 1L;

    private static final StorageType DEFAULT_STORAGE_TYPE = StorageType.LOCAL;
    private static final StorageType UPDATED_STORAGE_TYPE = StorageType.S3;

    private static final String DEFAULT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_CHECKSUM = "BBBBBBBBBB";

    private static final FileStatus DEFAULT_STATUS = FileStatus.ACTIVE;
    private static final FileStatus UPDATED_STATUS = FileStatus.ARCHIVED;

    private static final Instant DEFAULT_UPLOADED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOADED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/ticket-files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/ticket-files/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketFileRepository ticketFileRepository;

    @Autowired
    private TicketFileMapper ticketFileMapper;

    @Autowired
    private TicketFileSearchRepository ticketFileSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketFileMockMvc;

    private TicketFile ticketFile;

    private TicketFile insertedTicketFile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketFile createEntity() {
        return new TicketFile()
            .ticketId(DEFAULT_TICKET_ID)
            .uploaderId(DEFAULT_UPLOADER_ID)
            .fileName(DEFAULT_FILE_NAME)
            .originalName(DEFAULT_ORIGINAL_NAME)
            .contentType(DEFAULT_CONTENT_TYPE)
            .capacity(DEFAULT_CAPACITY)
            .storageType(DEFAULT_STORAGE_TYPE)
            .path(DEFAULT_PATH)
            .url(DEFAULT_URL)
            .checksum(DEFAULT_CHECKSUM)
            .status(DEFAULT_STATUS)
            .uploadedAt(DEFAULT_UPLOADED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketFile createUpdatedEntity() {
        return new TicketFile()
            .ticketId(UPDATED_TICKET_ID)
            .uploaderId(UPDATED_UPLOADER_ID)
            .fileName(UPDATED_FILE_NAME)
            .originalName(UPDATED_ORIGINAL_NAME)
            .contentType(UPDATED_CONTENT_TYPE)
            .capacity(UPDATED_CAPACITY)
            .storageType(UPDATED_STORAGE_TYPE)
            .path(UPDATED_PATH)
            .url(UPDATED_URL)
            .checksum(UPDATED_CHECKSUM)
            .status(UPDATED_STATUS)
            .uploadedAt(UPDATED_UPLOADED_AT);
    }

    @BeforeEach
    void initTest() {
        ticketFile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketFile != null) {
            ticketFileRepository.delete(insertedTicketFile);
            ticketFileSearchRepository.delete(insertedTicketFile);
            insertedTicketFile = null;
        }
    }

    @Test
    @Transactional
    void createTicketFile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);
        var returnedTicketFileDTO = om.readValue(
            restTicketFileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketFileDTO.class
        );

        // Validate the TicketFile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTicketFile = ticketFileMapper.toEntity(returnedTicketFileDTO);
        assertTicketFileUpdatableFieldsEquals(returnedTicketFile, getPersistedTicketFile(returnedTicketFile));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTicketFile = returnedTicketFile;
    }

    @Test
    @Transactional
    void createTicketFileWithExistingId() throws Exception {
        // Create the TicketFile with an existing ID
        ticketFile.setId(1L);
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTicketIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // set the field null
        ticketFile.setTicketId(null);

        // Create the TicketFile, which fails.
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // set the field null
        ticketFile.setFileName(null);

        // Create the TicketFile, which fails.
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // set the field null
        ticketFile.setCapacity(null);

        // Create the TicketFile, which fails.
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStorageTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // set the field null
        ticketFile.setStorageType(null);

        // Create the TicketFile, which fails.
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // set the field null
        ticketFile.setStatus(null);

        // Create the TicketFile, which fails.
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUploadedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        // set the field null
        ticketFile.setUploadedAt(null);

        // Create the TicketFile, which fails.
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        restTicketFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTicketFiles() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList
        restTicketFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].uploaderId").value(hasItem(DEFAULT_UPLOADER_ID.intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].originalName").value(hasItem(DEFAULT_ORIGINAL_NAME)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY.intValue())))
            .andExpect(jsonPath("$.[*].storageType").value(hasItem(DEFAULT_STORAGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())));
    }

    @Test
    @Transactional
    void getTicketFile() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get the ticketFile
        restTicketFileMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketFile.getId().intValue()))
            .andExpect(jsonPath("$.ticketId").value(DEFAULT_TICKET_ID.intValue()))
            .andExpect(jsonPath("$.uploaderId").value(DEFAULT_UPLOADER_ID.intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.originalName").value(DEFAULT_ORIGINAL_NAME))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY.intValue()))
            .andExpect(jsonPath("$.storageType").value(DEFAULT_STORAGE_TYPE.toString()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.checksum").value(DEFAULT_CHECKSUM))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.uploadedAt").value(DEFAULT_UPLOADED_AT.toString()));
    }

    @Test
    @Transactional
    void getTicketFilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        Long id = ticketFile.getId();

        defaultTicketFileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTicketFileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTicketFileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId equals to
        defaultTicketFileFiltering("ticketId.equals=" + DEFAULT_TICKET_ID, "ticketId.equals=" + UPDATED_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId in
        defaultTicketFileFiltering("ticketId.in=" + DEFAULT_TICKET_ID + "," + UPDATED_TICKET_ID, "ticketId.in=" + UPDATED_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId is not null
        defaultTicketFileFiltering("ticketId.specified=true", "ticketId.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId is greater than or equal to
        defaultTicketFileFiltering("ticketId.greaterThanOrEqual=" + DEFAULT_TICKET_ID, "ticketId.greaterThanOrEqual=" + UPDATED_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId is less than or equal to
        defaultTicketFileFiltering("ticketId.lessThanOrEqual=" + DEFAULT_TICKET_ID, "ticketId.lessThanOrEqual=" + SMALLER_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId is less than
        defaultTicketFileFiltering("ticketId.lessThan=" + UPDATED_TICKET_ID, "ticketId.lessThan=" + DEFAULT_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByTicketIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where ticketId is greater than
        defaultTicketFileFiltering("ticketId.greaterThan=" + SMALLER_TICKET_ID, "ticketId.greaterThan=" + DEFAULT_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId equals to
        defaultTicketFileFiltering("uploaderId.equals=" + DEFAULT_UPLOADER_ID, "uploaderId.equals=" + UPDATED_UPLOADER_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId in
        defaultTicketFileFiltering(
            "uploaderId.in=" + DEFAULT_UPLOADER_ID + "," + UPDATED_UPLOADER_ID,
            "uploaderId.in=" + UPDATED_UPLOADER_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId is not null
        defaultTicketFileFiltering("uploaderId.specified=true", "uploaderId.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId is greater than or equal to
        defaultTicketFileFiltering(
            "uploaderId.greaterThanOrEqual=" + DEFAULT_UPLOADER_ID,
            "uploaderId.greaterThanOrEqual=" + UPDATED_UPLOADER_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId is less than or equal to
        defaultTicketFileFiltering(
            "uploaderId.lessThanOrEqual=" + DEFAULT_UPLOADER_ID,
            "uploaderId.lessThanOrEqual=" + SMALLER_UPLOADER_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId is less than
        defaultTicketFileFiltering("uploaderId.lessThan=" + UPDATED_UPLOADER_ID, "uploaderId.lessThan=" + DEFAULT_UPLOADER_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploaderIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploaderId is greater than
        defaultTicketFileFiltering("uploaderId.greaterThan=" + SMALLER_UPLOADER_ID, "uploaderId.greaterThan=" + DEFAULT_UPLOADER_ID);
    }

    @Test
    @Transactional
    void getAllTicketFilesByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where fileName equals to
        defaultTicketFileFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllTicketFilesByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where fileName in
        defaultTicketFileFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllTicketFilesByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where fileName is not null
        defaultTicketFileFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where fileName contains
        defaultTicketFileFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllTicketFilesByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where fileName does not contain
        defaultTicketFileFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllTicketFilesByOriginalNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where originalName equals to
        defaultTicketFileFiltering("originalName.equals=" + DEFAULT_ORIGINAL_NAME, "originalName.equals=" + UPDATED_ORIGINAL_NAME);
    }

    @Test
    @Transactional
    void getAllTicketFilesByOriginalNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where originalName in
        defaultTicketFileFiltering(
            "originalName.in=" + DEFAULT_ORIGINAL_NAME + "," + UPDATED_ORIGINAL_NAME,
            "originalName.in=" + UPDATED_ORIGINAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByOriginalNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where originalName is not null
        defaultTicketFileFiltering("originalName.specified=true", "originalName.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByOriginalNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where originalName contains
        defaultTicketFileFiltering("originalName.contains=" + DEFAULT_ORIGINAL_NAME, "originalName.contains=" + UPDATED_ORIGINAL_NAME);
    }

    @Test
    @Transactional
    void getAllTicketFilesByOriginalNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where originalName does not contain
        defaultTicketFileFiltering(
            "originalName.doesNotContain=" + UPDATED_ORIGINAL_NAME,
            "originalName.doesNotContain=" + DEFAULT_ORIGINAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where contentType equals to
        defaultTicketFileFiltering("contentType.equals=" + DEFAULT_CONTENT_TYPE, "contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllTicketFilesByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where contentType in
        defaultTicketFileFiltering(
            "contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE,
            "contentType.in=" + UPDATED_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where contentType is not null
        defaultTicketFileFiltering("contentType.specified=true", "contentType.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByContentTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where contentType contains
        defaultTicketFileFiltering("contentType.contains=" + DEFAULT_CONTENT_TYPE, "contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllTicketFilesByContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where contentType does not contain
        defaultTicketFileFiltering(
            "contentType.doesNotContain=" + UPDATED_CONTENT_TYPE,
            "contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity equals to
        defaultTicketFileFiltering("capacity.equals=" + DEFAULT_CAPACITY, "capacity.equals=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity in
        defaultTicketFileFiltering("capacity.in=" + DEFAULT_CAPACITY + "," + UPDATED_CAPACITY, "capacity.in=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity is not null
        defaultTicketFileFiltering("capacity.specified=true", "capacity.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity is greater than or equal to
        defaultTicketFileFiltering("capacity.greaterThanOrEqual=" + DEFAULT_CAPACITY, "capacity.greaterThanOrEqual=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity is less than or equal to
        defaultTicketFileFiltering("capacity.lessThanOrEqual=" + DEFAULT_CAPACITY, "capacity.lessThanOrEqual=" + SMALLER_CAPACITY);
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity is less than
        defaultTicketFileFiltering("capacity.lessThan=" + UPDATED_CAPACITY, "capacity.lessThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllTicketFilesByCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where capacity is greater than
        defaultTicketFileFiltering("capacity.greaterThan=" + SMALLER_CAPACITY, "capacity.greaterThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllTicketFilesByStorageTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where storageType equals to
        defaultTicketFileFiltering("storageType.equals=" + DEFAULT_STORAGE_TYPE, "storageType.equals=" + UPDATED_STORAGE_TYPE);
    }

    @Test
    @Transactional
    void getAllTicketFilesByStorageTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where storageType in
        defaultTicketFileFiltering(
            "storageType.in=" + DEFAULT_STORAGE_TYPE + "," + UPDATED_STORAGE_TYPE,
            "storageType.in=" + UPDATED_STORAGE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByStorageTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where storageType is not null
        defaultTicketFileFiltering("storageType.specified=true", "storageType.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByPathIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where path equals to
        defaultTicketFileFiltering("path.equals=" + DEFAULT_PATH, "path.equals=" + UPDATED_PATH);
    }

    @Test
    @Transactional
    void getAllTicketFilesByPathIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where path in
        defaultTicketFileFiltering("path.in=" + DEFAULT_PATH + "," + UPDATED_PATH, "path.in=" + UPDATED_PATH);
    }

    @Test
    @Transactional
    void getAllTicketFilesByPathIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where path is not null
        defaultTicketFileFiltering("path.specified=true", "path.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByPathContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where path contains
        defaultTicketFileFiltering("path.contains=" + DEFAULT_PATH, "path.contains=" + UPDATED_PATH);
    }

    @Test
    @Transactional
    void getAllTicketFilesByPathNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where path does not contain
        defaultTicketFileFiltering("path.doesNotContain=" + UPDATED_PATH, "path.doesNotContain=" + DEFAULT_PATH);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where url equals to
        defaultTicketFileFiltering("url.equals=" + DEFAULT_URL, "url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where url in
        defaultTicketFileFiltering("url.in=" + DEFAULT_URL + "," + UPDATED_URL, "url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where url is not null
        defaultTicketFileFiltering("url.specified=true", "url.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where url contains
        defaultTicketFileFiltering("url.contains=" + DEFAULT_URL, "url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where url does not contain
        defaultTicketFileFiltering("url.doesNotContain=" + UPDATED_URL, "url.doesNotContain=" + DEFAULT_URL);
    }

    @Test
    @Transactional
    void getAllTicketFilesByChecksumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where checksum equals to
        defaultTicketFileFiltering("checksum.equals=" + DEFAULT_CHECKSUM, "checksum.equals=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllTicketFilesByChecksumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where checksum in
        defaultTicketFileFiltering("checksum.in=" + DEFAULT_CHECKSUM + "," + UPDATED_CHECKSUM, "checksum.in=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllTicketFilesByChecksumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where checksum is not null
        defaultTicketFileFiltering("checksum.specified=true", "checksum.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByChecksumContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where checksum contains
        defaultTicketFileFiltering("checksum.contains=" + DEFAULT_CHECKSUM, "checksum.contains=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllTicketFilesByChecksumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where checksum does not contain
        defaultTicketFileFiltering("checksum.doesNotContain=" + UPDATED_CHECKSUM, "checksum.doesNotContain=" + DEFAULT_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllTicketFilesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where status equals to
        defaultTicketFileFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTicketFilesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where status in
        defaultTicketFileFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTicketFilesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where status is not null
        defaultTicketFileFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploadedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploadedAt equals to
        defaultTicketFileFiltering("uploadedAt.equals=" + DEFAULT_UPLOADED_AT, "uploadedAt.equals=" + UPDATED_UPLOADED_AT);
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploadedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploadedAt in
        defaultTicketFileFiltering(
            "uploadedAt.in=" + DEFAULT_UPLOADED_AT + "," + UPDATED_UPLOADED_AT,
            "uploadedAt.in=" + UPDATED_UPLOADED_AT
        );
    }

    @Test
    @Transactional
    void getAllTicketFilesByUploadedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        // Get all the ticketFileList where uploadedAt is not null
        defaultTicketFileFiltering("uploadedAt.specified=true", "uploadedAt.specified=false");
    }

    private void defaultTicketFileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTicketFileShouldBeFound(shouldBeFound);
        defaultTicketFileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketFileShouldBeFound(String filter) throws Exception {
        restTicketFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].uploaderId").value(hasItem(DEFAULT_UPLOADER_ID.intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].originalName").value(hasItem(DEFAULT_ORIGINAL_NAME)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY.intValue())))
            .andExpect(jsonPath("$.[*].storageType").value(hasItem(DEFAULT_STORAGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())));

        // Check, that the count call also returns 1
        restTicketFileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketFileShouldNotBeFound(String filter) throws Exception {
        restTicketFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketFileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicketFile() throws Exception {
        // Get the ticketFile
        restTicketFileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketFile() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketFileSearchRepository.save(ticketFile);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());

        // Update the ticketFile
        TicketFile updatedTicketFile = ticketFileRepository.findById(ticketFile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketFile are not directly saved in db
        em.detach(updatedTicketFile);
        updatedTicketFile
            .ticketId(UPDATED_TICKET_ID)
            .uploaderId(UPDATED_UPLOADER_ID)
            .fileName(UPDATED_FILE_NAME)
            .originalName(UPDATED_ORIGINAL_NAME)
            .contentType(UPDATED_CONTENT_TYPE)
            .capacity(UPDATED_CAPACITY)
            .storageType(UPDATED_STORAGE_TYPE)
            .path(UPDATED_PATH)
            .url(UPDATED_URL)
            .checksum(UPDATED_CHECKSUM)
            .status(UPDATED_STATUS)
            .uploadedAt(UPDATED_UPLOADED_AT);
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(updatedTicketFile);

        restTicketFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketFileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketFileDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketFileToMatchAllProperties(updatedTicketFile);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TicketFile> ticketFileSearchList = Streamable.of(ticketFileSearchRepository.findAll()).toList();
                TicketFile testTicketFileSearch = ticketFileSearchList.get(searchDatabaseSizeAfter - 1);

                assertTicketFileAllPropertiesEquals(testTicketFileSearch, updatedTicketFile);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicketFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        ticketFile.setId(longCount.incrementAndGet());

        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketFileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        ticketFile.setId(longCount.incrementAndGet());

        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        ticketFile.setId(longCount.incrementAndGet());

        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketFileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketFileWithPatch() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketFile using partial update
        TicketFile partialUpdatedTicketFile = new TicketFile();
        partialUpdatedTicketFile.setId(ticketFile.getId());

        partialUpdatedTicketFile.originalName(UPDATED_ORIGINAL_NAME).path(UPDATED_PATH).status(UPDATED_STATUS);

        restTicketFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketFile))
            )
            .andExpect(status().isOk());

        // Validate the TicketFile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketFileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketFile, ticketFile),
            getPersistedTicketFile(ticketFile)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketFileWithPatch() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketFile using partial update
        TicketFile partialUpdatedTicketFile = new TicketFile();
        partialUpdatedTicketFile.setId(ticketFile.getId());

        partialUpdatedTicketFile
            .ticketId(UPDATED_TICKET_ID)
            .uploaderId(UPDATED_UPLOADER_ID)
            .fileName(UPDATED_FILE_NAME)
            .originalName(UPDATED_ORIGINAL_NAME)
            .contentType(UPDATED_CONTENT_TYPE)
            .capacity(UPDATED_CAPACITY)
            .storageType(UPDATED_STORAGE_TYPE)
            .path(UPDATED_PATH)
            .url(UPDATED_URL)
            .checksum(UPDATED_CHECKSUM)
            .status(UPDATED_STATUS)
            .uploadedAt(UPDATED_UPLOADED_AT);

        restTicketFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketFile))
            )
            .andExpect(status().isOk());

        // Validate the TicketFile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketFileUpdatableFieldsEquals(partialUpdatedTicketFile, getPersistedTicketFile(partialUpdatedTicketFile));
    }

    @Test
    @Transactional
    void patchNonExistingTicketFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        ticketFile.setId(longCount.incrementAndGet());

        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketFileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        ticketFile.setId(longCount.incrementAndGet());

        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        ticketFile.setId(longCount.incrementAndGet());

        // Create the TicketFile
        TicketFileDTO ticketFileDTO = ticketFileMapper.toDto(ticketFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketFileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketFileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicketFile() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);
        ticketFileRepository.save(ticketFile);
        ticketFileSearchRepository.save(ticketFile);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticketFile
        restTicketFileMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketFile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketFileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicketFile() throws Exception {
        // Initialize the database
        insertedTicketFile = ticketFileRepository.saveAndFlush(ticketFile);
        ticketFileSearchRepository.save(ticketFile);

        // Search the ticketFile
        restTicketFileMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticketFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].uploaderId").value(hasItem(DEFAULT_UPLOADER_ID.intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].originalName").value(hasItem(DEFAULT_ORIGINAL_NAME)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY.intValue())))
            .andExpect(jsonPath("$.[*].storageType").value(hasItem(DEFAULT_STORAGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return ticketFileRepository.count();
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

    protected TicketFile getPersistedTicketFile(TicketFile ticketFile) {
        return ticketFileRepository.findById(ticketFile.getId()).orElseThrow();
    }

    protected void assertPersistedTicketFileToMatchAllProperties(TicketFile expectedTicketFile) {
        assertTicketFileAllPropertiesEquals(expectedTicketFile, getPersistedTicketFile(expectedTicketFile));
    }

    protected void assertPersistedTicketFileToMatchUpdatableProperties(TicketFile expectedTicketFile) {
        assertTicketFileAllUpdatablePropertiesEquals(expectedTicketFile, getPersistedTicketFile(expectedTicketFile));
    }
}
