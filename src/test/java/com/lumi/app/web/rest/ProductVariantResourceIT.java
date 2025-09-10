package com.lumi.app.web.rest;

import static com.lumi.app.domain.ProductVariantAsserts.*;
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
import com.lumi.app.domain.ProductVariant;
import com.lumi.app.repository.ProductVariantRepository;
import com.lumi.app.repository.search.ProductVariantSearchRepository;
import com.lumi.app.service.ProductVariantService;
import com.lumi.app.service.dto.ProductVariantDTO;
import com.lumi.app.service.mapper.ProductVariantMapper;
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
 * Integration tests for the {@link ProductVariantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductVariantResourceIT {

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_COMPARE_AT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_COMPARE_AT_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_COMPARE_AT_PRICE = new BigDecimal(1 - 1);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final Long DEFAULT_STOCK_QUANTITY = 0L;
    private static final Long UPDATED_STOCK_QUANTITY = 1L;
    private static final Long SMALLER_STOCK_QUANTITY = 0L - 1L;

    private static final Double DEFAULT_WEIGHT = 1D;
    private static final Double UPDATED_WEIGHT = 2D;
    private static final Double SMALLER_WEIGHT = 1D - 1D;

    private static final Double DEFAULT_LENGTH = 1D;
    private static final Double UPDATED_LENGTH = 2D;
    private static final Double SMALLER_LENGTH = 1D - 1D;

    private static final Double DEFAULT_WIDTH = 1D;
    private static final Double UPDATED_WIDTH = 2D;
    private static final Double SMALLER_WIDTH = 1D - 1D;

    private static final Double DEFAULT_HEIGHT = 1D;
    private static final Double UPDATED_HEIGHT = 2D;
    private static final Double SMALLER_HEIGHT = 1D - 1D;

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/product-variants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/product-variants/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductVariantRepository productVariantRepositoryMock;

    @Autowired
    private ProductVariantMapper productVariantMapper;

    @Mock
    private ProductVariantService productVariantServiceMock;

    @Autowired
    private ProductVariantSearchRepository productVariantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductVariantMockMvc;

    private ProductVariant productVariant;

    private ProductVariant insertedProductVariant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariant createEntity() {
        return new ProductVariant()
            .sku(DEFAULT_SKU)
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .compareAtPrice(DEFAULT_COMPARE_AT_PRICE)
            .currency(DEFAULT_CURRENCY)
            .stockQuantity(DEFAULT_STOCK_QUANTITY)
            .weight(DEFAULT_WEIGHT)
            .length(DEFAULT_LENGTH)
            .width(DEFAULT_WIDTH)
            .height(DEFAULT_HEIGHT)
            .isDefault(DEFAULT_IS_DEFAULT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariant createUpdatedEntity() {
        return new ProductVariant()
            .sku(UPDATED_SKU)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .currency(UPDATED_CURRENCY)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .weight(UPDATED_WEIGHT)
            .length(UPDATED_LENGTH)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .isDefault(UPDATED_IS_DEFAULT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        productVariant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductVariant != null) {
            productVariantRepository.delete(insertedProductVariant);
            productVariantSearchRepository.delete(insertedProductVariant);
            insertedProductVariant = null;
        }
    }

    @Test
    @Transactional
    void createProductVariant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);
        var returnedProductVariantDTO = om.readValue(
            restProductVariantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductVariantDTO.class
        );

        // Validate the ProductVariant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductVariant = productVariantMapper.toEntity(returnedProductVariantDTO);
        assertProductVariantUpdatableFieldsEquals(returnedProductVariant, getPersistedProductVariant(returnedProductVariant));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProductVariant = returnedProductVariant;
    }

    @Test
    @Transactional
    void createProductVariantWithExistingId() throws Exception {
        // Create the ProductVariant with an existing ID
        productVariant.setId(1L);
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSkuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // set the field null
        productVariant.setSku(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // set the field null
        productVariant.setName(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // set the field null
        productVariant.setPrice(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // set the field null
        productVariant.setStockQuantity(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsDefaultIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // set the field null
        productVariant.setIsDefault(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        // set the field null
        productVariant.setCreatedAt(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProductVariants() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVariant.getId().intValue())))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].compareAtPrice").value(hasItem(sameNumber(DEFAULT_COMPARE_AT_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariantsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productVariantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVariantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productVariantServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productVariantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVariantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productVariantRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get the productVariant
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL_ID, productVariant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productVariant.getId().intValue()))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.compareAtPrice").value(sameNumber(DEFAULT_COMPARE_AT_PRICE)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.stockQuantity").value(DEFAULT_STOCK_QUANTITY.intValue()))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT))
            .andExpect(jsonPath("$.length").value(DEFAULT_LENGTH))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getProductVariantsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        Long id = productVariant.getId();

        defaultProductVariantFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProductVariantFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProductVariantFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku equals to
        defaultProductVariantFiltering("sku.equals=" + DEFAULT_SKU, "sku.equals=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku in
        defaultProductVariantFiltering("sku.in=" + DEFAULT_SKU + "," + UPDATED_SKU, "sku.in=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku is not null
        defaultProductVariantFiltering("sku.specified=true", "sku.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku contains
        defaultProductVariantFiltering("sku.contains=" + DEFAULT_SKU, "sku.contains=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku does not contain
        defaultProductVariantFiltering("sku.doesNotContain=" + UPDATED_SKU, "sku.doesNotContain=" + DEFAULT_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name equals to
        defaultProductVariantFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name in
        defaultProductVariantFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name is not null
        defaultProductVariantFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name contains
        defaultProductVariantFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name does not contain
        defaultProductVariantFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price equals to
        defaultProductVariantFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price in
        defaultProductVariantFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is not null
        defaultProductVariantFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is greater than or equal to
        defaultProductVariantFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is less than or equal to
        defaultProductVariantFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is less than
        defaultProductVariantFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is greater than
        defaultProductVariantFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice equals to
        defaultProductVariantFiltering(
            "compareAtPrice.equals=" + DEFAULT_COMPARE_AT_PRICE,
            "compareAtPrice.equals=" + UPDATED_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice in
        defaultProductVariantFiltering(
            "compareAtPrice.in=" + DEFAULT_COMPARE_AT_PRICE + "," + UPDATED_COMPARE_AT_PRICE,
            "compareAtPrice.in=" + UPDATED_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is not null
        defaultProductVariantFiltering("compareAtPrice.specified=true", "compareAtPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is greater than or equal to
        defaultProductVariantFiltering(
            "compareAtPrice.greaterThanOrEqual=" + DEFAULT_COMPARE_AT_PRICE,
            "compareAtPrice.greaterThanOrEqual=" + UPDATED_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is less than or equal to
        defaultProductVariantFiltering(
            "compareAtPrice.lessThanOrEqual=" + DEFAULT_COMPARE_AT_PRICE,
            "compareAtPrice.lessThanOrEqual=" + SMALLER_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is less than
        defaultProductVariantFiltering(
            "compareAtPrice.lessThan=" + UPDATED_COMPARE_AT_PRICE,
            "compareAtPrice.lessThan=" + DEFAULT_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is greater than
        defaultProductVariantFiltering(
            "compareAtPrice.greaterThan=" + SMALLER_COMPARE_AT_PRICE,
            "compareAtPrice.greaterThan=" + DEFAULT_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency equals to
        defaultProductVariantFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency in
        defaultProductVariantFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency is not null
        defaultProductVariantFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency contains
        defaultProductVariantFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency does not contain
        defaultProductVariantFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity equals to
        defaultProductVariantFiltering("stockQuantity.equals=" + DEFAULT_STOCK_QUANTITY, "stockQuantity.equals=" + UPDATED_STOCK_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity in
        defaultProductVariantFiltering(
            "stockQuantity.in=" + DEFAULT_STOCK_QUANTITY + "," + UPDATED_STOCK_QUANTITY,
            "stockQuantity.in=" + UPDATED_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is not null
        defaultProductVariantFiltering("stockQuantity.specified=true", "stockQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is greater than or equal to
        defaultProductVariantFiltering(
            "stockQuantity.greaterThanOrEqual=" + DEFAULT_STOCK_QUANTITY,
            "stockQuantity.greaterThanOrEqual=" + UPDATED_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is less than or equal to
        defaultProductVariantFiltering(
            "stockQuantity.lessThanOrEqual=" + DEFAULT_STOCK_QUANTITY,
            "stockQuantity.lessThanOrEqual=" + SMALLER_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is less than
        defaultProductVariantFiltering(
            "stockQuantity.lessThan=" + UPDATED_STOCK_QUANTITY,
            "stockQuantity.lessThan=" + DEFAULT_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is greater than
        defaultProductVariantFiltering(
            "stockQuantity.greaterThan=" + SMALLER_STOCK_QUANTITY,
            "stockQuantity.greaterThan=" + DEFAULT_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight equals to
        defaultProductVariantFiltering("weight.equals=" + DEFAULT_WEIGHT, "weight.equals=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight in
        defaultProductVariantFiltering("weight.in=" + DEFAULT_WEIGHT + "," + UPDATED_WEIGHT, "weight.in=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight is not null
        defaultProductVariantFiltering("weight.specified=true", "weight.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight is greater than or equal to
        defaultProductVariantFiltering("weight.greaterThanOrEqual=" + DEFAULT_WEIGHT, "weight.greaterThanOrEqual=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight is less than or equal to
        defaultProductVariantFiltering("weight.lessThanOrEqual=" + DEFAULT_WEIGHT, "weight.lessThanOrEqual=" + SMALLER_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight is less than
        defaultProductVariantFiltering("weight.lessThan=" + UPDATED_WEIGHT, "weight.lessThan=" + DEFAULT_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where weight is greater than
        defaultProductVariantFiltering("weight.greaterThan=" + SMALLER_WEIGHT, "weight.greaterThan=" + DEFAULT_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length equals to
        defaultProductVariantFiltering("length.equals=" + DEFAULT_LENGTH, "length.equals=" + UPDATED_LENGTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length in
        defaultProductVariantFiltering("length.in=" + DEFAULT_LENGTH + "," + UPDATED_LENGTH, "length.in=" + UPDATED_LENGTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length is not null
        defaultProductVariantFiltering("length.specified=true", "length.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length is greater than or equal to
        defaultProductVariantFiltering("length.greaterThanOrEqual=" + DEFAULT_LENGTH, "length.greaterThanOrEqual=" + UPDATED_LENGTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length is less than or equal to
        defaultProductVariantFiltering("length.lessThanOrEqual=" + DEFAULT_LENGTH, "length.lessThanOrEqual=" + SMALLER_LENGTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length is less than
        defaultProductVariantFiltering("length.lessThan=" + UPDATED_LENGTH, "length.lessThan=" + DEFAULT_LENGTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByLengthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where length is greater than
        defaultProductVariantFiltering("length.greaterThan=" + SMALLER_LENGTH, "length.greaterThan=" + DEFAULT_LENGTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width equals to
        defaultProductVariantFiltering("width.equals=" + DEFAULT_WIDTH, "width.equals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width in
        defaultProductVariantFiltering("width.in=" + DEFAULT_WIDTH + "," + UPDATED_WIDTH, "width.in=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width is not null
        defaultProductVariantFiltering("width.specified=true", "width.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width is greater than or equal to
        defaultProductVariantFiltering("width.greaterThanOrEqual=" + DEFAULT_WIDTH, "width.greaterThanOrEqual=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width is less than or equal to
        defaultProductVariantFiltering("width.lessThanOrEqual=" + DEFAULT_WIDTH, "width.lessThanOrEqual=" + SMALLER_WIDTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width is less than
        defaultProductVariantFiltering("width.lessThan=" + UPDATED_WIDTH, "width.lessThan=" + DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByWidthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where width is greater than
        defaultProductVariantFiltering("width.greaterThan=" + SMALLER_WIDTH, "width.greaterThan=" + DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height equals to
        defaultProductVariantFiltering("height.equals=" + DEFAULT_HEIGHT, "height.equals=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height in
        defaultProductVariantFiltering("height.in=" + DEFAULT_HEIGHT + "," + UPDATED_HEIGHT, "height.in=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height is not null
        defaultProductVariantFiltering("height.specified=true", "height.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height is greater than or equal to
        defaultProductVariantFiltering("height.greaterThanOrEqual=" + DEFAULT_HEIGHT, "height.greaterThanOrEqual=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height is less than or equal to
        defaultProductVariantFiltering("height.lessThanOrEqual=" + DEFAULT_HEIGHT, "height.lessThanOrEqual=" + SMALLER_HEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height is less than
        defaultProductVariantFiltering("height.lessThan=" + UPDATED_HEIGHT, "height.lessThan=" + DEFAULT_HEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByHeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where height is greater than
        defaultProductVariantFiltering("height.greaterThan=" + SMALLER_HEIGHT, "height.greaterThan=" + DEFAULT_HEIGHT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByIsDefaultIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where isDefault equals to
        defaultProductVariantFiltering("isDefault.equals=" + DEFAULT_IS_DEFAULT, "isDefault.equals=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByIsDefaultIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where isDefault in
        defaultProductVariantFiltering(
            "isDefault.in=" + DEFAULT_IS_DEFAULT + "," + UPDATED_IS_DEFAULT,
            "isDefault.in=" + UPDATED_IS_DEFAULT
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByIsDefaultIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where isDefault is not null
        defaultProductVariantFiltering("isDefault.specified=true", "isDefault.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where createdAt equals to
        defaultProductVariantFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where createdAt in
        defaultProductVariantFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where createdAt is not null
        defaultProductVariantFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where updatedAt equals to
        defaultProductVariantFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where updatedAt in
        defaultProductVariantFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where updatedAt is not null
        defaultProductVariantFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            productVariantRepository.saveAndFlush(productVariant);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        productVariant.setProduct(product);
        productVariantRepository.saveAndFlush(productVariant);
        Long productId = product.getId();
        // Get all the productVariantList where product equals to productId
        defaultProductVariantShouldBeFound("productId.equals=" + productId);

        // Get all the productVariantList where product equals to (productId + 1)
        defaultProductVariantShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultProductVariantFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProductVariantShouldBeFound(shouldBeFound);
        defaultProductVariantShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductVariantShouldBeFound(String filter) throws Exception {
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVariant.getId().intValue())))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].compareAtPrice").value(hasItem(sameNumber(DEFAULT_COMPARE_AT_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductVariantShouldNotBeFound(String filter) throws Exception {
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProductVariant() throws Exception {
        // Get the productVariant
        restProductVariantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariantSearchRepository.save(productVariant);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());

        // Update the productVariant
        ProductVariant updatedProductVariant = productVariantRepository.findById(productVariant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductVariant are not directly saved in db
        em.detach(updatedProductVariant);
        updatedProductVariant
            .sku(UPDATED_SKU)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .currency(UPDATED_CURRENCY)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .weight(UPDATED_WEIGHT)
            .length(UPDATED_LENGTH)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .isDefault(UPDATED_IS_DEFAULT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(updatedProductVariant);

        restProductVariantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productVariantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductVariantToMatchAllProperties(updatedProductVariant);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductVariant> productVariantSearchList = Streamable.of(productVariantSearchRepository.findAll()).toList();
                ProductVariant testProductVariantSearch = productVariantSearchList.get(searchDatabaseSizeAfter - 1);

                assertProductVariantAllPropertiesEquals(testProductVariantSearch, updatedProductVariant);
            });
    }

    @Test
    @Transactional
    void putNonExistingProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productVariantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProductVariantWithPatch() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVariant using partial update
        ProductVariant partialUpdatedProductVariant = new ProductVariant();
        partialUpdatedProductVariant.setId(productVariant.getId());

        partialUpdatedProductVariant.price(UPDATED_PRICE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductVariant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductVariant))
            )
            .andExpect(status().isOk());

        // Validate the ProductVariant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductVariantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductVariant, productVariant),
            getPersistedProductVariant(productVariant)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductVariantWithPatch() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVariant using partial update
        ProductVariant partialUpdatedProductVariant = new ProductVariant();
        partialUpdatedProductVariant.setId(productVariant.getId());

        partialUpdatedProductVariant
            .sku(UPDATED_SKU)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .currency(UPDATED_CURRENCY)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .weight(UPDATED_WEIGHT)
            .length(UPDATED_LENGTH)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .isDefault(UPDATED_IS_DEFAULT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductVariant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductVariant))
            )
            .andExpect(status().isOk());

        // Validate the ProductVariant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductVariantUpdatableFieldsEquals(partialUpdatedProductVariant, getPersistedProductVariant(partialUpdatedProductVariant));
    }

    @Test
    @Transactional
    void patchNonExistingProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productVariantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);
        productVariantRepository.save(productVariant);
        productVariantSearchRepository.save(productVariant);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productVariant
        restProductVariantMockMvc
            .perform(delete(ENTITY_API_URL_ID, productVariant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);
        productVariantSearchRepository.save(productVariant);

        // Search the productVariant
        restProductVariantMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + productVariant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVariant.getId().intValue())))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].compareAtPrice").value(hasItem(sameNumber(DEFAULT_COMPARE_AT_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return productVariantRepository.count();
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

    protected ProductVariant getPersistedProductVariant(ProductVariant productVariant) {
        return productVariantRepository.findById(productVariant.getId()).orElseThrow();
    }

    protected void assertPersistedProductVariantToMatchAllProperties(ProductVariant expectedProductVariant) {
        assertProductVariantAllPropertiesEquals(expectedProductVariant, getPersistedProductVariant(expectedProductVariant));
    }

    protected void assertPersistedProductVariantToMatchUpdatableProperties(ProductVariant expectedProductVariant) {
        assertProductVariantAllUpdatablePropertiesEquals(expectedProductVariant, getPersistedProductVariant(expectedProductVariant));
    }
}
