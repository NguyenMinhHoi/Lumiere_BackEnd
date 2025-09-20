package com.lumi.app.web.rest;

import static com.lumi.app.domain.IntegrationWebhookAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.IntegrationWebhook;
import com.lumi.app.repository.IntegrationWebhookRepository;
import com.lumi.app.repository.search.IntegrationWebhookSearchRepository;
import com.lumi.app.service.dto.IntegrationWebhookDTO;
import com.lumi.app.service.mapper.IntegrationWebhookMapper;
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
 * Integration tests for the {@link IntegrationWebhookResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IntegrationWebhookResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SECRET = "AAAAAAAAAA";
    private static final String UPDATED_SECRET = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_SUBSCRIBED_EVENTS = "AAAAAAAAAA";
    private static final String UPDATED_SUBSCRIBED_EVENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/integration-webhooks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/integration-webhooks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IntegrationWebhookRepository integrationWebhookRepository;

    @Autowired
    private IntegrationWebhookMapper integrationWebhookMapper;

    @Autowired
    private IntegrationWebhookSearchRepository integrationWebhookSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIntegrationWebhookMockMvc;

    private IntegrationWebhook integrationWebhook;

    private IntegrationWebhook insertedIntegrationWebhook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntegrationWebhook createEntity() {
        return new IntegrationWebhook()
            .name(DEFAULT_NAME)
            .targetUrl(DEFAULT_TARGET_URL)
            .secret(DEFAULT_SECRET)
            .isActive(DEFAULT_IS_ACTIVE)
            .subscribedEvents(DEFAULT_SUBSCRIBED_EVENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntegrationWebhook createUpdatedEntity() {
        return new IntegrationWebhook()
            .name(UPDATED_NAME)
            .targetUrl(UPDATED_TARGET_URL)
            .secret(UPDATED_SECRET)
            .isActive(UPDATED_IS_ACTIVE)
            .subscribedEvents(UPDATED_SUBSCRIBED_EVENTS);
    }

    @BeforeEach
    void initTest() {
        integrationWebhook = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIntegrationWebhook != null) {
            integrationWebhookRepository.delete(insertedIntegrationWebhook);
            integrationWebhookSearchRepository.delete(insertedIntegrationWebhook);
            insertedIntegrationWebhook = null;
        }
    }

    @Test
    @Transactional
    void createIntegrationWebhook() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);
        var returnedIntegrationWebhookDTO = om.readValue(
            restIntegrationWebhookMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationWebhookDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IntegrationWebhookDTO.class
        );

        // Validate the IntegrationWebhook in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIntegrationWebhook = integrationWebhookMapper.toEntity(returnedIntegrationWebhookDTO);
        assertIntegrationWebhookUpdatableFieldsEquals(
            returnedIntegrationWebhook,
            getPersistedIntegrationWebhook(returnedIntegrationWebhook)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedIntegrationWebhook = returnedIntegrationWebhook;
    }

    @Test
    @Transactional
    void createIntegrationWebhookWithExistingId() throws Exception {
        // Create the IntegrationWebhook with an existing ID
        integrationWebhook.setId(1L);
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntegrationWebhookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationWebhookDTO)))
            .andExpect(status().isBadRequest());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        // set the field null
        integrationWebhook.setName(null);

        // Create the IntegrationWebhook, which fails.
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        restIntegrationWebhookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationWebhookDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTargetUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        // set the field null
        integrationWebhook.setTargetUrl(null);

        // Create the IntegrationWebhook, which fails.
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        restIntegrationWebhookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationWebhookDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        // set the field null
        integrationWebhook.setIsActive(null);

        // Create the IntegrationWebhook, which fails.
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        restIntegrationWebhookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationWebhookDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllIntegrationWebhooks() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);

        // Get all the integrationWebhookList
        restIntegrationWebhookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integrationWebhook.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].subscribedEvents").value(hasItem(DEFAULT_SUBSCRIBED_EVENTS)));
    }

    @Test
    @Transactional
    void getIntegrationWebhook() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);

        // Get the integrationWebhook
        restIntegrationWebhookMockMvc
            .perform(get(ENTITY_API_URL_ID, integrationWebhook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(integrationWebhook.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.targetUrl").value(DEFAULT_TARGET_URL))
            .andExpect(jsonPath("$.secret").value(DEFAULT_SECRET))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.subscribedEvents").value(DEFAULT_SUBSCRIBED_EVENTS));
    }

    @Test
    @Transactional
    void getNonExistingIntegrationWebhook() throws Exception {
        // Get the integrationWebhook
        restIntegrationWebhookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIntegrationWebhook() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrationWebhookSearchRepository.save(integrationWebhook);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());

        // Update the integrationWebhook
        IntegrationWebhook updatedIntegrationWebhook = integrationWebhookRepository.findById(integrationWebhook.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIntegrationWebhook are not directly saved in db
        em.detach(updatedIntegrationWebhook);
        updatedIntegrationWebhook
            .name(UPDATED_NAME)
            .targetUrl(UPDATED_TARGET_URL)
            .secret(UPDATED_SECRET)
            .isActive(UPDATED_IS_ACTIVE)
            .subscribedEvents(UPDATED_SUBSCRIBED_EVENTS);
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(updatedIntegrationWebhook);

        restIntegrationWebhookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, integrationWebhookDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(integrationWebhookDTO))
            )
            .andExpect(status().isOk());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIntegrationWebhookToMatchAllProperties(updatedIntegrationWebhook);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<IntegrationWebhook> integrationWebhookSearchList = Streamable.of(
                    integrationWebhookSearchRepository.findAll()
                ).toList();
                IntegrationWebhook testIntegrationWebhookSearch = integrationWebhookSearchList.get(searchDatabaseSizeAfter - 1);

                assertIntegrationWebhookAllPropertiesEquals(testIntegrationWebhookSearch, updatedIntegrationWebhook);
            });
    }

    @Test
    @Transactional
    void putNonExistingIntegrationWebhook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        integrationWebhook.setId(longCount.incrementAndGet());

        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntegrationWebhookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, integrationWebhookDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(integrationWebhookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchIntegrationWebhook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        integrationWebhook.setId(longCount.incrementAndGet());

        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationWebhookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(integrationWebhookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIntegrationWebhook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        integrationWebhook.setId(longCount.incrementAndGet());

        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationWebhookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(integrationWebhookDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateIntegrationWebhookWithPatch() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrationWebhook using partial update
        IntegrationWebhook partialUpdatedIntegrationWebhook = new IntegrationWebhook();
        partialUpdatedIntegrationWebhook.setId(integrationWebhook.getId());

        partialUpdatedIntegrationWebhook.targetUrl(UPDATED_TARGET_URL).secret(UPDATED_SECRET).subscribedEvents(UPDATED_SUBSCRIBED_EVENTS);

        restIntegrationWebhookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntegrationWebhook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntegrationWebhook))
            )
            .andExpect(status().isOk());

        // Validate the IntegrationWebhook in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntegrationWebhookUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIntegrationWebhook, integrationWebhook),
            getPersistedIntegrationWebhook(integrationWebhook)
        );
    }

    @Test
    @Transactional
    void fullUpdateIntegrationWebhookWithPatch() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrationWebhook using partial update
        IntegrationWebhook partialUpdatedIntegrationWebhook = new IntegrationWebhook();
        partialUpdatedIntegrationWebhook.setId(integrationWebhook.getId());

        partialUpdatedIntegrationWebhook
            .name(UPDATED_NAME)
            .targetUrl(UPDATED_TARGET_URL)
            .secret(UPDATED_SECRET)
            .isActive(UPDATED_IS_ACTIVE)
            .subscribedEvents(UPDATED_SUBSCRIBED_EVENTS);

        restIntegrationWebhookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntegrationWebhook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntegrationWebhook))
            )
            .andExpect(status().isOk());

        // Validate the IntegrationWebhook in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntegrationWebhookUpdatableFieldsEquals(
            partialUpdatedIntegrationWebhook,
            getPersistedIntegrationWebhook(partialUpdatedIntegrationWebhook)
        );
    }

    @Test
    @Transactional
    void patchNonExistingIntegrationWebhook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        integrationWebhook.setId(longCount.incrementAndGet());

        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntegrationWebhookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, integrationWebhookDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(integrationWebhookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIntegrationWebhook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        integrationWebhook.setId(longCount.incrementAndGet());

        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationWebhookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(integrationWebhookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIntegrationWebhook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        integrationWebhook.setId(longCount.incrementAndGet());

        // Create the IntegrationWebhook
        IntegrationWebhookDTO integrationWebhookDTO = integrationWebhookMapper.toDto(integrationWebhook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntegrationWebhookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(integrationWebhookDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntegrationWebhook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteIntegrationWebhook() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);
        integrationWebhookRepository.save(integrationWebhook);
        integrationWebhookSearchRepository.save(integrationWebhook);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the integrationWebhook
        restIntegrationWebhookMockMvc
            .perform(delete(ENTITY_API_URL_ID, integrationWebhook.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(integrationWebhookSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchIntegrationWebhook() throws Exception {
        // Initialize the database
        insertedIntegrationWebhook = integrationWebhookRepository.saveAndFlush(integrationWebhook);
        integrationWebhookSearchRepository.save(integrationWebhook);

        // Search the integrationWebhook
        restIntegrationWebhookMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + integrationWebhook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integrationWebhook.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].subscribedEvents").value(hasItem(DEFAULT_SUBSCRIBED_EVENTS)));
    }

    protected long getRepositoryCount() {
        return integrationWebhookRepository.count();
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

    protected IntegrationWebhook getPersistedIntegrationWebhook(IntegrationWebhook integrationWebhook) {
        return integrationWebhookRepository.findById(integrationWebhook.getId()).orElseThrow();
    }

    protected void assertPersistedIntegrationWebhookToMatchAllProperties(IntegrationWebhook expectedIntegrationWebhook) {
        assertIntegrationWebhookAllPropertiesEquals(expectedIntegrationWebhook, getPersistedIntegrationWebhook(expectedIntegrationWebhook));
    }

    protected void assertPersistedIntegrationWebhookToMatchUpdatableProperties(IntegrationWebhook expectedIntegrationWebhook) {
        assertIntegrationWebhookAllUpdatablePropertiesEquals(
            expectedIntegrationWebhook,
            getPersistedIntegrationWebhook(expectedIntegrationWebhook)
        );
    }
}
