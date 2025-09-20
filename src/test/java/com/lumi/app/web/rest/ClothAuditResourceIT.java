package com.lumi.app.web.rest;

import static com.lumi.app.domain.ClothAuditAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ClothAudit;
import com.lumi.app.domain.enumeration.AuditAction;
import com.lumi.app.repository.ClothAuditRepository;
import com.lumi.app.repository.search.ClothAuditSearchRepository;
import com.lumi.app.service.dto.ClothAuditDTO;
import com.lumi.app.service.mapper.ClothAuditMapper;
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
 * Integration tests for the {@link ClothAuditResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClothAuditResourceIT {

    private static final Long DEFAULT_CLOTH_ID = 1L;
    private static final Long UPDATED_CLOTH_ID = 2L;
    private static final Long SMALLER_CLOTH_ID = 1L - 1L;

    private static final Long DEFAULT_SUPPLIER_ID = 1L;
    private static final Long UPDATED_SUPPLIER_ID = 2L;
    private static final Long SMALLER_SUPPLIER_ID = 1L - 1L;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;
    private static final Long SMALLER_PRODUCT_ID = 1L - 1L;

    private static final AuditAction DEFAULT_ACTION = AuditAction.SEND;
    private static final AuditAction UPDATED_ACTION = AuditAction.RETURN;

    private static final Double DEFAULT_QUANTITY = 0D;
    private static final Double UPDATED_QUANTITY = 1D;
    private static final Double SMALLER_QUANTITY = 0D - 1D;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cloth-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cloth-audits/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClothAuditRepository clothAuditRepository;

    @Autowired
    private ClothAuditMapper clothAuditMapper;

    @Autowired
    private ClothAuditSearchRepository clothAuditSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClothAuditMockMvc;

    private ClothAudit clothAudit;

    private ClothAudit insertedClothAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothAudit createEntity() {
        return new ClothAudit()
            .clothId(DEFAULT_CLOTH_ID)
            .supplierId(DEFAULT_SUPPLIER_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .action(DEFAULT_ACTION)
            .quantity(DEFAULT_QUANTITY)
            .unit(DEFAULT_UNIT)
            .sentAt(DEFAULT_SENT_AT)
            .note(DEFAULT_NOTE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothAudit createUpdatedEntity() {
        return new ClothAudit()
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .productId(UPDATED_PRODUCT_ID)
            .action(UPDATED_ACTION)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .sentAt(UPDATED_SENT_AT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        clothAudit = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClothAudit != null) {
            clothAuditRepository.delete(insertedClothAudit);
            clothAuditSearchRepository.delete(insertedClothAudit);
            insertedClothAudit = null;
        }
    }

    @Test
    @Transactional
    void createClothAudit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);
        var returnedClothAuditDTO = om.readValue(
            restClothAuditMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClothAuditDTO.class
        );

        // Validate the ClothAudit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClothAudit = clothAuditMapper.toEntity(returnedClothAuditDTO);
        assertClothAuditUpdatableFieldsEquals(returnedClothAudit, getPersistedClothAudit(returnedClothAudit));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedClothAudit = returnedClothAudit;
    }

    @Test
    @Transactional
    void createClothAuditWithExistingId() throws Exception {
        // Create the ClothAudit with an existing ID
        clothAudit.setId(1L);
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkClothIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setClothId(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSupplierIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setSupplierId(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProductIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setProductId(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setAction(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setQuantity(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSentAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setSentAt(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        // set the field null
        clothAudit.setCreatedAt(null);

        // Create the ClothAudit, which fails.
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        restClothAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllClothAudits() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList
        restClothAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplierId").value(hasItem(DEFAULT_SUPPLIER_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getClothAudit() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get the clothAudit
        restClothAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, clothAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clothAudit.getId().intValue()))
            .andExpect(jsonPath("$.clothId").value(DEFAULT_CLOTH_ID.intValue()))
            .andExpect(jsonPath("$.supplierId").value(DEFAULT_SUPPLIER_ID.intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getClothAuditsByIdFiltering() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        Long id = clothAudit.getId();

        defaultClothAuditFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClothAuditFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClothAuditFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId equals to
        defaultClothAuditFiltering("clothId.equals=" + DEFAULT_CLOTH_ID, "clothId.equals=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId in
        defaultClothAuditFiltering("clothId.in=" + DEFAULT_CLOTH_ID + "," + UPDATED_CLOTH_ID, "clothId.in=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId is not null
        defaultClothAuditFiltering("clothId.specified=true", "clothId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId is greater than or equal to
        defaultClothAuditFiltering("clothId.greaterThanOrEqual=" + DEFAULT_CLOTH_ID, "clothId.greaterThanOrEqual=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId is less than or equal to
        defaultClothAuditFiltering("clothId.lessThanOrEqual=" + DEFAULT_CLOTH_ID, "clothId.lessThanOrEqual=" + SMALLER_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId is less than
        defaultClothAuditFiltering("clothId.lessThan=" + UPDATED_CLOTH_ID, "clothId.lessThan=" + DEFAULT_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByClothIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where clothId is greater than
        defaultClothAuditFiltering("clothId.greaterThan=" + SMALLER_CLOTH_ID, "clothId.greaterThan=" + DEFAULT_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId equals to
        defaultClothAuditFiltering("supplierId.equals=" + DEFAULT_SUPPLIER_ID, "supplierId.equals=" + UPDATED_SUPPLIER_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId in
        defaultClothAuditFiltering(
            "supplierId.in=" + DEFAULT_SUPPLIER_ID + "," + UPDATED_SUPPLIER_ID,
            "supplierId.in=" + UPDATED_SUPPLIER_ID
        );
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId is not null
        defaultClothAuditFiltering("supplierId.specified=true", "supplierId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId is greater than or equal to
        defaultClothAuditFiltering(
            "supplierId.greaterThanOrEqual=" + DEFAULT_SUPPLIER_ID,
            "supplierId.greaterThanOrEqual=" + UPDATED_SUPPLIER_ID
        );
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId is less than or equal to
        defaultClothAuditFiltering(
            "supplierId.lessThanOrEqual=" + DEFAULT_SUPPLIER_ID,
            "supplierId.lessThanOrEqual=" + SMALLER_SUPPLIER_ID
        );
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId is less than
        defaultClothAuditFiltering("supplierId.lessThan=" + UPDATED_SUPPLIER_ID, "supplierId.lessThan=" + DEFAULT_SUPPLIER_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsBySupplierIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where supplierId is greater than
        defaultClothAuditFiltering("supplierId.greaterThan=" + SMALLER_SUPPLIER_ID, "supplierId.greaterThan=" + DEFAULT_SUPPLIER_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId equals to
        defaultClothAuditFiltering("productId.equals=" + DEFAULT_PRODUCT_ID, "productId.equals=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId in
        defaultClothAuditFiltering("productId.in=" + DEFAULT_PRODUCT_ID + "," + UPDATED_PRODUCT_ID, "productId.in=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId is not null
        defaultClothAuditFiltering("productId.specified=true", "productId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId is greater than or equal to
        defaultClothAuditFiltering(
            "productId.greaterThanOrEqual=" + DEFAULT_PRODUCT_ID,
            "productId.greaterThanOrEqual=" + UPDATED_PRODUCT_ID
        );
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId is less than or equal to
        defaultClothAuditFiltering("productId.lessThanOrEqual=" + DEFAULT_PRODUCT_ID, "productId.lessThanOrEqual=" + SMALLER_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId is less than
        defaultClothAuditFiltering("productId.lessThan=" + UPDATED_PRODUCT_ID, "productId.lessThan=" + DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByProductIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where productId is greater than
        defaultClothAuditFiltering("productId.greaterThan=" + SMALLER_PRODUCT_ID, "productId.greaterThan=" + DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothAuditsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where action equals to
        defaultClothAuditFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllClothAuditsByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where action in
        defaultClothAuditFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllClothAuditsByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where action is not null
        defaultClothAuditFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity equals to
        defaultClothAuditFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity in
        defaultClothAuditFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity is not null
        defaultClothAuditFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity is greater than or equal to
        defaultClothAuditFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity is less than or equal to
        defaultClothAuditFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity is less than
        defaultClothAuditFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothAuditsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where quantity is greater than
        defaultClothAuditFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothAuditsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where unit equals to
        defaultClothAuditFiltering("unit.equals=" + DEFAULT_UNIT, "unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothAuditsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where unit in
        defaultClothAuditFiltering("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT, "unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothAuditsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where unit is not null
        defaultClothAuditFiltering("unit.specified=true", "unit.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where unit contains
        defaultClothAuditFiltering("unit.contains=" + DEFAULT_UNIT, "unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothAuditsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where unit does not contain
        defaultClothAuditFiltering("unit.doesNotContain=" + UPDATED_UNIT, "unit.doesNotContain=" + DEFAULT_UNIT);
    }

    @Test
    @Transactional
    void getAllClothAuditsBySentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where sentAt equals to
        defaultClothAuditFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllClothAuditsBySentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where sentAt in
        defaultClothAuditFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllClothAuditsBySentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where sentAt is not null
        defaultClothAuditFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where note equals to
        defaultClothAuditFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClothAuditsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where note in
        defaultClothAuditFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClothAuditsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where note is not null
        defaultClothAuditFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllClothAuditsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where note contains
        defaultClothAuditFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClothAuditsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where note does not contain
        defaultClothAuditFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllClothAuditsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where createdAt equals to
        defaultClothAuditFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClothAuditsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where createdAt in
        defaultClothAuditFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClothAuditsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        // Get all the clothAuditList where createdAt is not null
        defaultClothAuditFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultClothAuditFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClothAuditShouldBeFound(shouldBeFound);
        defaultClothAuditShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClothAuditShouldBeFound(String filter) throws Exception {
        restClothAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplierId").value(hasItem(DEFAULT_SUPPLIER_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restClothAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClothAuditShouldNotBeFound(String filter) throws Exception {
        restClothAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClothAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClothAudit() throws Exception {
        // Get the clothAudit
        restClothAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClothAudit() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        clothAuditSearchRepository.save(clothAudit);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());

        // Update the clothAudit
        ClothAudit updatedClothAudit = clothAuditRepository.findById(clothAudit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClothAudit are not directly saved in db
        em.detach(updatedClothAudit);
        updatedClothAudit
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .productId(UPDATED_PRODUCT_ID)
            .action(UPDATED_ACTION)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .sentAt(UPDATED_SENT_AT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(updatedClothAudit);

        restClothAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothAuditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothAuditDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClothAuditToMatchAllProperties(updatedClothAudit);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ClothAudit> clothAuditSearchList = Streamable.of(clothAuditSearchRepository.findAll()).toList();
                ClothAudit testClothAuditSearch = clothAuditSearchList.get(searchDatabaseSizeAfter - 1);

                assertClothAuditAllPropertiesEquals(testClothAuditSearch, updatedClothAudit);
            });
    }

    @Test
    @Transactional
    void putNonExistingClothAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        clothAudit.setId(longCount.incrementAndGet());

        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothAuditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchClothAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        clothAudit.setId(longCount.incrementAndGet());

        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClothAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        clothAudit.setId(longCount.incrementAndGet());

        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothAuditMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateClothAuditWithPatch() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothAudit using partial update
        ClothAudit partialUpdatedClothAudit = new ClothAudit();
        partialUpdatedClothAudit.setId(clothAudit.getId());

        partialUpdatedClothAudit
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .sentAt(UPDATED_SENT_AT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);

        restClothAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothAudit))
            )
            .andExpect(status().isOk());

        // Validate the ClothAudit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothAuditUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClothAudit, clothAudit),
            getPersistedClothAudit(clothAudit)
        );
    }

    @Test
    @Transactional
    void fullUpdateClothAuditWithPatch() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothAudit using partial update
        ClothAudit partialUpdatedClothAudit = new ClothAudit();
        partialUpdatedClothAudit.setId(clothAudit.getId());

        partialUpdatedClothAudit
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .productId(UPDATED_PRODUCT_ID)
            .action(UPDATED_ACTION)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .sentAt(UPDATED_SENT_AT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);

        restClothAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothAudit))
            )
            .andExpect(status().isOk());

        // Validate the ClothAudit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothAuditUpdatableFieldsEquals(partialUpdatedClothAudit, getPersistedClothAudit(partialUpdatedClothAudit));
    }

    @Test
    @Transactional
    void patchNonExistingClothAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        clothAudit.setId(longCount.incrementAndGet());

        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clothAuditDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClothAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        clothAudit.setId(longCount.incrementAndGet());

        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClothAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        clothAudit.setId(longCount.incrementAndGet());

        // Create the ClothAudit
        ClothAuditDTO clothAuditDTO = clothAuditMapper.toDto(clothAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothAuditMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clothAuditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteClothAudit() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);
        clothAuditRepository.save(clothAudit);
        clothAuditSearchRepository.save(clothAudit);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the clothAudit
        restClothAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, clothAudit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothAuditSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchClothAudit() throws Exception {
        // Initialize the database
        insertedClothAudit = clothAuditRepository.saveAndFlush(clothAudit);
        clothAuditSearchRepository.save(clothAudit);

        // Search the clothAudit
        restClothAuditMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + clothAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplierId").value(hasItem(DEFAULT_SUPPLIER_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return clothAuditRepository.count();
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

    protected ClothAudit getPersistedClothAudit(ClothAudit clothAudit) {
        return clothAuditRepository.findById(clothAudit.getId()).orElseThrow();
    }

    protected void assertPersistedClothAuditToMatchAllProperties(ClothAudit expectedClothAudit) {
        assertClothAuditAllPropertiesEquals(expectedClothAudit, getPersistedClothAudit(expectedClothAudit));
    }

    protected void assertPersistedClothAuditToMatchUpdatableProperties(ClothAudit expectedClothAudit) {
        assertClothAuditAllUpdatablePropertiesEquals(expectedClothAudit, getPersistedClothAudit(expectedClothAudit));
    }
}
