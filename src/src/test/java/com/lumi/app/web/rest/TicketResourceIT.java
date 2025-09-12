package com.lumi.app.web.rest;

import static com.lumi.app.domain.TicketAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Ticket;
import com.lumi.app.domain.enumeration.ChannelType;
import com.lumi.app.domain.enumeration.Priority;
import com.lumi.app.domain.enumeration.TicketStatus;
import com.lumi.app.repository.TicketRepository;
import com.lumi.app.repository.search.TicketSearchRepository;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.mapper.TicketMapper;
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
 * Integration tests for the {@link TicketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketResourceIT {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;
    private static final Long SMALLER_CUSTOMER_ID = 1L - 1L;

    private static final Long DEFAULT_SLA_PLAN_ID = 1L;
    private static final Long UPDATED_SLA_PLAN_ID = 2L;
    private static final Long SMALLER_SLA_PLAN_ID = 1L - 1L;

    private static final Long DEFAULT_ORDER_ID = 1L;
    private static final Long UPDATED_ORDER_ID = 2L;
    private static final Long SMALLER_ORDER_ID = 1L - 1L;

    private static final Long DEFAULT_ASSIGNEE_EMPLOYEE_ID = 1L;
    private static final Long UPDATED_ASSIGNEE_EMPLOYEE_ID = 2L;
    private static final Long SMALLER_ASSIGNEE_EMPLOYEE_ID = 1L - 1L;

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TicketStatus DEFAULT_STATUS = TicketStatus.OPEN;
    private static final TicketStatus UPDATED_STATUS = TicketStatus.IN_PROGRESS;

    private static final Priority DEFAULT_PRIORITY = Priority.LOW;
    private static final Priority UPDATED_PRIORITY = Priority.MEDIUM;

    private static final ChannelType DEFAULT_CHANNEL = ChannelType.WEB;
    private static final ChannelType UPDATED_CHANNEL = ChannelType.EMAIL;

    private static final Instant DEFAULT_OPENED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPENED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FIRST_RESPONSE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FIRST_RESPONSE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RESOLVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SLA_DUE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SLA_DUE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/tickets/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketSearchRepository ticketSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    private Ticket insertedTicket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createEntity() {
        return new Ticket()
            .customerId(DEFAULT_CUSTOMER_ID)
            .slaPlanId(DEFAULT_SLA_PLAN_ID)
            .orderId(DEFAULT_ORDER_ID)
            .assigneeEmployeeId(DEFAULT_ASSIGNEE_EMPLOYEE_ID)
            .code(DEFAULT_CODE)
            .subject(DEFAULT_SUBJECT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .priority(DEFAULT_PRIORITY)
            .channel(DEFAULT_CHANNEL)
            .openedAt(DEFAULT_OPENED_AT)
            .firstResponseAt(DEFAULT_FIRST_RESPONSE_AT)
            .resolvedAt(DEFAULT_RESOLVED_AT)
            .slaDueAt(DEFAULT_SLA_DUE_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createUpdatedEntity() {
        return new Ticket()
            .customerId(UPDATED_CUSTOMER_ID)
            .slaPlanId(UPDATED_SLA_PLAN_ID)
            .orderId(UPDATED_ORDER_ID)
            .assigneeEmployeeId(UPDATED_ASSIGNEE_EMPLOYEE_ID)
            .code(UPDATED_CODE)
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .channel(UPDATED_CHANNEL)
            .openedAt(UPDATED_OPENED_AT)
            .firstResponseAt(UPDATED_FIRST_RESPONSE_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .slaDueAt(UPDATED_SLA_DUE_AT);
    }

    @BeforeEach
    void initTest() {
        ticket = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicket != null) {
            ticketRepository.delete(insertedTicket);
            ticketSearchRepository.delete(insertedTicket);
            insertedTicket = null;
        }
    }

    @Test
    @Transactional
    void createTicket() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);
        var returnedTicketDTO = om.readValue(
            restTicketMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketDTO.class
        );

        // Validate the Ticket in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTicket = ticketMapper.toEntity(returnedTicketDTO);
        assertTicketUpdatableFieldsEquals(returnedTicket, getPersistedTicket(returnedTicket));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTicket = returnedTicket;
    }

    @Test
    @Transactional
    void createTicketWithExistingId() throws Exception {
        // Create the Ticket with an existing ID
        ticket.setId(1L);
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCustomerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setCustomerId(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setCode(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setSubject(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setStatus(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPriorityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setPriority(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setChannel(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOpenedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setOpenedAt(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTickets() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].slaPlanId").value(hasItem(DEFAULT_SLA_PLAN_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].assigneeEmployeeId").value(hasItem(DEFAULT_ASSIGNEE_EMPLOYEE_ID.intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].openedAt").value(hasItem(DEFAULT_OPENED_AT.toString())))
            .andExpect(jsonPath("$.[*].firstResponseAt").value(hasItem(DEFAULT_FIRST_RESPONSE_AT.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())))
            .andExpect(jsonPath("$.[*].slaDueAt").value(hasItem(DEFAULT_SLA_DUE_AT.toString())));
    }

    @Test
    @Transactional
    void getTicket() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc
            .perform(get(ENTITY_API_URL_ID, ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID.intValue()))
            .andExpect(jsonPath("$.slaPlanId").value(DEFAULT_SLA_PLAN_ID.intValue()))
            .andExpect(jsonPath("$.orderId").value(DEFAULT_ORDER_ID.intValue()))
            .andExpect(jsonPath("$.assigneeEmployeeId").value(DEFAULT_ASSIGNEE_EMPLOYEE_ID.intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.openedAt").value(DEFAULT_OPENED_AT.toString()))
            .andExpect(jsonPath("$.firstResponseAt").value(DEFAULT_FIRST_RESPONSE_AT.toString()))
            .andExpect(jsonPath("$.resolvedAt").value(DEFAULT_RESOLVED_AT.toString()))
            .andExpect(jsonPath("$.slaDueAt").value(DEFAULT_SLA_DUE_AT.toString()));
    }

    @Test
    @Transactional
    void getTicketsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        Long id = ticket.getId();

        defaultTicketFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTicketFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTicketFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId equals to
        defaultTicketFiltering("customerId.equals=" + DEFAULT_CUSTOMER_ID, "customerId.equals=" + UPDATED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId in
        defaultTicketFiltering("customerId.in=" + DEFAULT_CUSTOMER_ID + "," + UPDATED_CUSTOMER_ID, "customerId.in=" + UPDATED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId is not null
        defaultTicketFiltering("customerId.specified=true", "customerId.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId is greater than or equal to
        defaultTicketFiltering(
            "customerId.greaterThanOrEqual=" + DEFAULT_CUSTOMER_ID,
            "customerId.greaterThanOrEqual=" + UPDATED_CUSTOMER_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId is less than or equal to
        defaultTicketFiltering("customerId.lessThanOrEqual=" + DEFAULT_CUSTOMER_ID, "customerId.lessThanOrEqual=" + SMALLER_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId is less than
        defaultTicketFiltering("customerId.lessThan=" + UPDATED_CUSTOMER_ID, "customerId.lessThan=" + DEFAULT_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByCustomerIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where customerId is greater than
        defaultTicketFiltering("customerId.greaterThan=" + SMALLER_CUSTOMER_ID, "customerId.greaterThan=" + DEFAULT_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId equals to
        defaultTicketFiltering("slaPlanId.equals=" + DEFAULT_SLA_PLAN_ID, "slaPlanId.equals=" + UPDATED_SLA_PLAN_ID);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId in
        defaultTicketFiltering("slaPlanId.in=" + DEFAULT_SLA_PLAN_ID + "," + UPDATED_SLA_PLAN_ID, "slaPlanId.in=" + UPDATED_SLA_PLAN_ID);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId is not null
        defaultTicketFiltering("slaPlanId.specified=true", "slaPlanId.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId is greater than or equal to
        defaultTicketFiltering(
            "slaPlanId.greaterThanOrEqual=" + DEFAULT_SLA_PLAN_ID,
            "slaPlanId.greaterThanOrEqual=" + UPDATED_SLA_PLAN_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId is less than or equal to
        defaultTicketFiltering("slaPlanId.lessThanOrEqual=" + DEFAULT_SLA_PLAN_ID, "slaPlanId.lessThanOrEqual=" + SMALLER_SLA_PLAN_ID);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId is less than
        defaultTicketFiltering("slaPlanId.lessThan=" + UPDATED_SLA_PLAN_ID, "slaPlanId.lessThan=" + DEFAULT_SLA_PLAN_ID);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaPlanIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaPlanId is greater than
        defaultTicketFiltering("slaPlanId.greaterThan=" + SMALLER_SLA_PLAN_ID, "slaPlanId.greaterThan=" + DEFAULT_SLA_PLAN_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId equals to
        defaultTicketFiltering("orderId.equals=" + DEFAULT_ORDER_ID, "orderId.equals=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId in
        defaultTicketFiltering("orderId.in=" + DEFAULT_ORDER_ID + "," + UPDATED_ORDER_ID, "orderId.in=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId is not null
        defaultTicketFiltering("orderId.specified=true", "orderId.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId is greater than or equal to
        defaultTicketFiltering("orderId.greaterThanOrEqual=" + DEFAULT_ORDER_ID, "orderId.greaterThanOrEqual=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId is less than or equal to
        defaultTicketFiltering("orderId.lessThanOrEqual=" + DEFAULT_ORDER_ID, "orderId.lessThanOrEqual=" + SMALLER_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId is less than
        defaultTicketFiltering("orderId.lessThan=" + UPDATED_ORDER_ID, "orderId.lessThan=" + DEFAULT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByOrderIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where orderId is greater than
        defaultTicketFiltering("orderId.greaterThan=" + SMALLER_ORDER_ID, "orderId.greaterThan=" + DEFAULT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId equals to
        defaultTicketFiltering(
            "assigneeEmployeeId.equals=" + DEFAULT_ASSIGNEE_EMPLOYEE_ID,
            "assigneeEmployeeId.equals=" + UPDATED_ASSIGNEE_EMPLOYEE_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId in
        defaultTicketFiltering(
            "assigneeEmployeeId.in=" + DEFAULT_ASSIGNEE_EMPLOYEE_ID + "," + UPDATED_ASSIGNEE_EMPLOYEE_ID,
            "assigneeEmployeeId.in=" + UPDATED_ASSIGNEE_EMPLOYEE_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId is not null
        defaultTicketFiltering("assigneeEmployeeId.specified=true", "assigneeEmployeeId.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId is greater than or equal to
        defaultTicketFiltering(
            "assigneeEmployeeId.greaterThanOrEqual=" + DEFAULT_ASSIGNEE_EMPLOYEE_ID,
            "assigneeEmployeeId.greaterThanOrEqual=" + UPDATED_ASSIGNEE_EMPLOYEE_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId is less than or equal to
        defaultTicketFiltering(
            "assigneeEmployeeId.lessThanOrEqual=" + DEFAULT_ASSIGNEE_EMPLOYEE_ID,
            "assigneeEmployeeId.lessThanOrEqual=" + SMALLER_ASSIGNEE_EMPLOYEE_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId is less than
        defaultTicketFiltering(
            "assigneeEmployeeId.lessThan=" + UPDATED_ASSIGNEE_EMPLOYEE_ID,
            "assigneeEmployeeId.lessThan=" + DEFAULT_ASSIGNEE_EMPLOYEE_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByAssigneeEmployeeIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assigneeEmployeeId is greater than
        defaultTicketFiltering(
            "assigneeEmployeeId.greaterThan=" + SMALLER_ASSIGNEE_EMPLOYEE_ID,
            "assigneeEmployeeId.greaterThan=" + DEFAULT_ASSIGNEE_EMPLOYEE_ID
        );
    }

    @Test
    @Transactional
    void getAllTicketsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code equals to
        defaultTicketFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code in
        defaultTicketFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code is not null
        defaultTicketFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code contains
        defaultTicketFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code does not contain
        defaultTicketFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where subject equals to
        defaultTicketFiltering("subject.equals=" + DEFAULT_SUBJECT, "subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllTicketsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where subject in
        defaultTicketFiltering("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT, "subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllTicketsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where subject is not null
        defaultTicketFiltering("subject.specified=true", "subject.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where subject contains
        defaultTicketFiltering("subject.contains=" + DEFAULT_SUBJECT, "subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllTicketsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where subject does not contain
        defaultTicketFiltering("subject.doesNotContain=" + UPDATED_SUBJECT, "subject.doesNotContain=" + DEFAULT_SUBJECT);
    }

    @Test
    @Transactional
    void getAllTicketsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status equals to
        defaultTicketFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTicketsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status in
        defaultTicketFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTicketsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status is not null
        defaultTicketFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority equals to
        defaultTicketFiltering("priority.equals=" + DEFAULT_PRIORITY, "priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTicketsByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority in
        defaultTicketFiltering("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY, "priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTicketsByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority is not null
        defaultTicketFiltering("priority.specified=true", "priority.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByChannelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where channel equals to
        defaultTicketFiltering("channel.equals=" + DEFAULT_CHANNEL, "channel.equals=" + UPDATED_CHANNEL);
    }

    @Test
    @Transactional
    void getAllTicketsByChannelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where channel in
        defaultTicketFiltering("channel.in=" + DEFAULT_CHANNEL + "," + UPDATED_CHANNEL, "channel.in=" + UPDATED_CHANNEL);
    }

    @Test
    @Transactional
    void getAllTicketsByChannelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where channel is not null
        defaultTicketFiltering("channel.specified=true", "channel.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByOpenedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where openedAt equals to
        defaultTicketFiltering("openedAt.equals=" + DEFAULT_OPENED_AT, "openedAt.equals=" + UPDATED_OPENED_AT);
    }

    @Test
    @Transactional
    void getAllTicketsByOpenedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where openedAt in
        defaultTicketFiltering("openedAt.in=" + DEFAULT_OPENED_AT + "," + UPDATED_OPENED_AT, "openedAt.in=" + UPDATED_OPENED_AT);
    }

    @Test
    @Transactional
    void getAllTicketsByOpenedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where openedAt is not null
        defaultTicketFiltering("openedAt.specified=true", "openedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByFirstResponseAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where firstResponseAt equals to
        defaultTicketFiltering(
            "firstResponseAt.equals=" + DEFAULT_FIRST_RESPONSE_AT,
            "firstResponseAt.equals=" + UPDATED_FIRST_RESPONSE_AT
        );
    }

    @Test
    @Transactional
    void getAllTicketsByFirstResponseAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where firstResponseAt in
        defaultTicketFiltering(
            "firstResponseAt.in=" + DEFAULT_FIRST_RESPONSE_AT + "," + UPDATED_FIRST_RESPONSE_AT,
            "firstResponseAt.in=" + UPDATED_FIRST_RESPONSE_AT
        );
    }

    @Test
    @Transactional
    void getAllTicketsByFirstResponseAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where firstResponseAt is not null
        defaultTicketFiltering("firstResponseAt.specified=true", "firstResponseAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByResolvedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where resolvedAt equals to
        defaultTicketFiltering("resolvedAt.equals=" + DEFAULT_RESOLVED_AT, "resolvedAt.equals=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllTicketsByResolvedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where resolvedAt in
        defaultTicketFiltering("resolvedAt.in=" + DEFAULT_RESOLVED_AT + "," + UPDATED_RESOLVED_AT, "resolvedAt.in=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllTicketsByResolvedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where resolvedAt is not null
        defaultTicketFiltering("resolvedAt.specified=true", "resolvedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsBySlaDueAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaDueAt equals to
        defaultTicketFiltering("slaDueAt.equals=" + DEFAULT_SLA_DUE_AT, "slaDueAt.equals=" + UPDATED_SLA_DUE_AT);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaDueAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaDueAt in
        defaultTicketFiltering("slaDueAt.in=" + DEFAULT_SLA_DUE_AT + "," + UPDATED_SLA_DUE_AT, "slaDueAt.in=" + UPDATED_SLA_DUE_AT);
    }

    @Test
    @Transactional
    void getAllTicketsBySlaDueAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where slaDueAt is not null
        defaultTicketFiltering("slaDueAt.specified=true", "slaDueAt.specified=false");
    }

    private void defaultTicketFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTicketShouldBeFound(shouldBeFound);
        defaultTicketShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketShouldBeFound(String filter) throws Exception {
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].slaPlanId").value(hasItem(DEFAULT_SLA_PLAN_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].assigneeEmployeeId").value(hasItem(DEFAULT_ASSIGNEE_EMPLOYEE_ID.intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].openedAt").value(hasItem(DEFAULT_OPENED_AT.toString())))
            .andExpect(jsonPath("$.[*].firstResponseAt").value(hasItem(DEFAULT_FIRST_RESPONSE_AT.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())))
            .andExpect(jsonPath("$.[*].slaDueAt").value(hasItem(DEFAULT_SLA_DUE_AT.toString())));

        // Check, that the count call also returns 1
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketShouldNotBeFound(String filter) throws Exception {
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicket() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketSearchRepository.save(ticket);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicket are not directly saved in db
        em.detach(updatedTicket);
        updatedTicket
            .customerId(UPDATED_CUSTOMER_ID)
            .slaPlanId(UPDATED_SLA_PLAN_ID)
            .orderId(UPDATED_ORDER_ID)
            .assigneeEmployeeId(UPDATED_ASSIGNEE_EMPLOYEE_ID)
            .code(UPDATED_CODE)
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .channel(UPDATED_CHANNEL)
            .openedAt(UPDATED_OPENED_AT)
            .firstResponseAt(UPDATED_FIRST_RESPONSE_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .slaDueAt(UPDATED_SLA_DUE_AT);
        TicketDTO ticketDTO = ticketMapper.toDto(updatedTicket);

        restTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO))
            )
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketToMatchAllProperties(updatedTicket);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Ticket> ticketSearchList = Streamable.of(ticketSearchRepository.findAll()).toList();
                Ticket testTicketSearch = ticketSearchList.get(searchDatabaseSizeAfter - 1);

                assertTicketAllPropertiesEquals(testTicketSearch, updatedTicket);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(longCount.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(longCount.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(longCount.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket
            .customerId(UPDATED_CUSTOMER_ID)
            .slaPlanId(UPDATED_SLA_PLAN_ID)
            .orderId(UPDATED_ORDER_ID)
            .assigneeEmployeeId(UPDATED_ASSIGNEE_EMPLOYEE_ID)
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .channel(UPDATED_CHANNEL)
            .openedAt(UPDATED_OPENED_AT)
            .slaDueAt(UPDATED_SLA_DUE_AT);

        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicket))
            )
            .andExpect(status().isOk());

        // Validate the Ticket in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTicket, ticket), getPersistedTicket(ticket));
    }

    @Test
    @Transactional
    void fullUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket
            .customerId(UPDATED_CUSTOMER_ID)
            .slaPlanId(UPDATED_SLA_PLAN_ID)
            .orderId(UPDATED_ORDER_ID)
            .assigneeEmployeeId(UPDATED_ASSIGNEE_EMPLOYEE_ID)
            .code(UPDATED_CODE)
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .channel(UPDATED_CHANNEL)
            .openedAt(UPDATED_OPENED_AT)
            .firstResponseAt(UPDATED_FIRST_RESPONSE_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .slaDueAt(UPDATED_SLA_DUE_AT);

        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicket))
            )
            .andExpect(status().isOk());

        // Validate the Ticket in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketUpdatableFieldsEquals(partialUpdatedTicket, getPersistedTicket(partialUpdatedTicket));
    }

    @Test
    @Transactional
    void patchNonExistingTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(longCount.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(longCount.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(longCount.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicket() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);
        ticketRepository.save(ticket);
        ticketSearchRepository.save(ticket);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticket
        restTicketMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticket.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicket() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.saveAndFlush(ticket);
        ticketSearchRepository.save(ticket);

        // Search the ticket
        restTicketMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].slaPlanId").value(hasItem(DEFAULT_SLA_PLAN_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].assigneeEmployeeId").value(hasItem(DEFAULT_ASSIGNEE_EMPLOYEE_ID.intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].openedAt").value(hasItem(DEFAULT_OPENED_AT.toString())))
            .andExpect(jsonPath("$.[*].firstResponseAt").value(hasItem(DEFAULT_FIRST_RESPONSE_AT.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())))
            .andExpect(jsonPath("$.[*].slaDueAt").value(hasItem(DEFAULT_SLA_DUE_AT.toString())));
    }

    protected long getRepositoryCount() {
        return ticketRepository.count();
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

    protected Ticket getPersistedTicket(Ticket ticket) {
        return ticketRepository.findById(ticket.getId()).orElseThrow();
    }

    protected void assertPersistedTicketToMatchAllProperties(Ticket expectedTicket) {
        assertTicketAllPropertiesEquals(expectedTicket, getPersistedTicket(expectedTicket));
    }

    protected void assertPersistedTicketToMatchUpdatableProperties(Ticket expectedTicket) {
        assertTicketAllUpdatablePropertiesEquals(expectedTicket, getPersistedTicket(expectedTicket));
    }
}
