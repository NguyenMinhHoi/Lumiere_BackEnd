package com.lumi.app.web.rest;

import static com.lumi.app.domain.KnowledgeArticleAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.domain.Tag;
import com.lumi.app.repository.KnowledgeArticleRepository;
import com.lumi.app.repository.search.KnowledgeArticleSearchRepository;
import com.lumi.app.service.KnowledgeArticleService;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import com.lumi.app.service.mapper.KnowledgeArticleMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link KnowledgeArticleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class KnowledgeArticleResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final Long DEFAULT_VIEWS = 0L;
    private static final Long UPDATED_VIEWS = 1L;
    private static final Long SMALLER_VIEWS = 0L - 1L;

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/knowledge-articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/knowledge-articles/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KnowledgeArticleRepository knowledgeArticleRepository;

    @Mock
    private KnowledgeArticleRepository knowledgeArticleRepositoryMock;

    @Autowired
    private KnowledgeArticleMapper knowledgeArticleMapper;

    @Mock
    private KnowledgeArticleService knowledgeArticleServiceMock;

    @Autowired
    private KnowledgeArticleSearchRepository knowledgeArticleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKnowledgeArticleMockMvc;

    private KnowledgeArticle knowledgeArticle;

    private KnowledgeArticle insertedKnowledgeArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeArticle createEntity() {
        return new KnowledgeArticle()
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .published(DEFAULT_PUBLISHED)
            .views(DEFAULT_VIEWS)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeArticle createUpdatedEntity() {
        return new KnowledgeArticle()
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .published(UPDATED_PUBLISHED)
            .views(UPDATED_VIEWS)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        knowledgeArticle = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedKnowledgeArticle != null) {
            knowledgeArticleRepository.delete(insertedKnowledgeArticle);
            knowledgeArticleSearchRepository.delete(insertedKnowledgeArticle);
            insertedKnowledgeArticle = null;
        }
    }

    @Test
    @Transactional
    void createKnowledgeArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);
        var returnedKnowledgeArticleDTO = om.readValue(
            restKnowledgeArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeArticleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KnowledgeArticleDTO.class
        );

        // Validate the KnowledgeArticle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKnowledgeArticle = knowledgeArticleMapper.toEntity(returnedKnowledgeArticleDTO);
        assertKnowledgeArticleUpdatableFieldsEquals(returnedKnowledgeArticle, getPersistedKnowledgeArticle(returnedKnowledgeArticle));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedKnowledgeArticle = returnedKnowledgeArticle;
    }

    @Test
    @Transactional
    void createKnowledgeArticleWithExistingId() throws Exception {
        // Create the KnowledgeArticle with an existing ID
        knowledgeArticle.setId(1L);
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restKnowledgeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeArticleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        // set the field null
        knowledgeArticle.setTitle(null);

        // Create the KnowledgeArticle, which fails.
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        restKnowledgeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        // set the field null
        knowledgeArticle.setPublished(null);

        // Create the KnowledgeArticle, which fails.
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        restKnowledgeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkViewsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        // set the field null
        knowledgeArticle.setViews(null);

        // Create the KnowledgeArticle, which fails.
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        restKnowledgeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticles() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeArticlesWithEagerRelationshipsIsEnabled() throws Exception {
        when(knowledgeArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKnowledgeArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(knowledgeArticleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeArticlesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(knowledgeArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKnowledgeArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(knowledgeArticleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getKnowledgeArticle() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get the knowledgeArticle
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, knowledgeArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(knowledgeArticle.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS.intValue()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getKnowledgeArticlesByIdFiltering() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        Long id = knowledgeArticle.getId();

        defaultKnowledgeArticleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultKnowledgeArticleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultKnowledgeArticleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where title equals to
        defaultKnowledgeArticleFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where title in
        defaultKnowledgeArticleFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where title is not null
        defaultKnowledgeArticleFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where title contains
        defaultKnowledgeArticleFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where title does not contain
        defaultKnowledgeArticleFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where published equals to
        defaultKnowledgeArticleFiltering("published.equals=" + DEFAULT_PUBLISHED, "published.equals=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where published in
        defaultKnowledgeArticleFiltering(
            "published.in=" + DEFAULT_PUBLISHED + "," + UPDATED_PUBLISHED,
            "published.in=" + UPDATED_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where published is not null
        defaultKnowledgeArticleFiltering("published.specified=true", "published.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views equals to
        defaultKnowledgeArticleFiltering("views.equals=" + DEFAULT_VIEWS, "views.equals=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views in
        defaultKnowledgeArticleFiltering("views.in=" + DEFAULT_VIEWS + "," + UPDATED_VIEWS, "views.in=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views is not null
        defaultKnowledgeArticleFiltering("views.specified=true", "views.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views is greater than or equal to
        defaultKnowledgeArticleFiltering("views.greaterThanOrEqual=" + DEFAULT_VIEWS, "views.greaterThanOrEqual=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views is less than or equal to
        defaultKnowledgeArticleFiltering("views.lessThanOrEqual=" + DEFAULT_VIEWS, "views.lessThanOrEqual=" + SMALLER_VIEWS);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views is less than
        defaultKnowledgeArticleFiltering("views.lessThan=" + UPDATED_VIEWS, "views.lessThan=" + DEFAULT_VIEWS);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByViewsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where views is greater than
        defaultKnowledgeArticleFiltering("views.greaterThan=" + SMALLER_VIEWS, "views.greaterThan=" + DEFAULT_VIEWS);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where updatedAt equals to
        defaultKnowledgeArticleFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where updatedAt in
        defaultKnowledgeArticleFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        // Get all the knowledgeArticleList where updatedAt is not null
        defaultKnowledgeArticleFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByCategoryIsEqualToSomething() throws Exception {
        KnowledgeCategory category;
        if (TestUtil.findAll(em, KnowledgeCategory.class).isEmpty()) {
            knowledgeArticleRepository.saveAndFlush(knowledgeArticle);
            category = KnowledgeCategoryResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, KnowledgeCategory.class).get(0);
        }
        em.persist(category);
        em.flush();
        knowledgeArticle.setCategory(category);
        knowledgeArticleRepository.saveAndFlush(knowledgeArticle);
        Long categoryId = category.getId();
        // Get all the knowledgeArticleList where category equals to categoryId
        defaultKnowledgeArticleShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the knowledgeArticleList where category equals to (categoryId + 1)
        defaultKnowledgeArticleShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllKnowledgeArticlesByTagsIsEqualToSomething() throws Exception {
        Tag tags;
        if (TestUtil.findAll(em, Tag.class).isEmpty()) {
            knowledgeArticleRepository.saveAndFlush(knowledgeArticle);
            tags = TagResourceIT.createEntity();
        } else {
            tags = TestUtil.findAll(em, Tag.class).get(0);
        }
        em.persist(tags);
        em.flush();
        knowledgeArticle.addTags(tags);
        knowledgeArticleRepository.saveAndFlush(knowledgeArticle);
        Long tagsId = tags.getId();
        // Get all the knowledgeArticleList where tags equals to tagsId
        defaultKnowledgeArticleShouldBeFound("tagsId.equals=" + tagsId);

        // Get all the knowledgeArticleList where tags equals to (tagsId + 1)
        defaultKnowledgeArticleShouldNotBeFound("tagsId.equals=" + (tagsId + 1));
    }

    private void defaultKnowledgeArticleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultKnowledgeArticleShouldBeFound(shouldBeFound);
        defaultKnowledgeArticleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKnowledgeArticleShouldBeFound(String filter) throws Exception {
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKnowledgeArticleShouldNotBeFound(String filter) throws Exception {
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKnowledgeArticle() throws Exception {
        // Get the knowledgeArticle
        restKnowledgeArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKnowledgeArticle() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeArticleSearchRepository.save(knowledgeArticle);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());

        // Update the knowledgeArticle
        KnowledgeArticle updatedKnowledgeArticle = knowledgeArticleRepository.findById(knowledgeArticle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKnowledgeArticle are not directly saved in db
        em.detach(updatedKnowledgeArticle);
        updatedKnowledgeArticle
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .published(UPDATED_PUBLISHED)
            .views(UPDATED_VIEWS)
            .updatedAt(UPDATED_UPDATED_AT);
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(updatedKnowledgeArticle);

        restKnowledgeArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeArticleDTO))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKnowledgeArticleToMatchAllProperties(updatedKnowledgeArticle);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<KnowledgeArticle> knowledgeArticleSearchList = Streamable.of(knowledgeArticleSearchRepository.findAll()).toList();
                KnowledgeArticle testKnowledgeArticleSearch = knowledgeArticleSearchList.get(searchDatabaseSizeAfter - 1);

                assertKnowledgeArticleAllPropertiesEquals(testKnowledgeArticleSearch, updatedKnowledgeArticle);
            });
    }

    @Test
    @Transactional
    void putNonExistingKnowledgeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        knowledgeArticle.setId(longCount.incrementAndGet());

        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchKnowledgeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        knowledgeArticle.setId(longCount.incrementAndGet());

        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKnowledgeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        knowledgeArticle.setId(longCount.incrementAndGet());

        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateKnowledgeArticleWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeArticle using partial update
        KnowledgeArticle partialUpdatedKnowledgeArticle = new KnowledgeArticle();
        partialUpdatedKnowledgeArticle.setId(knowledgeArticle.getId());

        partialUpdatedKnowledgeArticle.content(UPDATED_CONTENT).views(UPDATED_VIEWS).updatedAt(UPDATED_UPDATED_AT);

        restKnowledgeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeArticle))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeArticleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedKnowledgeArticle, knowledgeArticle),
            getPersistedKnowledgeArticle(knowledgeArticle)
        );
    }

    @Test
    @Transactional
    void fullUpdateKnowledgeArticleWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeArticle using partial update
        KnowledgeArticle partialUpdatedKnowledgeArticle = new KnowledgeArticle();
        partialUpdatedKnowledgeArticle.setId(knowledgeArticle.getId());

        partialUpdatedKnowledgeArticle
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .published(UPDATED_PUBLISHED)
            .views(UPDATED_VIEWS)
            .updatedAt(UPDATED_UPDATED_AT);

        restKnowledgeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeArticle))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeArticleUpdatableFieldsEquals(
            partialUpdatedKnowledgeArticle,
            getPersistedKnowledgeArticle(partialUpdatedKnowledgeArticle)
        );
    }

    @Test
    @Transactional
    void patchNonExistingKnowledgeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        knowledgeArticle.setId(longCount.incrementAndGet());

        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, knowledgeArticleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKnowledgeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        knowledgeArticle.setId(longCount.incrementAndGet());

        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKnowledgeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        knowledgeArticle.setId(longCount.incrementAndGet());

        // Create the KnowledgeArticle
        KnowledgeArticleDTO knowledgeArticleDTO = knowledgeArticleMapper.toDto(knowledgeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(knowledgeArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteKnowledgeArticle() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);
        knowledgeArticleRepository.save(knowledgeArticle);
        knowledgeArticleSearchRepository.save(knowledgeArticle);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the knowledgeArticle
        restKnowledgeArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, knowledgeArticle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeArticleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchKnowledgeArticle() throws Exception {
        // Initialize the database
        insertedKnowledgeArticle = knowledgeArticleRepository.saveAndFlush(knowledgeArticle);
        knowledgeArticleSearchRepository.save(knowledgeArticle);

        // Search the knowledgeArticle
        restKnowledgeArticleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + knowledgeArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return knowledgeArticleRepository.count();
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

    protected KnowledgeArticle getPersistedKnowledgeArticle(KnowledgeArticle knowledgeArticle) {
        return knowledgeArticleRepository.findById(knowledgeArticle.getId()).orElseThrow();
    }

    protected void assertPersistedKnowledgeArticleToMatchAllProperties(KnowledgeArticle expectedKnowledgeArticle) {
        assertKnowledgeArticleAllPropertiesEquals(expectedKnowledgeArticle, getPersistedKnowledgeArticle(expectedKnowledgeArticle));
    }

    protected void assertPersistedKnowledgeArticleToMatchUpdatableProperties(KnowledgeArticle expectedKnowledgeArticle) {
        assertKnowledgeArticleAllUpdatablePropertiesEquals(
            expectedKnowledgeArticle,
            getPersistedKnowledgeArticle(expectedKnowledgeArticle)
        );
    }
}
