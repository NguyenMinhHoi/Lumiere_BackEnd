package com.lumi.app.web.rest;

import static com.lumi.app.domain.VoucherAsserts.*;
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
import com.lumi.app.domain.Voucher;
import com.lumi.app.domain.enumeration.VoucherStatus;
import com.lumi.app.domain.enumeration.VoucherType;
import com.lumi.app.repository.VoucherRepository;
import com.lumi.app.repository.search.VoucherSearchRepository;
import com.lumi.app.service.dto.VoucherDTO;
import com.lumi.app.service.mapper.VoucherMapper;
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
 * Integration tests for the {@link VoucherResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VoucherResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final VoucherType DEFAULT_DISCOUNT_TYPE = VoucherType.PERCENT;
    private static final VoucherType UPDATED_DISCOUNT_TYPE = VoucherType.FIXED;

    private static final BigDecimal DEFAULT_DISCOUNT_VALUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_VALUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MIN_ORDER_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_ORDER_VALUE = new BigDecimal(2);
    private static final BigDecimal SMALLER_MIN_ORDER_VALUE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_MAX_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_DISCOUNT_VALUE = new BigDecimal(2);
    private static final BigDecimal SMALLER_MAX_DISCOUNT_VALUE = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_USAGE_LIMIT = 1;
    private static final Integer UPDATED_USAGE_LIMIT = 2;
    private static final Integer SMALLER_USAGE_LIMIT = 1 - 1;

    private static final Integer DEFAULT_USED_COUNT = 0;
    private static final Integer UPDATED_USED_COUNT = 1;
    private static final Integer SMALLER_USED_COUNT = 0 - 1;

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_VALID_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final VoucherStatus DEFAULT_STATUS = VoucherStatus.ACTIVE;
    private static final VoucherStatus UPDATED_STATUS = VoucherStatus.EXPIRED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/vouchers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/vouchers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private VoucherSearchRepository voucherSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVoucherMockMvc;

    private Voucher voucher;

    private Voucher insertedVoucher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Voucher createEntity() {
        return new Voucher()
            .code(DEFAULT_CODE)
            .discountType(DEFAULT_DISCOUNT_TYPE)
            .discountValue(DEFAULT_DISCOUNT_VALUE)
            .minOrderValue(DEFAULT_MIN_ORDER_VALUE)
            .maxDiscountValue(DEFAULT_MAX_DISCOUNT_VALUE)
            .usageLimit(DEFAULT_USAGE_LIMIT)
            .usedCount(DEFAULT_USED_COUNT)
            .validFrom(DEFAULT_VALID_FROM)
            .validTo(DEFAULT_VALID_TO)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Voucher createUpdatedEntity() {
        return new Voucher()
            .code(UPDATED_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minOrderValue(UPDATED_MIN_ORDER_VALUE)
            .maxDiscountValue(UPDATED_MAX_DISCOUNT_VALUE)
            .usageLimit(UPDATED_USAGE_LIMIT)
            .usedCount(UPDATED_USED_COUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        voucher = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVoucher != null) {
            voucherRepository.delete(insertedVoucher);
            voucherSearchRepository.delete(insertedVoucher);
            insertedVoucher = null;
        }
    }

    @Test
    @Transactional
    void createVoucher() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);
        var returnedVoucherDTO = om.readValue(
            restVoucherMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VoucherDTO.class
        );

        // Validate the Voucher in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVoucher = voucherMapper.toEntity(returnedVoucherDTO);
        assertVoucherUpdatableFieldsEquals(returnedVoucher, getPersistedVoucher(returnedVoucher));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedVoucher = returnedVoucher;
    }

    @Test
    @Transactional
    void createVoucherWithExistingId() throws Exception {
        // Create the Voucher with an existing ID
        voucher.setId(1L);
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setCode(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setDiscountType(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setDiscountValue(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUsedCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setUsedCount(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setValidFrom(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkValidToIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setValidTo(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setStatus(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        // set the field null
        voucher.setCreatedAt(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllVouchers() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voucher.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].minOrderValue").value(hasItem(sameNumber(DEFAULT_MIN_ORDER_VALUE))))
            .andExpect(jsonPath("$.[*].maxDiscountValue").value(hasItem(sameNumber(DEFAULT_MAX_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].usageLimit").value(hasItem(DEFAULT_USAGE_LIMIT)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get the voucher
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL_ID, voucher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(voucher.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.discountType").value(DEFAULT_DISCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.discountValue").value(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .andExpect(jsonPath("$.minOrderValue").value(sameNumber(DEFAULT_MIN_ORDER_VALUE)))
            .andExpect(jsonPath("$.maxDiscountValue").value(sameNumber(DEFAULT_MAX_DISCOUNT_VALUE)))
            .andExpect(jsonPath("$.usageLimit").value(DEFAULT_USAGE_LIMIT))
            .andExpect(jsonPath("$.usedCount").value(DEFAULT_USED_COUNT))
            .andExpect(jsonPath("$.validFrom").value(DEFAULT_VALID_FROM.toString()))
            .andExpect(jsonPath("$.validTo").value(DEFAULT_VALID_TO.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getVouchersByIdFiltering() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        Long id = voucher.getId();

        defaultVoucherFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVoucherFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVoucherFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVouchersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where code equals to
        defaultVoucherFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllVouchersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where code in
        defaultVoucherFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllVouchersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where code is not null
        defaultVoucherFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where code contains
        defaultVoucherFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllVouchersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where code does not contain
        defaultVoucherFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountType equals to
        defaultVoucherFiltering("discountType.equals=" + DEFAULT_DISCOUNT_TYPE, "discountType.equals=" + UPDATED_DISCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountType in
        defaultVoucherFiltering(
            "discountType.in=" + DEFAULT_DISCOUNT_TYPE + "," + UPDATED_DISCOUNT_TYPE,
            "discountType.in=" + UPDATED_DISCOUNT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountType is not null
        defaultVoucherFiltering("discountType.specified=true", "discountType.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue equals to
        defaultVoucherFiltering("discountValue.equals=" + DEFAULT_DISCOUNT_VALUE, "discountValue.equals=" + UPDATED_DISCOUNT_VALUE);
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue in
        defaultVoucherFiltering(
            "discountValue.in=" + DEFAULT_DISCOUNT_VALUE + "," + UPDATED_DISCOUNT_VALUE,
            "discountValue.in=" + UPDATED_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue is not null
        defaultVoucherFiltering("discountValue.specified=true", "discountValue.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue is greater than or equal to
        defaultVoucherFiltering(
            "discountValue.greaterThanOrEqual=" + DEFAULT_DISCOUNT_VALUE,
            "discountValue.greaterThanOrEqual=" + UPDATED_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue is less than or equal to
        defaultVoucherFiltering(
            "discountValue.lessThanOrEqual=" + DEFAULT_DISCOUNT_VALUE,
            "discountValue.lessThanOrEqual=" + SMALLER_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue is less than
        defaultVoucherFiltering("discountValue.lessThan=" + UPDATED_DISCOUNT_VALUE, "discountValue.lessThan=" + DEFAULT_DISCOUNT_VALUE);
    }

    @Test
    @Transactional
    void getAllVouchersByDiscountValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where discountValue is greater than
        defaultVoucherFiltering(
            "discountValue.greaterThan=" + SMALLER_DISCOUNT_VALUE,
            "discountValue.greaterThan=" + DEFAULT_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue equals to
        defaultVoucherFiltering("minOrderValue.equals=" + DEFAULT_MIN_ORDER_VALUE, "minOrderValue.equals=" + UPDATED_MIN_ORDER_VALUE);
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue in
        defaultVoucherFiltering(
            "minOrderValue.in=" + DEFAULT_MIN_ORDER_VALUE + "," + UPDATED_MIN_ORDER_VALUE,
            "minOrderValue.in=" + UPDATED_MIN_ORDER_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue is not null
        defaultVoucherFiltering("minOrderValue.specified=true", "minOrderValue.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue is greater than or equal to
        defaultVoucherFiltering(
            "minOrderValue.greaterThanOrEqual=" + DEFAULT_MIN_ORDER_VALUE,
            "minOrderValue.greaterThanOrEqual=" + UPDATED_MIN_ORDER_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue is less than or equal to
        defaultVoucherFiltering(
            "minOrderValue.lessThanOrEqual=" + DEFAULT_MIN_ORDER_VALUE,
            "minOrderValue.lessThanOrEqual=" + SMALLER_MIN_ORDER_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue is less than
        defaultVoucherFiltering("minOrderValue.lessThan=" + UPDATED_MIN_ORDER_VALUE, "minOrderValue.lessThan=" + DEFAULT_MIN_ORDER_VALUE);
    }

    @Test
    @Transactional
    void getAllVouchersByMinOrderValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where minOrderValue is greater than
        defaultVoucherFiltering(
            "minOrderValue.greaterThan=" + SMALLER_MIN_ORDER_VALUE,
            "minOrderValue.greaterThan=" + DEFAULT_MIN_ORDER_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue equals to
        defaultVoucherFiltering(
            "maxDiscountValue.equals=" + DEFAULT_MAX_DISCOUNT_VALUE,
            "maxDiscountValue.equals=" + UPDATED_MAX_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue in
        defaultVoucherFiltering(
            "maxDiscountValue.in=" + DEFAULT_MAX_DISCOUNT_VALUE + "," + UPDATED_MAX_DISCOUNT_VALUE,
            "maxDiscountValue.in=" + UPDATED_MAX_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue is not null
        defaultVoucherFiltering("maxDiscountValue.specified=true", "maxDiscountValue.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue is greater than or equal to
        defaultVoucherFiltering(
            "maxDiscountValue.greaterThanOrEqual=" + DEFAULT_MAX_DISCOUNT_VALUE,
            "maxDiscountValue.greaterThanOrEqual=" + UPDATED_MAX_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue is less than or equal to
        defaultVoucherFiltering(
            "maxDiscountValue.lessThanOrEqual=" + DEFAULT_MAX_DISCOUNT_VALUE,
            "maxDiscountValue.lessThanOrEqual=" + SMALLER_MAX_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue is less than
        defaultVoucherFiltering(
            "maxDiscountValue.lessThan=" + UPDATED_MAX_DISCOUNT_VALUE,
            "maxDiscountValue.lessThan=" + DEFAULT_MAX_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByMaxDiscountValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where maxDiscountValue is greater than
        defaultVoucherFiltering(
            "maxDiscountValue.greaterThan=" + SMALLER_MAX_DISCOUNT_VALUE,
            "maxDiscountValue.greaterThan=" + DEFAULT_MAX_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit equals to
        defaultVoucherFiltering("usageLimit.equals=" + DEFAULT_USAGE_LIMIT, "usageLimit.equals=" + UPDATED_USAGE_LIMIT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit in
        defaultVoucherFiltering("usageLimit.in=" + DEFAULT_USAGE_LIMIT + "," + UPDATED_USAGE_LIMIT, "usageLimit.in=" + UPDATED_USAGE_LIMIT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit is not null
        defaultVoucherFiltering("usageLimit.specified=true", "usageLimit.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit is greater than or equal to
        defaultVoucherFiltering(
            "usageLimit.greaterThanOrEqual=" + DEFAULT_USAGE_LIMIT,
            "usageLimit.greaterThanOrEqual=" + UPDATED_USAGE_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit is less than or equal to
        defaultVoucherFiltering("usageLimit.lessThanOrEqual=" + DEFAULT_USAGE_LIMIT, "usageLimit.lessThanOrEqual=" + SMALLER_USAGE_LIMIT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit is less than
        defaultVoucherFiltering("usageLimit.lessThan=" + UPDATED_USAGE_LIMIT, "usageLimit.lessThan=" + DEFAULT_USAGE_LIMIT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsageLimitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usageLimit is greater than
        defaultVoucherFiltering("usageLimit.greaterThan=" + SMALLER_USAGE_LIMIT, "usageLimit.greaterThan=" + DEFAULT_USAGE_LIMIT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount equals to
        defaultVoucherFiltering("usedCount.equals=" + DEFAULT_USED_COUNT, "usedCount.equals=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount in
        defaultVoucherFiltering("usedCount.in=" + DEFAULT_USED_COUNT + "," + UPDATED_USED_COUNT, "usedCount.in=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount is not null
        defaultVoucherFiltering("usedCount.specified=true", "usedCount.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount is greater than or equal to
        defaultVoucherFiltering("usedCount.greaterThanOrEqual=" + DEFAULT_USED_COUNT, "usedCount.greaterThanOrEqual=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount is less than or equal to
        defaultVoucherFiltering("usedCount.lessThanOrEqual=" + DEFAULT_USED_COUNT, "usedCount.lessThanOrEqual=" + SMALLER_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount is less than
        defaultVoucherFiltering("usedCount.lessThan=" + UPDATED_USED_COUNT, "usedCount.lessThan=" + DEFAULT_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllVouchersByUsedCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where usedCount is greater than
        defaultVoucherFiltering("usedCount.greaterThan=" + SMALLER_USED_COUNT, "usedCount.greaterThan=" + DEFAULT_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllVouchersByValidFromIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where validFrom equals to
        defaultVoucherFiltering("validFrom.equals=" + DEFAULT_VALID_FROM, "validFrom.equals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllVouchersByValidFromIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where validFrom in
        defaultVoucherFiltering("validFrom.in=" + DEFAULT_VALID_FROM + "," + UPDATED_VALID_FROM, "validFrom.in=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllVouchersByValidFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where validFrom is not null
        defaultVoucherFiltering("validFrom.specified=true", "validFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByValidToIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where validTo equals to
        defaultVoucherFiltering("validTo.equals=" + DEFAULT_VALID_TO, "validTo.equals=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllVouchersByValidToIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where validTo in
        defaultVoucherFiltering("validTo.in=" + DEFAULT_VALID_TO + "," + UPDATED_VALID_TO, "validTo.in=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllVouchersByValidToIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where validTo is not null
        defaultVoucherFiltering("validTo.specified=true", "validTo.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where status equals to
        defaultVoucherFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVouchersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where status in
        defaultVoucherFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVouchersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where status is not null
        defaultVoucherFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where createdAt equals to
        defaultVoucherFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVouchersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where createdAt in
        defaultVoucherFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVouchersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where createdAt is not null
        defaultVoucherFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVouchersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where updatedAt equals to
        defaultVoucherFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVouchersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where updatedAt in
        defaultVoucherFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVouchersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList where updatedAt is not null
        defaultVoucherFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultVoucherFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVoucherShouldBeFound(shouldBeFound);
        defaultVoucherShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVoucherShouldBeFound(String filter) throws Exception {
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voucher.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].minOrderValue").value(hasItem(sameNumber(DEFAULT_MIN_ORDER_VALUE))))
            .andExpect(jsonPath("$.[*].maxDiscountValue").value(hasItem(sameNumber(DEFAULT_MAX_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].usageLimit").value(hasItem(DEFAULT_USAGE_LIMIT)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVoucherShouldNotBeFound(String filter) throws Exception {
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVoucher() throws Exception {
        // Get the voucher
        restVoucherMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucherSearchRepository.save(voucher);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());

        // Update the voucher
        Voucher updatedVoucher = voucherRepository.findById(voucher.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVoucher are not directly saved in db
        em.detach(updatedVoucher);
        updatedVoucher
            .code(UPDATED_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minOrderValue(UPDATED_MIN_ORDER_VALUE)
            .maxDiscountValue(UPDATED_MAX_DISCOUNT_VALUE)
            .usageLimit(UPDATED_USAGE_LIMIT)
            .usedCount(UPDATED_USED_COUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        VoucherDTO voucherDTO = voucherMapper.toDto(updatedVoucher);

        restVoucherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voucherDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isOk());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVoucherToMatchAllProperties(updatedVoucher);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Voucher> voucherSearchList = Streamable.of(voucherSearchRepository.findAll()).toList();
                Voucher testVoucherSearch = voucherSearchList.get(searchDatabaseSizeAfter - 1);

                assertVoucherAllPropertiesEquals(testVoucherSearch, updatedVoucher);
            });
    }

    @Test
    @Transactional
    void putNonExistingVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voucherDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateVoucherWithPatch() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucher using partial update
        Voucher partialUpdatedVoucher = new Voucher();
        partialUpdatedVoucher.setId(voucher.getId());

        partialUpdatedVoucher
            .code(UPDATED_CODE)
            .maxDiscountValue(UPDATED_MAX_DISCOUNT_VALUE)
            .validFrom(UPDATED_VALID_FROM)
            .status(UPDATED_STATUS)
            .updatedAt(UPDATED_UPDATED_AT);

        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVoucher.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVoucher))
            )
            .andExpect(status().isOk());

        // Validate the Voucher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVoucherUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVoucher, voucher), getPersistedVoucher(voucher));
    }

    @Test
    @Transactional
    void fullUpdateVoucherWithPatch() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucher using partial update
        Voucher partialUpdatedVoucher = new Voucher();
        partialUpdatedVoucher.setId(voucher.getId());

        partialUpdatedVoucher
            .code(UPDATED_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minOrderValue(UPDATED_MIN_ORDER_VALUE)
            .maxDiscountValue(UPDATED_MAX_DISCOUNT_VALUE)
            .usageLimit(UPDATED_USAGE_LIMIT)
            .usedCount(UPDATED_USED_COUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVoucher.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVoucher))
            )
            .andExpect(status().isOk());

        // Validate the Voucher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVoucherUpdatableFieldsEquals(partialUpdatedVoucher, getPersistedVoucher(partialUpdatedVoucher));
    }

    @Test
    @Transactional
    void patchNonExistingVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, voucherDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);
        voucherRepository.save(voucher);
        voucherSearchRepository.save(voucher);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the voucher
        restVoucherMockMvc
            .perform(delete(ENTITY_API_URL_ID, voucher.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(voucherSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);
        voucherSearchRepository.save(voucher);

        // Search the voucher
        restVoucherMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + voucher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voucher.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].minOrderValue").value(hasItem(sameNumber(DEFAULT_MIN_ORDER_VALUE))))
            .andExpect(jsonPath("$.[*].maxDiscountValue").value(hasItem(sameNumber(DEFAULT_MAX_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].usageLimit").value(hasItem(DEFAULT_USAGE_LIMIT)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return voucherRepository.count();
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

    protected Voucher getPersistedVoucher(Voucher voucher) {
        return voucherRepository.findById(voucher.getId()).orElseThrow();
    }

    protected void assertPersistedVoucherToMatchAllProperties(Voucher expectedVoucher) {
        assertVoucherAllPropertiesEquals(expectedVoucher, getPersistedVoucher(expectedVoucher));
    }

    protected void assertPersistedVoucherToMatchUpdatableProperties(Voucher expectedVoucher) {
        assertVoucherAllUpdatablePropertiesEquals(expectedVoucher, getPersistedVoucher(expectedVoucher));
    }
}
