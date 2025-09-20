package com.lumi.app.web.rest;

import static com.lumi.app.domain.ClothAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Cloth;
import com.lumi.app.domain.enumeration.ClothStatus;
import com.lumi.app.repository.ClothRepository;
import com.lumi.app.repository.search.ClothSearchRepository;
import com.lumi.app.service.dto.ClothDTO;
import com.lumi.app.service.mapper.ClothMapper;
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
 * Integration tests for the {@link ClothResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClothResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MATERIAL = "AAAAAAAAAA";
    private static final String UPDATED_MATERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final Double DEFAULT_WIDTH = 1D;
    private static final Double UPDATED_WIDTH = 2D;
    private static final Double SMALLER_WIDTH = 1D - 1D;

    private static final Double DEFAULT_LENGTH = 1D;
    private static final Double UPDATED_LENGTH = 2D;
    private static final Double SMALLER_LENGTH = 1D - 1D;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final ClothStatus DEFAULT_STATUS = ClothStatus.AVAILABLE;
    private static final ClothStatus UPDATED_STATUS = ClothStatus.RESERVED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cloths";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cloths/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClothRepository clothRepository;

    @Autowired
    private ClothMapper clothMapper;

    @Autowired
    private ClothSearchRepository clothSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClothMockMvc;

    private Cloth cloth;

    private Cloth insertedCloth;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cloth createEntity() {
        return new Cloth()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .material(DEFAULT_MATERIAL)
            .color(DEFAULT_COLOR)
            .width(DEFAULT_WIDTH)
            .length(DEFAULT_LENGTH)
            .unit(DEFAULT_UNIT)
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
    public static Cloth createUpdatedEntity() {
        return new Cloth()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .material(UPDATED_MATERIAL)
            .color(UPDATED_COLOR)
            .width(UPDATED_WIDTH)
            .length(UPDATED_LENGTH)
            .unit(UPDATED_UNIT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        cloth = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCloth != null) {
            clothRepository.delete(insertedCloth);
            clothSearchRepository.delete(insertedCloth);
            insertedCloth = null;
        }
    }

    @Test
    @Transactional
    void createCloth() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);
        var returnedClothDTO = om.readValue(
            restClothMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClothDTO.class
        );

        // Validate the Cloth in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCloth = clothMapper.toEntity(returnedClothDTO);
        assertClothUpdatableFieldsEquals(returnedCloth, getPersistedCloth(returnedCloth));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCloth = returnedCloth;
    }

    @Test
    @Transactional
    void createClothWithExistingId() throws Exception {
        // Create the Cloth with an existing ID
        cloth.setId(1L);
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClothMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        // set the field null
        cloth.setCode(null);

        // Create the Cloth, which fails.
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        restClothMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        // set the field null
        cloth.setName(null);

        // Create the Cloth, which fails.
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        restClothMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        // set the field null
        cloth.setStatus(null);

        // Create the Cloth, which fails.
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        restClothMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        // set the field null
        cloth.setCreatedAt(null);

        // Create the Cloth, which fails.
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        restClothMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCloths() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList
        restClothMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cloth.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].material").value(hasItem(DEFAULT_MATERIAL)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCloth() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get the cloth
        restClothMockMvc
            .perform(get(ENTITY_API_URL_ID, cloth.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cloth.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.material").value(DEFAULT_MATERIAL))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.length").value(DEFAULT_LENGTH))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getClothsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        Long id = cloth.getId();

        defaultClothFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClothFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClothFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClothsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where code equals to
        defaultClothFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllClothsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where code in
        defaultClothFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllClothsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where code is not null
        defaultClothFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where code contains
        defaultClothFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllClothsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where code does not contain
        defaultClothFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllClothsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where name equals to
        defaultClothFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClothsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where name in
        defaultClothFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClothsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where name is not null
        defaultClothFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where name contains
        defaultClothFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClothsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where name does not contain
        defaultClothFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllClothsByMaterialIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where material equals to
        defaultClothFiltering("material.equals=" + DEFAULT_MATERIAL, "material.equals=" + UPDATED_MATERIAL);
    }

    @Test
    @Transactional
    void getAllClothsByMaterialIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where material in
        defaultClothFiltering("material.in=" + DEFAULT_MATERIAL + "," + UPDATED_MATERIAL, "material.in=" + UPDATED_MATERIAL);
    }

    @Test
    @Transactional
    void getAllClothsByMaterialIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where material is not null
        defaultClothFiltering("material.specified=true", "material.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByMaterialContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where material contains
        defaultClothFiltering("material.contains=" + DEFAULT_MATERIAL, "material.contains=" + UPDATED_MATERIAL);
    }

    @Test
    @Transactional
    void getAllClothsByMaterialNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where material does not contain
        defaultClothFiltering("material.doesNotContain=" + UPDATED_MATERIAL, "material.doesNotContain=" + DEFAULT_MATERIAL);
    }

    @Test
    @Transactional
    void getAllClothsByColorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where color equals to
        defaultClothFiltering("color.equals=" + DEFAULT_COLOR, "color.equals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllClothsByColorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where color in
        defaultClothFiltering("color.in=" + DEFAULT_COLOR + "," + UPDATED_COLOR, "color.in=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllClothsByColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where color is not null
        defaultClothFiltering("color.specified=true", "color.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByColorContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where color contains
        defaultClothFiltering("color.contains=" + DEFAULT_COLOR, "color.contains=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllClothsByColorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where color does not contain
        defaultClothFiltering("color.doesNotContain=" + UPDATED_COLOR, "color.doesNotContain=" + DEFAULT_COLOR);
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width equals to
        defaultClothFiltering("width.equals=" + DEFAULT_WIDTH, "width.equals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width in
        defaultClothFiltering("width.in=" + DEFAULT_WIDTH + "," + UPDATED_WIDTH, "width.in=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width is not null
        defaultClothFiltering("width.specified=true", "width.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width is greater than or equal to
        defaultClothFiltering("width.greaterThanOrEqual=" + DEFAULT_WIDTH, "width.greaterThanOrEqual=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width is less than or equal to
        defaultClothFiltering("width.lessThanOrEqual=" + DEFAULT_WIDTH, "width.lessThanOrEqual=" + SMALLER_WIDTH);
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width is less than
        defaultClothFiltering("width.lessThan=" + UPDATED_WIDTH, "width.lessThan=" + DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void getAllClothsByWidthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where width is greater than
        defaultClothFiltering("width.greaterThan=" + SMALLER_WIDTH, "width.greaterThan=" + DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length equals to
        defaultClothFiltering("length.equals=" + DEFAULT_LENGTH, "length.equals=" + UPDATED_LENGTH);
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length in
        defaultClothFiltering("length.in=" + DEFAULT_LENGTH + "," + UPDATED_LENGTH, "length.in=" + UPDATED_LENGTH);
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length is not null
        defaultClothFiltering("length.specified=true", "length.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length is greater than or equal to
        defaultClothFiltering("length.greaterThanOrEqual=" + DEFAULT_LENGTH, "length.greaterThanOrEqual=" + UPDATED_LENGTH);
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length is less than or equal to
        defaultClothFiltering("length.lessThanOrEqual=" + DEFAULT_LENGTH, "length.lessThanOrEqual=" + SMALLER_LENGTH);
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length is less than
        defaultClothFiltering("length.lessThan=" + UPDATED_LENGTH, "length.lessThan=" + DEFAULT_LENGTH);
    }

    @Test
    @Transactional
    void getAllClothsByLengthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where length is greater than
        defaultClothFiltering("length.greaterThan=" + SMALLER_LENGTH, "length.greaterThan=" + DEFAULT_LENGTH);
    }

    @Test
    @Transactional
    void getAllClothsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where unit equals to
        defaultClothFiltering("unit.equals=" + DEFAULT_UNIT, "unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where unit in
        defaultClothFiltering("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT, "unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where unit is not null
        defaultClothFiltering("unit.specified=true", "unit.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where unit contains
        defaultClothFiltering("unit.contains=" + DEFAULT_UNIT, "unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllClothsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where unit does not contain
        defaultClothFiltering("unit.doesNotContain=" + UPDATED_UNIT, "unit.doesNotContain=" + DEFAULT_UNIT);
    }

    @Test
    @Transactional
    void getAllClothsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where status equals to
        defaultClothFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClothsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where status in
        defaultClothFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClothsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where status is not null
        defaultClothFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where createdAt equals to
        defaultClothFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClothsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where createdAt in
        defaultClothFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClothsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where createdAt is not null
        defaultClothFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClothsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where updatedAt equals to
        defaultClothFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClothsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where updatedAt in
        defaultClothFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClothsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        // Get all the clothList where updatedAt is not null
        defaultClothFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultClothFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClothShouldBeFound(shouldBeFound);
        defaultClothShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClothShouldBeFound(String filter) throws Exception {
        restClothMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cloth.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].material").value(hasItem(DEFAULT_MATERIAL)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restClothMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClothShouldNotBeFound(String filter) throws Exception {
        restClothMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClothMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCloth() throws Exception {
        // Get the cloth
        restClothMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCloth() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        clothSearchRepository.save(cloth);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());

        // Update the cloth
        Cloth updatedCloth = clothRepository.findById(cloth.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCloth are not directly saved in db
        em.detach(updatedCloth);
        updatedCloth
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .material(UPDATED_MATERIAL)
            .color(UPDATED_COLOR)
            .width(UPDATED_WIDTH)
            .length(UPDATED_LENGTH)
            .unit(UPDATED_UNIT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ClothDTO clothDTO = clothMapper.toDto(updatedCloth);

        restClothMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClothToMatchAllProperties(updatedCloth);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Cloth> clothSearchList = Streamable.of(clothSearchRepository.findAll()).toList();
                Cloth testClothSearch = clothSearchList.get(searchDatabaseSizeAfter - 1);

                assertClothAllPropertiesEquals(testClothSearch, updatedCloth);
            });
    }

    @Test
    @Transactional
    void putNonExistingCloth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        cloth.setId(longCount.incrementAndGet());

        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clothDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCloth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        cloth.setId(longCount.incrementAndGet());

        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCloth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        cloth.setId(longCount.incrementAndGet());

        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateClothWithPatch() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cloth using partial update
        Cloth partialUpdatedCloth = new Cloth();
        partialUpdatedCloth.setId(cloth.getId());

        partialUpdatedCloth
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .material(UPDATED_MATERIAL)
            .color(UPDATED_COLOR)
            .width(UPDATED_WIDTH)
            .unit(UPDATED_UNIT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restClothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCloth.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCloth))
            )
            .andExpect(status().isOk());

        // Validate the Cloth in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCloth, cloth), getPersistedCloth(cloth));
    }

    @Test
    @Transactional
    void fullUpdateClothWithPatch() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cloth using partial update
        Cloth partialUpdatedCloth = new Cloth();
        partialUpdatedCloth.setId(cloth.getId());

        partialUpdatedCloth
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .material(UPDATED_MATERIAL)
            .color(UPDATED_COLOR)
            .width(UPDATED_WIDTH)
            .length(UPDATED_LENGTH)
            .unit(UPDATED_UNIT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restClothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCloth.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCloth))
            )
            .andExpect(status().isOk());

        // Validate the Cloth in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClothUpdatableFieldsEquals(partialUpdatedCloth, getPersistedCloth(partialUpdatedCloth));
    }

    @Test
    @Transactional
    void patchNonExistingCloth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        cloth.setId(longCount.incrementAndGet());

        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clothDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCloth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        cloth.setId(longCount.incrementAndGet());

        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCloth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        cloth.setId(longCount.incrementAndGet());

        // Create the Cloth
        ClothDTO clothDTO = clothMapper.toDto(cloth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClothMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clothDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cloth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCloth() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);
        clothRepository.save(cloth);
        clothSearchRepository.save(cloth);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the cloth
        restClothMockMvc
            .perform(delete(ENTITY_API_URL_ID, cloth.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(clothSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCloth() throws Exception {
        // Initialize the database
        insertedCloth = clothRepository.saveAndFlush(cloth);
        clothSearchRepository.save(cloth);

        // Search the cloth
        restClothMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cloth.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cloth.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].material").value(hasItem(DEFAULT_MATERIAL)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return clothRepository.count();
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

    protected Cloth getPersistedCloth(Cloth cloth) {
        return clothRepository.findById(cloth.getId()).orElseThrow();
    }

    protected void assertPersistedClothToMatchAllProperties(Cloth expectedCloth) {
        assertClothAllPropertiesEquals(expectedCloth, getPersistedCloth(expectedCloth));
    }

    protected void assertPersistedClothToMatchUpdatableProperties(Cloth expectedCloth) {
        assertClothAllUpdatablePropertiesEquals(expectedCloth, getPersistedCloth(expectedCloth));
    }
}
