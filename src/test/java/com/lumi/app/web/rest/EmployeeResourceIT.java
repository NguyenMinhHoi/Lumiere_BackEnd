package com.lumi.app.web.rest;

import static com.lumi.app.domain.EmployeeAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Employee;
import com.lumi.app.domain.enumeration.EmployeeRole;
import com.lumi.app.domain.enumeration.EmployeeStatus;
import com.lumi.app.repository.EmployeeRepository;
import com.lumi.app.repository.search.EmployeeSearchRepository;
import com.lumi.app.service.dto.EmployeeDTO;
import com.lumi.app.service.mapper.EmployeeMapper;
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
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EmployeeResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "M@U&.z";
    private static final String UPDATED_EMAIL = "6@n|DR.BBm!";

    private static final String DEFAULT_PHONE = "8)051)-247056 )-51";
    private static final String UPDATED_PHONE = "-  2232815554-83";

    private static final EmployeeRole DEFAULT_ROLE = EmployeeRole.AGENT;
    private static final EmployeeRole UPDATED_ROLE = EmployeeRole.SUPERVISOR;

    private static final EmployeeStatus DEFAULT_STATUS = EmployeeStatus.ACTIVE;
    private static final EmployeeStatus UPDATED_STATUS = EmployeeStatus.INACTIVE;

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_JOINED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_JOINED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/employees/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeSearchRepository employeeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    private Employee insertedEmployee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity() {
        return new Employee()
            .code(DEFAULT_CODE)
            .fullName(DEFAULT_FULL_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .role(DEFAULT_ROLE)
            .status(DEFAULT_STATUS)
            .department(DEFAULT_DEPARTMENT)
            .joinedAt(DEFAULT_JOINED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity() {
        return new Employee()
            .code(UPDATED_CODE)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .joinedAt(UPDATED_JOINED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        employee = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEmployee != null) {
            employeeRepository.delete(insertedEmployee);
            employeeSearchRepository.delete(insertedEmployee);
            insertedEmployee = null;
        }
    }

    @Test
    @Transactional
    void createEmployee() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);
        var returnedEmployeeDTO = om.readValue(
            restEmployeeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EmployeeDTO.class
        );

        // Validate the Employee in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEmployee = employeeMapper.toEntity(returnedEmployeeDTO);
        assertEmployeeUpdatableFieldsEquals(returnedEmployee, getPersistedEmployee(returnedEmployee));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEmployee = returnedEmployee;
    }

    @Test
    @Transactional
    void createEmployeeWithExistingId() throws Exception {
        // Create the Employee with an existing ID
        employee.setId(1L);
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // set the field null
        employee.setCode(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // set the field null
        employee.setFullName(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // set the field null
        employee.setEmail(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // set the field null
        employee.setRole(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // set the field null
        employee.setStatus(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        // set the field null
        employee.setCreatedAt(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEmployees() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get the employee
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL_ID, employee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT))
            .andExpect(jsonPath("$.joinedAt").value(DEFAULT_JOINED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getEmployeesByIdFiltering() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        Long id = employee.getId();

        defaultEmployeeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEmployeeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEmployeeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmployeesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where code equals to
        defaultEmployeeFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllEmployeesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where code in
        defaultEmployeeFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllEmployeesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where code is not null
        defaultEmployeeFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where code contains
        defaultEmployeeFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllEmployeesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where code does not contain
        defaultEmployeeFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllEmployeesByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where fullName equals to
        defaultEmployeeFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where fullName in
        defaultEmployeeFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where fullName is not null
        defaultEmployeeFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where fullName contains
        defaultEmployeeFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where fullName does not contain
        defaultEmployeeFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email equals to
        defaultEmployeeFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email in
        defaultEmployeeFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email is not null
        defaultEmployeeFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email contains
        defaultEmployeeFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email does not contain
        defaultEmployeeFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where phone equals to
        defaultEmployeeFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllEmployeesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where phone in
        defaultEmployeeFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllEmployeesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where phone is not null
        defaultEmployeeFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where phone contains
        defaultEmployeeFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllEmployeesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where phone does not contain
        defaultEmployeeFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllEmployeesByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where role equals to
        defaultEmployeeFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where role in
        defaultEmployeeFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where role is not null
        defaultEmployeeFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where status equals to
        defaultEmployeeFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmployeesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where status in
        defaultEmployeeFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmployeesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where status is not null
        defaultEmployeeFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByDepartmentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where department equals to
        defaultEmployeeFiltering("department.equals=" + DEFAULT_DEPARTMENT, "department.equals=" + UPDATED_DEPARTMENT);
    }

    @Test
    @Transactional
    void getAllEmployeesByDepartmentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where department in
        defaultEmployeeFiltering("department.in=" + DEFAULT_DEPARTMENT + "," + UPDATED_DEPARTMENT, "department.in=" + UPDATED_DEPARTMENT);
    }

    @Test
    @Transactional
    void getAllEmployeesByDepartmentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where department is not null
        defaultEmployeeFiltering("department.specified=true", "department.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByDepartmentContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where department contains
        defaultEmployeeFiltering("department.contains=" + DEFAULT_DEPARTMENT, "department.contains=" + UPDATED_DEPARTMENT);
    }

    @Test
    @Transactional
    void getAllEmployeesByDepartmentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where department does not contain
        defaultEmployeeFiltering("department.doesNotContain=" + UPDATED_DEPARTMENT, "department.doesNotContain=" + DEFAULT_DEPARTMENT);
    }

    @Test
    @Transactional
    void getAllEmployeesByJoinedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where joinedAt equals to
        defaultEmployeeFiltering("joinedAt.equals=" + DEFAULT_JOINED_AT, "joinedAt.equals=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllEmployeesByJoinedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where joinedAt in
        defaultEmployeeFiltering("joinedAt.in=" + DEFAULT_JOINED_AT + "," + UPDATED_JOINED_AT, "joinedAt.in=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllEmployeesByJoinedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where joinedAt is not null
        defaultEmployeeFiltering("joinedAt.specified=true", "joinedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where createdAt equals to
        defaultEmployeeFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEmployeesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where createdAt in
        defaultEmployeeFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEmployeesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where createdAt is not null
        defaultEmployeeFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where updatedAt equals to
        defaultEmployeeFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllEmployeesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where updatedAt in
        defaultEmployeeFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllEmployeesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where updatedAt is not null
        defaultEmployeeFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultEmployeeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEmployeeShouldBeFound(shouldBeFound);
        defaultEmployeeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmployeeShouldBeFound(String filter) throws Exception {
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmployeeShouldNotBeFound(String filter) throws Exception {
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        employeeSearchRepository.save(employee);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee);
        updatedEmployee
            .code(UPDATED_CODE)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .joinedAt(UPDATED_JOINED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        EmployeeDTO employeeDTO = employeeMapper.toDto(updatedEmployee);

        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(employeeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmployeeToMatchAllProperties(updatedEmployee);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Employee> employeeSearchList = Streamable.of(employeeSearchRepository.findAll()).toList();
                Employee testEmployeeSearch = employeeSearchList.get(searchDatabaseSizeAfter - 1);

                assertEmployeeAllPropertiesEquals(testEmployeeSearch, updatedEmployee);
            });
    }

    @Test
    @Transactional
    void putNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(employeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(employeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee.fullName(UPDATED_FULL_NAME).phone(UPDATED_PHONE);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEmployee, employee), getPersistedEmployee(employee));
    }

    @Test
    @Transactional
    void fullUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .code(UPDATED_CODE)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .joinedAt(UPDATED_JOINED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(partialUpdatedEmployee, getPersistedEmployee(partialUpdatedEmployee));
    }

    @Test
    @Transactional
    void patchNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employeeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(employeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(employeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(employeeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);
        employeeRepository.save(employee);
        employeeSearchRepository.save(employee);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the employee
        restEmployeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, employee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);
        employeeSearchRepository.save(employee);

        // Search the employee
        restEmployeeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + employee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return employeeRepository.count();
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

    protected Employee getPersistedEmployee(Employee employee) {
        return employeeRepository.findById(employee.getId()).orElseThrow();
    }

    protected void assertPersistedEmployeeToMatchAllProperties(Employee expectedEmployee) {
        assertEmployeeAllPropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }

    protected void assertPersistedEmployeeToMatchUpdatableProperties(Employee expectedEmployee) {
        assertEmployeeAllUpdatablePropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }
}
