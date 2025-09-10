package com.lumi.app.web.rest;

import static com.lumi.app.domain.OrderItemAsserts.*;
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
import com.lumi.app.domain.OrderItem;
import com.lumi.app.domain.Orders;
import com.lumi.app.domain.ProductVariant;
import com.lumi.app.repository.OrderItemRepository;
import com.lumi.app.repository.search.OrderItemSearchRepository;
import com.lumi.app.service.OrderItemService;
import com.lumi.app.service.dto.OrderItemDTO;
import com.lumi.app.service.mapper.OrderItemMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link OrderItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderItemResourceIT {

    private static final Long DEFAULT_QUANTITY = 1L;
    private static final Long UPDATED_QUANTITY = 2L;
    private static final Long SMALLER_QUANTITY = 1L - 1L;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_NAME_SNAPSHOT = "AAAAAAAAAA";
    private static final String UPDATED_NAME_SNAPSHOT = "BBBBBBBBBB";

    private static final String DEFAULT_SKU_SNAPSHOT = "AAAAAAAAAA";
    private static final String UPDATED_SKU_SNAPSHOT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/order-items/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemRepository orderItemRepositoryMock;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderItemService orderItemServiceMock;

    @Autowired
    private OrderItemSearchRepository orderItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemMockMvc;

    private OrderItem orderItem;

    private OrderItem insertedOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createEntity() {
        return new OrderItem()
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .nameSnapshot(DEFAULT_NAME_SNAPSHOT)
            .skuSnapshot(DEFAULT_SKU_SNAPSHOT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createUpdatedEntity() {
        return new OrderItem()
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .nameSnapshot(UPDATED_NAME_SNAPSHOT)
            .skuSnapshot(UPDATED_SKU_SNAPSHOT);
    }

    @BeforeEach
    void initTest() {
        orderItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderItem != null) {
            orderItemRepository.delete(insertedOrderItem);
            orderItemSearchRepository.delete(insertedOrderItem);
            insertedOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        var returnedOrderItemDTO = om.readValue(
            restOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderItemDTO.class
        );

        // Validate the OrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderItem = orderItemMapper.toEntity(returnedOrderItemDTO);
        assertOrderItemUpdatableFieldsEquals(returnedOrderItem, getPersistedOrderItem(returnedOrderItem));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedOrderItem = returnedOrderItem;
    }

    @Test
    @Transactional
    void createOrderItemWithExistingId() throws Exception {
        // Create the OrderItem with an existing ID
        orderItem.setId(1L);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        // set the field null
        orderItem.setQuantity(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        // set the field null
        orderItem.setUnitPrice(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTotalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        // set the field null
        orderItem.setTotalPrice(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrderItems() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE))))
            .andExpect(jsonPath("$.[*].nameSnapshot").value(hasItem(DEFAULT_NAME_SNAPSHOT)))
            .andExpect(jsonPath("$.[*].skuSnapshot").value(hasItem(DEFAULT_SKU_SNAPSHOT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get the orderItem
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, orderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.intValue()))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.totalPrice").value(sameNumber(DEFAULT_TOTAL_PRICE)))
            .andExpect(jsonPath("$.nameSnapshot").value(DEFAULT_NAME_SNAPSHOT))
            .andExpect(jsonPath("$.skuSnapshot").value(DEFAULT_SKU_SNAPSHOT));
    }

    @Test
    @Transactional
    void getOrderItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        Long id = orderItem.getId();

        defaultOrderItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOrderItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOrderItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity equals to
        defaultOrderItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity in
        defaultOrderItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is not null
        defaultOrderItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is greater than or equal to
        defaultOrderItemFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is less than or equal to
        defaultOrderItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is less than
        defaultOrderItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is greater than
        defaultOrderItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice equals to
        defaultOrderItemFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice in
        defaultOrderItemFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is not null
        defaultOrderItemFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is greater than or equal to
        defaultOrderItemFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is less than or equal to
        defaultOrderItemFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is less than
        defaultOrderItemFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is greater than
        defaultOrderItemFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice equals to
        defaultOrderItemFiltering("totalPrice.equals=" + DEFAULT_TOTAL_PRICE, "totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice in
        defaultOrderItemFiltering(
            "totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE,
            "totalPrice.in=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is not null
        defaultOrderItemFiltering("totalPrice.specified=true", "totalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is greater than or equal to
        defaultOrderItemFiltering(
            "totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is less than or equal to
        defaultOrderItemFiltering("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE, "totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is less than
        defaultOrderItemFiltering("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE, "totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByTotalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is greater than
        defaultOrderItemFiltering("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE, "totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNameSnapshotIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where nameSnapshot equals to
        defaultOrderItemFiltering("nameSnapshot.equals=" + DEFAULT_NAME_SNAPSHOT, "nameSnapshot.equals=" + UPDATED_NAME_SNAPSHOT);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNameSnapshotIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where nameSnapshot in
        defaultOrderItemFiltering(
            "nameSnapshot.in=" + DEFAULT_NAME_SNAPSHOT + "," + UPDATED_NAME_SNAPSHOT,
            "nameSnapshot.in=" + UPDATED_NAME_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByNameSnapshotIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where nameSnapshot is not null
        defaultOrderItemFiltering("nameSnapshot.specified=true", "nameSnapshot.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByNameSnapshotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where nameSnapshot contains
        defaultOrderItemFiltering("nameSnapshot.contains=" + DEFAULT_NAME_SNAPSHOT, "nameSnapshot.contains=" + UPDATED_NAME_SNAPSHOT);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNameSnapshotNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where nameSnapshot does not contain
        defaultOrderItemFiltering(
            "nameSnapshot.doesNotContain=" + UPDATED_NAME_SNAPSHOT,
            "nameSnapshot.doesNotContain=" + DEFAULT_NAME_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsBySkuSnapshotIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where skuSnapshot equals to
        defaultOrderItemFiltering("skuSnapshot.equals=" + DEFAULT_SKU_SNAPSHOT, "skuSnapshot.equals=" + UPDATED_SKU_SNAPSHOT);
    }

    @Test
    @Transactional
    void getAllOrderItemsBySkuSnapshotIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where skuSnapshot in
        defaultOrderItemFiltering(
            "skuSnapshot.in=" + DEFAULT_SKU_SNAPSHOT + "," + UPDATED_SKU_SNAPSHOT,
            "skuSnapshot.in=" + UPDATED_SKU_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsBySkuSnapshotIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where skuSnapshot is not null
        defaultOrderItemFiltering("skuSnapshot.specified=true", "skuSnapshot.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsBySkuSnapshotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where skuSnapshot contains
        defaultOrderItemFiltering("skuSnapshot.contains=" + DEFAULT_SKU_SNAPSHOT, "skuSnapshot.contains=" + UPDATED_SKU_SNAPSHOT);
    }

    @Test
    @Transactional
    void getAllOrderItemsBySkuSnapshotNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where skuSnapshot does not contain
        defaultOrderItemFiltering(
            "skuSnapshot.doesNotContain=" + UPDATED_SKU_SNAPSHOT,
            "skuSnapshot.doesNotContain=" + DEFAULT_SKU_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderIsEqualToSomething() throws Exception {
        Orders order;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orderItemRepository.saveAndFlush(orderItem);
            order = OrdersResourceIT.createEntity();
        } else {
            order = TestUtil.findAll(em, Orders.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderItem.setOrder(order);
        orderItemRepository.saveAndFlush(orderItem);
        Long orderId = order.getId();
        // Get all the orderItemList where order equals to orderId
        defaultOrderItemShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderItemList where order equals to (orderId + 1)
        defaultOrderItemShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllOrderItemsByVariantIsEqualToSomething() throws Exception {
        ProductVariant variant;
        if (TestUtil.findAll(em, ProductVariant.class).isEmpty()) {
            orderItemRepository.saveAndFlush(orderItem);
            variant = ProductVariantResourceIT.createEntity();
        } else {
            variant = TestUtil.findAll(em, ProductVariant.class).get(0);
        }
        em.persist(variant);
        em.flush();
        orderItem.setVariant(variant);
        orderItemRepository.saveAndFlush(orderItem);
        Long variantId = variant.getId();
        // Get all the orderItemList where variant equals to variantId
        defaultOrderItemShouldBeFound("variantId.equals=" + variantId);

        // Get all the orderItemList where variant equals to (variantId + 1)
        defaultOrderItemShouldNotBeFound("variantId.equals=" + (variantId + 1));
    }

    private void defaultOrderItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrderItemShouldBeFound(shouldBeFound);
        defaultOrderItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderItemShouldBeFound(String filter) throws Exception {
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE))))
            .andExpect(jsonPath("$.[*].nameSnapshot").value(hasItem(DEFAULT_NAME_SNAPSHOT)))
            .andExpect(jsonPath("$.[*].skuSnapshot").value(hasItem(DEFAULT_SKU_SNAPSHOT)));

        // Check, that the count call also returns 1
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderItemShouldNotBeFound(String filter) throws Exception {
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderItem() throws Exception {
        // Get the orderItem
        restOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemSearchRepository.save(orderItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());

        // Update the orderItem
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderItem are not directly saved in db
        em.detach(updatedOrderItem);
        updatedOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .nameSnapshot(UPDATED_NAME_SNAPSHOT)
            .skuSnapshot(UPDATED_SKU_SNAPSHOT);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(updatedOrderItem);

        restOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemToMatchAllProperties(updatedOrderItem);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OrderItem> orderItemSearchList = Streamable.of(orderItemSearchRepository.findAll()).toList();
                OrderItem testOrderItemSearch = orderItemSearchList.get(searchDatabaseSizeAfter - 1);

                assertOrderItemAllPropertiesEquals(testOrderItemSearch, updatedOrderItem);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        orderItem.setId(longCount.incrementAndGet());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        orderItem.setId(longCount.incrementAndGet());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        orderItem.setId(longCount.incrementAndGet());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem.unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItem, orderItem),
            getPersistedOrderItem(orderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .nameSnapshot(UPDATED_NAME_SNAPSHOT)
            .skuSnapshot(UPDATED_SKU_SNAPSHOT);

        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(partialUpdatedOrderItem, getPersistedOrderItem(partialUpdatedOrderItem));
    }

    @Test
    @Transactional
    void patchNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        orderItem.setId(longCount.incrementAndGet());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        orderItem.setId(longCount.incrementAndGet());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        orderItem.setId(longCount.incrementAndGet());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);
        orderItemRepository.save(orderItem);
        orderItemSearchRepository.save(orderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orderItem
        restOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);
        orderItemSearchRepository.save(orderItem);

        // Search the orderItem
        restOrderItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE))))
            .andExpect(jsonPath("$.[*].nameSnapshot").value(hasItem(DEFAULT_NAME_SNAPSHOT)))
            .andExpect(jsonPath("$.[*].skuSnapshot").value(hasItem(DEFAULT_SKU_SNAPSHOT)));
    }

    protected long getRepositoryCount() {
        return orderItemRepository.count();
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

    protected OrderItem getPersistedOrderItem(OrderItem orderItem) {
        return orderItemRepository.findById(orderItem.getId()).orElseThrow();
    }

    protected void assertPersistedOrderItemToMatchAllProperties(OrderItem expectedOrderItem) {
        assertOrderItemAllPropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }

    protected void assertPersistedOrderItemToMatchUpdatableProperties(OrderItem expectedOrderItem) {
        assertOrderItemAllUpdatablePropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }
}
