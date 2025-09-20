package com.lumi.app.web.rest;

import static com.lumi.app.domain.ClothProductMapAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ClothProductMap;
import com.lumi.app.repository.ClothProductMapRepository;
import com.lumi.app.repository.search.ClothProductMapSearchRepository;
import com.lumi.app.service.dto.ClothProductMapDTO;
import com.lumi.app.service.mapper.ClothProductMapMapper;
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
 * Integration tests for the {@link ClothProductMapResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClothProductMapResourceIT {

    private static final Long DEFAULT_CLOTH_ID = 1L;
    private static final Long UPDATED_CLOTH_ID = 2L;
    private static final Long SMALLER_CLOTH_ID = 1L - 1L;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;
    private static final Long SMALLER_PRODUCT_ID = 1L - 1L;

    private static final Double DEFAULT_QUANTITY = 0D;
    private static final Double UPDATED_QUANTITY = 1D;
    private static final Double SMALLER_QUANTITY = 0D - 1D;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cloth-product-maps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cloth-product-maps/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClothProductMapRepository clothProductMapRepository;

    @Autowired
    private ClothProductMapMapper clothProductMapMapper;

    @Autowired
    private ClothProductMapSearchRepository clothProductMapSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClothProductMapMockMvc;

    private ClothProductMap clothProductMap;

    private ClothProductMap insertedClothProductMap;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothProductMap createEntity() {
        return new ClothProductMap()
            .clothId(DEFAULT_CLOTH_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .quantity(DEFAULT_QUANTITY)
            .unit(DEFAULT_UNIT)
            .note(DEFAULT_NOTE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClothProductMap createUpdatedEntity() {
        return new ClothProductMap()
            .clothId(UPDATED_CLOTH_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        clothProductMap = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClothProductMap != null) {
            clothProductMapRepository.delete(insertedClothProductMap);
            clothProductMapSearchRepository.delete(insertedClothProductMap);
            insertedClothProductMap = null;
        }
    }

    @Test
    @Transactional
    void createClothProductMap() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);
        var returnedClothProductMapDTO = om.readValue(
            restClothProductMapMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClothProductMapDTO.class
        );

        // Validate the ClothProductMap in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClothProductMap = clothProductMapMapper.toEntity(returnedClothProductMapDTO);
        assertClothProductMapUpdatableFieldsEquals(returnedClothProductMap, getPersistedClothProductMap(returnedClothProductMap));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedClothProductMap = returnedClothProductMap;
    }

    @Test
    @Transactional
    void createClothProductMapWithExistingId() throws Exception {
        // Create the ClothProductMap with an existing ID
        clothProductMap.setId(1L);
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClothProductMapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkClothIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        // set the field null
        clothProductMap.setClothId(null);

        // Create the ClothProductMap, which fails.
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        restClothProductMapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProductIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        // set the field null
        clothProductMap.setProductId(null);

        // Create the ClothProductMap, which fails.
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        restClothProductMapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        // set the field null
        clothProductMap.setQuantity(null);

        // Create the ClothProductMap, which fails.
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        restClothProductMapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        // set the field null
        clothProductMap.setCreatedAt(null);

        // Create the ClothProductMap, which fails.
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        restClothProductMapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllClothProductMaps() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList
        restClothProductMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothProductMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getClothProductMap() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get the clothProductMap
        restClothProductMapMockMvc
            .perform(get(ENTITY_API_URL_ID, clothProductMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clothProductMap.getId().intValue()))
            .andExpect(jsonPath("$.clothId").value(DEFAULT_CLOTH_ID.intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getClothProductMapsByIdFiltering() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        Long id = clothProductMap.getId();

        defaultClothProductMapFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClothProductMapFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClothProductMapFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId equals to
        defaultClothProductMapFiltering("clothId.equals=" + DEFAULT_CLOTH_ID, "clothId.equals=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId in
        defaultClothProductMapFiltering("clothId.in=" + DEFAULT_CLOTH_ID + "," + UPDATED_CLOTH_ID, "clothId.in=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId is not null
        defaultClothProductMapFiltering("clothId.specified=true", "clothId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId is greater than or equal to
        defaultClothProductMapFiltering("clothId.greaterThanOrEqual=" + DEFAULT_CLOTH_ID, "clothId.greaterThanOrEqual=" + UPDATED_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId is less than or equal to
        defaultClothProductMapFiltering("clothId.lessThanOrEqual=" + DEFAULT_CLOTH_ID, "clothId.lessThanOrEqual=" + SMALLER_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId is less than
        defaultClothProductMapFiltering("clothId.lessThan=" + UPDATED_CLOTH_ID, "clothId.lessThan=" + DEFAULT_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByClothIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where clothId is greater than
        defaultClothProductMapFiltering("clothId.greaterThan=" + SMALLER_CLOTH_ID, "clothId.greaterThan=" + DEFAULT_CLOTH_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId equals to
        defaultClothProductMapFiltering("productId.equals=" + DEFAULT_PRODUCT_ID, "productId.equals=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId in
        defaultClothProductMapFiltering(
            "productId.in=" + DEFAULT_PRODUCT_ID + "," + UPDATED_PRODUCT_ID,
            "productId.in=" + UPDATED_PRODUCT_ID
        );
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId is not null
        defaultClothProductMapFiltering("productId.specified=true", "productId.specified=false");
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId is greater than or equal to
        defaultClothProductMapFiltering(
            "productId.greaterThanOrEqual=" + DEFAULT_PRODUCT_ID,
            "productId.greaterThanOrEqual=" + UPDATED_PRODUCT_ID
        );
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId is less than or equal to
        defaultClothProductMapFiltering(
            "productId.lessThanOrEqual=" + DEFAULT_PRODUCT_ID,
            "productId.lessThanOrEqual=" + SMALLER_PRODUCT_ID
        );
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId is less than
        defaultClothProductMapFiltering("productId.lessThan=" + UPDATED_PRODUCT_ID, "productId.lessThan=" + DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByProductIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where productId is greater than
        defaultClothProductMapFiltering("productId.greaterThan=" + SMALLER_PRODUCT_ID, "productId.greaterThan=" + DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity equals to
        defaultClothProductMapFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity in
        defaultClothProductMapFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity is not null
        defaultClothProductMapFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity is greater than or equal to
        defaultClothProductMapFiltering(
            "quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY,
            "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity is less than or equal to
        defaultClothProductMapFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity is less than
        defaultClothProductMapFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where quantity is greater than
        defaultClothProductMapFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where unit equals to
        defaultClothProductMapFiltering("unit.equals=" + DEFAULT_UNIT, "unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where unit in
        defaultClothProductMapFiltering("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT, "unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where unit is not null
        defaultClothProductMapFiltering("unit.specified=true", "unit.specified=false");
    }

    @Test
    @Transactional
    void getAllClothProductMapsByUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where unit contains
        defaultClothProductMapFiltering("unit.contains=" + DEFAULT_UNIT, "unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where unit does not contain
        defaultClothProductMapFiltering("unit.doesNotContain=" + UPDATED_UNIT, "unit.doesNotContain=" + DEFAULT_UNIT);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where note equals to
        defaultClothProductMapFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where note in
        defaultClothProductMapFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where note is not null
        defaultClothProductMapFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllClothProductMapsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where note contains
        defaultClothProductMapFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where note does not contain
        defaultClothProductMapFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where createdAt equals to
        defaultClothProductMapFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClothProductMapsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where createdAt in
        defaultClothProductMapFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllClothProductMapsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        // Get all the clothProductMapList where createdAt is not null
        defaultClothProductMapFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultClothProductMapFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClothProductMapShouldBeFound(shouldBeFound);
        defaultClothProductMapShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClothProductMapShouldBeFound(String filter) throws Exception {
        restClothProductMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothProductMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restClothProductMapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClothProductMapShouldNotBeFound(String filter) throws Exception {
        restClothProductMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClothProductMapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClothProductMap() throws Exception {
        // Get the clothProductMap
        restClothProductMapMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClothProductMap() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        clothProductMapSearchRepository.save(clothProductMap);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());

        // Update the clothProductMap
        ClothProductMap updatedClothProductMap = clothProductMapRepository.findById(clothProductMap.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClothProductMap are not directly saved in db
        em.detach(updatedClothProductMap);
        updatedClothProductMap
            .clothId(UPDATED_CLOTH_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(updatedClothProductMap);

        restClothProductMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothProductMapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothProductMapDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClothProductMapToMatchAllProperties(updatedClothProductMap);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ClothProductMap> clothProductMapSearchList = Streamable.of(clothProductMapSearchRepository.findAll()).toList();
                ClothProductMap testClothProductMapSearch = clothProductMapSearchList.get(searchDatabaseSizeAfter - 1);

                assertClothProductMapAllPropertiesEquals(testClothProductMapSearch, updatedClothProductMap);
            });
    }

    @Test
    @Transactional
    void putNonExistingClothProductMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        clothProductMap.setId(longCount.incrementAndGet());

        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothProductMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothProductMapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothProductMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchClothProductMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        clothProductMap.setId(longCount.incrementAndGet());

        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothProductMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothProductMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClothProductMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        clothProductMap.setId(longCount.incrementAndGet());

        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothProductMapMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateClothProductMapWithPatch() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothProductMap using partial update
        ClothProductMap partialUpdatedClothProductMap = new ClothProductMap();
        partialUpdatedClothProductMap.setId(clothProductMap.getId());

        partialUpdatedClothProductMap.clothId(UPDATED_CLOTH_ID).productId(UPDATED_PRODUCT_ID);

        restClothProductMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothProductMap.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothProductMap))
            )
            .andExpect(status().isOk());

        // Validate the ClothProductMap in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothProductMapUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClothProductMap, clothProductMap),
            getPersistedClothProductMap(clothProductMap)
        );
    }

    @Test
    @Transactional
    void fullUpdateClothProductMapWithPatch() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clothProductMap using partial update
        ClothProductMap partialUpdatedClothProductMap = new ClothProductMap();
        partialUpdatedClothProductMap.setId(clothProductMap.getId());

        partialUpdatedClothProductMap
            .clothId(UPDATED_CLOTH_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT);

        restClothProductMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClothProductMap.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClothProductMap))
            )
            .andExpect(status().isOk());

        // Validate the ClothProductMap in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothProductMapUpdatableFieldsEquals(
            partialUpdatedClothProductMap,
            getPersistedClothProductMap(partialUpdatedClothProductMap)
        );
    }

    @Test
    @Transactional
    void patchNonExistingClothProductMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        clothProductMap.setId(longCount.incrementAndGet());

        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothProductMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clothProductMapDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothProductMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClothProductMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        clothProductMap.setId(longCount.incrementAndGet());

        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothProductMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothProductMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClothProductMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        clothProductMap.setId(longCount.incrementAndGet());

        // Create the ClothProductMap
        ClothProductMapDTO clothProductMapDTO = clothProductMapMapper.toDto(clothProductMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothProductMapMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clothProductMapDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClothProductMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteClothProductMap() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);
        clothProductMapRepository.save(clothProductMap);
        clothProductMapSearchRepository.save(clothProductMap);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the clothProductMap
        restClothProductMapMockMvc
            .perform(delete(ENTITY_API_URL_ID, clothProductMap.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothProductMapSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchClothProductMap() throws Exception {
        // Initialize the database
        insertedClothProductMap = clothProductMapRepository.saveAndFlush(clothProductMap);
        clothProductMapSearchRepository.save(clothProductMap);

        // Search the clothProductMap
        restClothProductMapMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + clothProductMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clothProductMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].clothId").value(hasItem(DEFAULT_CLOTH_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return clothProductMapRepository.count();
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

    protected ClothProductMap getPersistedClothProductMap(ClothProductMap clothProductMap) {
        return clothProductMapRepository.findById(clothProductMap.getId()).orElseThrow();
    }

    protected void assertPersistedClothProductMapToMatchAllProperties(ClothProductMap expectedClothProductMap) {
        assertClothProductMapAllPropertiesEquals(expectedClothProductMap, getPersistedClothProductMap(expectedClothProductMap));
    }

    protected void assertPersistedClothProductMapToMatchUpdatableProperties(ClothProductMap expectedClothProductMap) {
        assertClothProductMapAllUpdatablePropertiesEquals(expectedClothProductMap, getPersistedClothProductMap(expectedClothProductMap));
    }
}
