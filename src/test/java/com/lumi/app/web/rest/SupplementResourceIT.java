package com.lumi.app.web.rest;

import static com.lumi.app.domain.SupplementAsserts.*;
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
import com.lumi.app.domain.Product;
import com.lumi.app.domain.Supplement;
import com.lumi.app.domain.Supplier;
import com.lumi.app.repository.SupplementRepository;
import com.lumi.app.repository.search.SupplementSearchRepository;
import com.lumi.app.service.SupplementService;
import com.lumi.app.service.dto.SupplementDTO;
import com.lumi.app.service.mapper.SupplementMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SupplementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SupplementResourceIT {

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

    private static final String ENTITY_API_URL = "/api/supplements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/supplements/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupplementRepository supplementRepository;

    @Mock
    private SupplementRepository supplementRepositoryMock;

    @Autowired
    private SupplementMapper supplementMapper;

    @Mock
    private SupplementService supplementServiceMock;

    @Autowired
    private SupplementSearchRepository supplementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplementMockMvc;

    private Supplement supplement;

    private Supplement insertedSupplement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplement createEntity() {
        return new Supplement()
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
    public static Supplement createUpdatedEntity() {
        return new Supplement()
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
        supplement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSupplement != null) {
            supplementRepository.delete(insertedSupplement);
            supplementSearchRepository.delete(insertedSupplement);
            insertedSupplement = null;
        }
    }

    @Test
    @Transactional
    void createSupplement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);
        var returnedSupplementDTO = om.readValue(
            restSupplementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SupplementDTO.class
        );

        // Validate the Supplement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSupplement = supplementMapper.toEntity(returnedSupplementDTO);
        assertSupplementUpdatableFieldsEquals(returnedSupplement, getPersistedSupplement(returnedSupplement));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSupplement = returnedSupplement;
    }

    @Test
    @Transactional
    void createSupplementWithExistingId() throws Exception {
        // Create the Supplement with an existing ID
        supplement.setId(1L);
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSupplyPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        // set the field null
        supplement.setSupplyPrice(null);

        // Create the Supplement, which fails.
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        restSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsPreferredIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        // set the field null
        supplement.setIsPreferred(null);

        // Create the Supplement, which fails.
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        restSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        // set the field null
        supplement.setCreatedAt(null);

        // Create the Supplement, which fails.
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        restSupplementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSupplements() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList
        restSupplementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplement.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplyPrice").value(hasItem(sameNumber(DEFAULT_SUPPLY_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].leadTimeDays").value(hasItem(DEFAULT_LEAD_TIME_DAYS)))
            .andExpect(jsonPath("$.[*].minOrderQty").value(hasItem(DEFAULT_MIN_ORDER_QTY)))
            .andExpect(jsonPath("$.[*].isPreferred").value(hasItem(DEFAULT_IS_PREFERRED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupplementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(supplementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupplementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(supplementServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupplementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(supplementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupplementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(supplementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSupplement() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get the supplement
        restSupplementMockMvc
            .perform(get(ENTITY_API_URL_ID, supplement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplement.getId().intValue()))
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
    void getSupplementsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        Long id = supplement.getId();

        defaultSupplementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSupplementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSupplementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice equals to
        defaultSupplementFiltering("supplyPrice.equals=" + DEFAULT_SUPPLY_PRICE, "supplyPrice.equals=" + UPDATED_SUPPLY_PRICE);
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice in
        defaultSupplementFiltering(
            "supplyPrice.in=" + DEFAULT_SUPPLY_PRICE + "," + UPDATED_SUPPLY_PRICE,
            "supplyPrice.in=" + UPDATED_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice is not null
        defaultSupplementFiltering("supplyPrice.specified=true", "supplyPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice is greater than or equal to
        defaultSupplementFiltering(
            "supplyPrice.greaterThanOrEqual=" + DEFAULT_SUPPLY_PRICE,
            "supplyPrice.greaterThanOrEqual=" + UPDATED_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice is less than or equal to
        defaultSupplementFiltering(
            "supplyPrice.lessThanOrEqual=" + DEFAULT_SUPPLY_PRICE,
            "supplyPrice.lessThanOrEqual=" + SMALLER_SUPPLY_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice is less than
        defaultSupplementFiltering("supplyPrice.lessThan=" + UPDATED_SUPPLY_PRICE, "supplyPrice.lessThan=" + DEFAULT_SUPPLY_PRICE);
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplyPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where supplyPrice is greater than
        defaultSupplementFiltering("supplyPrice.greaterThan=" + SMALLER_SUPPLY_PRICE, "supplyPrice.greaterThan=" + DEFAULT_SUPPLY_PRICE);
    }

    @Test
    @Transactional
    void getAllSupplementsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where currency equals to
        defaultSupplementFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSupplementsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where currency in
        defaultSupplementFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSupplementsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where currency is not null
        defaultSupplementFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where currency contains
        defaultSupplementFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSupplementsByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where currency does not contain
        defaultSupplementFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays equals to
        defaultSupplementFiltering("leadTimeDays.equals=" + DEFAULT_LEAD_TIME_DAYS, "leadTimeDays.equals=" + UPDATED_LEAD_TIME_DAYS);
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays in
        defaultSupplementFiltering(
            "leadTimeDays.in=" + DEFAULT_LEAD_TIME_DAYS + "," + UPDATED_LEAD_TIME_DAYS,
            "leadTimeDays.in=" + UPDATED_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays is not null
        defaultSupplementFiltering("leadTimeDays.specified=true", "leadTimeDays.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays is greater than or equal to
        defaultSupplementFiltering(
            "leadTimeDays.greaterThanOrEqual=" + DEFAULT_LEAD_TIME_DAYS,
            "leadTimeDays.greaterThanOrEqual=" + UPDATED_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays is less than or equal to
        defaultSupplementFiltering(
            "leadTimeDays.lessThanOrEqual=" + DEFAULT_LEAD_TIME_DAYS,
            "leadTimeDays.lessThanOrEqual=" + SMALLER_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays is less than
        defaultSupplementFiltering("leadTimeDays.lessThan=" + UPDATED_LEAD_TIME_DAYS, "leadTimeDays.lessThan=" + DEFAULT_LEAD_TIME_DAYS);
    }

    @Test
    @Transactional
    void getAllSupplementsByLeadTimeDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where leadTimeDays is greater than
        defaultSupplementFiltering(
            "leadTimeDays.greaterThan=" + SMALLER_LEAD_TIME_DAYS,
            "leadTimeDays.greaterThan=" + DEFAULT_LEAD_TIME_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty equals to
        defaultSupplementFiltering("minOrderQty.equals=" + DEFAULT_MIN_ORDER_QTY, "minOrderQty.equals=" + UPDATED_MIN_ORDER_QTY);
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty in
        defaultSupplementFiltering(
            "minOrderQty.in=" + DEFAULT_MIN_ORDER_QTY + "," + UPDATED_MIN_ORDER_QTY,
            "minOrderQty.in=" + UPDATED_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty is not null
        defaultSupplementFiltering("minOrderQty.specified=true", "minOrderQty.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty is greater than or equal to
        defaultSupplementFiltering(
            "minOrderQty.greaterThanOrEqual=" + DEFAULT_MIN_ORDER_QTY,
            "minOrderQty.greaterThanOrEqual=" + UPDATED_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty is less than or equal to
        defaultSupplementFiltering(
            "minOrderQty.lessThanOrEqual=" + DEFAULT_MIN_ORDER_QTY,
            "minOrderQty.lessThanOrEqual=" + SMALLER_MIN_ORDER_QTY
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty is less than
        defaultSupplementFiltering("minOrderQty.lessThan=" + UPDATED_MIN_ORDER_QTY, "minOrderQty.lessThan=" + DEFAULT_MIN_ORDER_QTY);
    }

    @Test
    @Transactional
    void getAllSupplementsByMinOrderQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where minOrderQty is greater than
        defaultSupplementFiltering("minOrderQty.greaterThan=" + SMALLER_MIN_ORDER_QTY, "minOrderQty.greaterThan=" + DEFAULT_MIN_ORDER_QTY);
    }

    @Test
    @Transactional
    void getAllSupplementsByIsPreferredIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where isPreferred equals to
        defaultSupplementFiltering("isPreferred.equals=" + DEFAULT_IS_PREFERRED, "isPreferred.equals=" + UPDATED_IS_PREFERRED);
    }

    @Test
    @Transactional
    void getAllSupplementsByIsPreferredIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where isPreferred in
        defaultSupplementFiltering(
            "isPreferred.in=" + DEFAULT_IS_PREFERRED + "," + UPDATED_IS_PREFERRED,
            "isPreferred.in=" + UPDATED_IS_PREFERRED
        );
    }

    @Test
    @Transactional
    void getAllSupplementsByIsPreferredIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where isPreferred is not null
        defaultSupplementFiltering("isPreferred.specified=true", "isPreferred.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where createdAt equals to
        defaultSupplementFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSupplementsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where createdAt in
        defaultSupplementFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSupplementsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where createdAt is not null
        defaultSupplementFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where updatedAt equals to
        defaultSupplementFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSupplementsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where updatedAt in
        defaultSupplementFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSupplementsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        // Get all the supplementList where updatedAt is not null
        defaultSupplementFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplementsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            supplementRepository.saveAndFlush(supplement);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        supplement.setProduct(product);
        supplementRepository.saveAndFlush(supplement);
        Long productId = product.getId();
        // Get all the supplementList where product equals to productId
        defaultSupplementShouldBeFound("productId.equals=" + productId);

        // Get all the supplementList where product equals to (productId + 1)
        defaultSupplementShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    @Test
    @Transactional
    void getAllSupplementsBySupplierIsEqualToSomething() throws Exception {
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplementRepository.saveAndFlush(supplement);
            supplier = SupplierResourceIT.createEntity();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        em.persist(supplier);
        em.flush();
        supplement.setSupplier(supplier);
        supplementRepository.saveAndFlush(supplement);
        Long supplierId = supplier.getId();
        // Get all the supplementList where supplier equals to supplierId
        defaultSupplementShouldBeFound("supplierId.equals=" + supplierId);

        // Get all the supplementList where supplier equals to (supplierId + 1)
        defaultSupplementShouldNotBeFound("supplierId.equals=" + (supplierId + 1));
    }

    private void defaultSupplementFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSupplementShouldBeFound(shouldBeFound);
        defaultSupplementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSupplementShouldBeFound(String filter) throws Exception {
        restSupplementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplement.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplyPrice").value(hasItem(sameNumber(DEFAULT_SUPPLY_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].leadTimeDays").value(hasItem(DEFAULT_LEAD_TIME_DAYS)))
            .andExpect(jsonPath("$.[*].minOrderQty").value(hasItem(DEFAULT_MIN_ORDER_QTY)))
            .andExpect(jsonPath("$.[*].isPreferred").value(hasItem(DEFAULT_IS_PREFERRED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restSupplementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSupplementShouldNotBeFound(String filter) throws Exception {
        restSupplementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSupplementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSupplement() throws Exception {
        // Get the supplement
        restSupplementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplement() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplementSearchRepository.save(supplement);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());

        // Update the supplement
        Supplement updatedSupplement = supplementRepository.findById(supplement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSupplement are not directly saved in db
        em.detach(updatedSupplement);
        updatedSupplement
            .supplyPrice(UPDATED_SUPPLY_PRICE)
            .currency(UPDATED_CURRENCY)
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        SupplementDTO supplementDTO = supplementMapper.toDto(updatedSupplement);

        restSupplementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSupplementToMatchAllProperties(updatedSupplement);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Supplement> supplementSearchList = Streamable.of(supplementSearchRepository.findAll()).toList();
                Supplement testSupplementSearch = supplementSearchList.get(searchDatabaseSizeAfter - 1);

                assertSupplementAllPropertiesEquals(testSupplementSearch, updatedSupplement);
            });
    }

    @Test
    @Transactional
    void putNonExistingSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        supplement.setId(longCount.incrementAndGet());

        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        supplement.setId(longCount.incrementAndGet());

        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        supplement.setId(longCount.incrementAndGet());

        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSupplementWithPatch() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplement using partial update
        Supplement partialUpdatedSupplement = new Supplement();
        partialUpdatedSupplement.setId(supplement.getId());

        partialUpdatedSupplement
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .updatedAt(UPDATED_UPDATED_AT);

        restSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplement))
            )
            .andExpect(status().isOk());

        // Validate the Supplement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSupplement, supplement),
            getPersistedSupplement(supplement)
        );
    }

    @Test
    @Transactional
    void fullUpdateSupplementWithPatch() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplement using partial update
        Supplement partialUpdatedSupplement = new Supplement();
        partialUpdatedSupplement.setId(supplement.getId());

        partialUpdatedSupplement
            .supplyPrice(UPDATED_SUPPLY_PRICE)
            .currency(UPDATED_CURRENCY)
            .leadTimeDays(UPDATED_LEAD_TIME_DAYS)
            .minOrderQty(UPDATED_MIN_ORDER_QTY)
            .isPreferred(UPDATED_IS_PREFERRED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplement))
            )
            .andExpect(status().isOk());

        // Validate the Supplement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplementUpdatableFieldsEquals(partialUpdatedSupplement, getPersistedSupplement(partialUpdatedSupplement));
    }

    @Test
    @Transactional
    void patchNonExistingSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        supplement.setId(longCount.incrementAndGet());

        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        supplement.setId(longCount.incrementAndGet());

        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        supplement.setId(longCount.incrementAndGet());

        // Create the Supplement
        SupplementDTO supplementDTO = supplementMapper.toDto(supplement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(supplementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSupplement() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);
        supplementRepository.save(supplement);
        supplementSearchRepository.save(supplement);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the supplement
        restSupplementMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSupplement() throws Exception {
        // Initialize the database
        insertedSupplement = supplementRepository.saveAndFlush(supplement);
        supplementSearchRepository.save(supplement);

        // Search the supplement
        restSupplementMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + supplement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplement.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplyPrice").value(hasItem(sameNumber(DEFAULT_SUPPLY_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].leadTimeDays").value(hasItem(DEFAULT_LEAD_TIME_DAYS)))
            .andExpect(jsonPath("$.[*].minOrderQty").value(hasItem(DEFAULT_MIN_ORDER_QTY)))
            .andExpect(jsonPath("$.[*].isPreferred").value(hasItem(DEFAULT_IS_PREFERRED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return supplementRepository.count();
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

    protected Supplement getPersistedSupplement(Supplement supplement) {
        return supplementRepository.findById(supplement.getId()).orElseThrow();
    }

    protected void assertPersistedSupplementToMatchAllProperties(Supplement expectedSupplement) {
        assertSupplementAllPropertiesEquals(expectedSupplement, getPersistedSupplement(expectedSupplement));
    }

    protected void assertPersistedSupplementToMatchUpdatableProperties(Supplement expectedSupplement) {
        assertSupplementAllUpdatablePropertiesEquals(expectedSupplement, getPersistedSupplement(expectedSupplement));
    }
}
