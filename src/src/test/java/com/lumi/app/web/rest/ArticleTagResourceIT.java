package com.lumi.app.web.rest;

import static com.lumi.app.domain.ArticleTagAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ArticleTag;
import com.lumi.app.repository.ArticleTagRepository;
import com.lumi.app.repository.search.ArticleTagSearchRepository;
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
 * Integration tests for the {@link ArticleTagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArticleTagResourceIT {

    private static final Long DEFAULT_ARTICLE_ID = 1L;
    private static final Long UPDATED_ARTICLE_ID = 2L;

    private static final Long DEFAULT_TAG_ID = 1L;
    private static final Long UPDATED_TAG_ID = 2L;

    private static final String ENTITY_API_URL = "/api/article-tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/article-tags/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArticleTagRepository articleTagRepository;

    @Autowired
    private ArticleTagSearchRepository articleTagSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArticleTagMockMvc;

    private ArticleTag articleTag;

    private ArticleTag insertedArticleTag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ArticleTag createEntity() {
        return new ArticleTag().articleId(DEFAULT_ARTICLE_ID).tagId(DEFAULT_TAG_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ArticleTag createUpdatedEntity() {
        return new ArticleTag().articleId(UPDATED_ARTICLE_ID).tagId(UPDATED_TAG_ID);
    }

    @BeforeEach
    void initTest() {
        articleTag = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedArticleTag != null) {
            articleTagRepository.delete(insertedArticleTag);
            articleTagSearchRepository.delete(insertedArticleTag);
            insertedArticleTag = null;
        }
    }

    @Test
    @Transactional
    void createArticleTag() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        // Create the ArticleTag
        var returnedArticleTag = om.readValue(
            restArticleTagMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleTag)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ArticleTag.class
        );

        // Validate the ArticleTag in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertArticleTagUpdatableFieldsEquals(returnedArticleTag, getPersistedArticleTag(returnedArticleTag));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedArticleTag = returnedArticleTag;
    }

    @Test
    @Transactional
    void createArticleTagWithExistingId() throws Exception {
        // Create the ArticleTag with an existing ID
        articleTag.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleTag)))
            .andExpect(status().isBadRequest());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkArticleIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        // set the field null
        articleTag.setArticleId(null);

        // Create the ArticleTag, which fails.

        restArticleTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleTag)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTagIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        // set the field null
        articleTag.setTagId(null);

        // Create the ArticleTag, which fails.

        restArticleTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleTag)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllArticleTags() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);

        // Get all the articleTagList
        restArticleTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(articleTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].articleId").value(hasItem(DEFAULT_ARTICLE_ID.intValue())))
            .andExpect(jsonPath("$.[*].tagId").value(hasItem(DEFAULT_TAG_ID.intValue())));
    }

    @Test
    @Transactional
    void getArticleTag() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);

        // Get the articleTag
        restArticleTagMockMvc
            .perform(get(ENTITY_API_URL_ID, articleTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(articleTag.getId().intValue()))
            .andExpect(jsonPath("$.articleId").value(DEFAULT_ARTICLE_ID.intValue()))
            .andExpect(jsonPath("$.tagId").value(DEFAULT_TAG_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingArticleTag() throws Exception {
        // Get the articleTag
        restArticleTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingArticleTag() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        articleTagSearchRepository.save(articleTag);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());

        // Update the articleTag
        ArticleTag updatedArticleTag = articleTagRepository.findById(articleTag.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedArticleTag are not directly saved in db
        em.detach(updatedArticleTag);
        updatedArticleTag.articleId(UPDATED_ARTICLE_ID).tagId(UPDATED_TAG_ID);

        restArticleTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedArticleTag.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedArticleTag))
            )
            .andExpect(status().isOk());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedArticleTagToMatchAllProperties(updatedArticleTag);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ArticleTag> articleTagSearchList = Streamable.of(articleTagSearchRepository.findAll()).toList();
                ArticleTag testArticleTagSearch = articleTagSearchList.get(searchDatabaseSizeAfter - 1);

                assertArticleTagAllPropertiesEquals(testArticleTagSearch, updatedArticleTag);
            });
    }

    @Test
    @Transactional
    void putNonExistingArticleTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        articleTag.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, articleTag.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchArticleTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        articleTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(articleTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArticleTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        articleTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateArticleTagWithPatch() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the articleTag using partial update
        ArticleTag partialUpdatedArticleTag = new ArticleTag();
        partialUpdatedArticleTag.setId(articleTag.getId());

        restArticleTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticleTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArticleTag))
            )
            .andExpect(status().isOk());

        // Validate the ArticleTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArticleTagUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedArticleTag, articleTag),
            getPersistedArticleTag(articleTag)
        );
    }

    @Test
    @Transactional
    void fullUpdateArticleTagWithPatch() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the articleTag using partial update
        ArticleTag partialUpdatedArticleTag = new ArticleTag();
        partialUpdatedArticleTag.setId(articleTag.getId());

        partialUpdatedArticleTag.articleId(UPDATED_ARTICLE_ID).tagId(UPDATED_TAG_ID);

        restArticleTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticleTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArticleTag))
            )
            .andExpect(status().isOk());

        // Validate the ArticleTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArticleTagUpdatableFieldsEquals(partialUpdatedArticleTag, getPersistedArticleTag(partialUpdatedArticleTag));
    }

    @Test
    @Transactional
    void patchNonExistingArticleTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        articleTag.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, articleTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(articleTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArticleTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        articleTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(articleTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArticleTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        articleTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleTagMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(articleTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ArticleTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteArticleTag() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);
        articleTagRepository.save(articleTag);
        articleTagSearchRepository.save(articleTag);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the articleTag
        restArticleTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, articleTag.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(articleTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchArticleTag() throws Exception {
        // Initialize the database
        insertedArticleTag = articleTagRepository.saveAndFlush(articleTag);
        articleTagSearchRepository.save(articleTag);

        // Search the articleTag
        restArticleTagMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + articleTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(articleTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].articleId").value(hasItem(DEFAULT_ARTICLE_ID.intValue())))
            .andExpect(jsonPath("$.[*].tagId").value(hasItem(DEFAULT_TAG_ID.intValue())));
    }

    protected long getRepositoryCount() {
        return articleTagRepository.count();
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

    protected ArticleTag getPersistedArticleTag(ArticleTag articleTag) {
        return articleTagRepository.findById(articleTag.getId()).orElseThrow();
    }

    protected void assertPersistedArticleTagToMatchAllProperties(ArticleTag expectedArticleTag) {
        assertArticleTagAllPropertiesEquals(expectedArticleTag, getPersistedArticleTag(expectedArticleTag));
    }

    protected void assertPersistedArticleTagToMatchUpdatableProperties(ArticleTag expectedArticleTag) {
        assertArticleTagAllUpdatablePropertiesEquals(expectedArticleTag, getPersistedArticleTag(expectedArticleTag));
    }
}
