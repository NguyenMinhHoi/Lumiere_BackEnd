package com.lumi.app.web.rest;

import static com.lumi.app.domain.KnowledgeCategoryAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.repository.KnowledgeCategoryRepository;
import com.lumi.app.repository.search.KnowledgeCategorySearchRepository;
import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import com.lumi.app.service.mapper.KnowledgeCategoryMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link KnowledgeCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class KnowledgeCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/knowledge-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/knowledge-categories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KnowledgeCategoryRepository knowledgeCategoryRepository;

    @Autowired
    private KnowledgeCategoryMapper knowledgeCategoryMapper;

    @Autowired
    private KnowledgeCategorySearchRepository knowledgeCategorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKnowledgeCategoryMockMvc;

    private KnowledgeCategory knowledgeCategory;

    private KnowledgeCategory insertedKnowledgeCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeCategory createEntity() {
        return new KnowledgeCategory().name(DEFAULT_NAME).slug(DEFAULT_SLUG);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeCategory createUpdatedEntity() {
        return new KnowledgeCategory().name(UPDATED_NAME).slug(UPDATED_SLUG);
    }

    @BeforeEach
    void initTest() {
        knowledgeCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedKnowledgeCategory != null) {
            knowledgeCategoryRepository.delete(insertedKnowledgeCategory);
            knowledgeCategorySearchRepository.delete(insertedKnowledgeCategory);
            insertedKnowledgeCategory = null;
        }
    }

    @Test
    @Transactional
    void createKnowledgeCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);
        var returnedKnowledgeCategoryDTO = om.readValue(
            restKnowledgeCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeCategoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KnowledgeCategoryDTO.class
        );

        // Validate the KnowledgeCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKnowledgeCategory = knowledgeCategoryMapper.toEntity(returnedKnowledgeCategoryDTO);
        assertKnowledgeCategoryUpdatableFieldsEquals(returnedKnowledgeCategory, getPersistedKnowledgeCategory(returnedKnowledgeCategory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedKnowledgeCategory = returnedKnowledgeCategory;
    }

    @Test
    @Transactional
    void createKnowledgeCategoryWithExistingId() throws Exception {
        // Create the KnowledgeCategory with an existing ID
        knowledgeCategory.setId(1L);
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restKnowledgeCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        // set the field null
        knowledgeCategory.setName(null);

        // Create the KnowledgeCategory, which fails.
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        restKnowledgeCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        // set the field null
        knowledgeCategory.setSlug(null);

        // Create the KnowledgeCategory, which fails.
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        restKnowledgeCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllKnowledgeCategories() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);

        // Get all the knowledgeCategoryList
        restKnowledgeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)));
    }

    @Test
    @Transactional
    void getKnowledgeCategory() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);

        // Get the knowledgeCategory
        restKnowledgeCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, knowledgeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(knowledgeCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG));
    }

    @Test
    @Transactional
    void getNonExistingKnowledgeCategory() throws Exception {
        // Get the knowledgeCategory
        restKnowledgeCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKnowledgeCategory() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeCategorySearchRepository.save(knowledgeCategory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());

        // Update the knowledgeCategory
        KnowledgeCategory updatedKnowledgeCategory = knowledgeCategoryRepository.findById(knowledgeCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKnowledgeCategory are not directly saved in db
        em.detach(updatedKnowledgeCategory);
        updatedKnowledgeCategory.name(UPDATED_NAME).slug(UPDATED_SLUG);
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(updatedKnowledgeCategory);

        restKnowledgeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKnowledgeCategoryToMatchAllProperties(updatedKnowledgeCategory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<KnowledgeCategory> knowledgeCategorySearchList = Streamable.of(knowledgeCategorySearchRepository.findAll()).toList();
                KnowledgeCategory testKnowledgeCategorySearch = knowledgeCategorySearchList.get(searchDatabaseSizeAfter - 1);

                assertKnowledgeCategoryAllPropertiesEquals(testKnowledgeCategorySearch, updatedKnowledgeCategory);
            });
    }

    @Test
    @Transactional
    void putNonExistingKnowledgeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        knowledgeCategory.setId(longCount.incrementAndGet());

        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchKnowledgeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        knowledgeCategory.setId(longCount.incrementAndGet());

        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKnowledgeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        knowledgeCategory.setId(longCount.incrementAndGet());

        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateKnowledgeCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeCategory using partial update
        KnowledgeCategory partialUpdatedKnowledgeCategory = new KnowledgeCategory();
        partialUpdatedKnowledgeCategory.setId(knowledgeCategory.getId());

        partialUpdatedKnowledgeCategory.slug(UPDATED_SLUG);

        restKnowledgeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeCategory))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedKnowledgeCategory, knowledgeCategory),
            getPersistedKnowledgeCategory(knowledgeCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdateKnowledgeCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeCategory using partial update
        KnowledgeCategory partialUpdatedKnowledgeCategory = new KnowledgeCategory();
        partialUpdatedKnowledgeCategory.setId(knowledgeCategory.getId());

        partialUpdatedKnowledgeCategory.name(UPDATED_NAME).slug(UPDATED_SLUG);

        restKnowledgeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeCategory))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeCategoryUpdatableFieldsEquals(
            partialUpdatedKnowledgeCategory,
            getPersistedKnowledgeCategory(partialUpdatedKnowledgeCategory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingKnowledgeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        knowledgeCategory.setId(longCount.incrementAndGet());

        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, knowledgeCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKnowledgeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        knowledgeCategory.setId(longCount.incrementAndGet());

        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKnowledgeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        knowledgeCategory.setId(longCount.incrementAndGet());

        // Create the KnowledgeCategory
        KnowledgeCategoryDTO knowledgeCategoryDTO = knowledgeCategoryMapper.toDto(knowledgeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(knowledgeCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteKnowledgeCategory() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);
        knowledgeCategoryRepository.save(knowledgeCategory);
        knowledgeCategorySearchRepository.save(knowledgeCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the knowledgeCategory
        restKnowledgeCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, knowledgeCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(knowledgeCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchKnowledgeCategory() throws Exception {
        // Initialize the database
        insertedKnowledgeCategory = knowledgeCategoryRepository.saveAndFlush(knowledgeCategory);
        knowledgeCategorySearchRepository.save(knowledgeCategory);

        // Search the knowledgeCategory
        restKnowledgeCategoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + knowledgeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)));
    }

    protected long getRepositoryCount() {
        return knowledgeCategoryRepository.count();
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

    protected KnowledgeCategory getPersistedKnowledgeCategory(KnowledgeCategory knowledgeCategory) {
        return knowledgeCategoryRepository.findById(knowledgeCategory.getId()).orElseThrow();
    }

    protected void assertPersistedKnowledgeCategoryToMatchAllProperties(KnowledgeCategory expectedKnowledgeCategory) {
        assertKnowledgeCategoryAllPropertiesEquals(expectedKnowledgeCategory, getPersistedKnowledgeCategory(expectedKnowledgeCategory));
    }

    protected void assertPersistedKnowledgeCategoryToMatchUpdatableProperties(KnowledgeCategory expectedKnowledgeCategory) {
        assertKnowledgeCategoryAllUpdatablePropertiesEquals(
            expectedKnowledgeCategory,
            getPersistedKnowledgeCategory(expectedKnowledgeCategory)
        );
    }
}
