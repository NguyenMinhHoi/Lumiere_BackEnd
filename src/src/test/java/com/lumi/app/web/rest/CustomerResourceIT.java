package com.lumi.app.web.rest;

import static com.lumi.app.domain.CustomerAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Customer;
import com.lumi.app.domain.enumeration.CustomerTier;
import com.lumi.app.repository.CustomerRepository;
import com.lumi.app.repository.search.CustomerSearchRepository;
import com.lumi.app.service.dto.CustomerDTO;
import com.lumi.app.service.mapper.CustomerMapper;
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
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomerResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "B#;e@tQ_l.t.?$o/T";
    private static final String UPDATED_EMAIL = "ZQ@Ru^|kQ.X-%";

    private static final String DEFAULT_PHONE = "6018)62(8)8-59-(495 ";
    private static final String UPDATED_PHONE = "6(58(39886 )9 (";

    private static final CustomerTier DEFAULT_TIER = CustomerTier.BRONZE;
    private static final CustomerTier UPDATED_TIER = CustomerTier.SILVER;

    private static final Integer DEFAULT_POINTS = 0;
    private static final Integer UPDATED_POINTS = 1;
    private static final Integer SMALLER_POINTS = 0 - 1;

    private static final Instant DEFAULT_DOB = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DOB = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/customers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerSearchRepository customerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerMockMvc;

    private Customer customer;

    private Customer insertedCustomer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity() {
        return new Customer()
            .code(DEFAULT_CODE)
            .fullName(DEFAULT_FULL_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .tier(DEFAULT_TIER)
            .points(DEFAULT_POINTS)
            .dob(DEFAULT_DOB)
            .address(DEFAULT_ADDRESS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createUpdatedEntity() {
        return new Customer()
            .code(UPDATED_CODE)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .tier(UPDATED_TIER)
            .points(UPDATED_POINTS)
            .dob(UPDATED_DOB)
            .address(UPDATED_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        customer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomer != null) {
            customerRepository.delete(insertedCustomer);
            customerSearchRepository.delete(insertedCustomer);
            insertedCustomer = null;
        }
    }

    @Test
    @Transactional
    void createCustomer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);
        var returnedCustomerDTO = om.readValue(
            restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CustomerDTO.class
        );

        // Validate the Customer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomer = customerMapper.toEntity(returnedCustomerDTO);
        assertCustomerUpdatableFieldsEquals(returnedCustomer, getPersistedCustomer(returnedCustomer));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCustomer = returnedCustomer;
    }

    @Test
    @Transactional
    void createCustomerWithExistingId() throws Exception {
        // Create the Customer with an existing ID
        customer.setId(1L);
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // set the field null
        customer.setCode(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // set the field null
        customer.setFullName(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // set the field null
        customer.setEmail(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // set the field null
        customer.setTier(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPointsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // set the field null
        customer.setPoints(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        // set the field null
        customer.setCreatedAt(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCustomers() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].tier").value(hasItem(DEFAULT_TIER.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get the customer
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL_ID, customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.tier").value(DEFAULT_TIER.toString()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCustomersByIdFiltering() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        Long id = customer.getId();

        defaultCustomerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCustomersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where code equals to
        defaultCustomerFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCustomersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where code in
        defaultCustomerFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCustomersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where code is not null
        defaultCustomerFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where code contains
        defaultCustomerFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCustomersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where code does not contain
        defaultCustomerFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where fullName equals to
        defaultCustomerFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where fullName in
        defaultCustomerFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where fullName is not null
        defaultCustomerFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where fullName contains
        defaultCustomerFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where fullName does not contain
        defaultCustomerFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where email equals to
        defaultCustomerFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where email in
        defaultCustomerFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where email is not null
        defaultCustomerFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where email contains
        defaultCustomerFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where email does not contain
        defaultCustomerFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone equals to
        defaultCustomerFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone in
        defaultCustomerFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone is not null
        defaultCustomerFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone contains
        defaultCustomerFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone does not contain
        defaultCustomerFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByTierIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where tier equals to
        defaultCustomerFiltering("tier.equals=" + DEFAULT_TIER, "tier.equals=" + UPDATED_TIER);
    }

    @Test
    @Transactional
    void getAllCustomersByTierIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where tier in
        defaultCustomerFiltering("tier.in=" + DEFAULT_TIER + "," + UPDATED_TIER, "tier.in=" + UPDATED_TIER);
    }

    @Test
    @Transactional
    void getAllCustomersByTierIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where tier is not null
        defaultCustomerFiltering("tier.specified=true", "tier.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points equals to
        defaultCustomerFiltering("points.equals=" + DEFAULT_POINTS, "points.equals=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points in
        defaultCustomerFiltering("points.in=" + DEFAULT_POINTS + "," + UPDATED_POINTS, "points.in=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points is not null
        defaultCustomerFiltering("points.specified=true", "points.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points is greater than or equal to
        defaultCustomerFiltering("points.greaterThanOrEqual=" + DEFAULT_POINTS, "points.greaterThanOrEqual=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points is less than or equal to
        defaultCustomerFiltering("points.lessThanOrEqual=" + DEFAULT_POINTS, "points.lessThanOrEqual=" + SMALLER_POINTS);
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points is less than
        defaultCustomerFiltering("points.lessThan=" + UPDATED_POINTS, "points.lessThan=" + DEFAULT_POINTS);
    }

    @Test
    @Transactional
    void getAllCustomersByPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where points is greater than
        defaultCustomerFiltering("points.greaterThan=" + SMALLER_POINTS, "points.greaterThan=" + DEFAULT_POINTS);
    }

    @Test
    @Transactional
    void getAllCustomersByDobIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where dob equals to
        defaultCustomerFiltering("dob.equals=" + DEFAULT_DOB, "dob.equals=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllCustomersByDobIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where dob in
        defaultCustomerFiltering("dob.in=" + DEFAULT_DOB + "," + UPDATED_DOB, "dob.in=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllCustomersByDobIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where dob is not null
        defaultCustomerFiltering("dob.specified=true", "dob.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address equals to
        defaultCustomerFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address in
        defaultCustomerFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address is not null
        defaultCustomerFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address contains
        defaultCustomerFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address does not contain
        defaultCustomerFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where createdAt equals to
        defaultCustomerFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCustomersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where createdAt in
        defaultCustomerFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCustomersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where createdAt is not null
        defaultCustomerFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where updatedAt equals to
        defaultCustomerFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCustomersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where updatedAt in
        defaultCustomerFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCustomersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where updatedAt is not null
        defaultCustomerFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultCustomerFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCustomerShouldBeFound(shouldBeFound);
        defaultCustomerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerShouldBeFound(String filter) throws Exception {
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].tier").value(hasItem(DEFAULT_TIER.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCustomerShouldNotBeFound(String filter) throws Exception {
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCustomer() throws Exception {
        // Get the customer
        restCustomerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerSearchRepository.save(customer);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCustomer are not directly saved in db
        em.detach(updatedCustomer);
        updatedCustomer
            .code(UPDATED_CODE)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .tier(UPDATED_TIER)
            .points(UPDATED_POINTS)
            .dob(UPDATED_DOB)
            .address(UPDATED_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CustomerDTO customerDTO = customerMapper.toDto(updatedCustomer);

        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerToMatchAllProperties(updatedCustomer);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Customer> customerSearchList = Streamable.of(customerSearchRepository.findAll()).toList();
                Customer testCustomerSearch = customerSearchList.get(searchDatabaseSizeAfter - 1);

                assertCustomerAllPropertiesEquals(testCustomerSearch, updatedCustomer);
            });
    }

    @Test
    @Transactional
    void putNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer.code(UPDATED_CODE).email(UPDATED_EMAIL).points(UPDATED_POINTS).updatedAt(UPDATED_UPDATED_AT);

        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCustomer, customer), getPersistedCustomer(customer));
    }

    @Test
    @Transactional
    void fullUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer
            .code(UPDATED_CODE)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .tier(UPDATED_TIER)
            .points(UPDATED_POINTS)
            .dob(UPDATED_DOB)
            .address(UPDATED_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(partialUpdatedCustomer, getPersistedCustomer(partialUpdatedCustomer));
    }

    @Test
    @Transactional
    void patchNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);
        customerRepository.save(customer);
        customerSearchRepository.save(customer);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the customer
        restCustomerMockMvc
            .perform(delete(ENTITY_API_URL_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);
        customerSearchRepository.save(customer);

        // Search the customer
        restCustomerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].tier").value(hasItem(DEFAULT_TIER.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return customerRepository.count();
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

    protected Customer getPersistedCustomer(Customer customer) {
        return customerRepository.findById(customer.getId()).orElseThrow();
    }

    protected void assertPersistedCustomerToMatchAllProperties(Customer expectedCustomer) {
        assertCustomerAllPropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }

    protected void assertPersistedCustomerToMatchUpdatableProperties(Customer expectedCustomer) {
        assertCustomerAllUpdatablePropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }
}
