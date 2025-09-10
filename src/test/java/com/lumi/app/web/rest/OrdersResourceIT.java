package com.lumi.app.web.rest;

import static com.lumi.app.domain.OrdersAsserts.*;
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
import com.lumi.app.domain.Customer;
import com.lumi.app.domain.Orders;
import com.lumi.app.domain.enumeration.FulfillmentStatus;
import com.lumi.app.domain.enumeration.OrderStatus;
import com.lumi.app.domain.enumeration.PaymentStatus;
import com.lumi.app.repository.OrdersRepository;
import com.lumi.app.repository.search.OrdersSearchRepository;
import com.lumi.app.service.OrdersService;
import com.lumi.app.service.dto.OrdersDTO;
import com.lumi.app.service.mapper.OrdersMapper;
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
 * Integration tests for the {@link OrdersResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrdersResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DRAFT;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.PENDING;

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.UNPAID;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.PAID;

    private static final FulfillmentStatus DEFAULT_FULFILLMENT_STATUS = FulfillmentStatus.UNFULFILLED;
    private static final FulfillmentStatus UPDATED_FULFILLMENT_STATUS = FulfillmentStatus.PARTIALLY_FULFILLED;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PLACED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLACED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/orders/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrdersRepository ordersRepository;

    @Mock
    private OrdersRepository ordersRepositoryMock;

    @Autowired
    private OrdersMapper ordersMapper;

    @Mock
    private OrdersService ordersServiceMock;

    @Autowired
    private OrdersSearchRepository ordersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrdersMockMvc;

    private Orders orders;

    private Orders insertedOrders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createEntity() {
        return new Orders()
            .code(DEFAULT_CODE)
            .status(DEFAULT_STATUS)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .fulfillmentStatus(DEFAULT_FULFILLMENT_STATUS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .note(DEFAULT_NOTE)
            .placedAt(DEFAULT_PLACED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createUpdatedEntity() {
        return new Orders()
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .fulfillmentStatus(UPDATED_FULFILLMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .note(UPDATED_NOTE)
            .placedAt(UPDATED_PLACED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        orders = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrders != null) {
            ordersRepository.delete(insertedOrders);
            ordersSearchRepository.delete(insertedOrders);
            insertedOrders = null;
        }
    }

    @Test
    @Transactional
    void createOrders() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);
        var returnedOrdersDTO = om.readValue(
            restOrdersMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrdersDTO.class
        );

        // Validate the Orders in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrders = ordersMapper.toEntity(returnedOrdersDTO);
        assertOrdersUpdatableFieldsEquals(returnedOrders, getPersistedOrders(returnedOrders));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedOrders = returnedOrders;
    }

    @Test
    @Transactional
    void createOrdersWithExistingId() throws Exception {
        // Create the Orders with an existing ID
        orders.setId(1L);
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // set the field null
        orders.setCode(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // set the field null
        orders.setStatus(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaymentStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // set the field null
        orders.setPaymentStatus(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFulfillmentStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // set the field null
        orders.setFulfillmentStatus(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // set the field null
        orders.setTotalAmount(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fulfillmentStatus").value(hasItem(DEFAULT_FULFILLMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(ordersServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrdersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ordersServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ordersServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrdersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ordersRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get the orders
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orders.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.fulfillmentStatus").value(DEFAULT_FULFILLMENT_STATUS.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.placedAt").value(DEFAULT_PLACED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        Long id = orders.getId();

        defaultOrdersFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOrdersFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOrdersFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code equals to
        defaultOrdersFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code in
        defaultOrdersFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code is not null
        defaultOrdersFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code contains
        defaultOrdersFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code does not contain
        defaultOrdersFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status equals to
        defaultOrdersFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status in
        defaultOrdersFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status is not null
        defaultOrdersFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentStatus equals to
        defaultOrdersFiltering("paymentStatus.equals=" + DEFAULT_PAYMENT_STATUS, "paymentStatus.equals=" + UPDATED_PAYMENT_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentStatus in
        defaultOrdersFiltering(
            "paymentStatus.in=" + DEFAULT_PAYMENT_STATUS + "," + UPDATED_PAYMENT_STATUS,
            "paymentStatus.in=" + UPDATED_PAYMENT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentStatus is not null
        defaultOrdersFiltering("paymentStatus.specified=true", "paymentStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByFulfillmentStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where fulfillmentStatus equals to
        defaultOrdersFiltering(
            "fulfillmentStatus.equals=" + DEFAULT_FULFILLMENT_STATUS,
            "fulfillmentStatus.equals=" + UPDATED_FULFILLMENT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByFulfillmentStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where fulfillmentStatus in
        defaultOrdersFiltering(
            "fulfillmentStatus.in=" + DEFAULT_FULFILLMENT_STATUS + "," + UPDATED_FULFILLMENT_STATUS,
            "fulfillmentStatus.in=" + UPDATED_FULFILLMENT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByFulfillmentStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where fulfillmentStatus is not null
        defaultOrdersFiltering("fulfillmentStatus.specified=true", "fulfillmentStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount equals to
        defaultOrdersFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount in
        defaultOrdersFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is not null
        defaultOrdersFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is greater than or equal to
        defaultOrdersFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is less than or equal to
        defaultOrdersFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is less than
        defaultOrdersFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is greater than
        defaultOrdersFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where currency equals to
        defaultOrdersFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllOrdersByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where currency in
        defaultOrdersFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllOrdersByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where currency is not null
        defaultOrdersFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where currency contains
        defaultOrdersFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllOrdersByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where currency does not contain
        defaultOrdersFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note equals to
        defaultOrdersFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note in
        defaultOrdersFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note is not null
        defaultOrdersFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note contains
        defaultOrdersFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note does not contain
        defaultOrdersFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByPlacedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where placedAt equals to
        defaultOrdersFiltering("placedAt.equals=" + DEFAULT_PLACED_AT, "placedAt.equals=" + UPDATED_PLACED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByPlacedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where placedAt in
        defaultOrdersFiltering("placedAt.in=" + DEFAULT_PLACED_AT + "," + UPDATED_PLACED_AT, "placedAt.in=" + UPDATED_PLACED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByPlacedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where placedAt is not null
        defaultOrdersFiltering("placedAt.specified=true", "placedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where updatedAt equals to
        defaultOrdersFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where updatedAt in
        defaultOrdersFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where updatedAt is not null
        defaultOrdersFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            ordersRepository.saveAndFlush(orders);
            customer = CustomerResourceIT.createEntity();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        orders.setCustomer(customer);
        ordersRepository.saveAndFlush(orders);
        Long customerId = customer.getId();
        // Get all the ordersList where customer equals to customerId
        defaultOrdersShouldBeFound("customerId.equals=" + customerId);

        // Get all the ordersList where customer equals to (customerId + 1)
        defaultOrdersShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultOrdersFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrdersShouldBeFound(shouldBeFound);
        defaultOrdersShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrdersShouldBeFound(String filter) throws Exception {
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fulfillmentStatus").value(hasItem(DEFAULT_FULFILLMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrdersShouldNotBeFound(String filter) throws Exception {
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrders() throws Exception {
        // Get the orders
        restOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordersSearchRepository.save(orders);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrders are not directly saved in db
        em.detach(updatedOrders);
        updatedOrders
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .fulfillmentStatus(UPDATED_FULFILLMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .note(UPDATED_NOTE)
            .placedAt(UPDATED_PLACED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        OrdersDTO ordersDTO = ordersMapper.toDto(updatedOrders);

        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ordersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrdersToMatchAllProperties(updatedOrders);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Orders> ordersSearchList = Streamable.of(ordersSearchRepository.findAll()).toList();
                Orders testOrdersSearch = ordersSearchList.get(searchDatabaseSizeAfter - 1);

                assertOrdersAllPropertiesEquals(testOrdersSearch, updatedOrders);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ordersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .fulfillmentStatus(UPDATED_FULFILLMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .updatedAt(UPDATED_UPDATED_AT);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrders, orders), getPersistedOrders(orders));
    }

    @Test
    @Transactional
    void fullUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .fulfillmentStatus(UPDATED_FULFILLMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .note(UPDATED_NOTE)
            .placedAt(UPDATED_PLACED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(partialUpdatedOrders, getPersistedOrders(partialUpdatedOrders));
    }

    @Test
    @Transactional
    void patchNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ordersDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);
        ordersRepository.save(orders);
        ordersSearchRepository.save(orders);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orders
        restOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, orders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);
        ordersSearchRepository.save(orders);

        // Search the orders
        restOrdersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fulfillmentStatus").value(hasItem(DEFAULT_FULFILLMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return ordersRepository.count();
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

    protected Orders getPersistedOrders(Orders orders) {
        return ordersRepository.findById(orders.getId()).orElseThrow();
    }

    protected void assertPersistedOrdersToMatchAllProperties(Orders expectedOrders) {
        assertOrdersAllPropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }

    protected void assertPersistedOrdersToMatchUpdatableProperties(Orders expectedOrders) {
        assertOrdersAllUpdatablePropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }
}
