package com.lumi.app.web.rest;

import static com.lumi.app.domain.SupplierAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Supplier;
import com.lumi.app.domain.enumeration.SupplierStatus;
import com.lumi.app.repository.SupplierRepository;
import com.lumi.app.repository.search.SupplierSearchRepository;
import com.lumi.app.service.dto.SupplierDTO;
import com.lumi.app.service.mapper.SupplierMapper;
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
 * Integration tests for the {@link SupplierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SupplierResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "n@0zG.:";
    private static final String UPDATED_EMAIL = "5fzo@bKX<v.EFx{!";

    private static final String DEFAULT_PHONE = "2236+4  51";
    private static final String UPDATED_PHONE = "-485270995";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final SupplierStatus DEFAULT_STATUS = SupplierStatus.ACTIVE;
    private static final SupplierStatus UPDATED_STATUS = SupplierStatus.INACTIVE;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/suppliers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/suppliers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private SupplierSearchRepository supplierSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplierMockMvc;

    private Supplier supplier;

    private Supplier insertedSupplier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplier createEntity() {
        return new Supplier()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
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
    public static Supplier createUpdatedEntity() {
        return new Supplier()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        supplier = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSupplier != null) {
            supplierRepository.delete(insertedSupplier);
            supplierSearchRepository.delete(insertedSupplier);
            insertedSupplier = null;
        }
    }

    @Test
    @Transactional
    void createSupplier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);
        var returnedSupplierDTO = om.readValue(
            restSupplierMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SupplierDTO.class
        );

        // Validate the Supplier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSupplier = supplierMapper.toEntity(returnedSupplierDTO);
        assertSupplierUpdatableFieldsEquals(returnedSupplier, getPersistedSupplier(returnedSupplier));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSupplier = returnedSupplier;
    }

    @Test
    @Transactional
    void createSupplierWithExistingId() throws Exception {
        // Create the Supplier with an existing ID
        supplier.setId(1L);
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        // set the field null
        supplier.setCode(null);

        // Create the Supplier, which fails.
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        // set the field null
        supplier.setName(null);

        // Create the Supplier, which fails.
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        // set the field null
        supplier.setStatus(null);

        // Create the Supplier, which fails.
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        // set the field null
        supplier.setCreatedAt(null);

        // Create the Supplier, which fails.
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSuppliers() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get the supplier
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL_ID, supplier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplier.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getSuppliersByIdFiltering() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        Long id = supplier.getId();

        defaultSupplierFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSupplierFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSupplierFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSuppliersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where code equals to
        defaultSupplierFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSuppliersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where code in
        defaultSupplierFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSuppliersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where code is not null
        defaultSupplierFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where code contains
        defaultSupplierFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSuppliersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where code does not contain
        defaultSupplierFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name equals to
        defaultSupplierFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name in
        defaultSupplierFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name is not null
        defaultSupplierFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name contains
        defaultSupplierFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name does not contain
        defaultSupplierFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where email equals to
        defaultSupplierFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSuppliersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where email in
        defaultSupplierFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSuppliersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where email is not null
        defaultSupplierFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where email contains
        defaultSupplierFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSuppliersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where email does not contain
        defaultSupplierFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllSuppliersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where phone equals to
        defaultSupplierFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllSuppliersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where phone in
        defaultSupplierFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllSuppliersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where phone is not null
        defaultSupplierFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where phone contains
        defaultSupplierFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllSuppliersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where phone does not contain
        defaultSupplierFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllSuppliersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where address equals to
        defaultSupplierFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllSuppliersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where address in
        defaultSupplierFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllSuppliersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where address is not null
        defaultSupplierFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where address contains
        defaultSupplierFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllSuppliersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where address does not contain
        defaultSupplierFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllSuppliersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where status equals to
        defaultSupplierFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSuppliersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where status in
        defaultSupplierFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSuppliersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where status is not null
        defaultSupplierFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where createdAt equals to
        defaultSupplierFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSuppliersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where createdAt in
        defaultSupplierFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSuppliersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where createdAt is not null
        defaultSupplierFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where updatedAt equals to
        defaultSupplierFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSuppliersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where updatedAt in
        defaultSupplierFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSuppliersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where updatedAt is not null
        defaultSupplierFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultSupplierFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSupplierShouldBeFound(shouldBeFound);
        defaultSupplierShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSupplierShouldBeFound(String filter) throws Exception {
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSupplierShouldNotBeFound(String filter) throws Exception {
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSupplier() throws Exception {
        // Get the supplier
        restSupplierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplierSearchRepository.save(supplier);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());

        // Update the supplier
        Supplier updatedSupplier = supplierRepository.findById(supplier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSupplier are not directly saved in db
        em.detach(updatedSupplier);
        updatedSupplier
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        SupplierDTO supplierDTO = supplierMapper.toDto(updatedSupplier);

        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isOk());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSupplierToMatchAllProperties(updatedSupplier);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Supplier> supplierSearchList = Streamable.of(supplierSearchRepository.findAll()).toList();
                Supplier testSupplierSearch = supplierSearchList.get(searchDatabaseSizeAfter - 1);

                assertSupplierAllPropertiesEquals(testSupplierSearch, updatedSupplier);
            });
    }

    @Test
    @Transactional
    void putNonExistingSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSupplierWithPatch() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplier using partial update
        Supplier partialUpdatedSupplier = new Supplier();
        partialUpdatedSupplier.setId(supplier.getId());

        partialUpdatedSupplier.name(UPDATED_NAME).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplier))
            )
            .andExpect(status().isOk());

        // Validate the Supplier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplierUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSupplier, supplier), getPersistedSupplier(supplier));
    }

    @Test
    @Transactional
    void fullUpdateSupplierWithPatch() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplier using partial update
        Supplier partialUpdatedSupplier = new Supplier();
        partialUpdatedSupplier.setId(supplier.getId());

        partialUpdatedSupplier
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplier))
            )
            .andExpect(status().isOk());

        // Validate the Supplier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplierUpdatableFieldsEquals(partialUpdatedSupplier, getPersistedSupplier(partialUpdatedSupplier));
    }

    @Test
    @Transactional
    void patchNonExistingSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);
        supplierRepository.save(supplier);
        supplierSearchRepository.save(supplier);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the supplier
        restSupplierMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplierSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);
        supplierSearchRepository.save(supplier);

        // Search the supplier
        restSupplierMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + supplier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return supplierRepository.count();
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

    protected Supplier getPersistedSupplier(Supplier supplier) {
        return supplierRepository.findById(supplier.getId()).orElseThrow();
    }

    protected void assertPersistedSupplierToMatchAllProperties(Supplier expectedSupplier) {
        assertSupplierAllPropertiesEquals(expectedSupplier, getPersistedSupplier(expectedSupplier));
    }

    protected void assertPersistedSupplierToMatchUpdatableProperties(Supplier expectedSupplier) {
        assertSupplierAllUpdatablePropertiesEquals(expectedSupplier, getPersistedSupplier(expectedSupplier));
    }
}
