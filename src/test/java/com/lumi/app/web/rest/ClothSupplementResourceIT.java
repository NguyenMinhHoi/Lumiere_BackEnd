package com.lumi.app.web.rest;

import static com.lumi.app.domain.ClothSupplementAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ClothSupplement;
import com.lumi.app.repository.ClothSupplementRepository;
import com.lumi.app.repository.search.ClothSupplementSearchRepository;
import com.lumi.app.service.dto.ClothSupplementDTO;
import com.lumi.app.service.mapper.ClothSupplementMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ClothSupplementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClothSupplementResourceIT {

    private static final Long DEFAULT_CLOTH_ID = 1L;
    private static final Long UPDATED_CLOTH_ID = 2L;
    private static final Long SMALLER_CLOTH_ID = 1L - 1L;

    private static final Long DEFAULT_SUPPLIER_ID = 1L;
    private static final Long UPDATED_SUPPLIER_ID = 2L;
    private static final Long SMALLER_SUPPLIER_ID = 1L - 1L;

    private static final BigDecimal DEFAULT_SUPPLY_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUPPLY_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SUPPLY_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final Integer DEFAULT_LEAD_TIME_DAYS = 0;
    private static final Integer UPDATED_LEAD_TIME_DAYS = 1;
    private static final Integer SMALLER_LEAD_TIME_DAYS = 0 - 1;

    private static final Integer DEFAULT_MIN_ORDER_QTY = 1;
    private static final Integer UPDATED_MIN_ORDER_QTY = 2;
    private static final Integer SMALLER_MIN_ORDER_QTY = 1 - 1;

    private static final Boolean DEFAULT_IS_PREFERRED = false;
    private static final Boolean UPDATED_IS_PREFERRED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cloth-supplements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cloth-supplements/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClothSupplementRepository clothSupplementRepository;

    @Autowired
    private ClothSupplementMapper clothSupplementMapper;

    @Autowired
    private ClothSupplementSearchRepository clothSupplementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClothSupplementMockMvc;

    private ClothSupplement clothSupplement;

    private ClothSupplement insertedClothSupplement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothSupplement createEntity() {
        return new ClothSupplement()
            .clothId(DEFAULT_CLOTH_ID)
            .supplierId(DEFAULT_SUPPLIER_ID)
            .supplyPrice(DEFAULT_SUPPLY_PRICE)
            .currency(DEFAULT_CURRENCY)
            .leadTimeDays(DEFAULT_LEAD_TIME_DAYS)
            .minOrderQty(DEFAULT_MIN_ORDER_QTY)
            .isPreferred(DEFAULT_IS_PREFERRED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothSupplement createUpdatedEntity() {
        return new ClothSupplement()
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .supplyPrice(UPDATED_SUPPLY_PRICE)
            .currency(UPDATED_CURRENCY)
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        clothSupplement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClothSupplement != null) {
            clothSupplementRepository.delete(insertedClothSupplement);
            clothSupplementSearchRepository.delete(insertedClothSupplement);
            insertedClothSupplement = null;
        }
    }

    @Test
    @Transactional
    void createClothSupplement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);
        var returnedClothSupplementDTO = om.readValue(
            restClothSupplementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClothSupplementDTO.class
        );

        // Validate the ClothSupplement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClothSupplement = clothSupplementMapper.toEntity(returnedClothSupplementDTO);
        assertClothSupplementUpdatableFieldsEquals(returnedClothSupplement, getPersistedClothSupplement(returnedClothSupplement));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedClothSupplement = returnedClothSupplement;
    }

    @Test
    @Transactional
    void createClothSupplementWithExistingId() throws Exception {
        // Create the ClothSupplement with an existing ID
        clothSupplement.setId(1L);
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClothSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkClothIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        // set the field null
        clothSupplement.setClothId(null);

        // Create the ClothSupplement, which fails.
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        restClothSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSupplierIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        // set the field null
        clothSupplement.setSupplierId(null);

        // Create the ClothSupplement, which fails.
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        restClothSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSupplyPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        // set the field null
        clothSupplement.setSupplyPrice(null);

        // Create the ClothSupplement, which fails.
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        restClothSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsPreferredIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        // set the field null
        clothSupplement.setIsPreferred(null);

        // Create the ClothSupplement, which fails.
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        restClothSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        // set the field null
        clothSupplement.setCreatedAt(null);

        // Create the ClothSupplement, which fails.
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        restClothSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllClothSupplements() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList
        restClothSupplementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothSupplement.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplierId").value(hasItem(DEFAULT_SUPPLIER_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplyPrice").value(hasItem(sameNumber(DEFAULT_SUPPLY_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].leadTimeDays").value(hasItem(DEFAULT_LEAD_TIME_DAYS)))
            .andExpect(jsonPath("$.[*].minOrderQty").value(hasItem(DEFAULT_MIN_ORDER_QTY)))
            .andExpect(jsonPath("$.[*].isPreferred").value(hasItem(DEFAULT_IS_PREFERRED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getClothSupplement() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get the clothSupplement
        restClothSupplementMockMvc
            .perform(get(ENTITY_API_URL_ID, clothSupplement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clothSupplement.getId().intValue()))
            .andExpect(jsonPath("$.clothId").value(DEFAULT_CLOTH_ID.intValue()))
            .andExpect(jsonPath("$.supplierId").value(DEFAULT_SUPPLIER_ID.intValue()))
            .andExpect(jsonPath("$.supplyPrice").value(sameNumber(DEFAULT_SUPPLY_PRICE)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.leadTimeDays").value(DEFAULT_LEAD_TIME_DAYS))
            .andExpect(jsonPath("$.minOrderQty").value(DEFAULT_MIN_ORDER_QTY))
            .andExpect(jsonPath("$.isPreferred").value(DEFAULT_IS_PREFERRED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getClothSupplementsByIdFiltering() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        Long id = clothSupplement.getId();

        defaultClothSupplementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClothSupplementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClothSupplementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId equals to
        defaultClothSupplementFiltering("clothId.equals=" + DEFAULT_CLOTH_ID, "clothId.equals=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId in
        defaultClothSupplementFiltering("clothId.in=" + DEFAULT_CLOTH_ID + "," + UPDATED_CLOTH_ID, "clothId.in=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId is not null
        defaultClothSupplementFiltering("clothId.specified=true", "clothId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId is greater than or equal to
        defaultClothSupplementFiltering("clothId.greaterThanOrEqual=" + DEFAULT_CLOTH_ID, "clothId.greaterThanOrEqual=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId is less than or equal to
        defaultClothSupplementFiltering("clothId.lessThanOrEqual=" + DEFAULT_CLOTH_ID, "clothId.lessThanOrEqual=" + SMALLER_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId is less than
        defaultClothSupplementFiltering("clothId.lessThan=" + UPDATED_CLOTH_ID, "clothId.lessThan=" + DEFAULT_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByClothIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where clothId is greater than
        defaultClothSupplementFiltering("clothId.greaterThan=" + SMALLER_CLOTH_ID, "clothId.greaterThan=" + DEFAULT_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId equals to
        defaultClothSupplementFiltering("supplierId.equals=" + DEFAULT_SUPPLIER_ID, "supplierId.equals=" + UPDATED_SUPPLIER_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId in
        defaultClothSupplementFiltering(
            "supplierId.in=" + DEFAULT_SUPPLIER_ID + "," + UPDATED_SUPPLIER_ID,
            "supplierId.in=" + UPDATED_SUPPLIER_ID
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId is not null
        defaultClothSupplementFiltering("supplierId.specified=true", "supplierId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId is greater than or equal to
        defaultClothSupplementFiltering(
            "supplierId.greaterThanOrEqual=" + DEFAULT_SUPPLIER_ID,
            "supplierId.greaterThanOrEqual=" + UPDATED_SUPPLIER_ID
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId is less than or equal to
        defaultClothSupplementFiltering(
            "supplierId.lessThanOrEqual=" + DEFAULT_SUPPLIER_ID,
            "supplierId.lessThanOrEqual=" + SMALLER_SUPPLIER_ID
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId is less than
        defaultClothSupplementFiltering("supplierId.lessThan=" + UPDATED_SUPPLIER_ID, "supplierId.lessThan=" + DEFAULT_SUPPLIER_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplierIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplierId is greater than
        defaultClothSupplementFiltering("supplierId.greaterThan=" + SMALLER_SUPPLIER_ID, "supplierId.greaterThan=" + DEFAULT_SUPPLIER_ID);
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice equals to
        defaultClothSupplementFiltering("supplyPrice.equals=" + DEFAULT_SUPPLY_PRICE, "supplyPrice.equals=" + UPDATED_SUPPLY_PRICE);
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice in
        defaultClothSupplementFiltering(
            "supplyPrice.in=" + DEFAULT_SUPPLY_PRICE + "," + UPDATED_SUPPLY_PRICE,
            "supplyPrice.in=" + UPDATED_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice is not null
        defaultClothSupplementFiltering("supplyPrice.specified=true", "supplyPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice is greater than or equal to
        defaultClothSupplementFiltering(
            "supplyPrice.greaterThanOrEqual=" + DEFAULT_SUPPLY_PRICE,
            "supplyPrice.greaterThanOrEqual=" + UPDATED_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice is less than or equal to
        defaultClothSupplementFiltering(
            "supplyPrice.lessThanOrEqual=" + DEFAULT_SUPPLY_PRICE,
            "supplyPrice.lessThanOrEqual=" + SMALLER_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice is less than
        defaultClothSupplementFiltering("supplyPrice.lessThan=" + UPDATED_SUPPLY_PRICE, "supplyPrice.lessThan=" + DEFAULT_SUPPLY_PRICE);
    }

    @Test
    @Transactional
    void getAllClothSupplementsBySupplyPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where supplyPrice is greater than
        defaultClothSupplementFiltering(
            "supplyPrice.greaterThan=" + SMALLER_SUPPLY_PRICE,
            "supplyPrice.greaterThan=" + DEFAULT_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where currency equals to
        defaultClothSupplementFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where currency in
        defaultClothSupplementFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where currency is not null
        defaultClothSupplementFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where currency contains
        defaultClothSupplementFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where currency does not contain
        defaultClothSupplementFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays equals to
        defaultClothSupplementFiltering("leadTimeDays.equals=" + DEFAULT_LEAD_TIME_DAYS, "leadTimeDays.equals=" + UPDATED_LEAD_TIME_DAYS);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays in
        defaultClothSupplementFiltering(
            "leadTimeDays.in=" + DEFAULT_LEAD_TIME_DAYS + "," + UPDATED_LEAD_TIME_DAYS,
            "leadTimeDays.in=" + UPDATED_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays is not null
        defaultClothSupplementFiltering("leadTimeDays.specified=true", "leadTimeDays.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays is greater than or equal to
        defaultClothSupplementFiltering(
            "leadTimeDays.greaterThanOrEqual=" + DEFAULT_LEAD_TIME_DAYS,
            "leadTimeDays.greaterThanOrEqual=" + UPDATED_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays is less than or equal to
        defaultClothSupplementFiltering(
            "leadTimeDays.lessThanOrEqual=" + DEFAULT_LEAD_TIME_DAYS,
            "leadTimeDays.lessThanOrEqual=" + SMALLER_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays is less than
        defaultClothSupplementFiltering(
            "leadTimeDays.lessThan=" + UPDATED_LEAD_TIME_DAYS,
            "leadTimeDays.lessThan=" + DEFAULT_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByLeadTimeDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where leadTimeDays is greater than
        defaultClothSupplementFiltering(
            "leadTimeDays.greaterThan=" + SMALLER_LEAD_TIME_DAYS,
            "leadTimeDays.greaterThan=" + DEFAULT_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty equals to
        defaultClothSupplementFiltering("minOrderQty.equals=" + DEFAULT_MIN_ORDER_QTY, "minOrderQty.equals=" + UPDATED_MIN_ORDER_QTY);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty in
        defaultClothSupplementFiltering(
            "minOrderQty.in=" + DEFAULT_MIN_ORDER_QTY + "," + UPDATED_MIN_ORDER_QTY,
            "minOrderQty.in=" + UPDATED_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty is not null
        defaultClothSupplementFiltering("minOrderQty.specified=true", "minOrderQty.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty is greater than or equal to
        defaultClothSupplementFiltering(
            "minOrderQty.greaterThanOrEqual=" + DEFAULT_MIN_ORDER_QTY,
            "minOrderQty.greaterThanOrEqual=" + UPDATED_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty is less than or equal to
        defaultClothSupplementFiltering(
            "minOrderQty.lessThanOrEqual=" + DEFAULT_MIN_ORDER_QTY,
            "minOrderQty.lessThanOrEqual=" + SMALLER_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty is less than
        defaultClothSupplementFiltering("minOrderQty.lessThan=" + UPDATED_MIN_ORDER_QTY, "minOrderQty.lessThan=" + DEFAULT_MIN_ORDER_QTY);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByMinOrderQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where minOrderQty is greater than
        defaultClothSupplementFiltering(
            "minOrderQty.greaterThan=" + SMALLER_MIN_ORDER_QTY,
            "minOrderQty.greaterThan=" + DEFAULT_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByIsPreferredIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where isPreferred equals to
        defaultClothSupplementFiltering("isPreferred.equals=" + DEFAULT_IS_PREFERRED, "isPreferred.equals=" + UPDATED_IS_PREFERRED);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByIsPreferredIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where isPreferred in
        defaultClothSupplementFiltering(
            "isPreferred.in=" + DEFAULT_IS_PREFERRED + "," + UPDATED_IS_PREFERRED,
            "isPreferred.in=" + UPDATED_IS_PREFERRED
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByIsPreferredIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where isPreferred is not null
        defaultClothSupplementFiltering("isPreferred.specified=true", "isPreferred.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where createdAt equals to
        defaultClothSupplementFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where createdAt in
        defaultClothSupplementFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where createdAt is not null
        defaultClothSupplementFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClothSupplementsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where updatedAt equals to
        defaultClothSupplementFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClothSupplementsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where updatedAt in
        defaultClothSupplementFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllClothSupplementsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        // Get all the clothSupplementList where updatedAt is not null
        defaultClothSupplementFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultClothSupplementFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClothSupplementShouldBeFound(shouldBeFound);
        defaultClothSupplementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClothSupplementShouldBeFound(String filter) throws Exception {
        restClothSupplementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothSupplement.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplierId").value(hasItem(DEFAULT_SUPPLIER_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplyPrice").value(hasItem(sameNumber(DEFAULT_SUPPLY_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].leadTimeDays").value(hasItem(DEFAULT_LEAD_TIME_DAYS)))
            .andExpect(jsonPath("$.[*].minOrderQty").value(hasItem(DEFAULT_MIN_ORDER_QTY)))
            .andExpect(jsonPath("$.[*].isPreferred").value(hasItem(DEFAULT_IS_PREFERRED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restClothSupplementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClothSupplementShouldNotBeFound(String filter) throws Exception {
        restClothSupplementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClothSupplementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClothSupplement() throws Exception {
        // Get the clothSupplement
        restClothSupplementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClothSupplement() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        clothSupplementSearchRepository.save(clothSupplement);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());

        // Update the clothSupplement
        ClothSupplement updatedClothSupplement = clothSupplementRepository.findById(clothSupplement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClothSupplement are not directly saved in db
        em.detach(updatedClothSupplement);
        updatedClothSupplement
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .supplyPrice(UPDATED_SUPPLY_PRICE)
            .currency(UPDATED_CURRENCY)
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(updatedClothSupplement);

        restClothSupplementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothSupplementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothSupplementDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClothSupplementToMatchAllProperties(updatedClothSupplement);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ClothSupplement> clothSupplementSearchList = Streamable.of(clothSupplementSearchRepository.findAll()).toList();
                ClothSupplement testClothSupplementSearch = clothSupplementSearchList.get(searchDatabaseSizeAfter - 1);

                assertClothSupplementAllPropertiesEquals(testClothSupplementSearch, updatedClothSupplement);
            });
    }

    @Test
    @Transactional
    void putNonExistingClothSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        clothSupplement.setId(longCount.incrementAndGet());

        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothSupplementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothSupplementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothSupplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchClothSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        clothSupplement.setId(longCount.incrementAndGet());

        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothSupplementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothSupplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClothSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        clothSupplement.setId(longCount.incrementAndGet());

        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothSupplementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateClothSupplementWithPatch() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothSupplement using partial update
        ClothSupplement partialUpdatedClothSupplement = new ClothSupplement();
        partialUpdatedClothSupplement.setId(clothSupplement.getId());

        partialUpdatedClothSupplement
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .supplyPrice(UPDATED_SUPPLY_PRICE)
            .currency(UPDATED_CURRENCY)
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restClothSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothSupplement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothSupplement))
            )
            .andExpect(status().isOk());

        // Validate the ClothSupplement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothSupplementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClothSupplement, clothSupplement),
            getPersistedClothSupplement(clothSupplement)
        );
    }

    @Test
    @Transactional
    void fullUpdateClothSupplementWithPatch() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothSupplement using partial update
        ClothSupplement partialUpdatedClothSupplement = new ClothSupplement();
        partialUpdatedClothSupplement.setId(clothSupplement.getId());

        partialUpdatedClothSupplement
            .clothId(UPDATED_CLOTH_ID)
            .supplierId(UPDATED_SUPPLIER_ID)
            .supplyPrice(UPDATED_SUPPLY_PRICE)
            .currency(UPDATED_CURRENCY)
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restClothSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothSupplement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothSupplement))
            )
            .andExpect(status().isOk());

        // Validate the ClothSupplement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothSupplementUpdatableFieldsEquals(
            partialUpdatedClothSupplement,
            getPersistedClothSupplement(partialUpdatedClothSupplement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingClothSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        clothSupplement.setId(longCount.incrementAndGet());

        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clothSupplementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothSupplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClothSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        clothSupplement.setId(longCount.incrementAndGet());

        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothSupplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClothSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        clothSupplement.setId(longCount.incrementAndGet());

        // Create the ClothSupplement
        ClothSupplementDTO clothSupplementDTO = clothSupplementMapper.toDto(clothSupplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothSupplementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clothSupplementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothSupplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteClothSupplement() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);
        clothSupplementRepository.save(clothSupplement);
        clothSupplementSearchRepository.save(clothSupplement);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the clothSupplement
        restClothSupplementMockMvc
            .perform(delete(ENTITY_API_URL_ID, clothSupplement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSupplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchClothSupplement() throws Exception {
        // Initialize the database
        insertedClothSupplement = clothSupplementRepository.saveAndFlush(clothSupplement);
        clothSupplementSearchRepository.save(clothSupplement);

        // Search the clothSupplement
        restClothSupplementMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + clothSupplement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothSupplement.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplierId").value(hasItem(DEFAULT_SUPPLIER_ID.intValue())))
            .andExpect(jsonPath("$.[*].supplyPrice").value(hasItem(sameNumber(DEFAULT_SUPPLY_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].leadTimeDays").value(hasItem(DEFAULT_LEAD_TIME_DAYS)))
            .andExpect(jsonPath("$.[*].minOrderQty").value(hasItem(DEFAULT_MIN_ORDER_QTY)))
            .andExpect(jsonPath("$.[*].isPreferred").value(hasItem(DEFAULT_IS_PREFERRED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return clothSupplementRepository.count();
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

    protected ClothSupplement getPersistedClothSupplement(ClothSupplement clothSupplement) {
        return clothSupplementRepository.findById(clothSupplement.getId()).orElseThrow();
    }

    protected void assertPersistedClothSupplementToMatchAllProperties(ClothSupplement expectedClothSupplement) {
        assertClothSupplementAllPropertiesEquals(expectedClothSupplement, getPersistedClothSupplement(expectedClothSupplement));
    }

    protected void assertPersistedClothSupplementToMatchUpdatableProperties(ClothSupplement expectedClothSupplement) {
        assertClothSupplementAllUpdatablePropertiesEquals(expectedClothSupplement, getPersistedClothSupplement(expectedClothSupplement));
    }
}
