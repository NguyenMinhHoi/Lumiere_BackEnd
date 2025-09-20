package com.lumi.app.web.rest;

import static com.lumi.app.domain.TicketTagAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.TicketTag;
import com.lumi.app.repository.TicketTagRepository;
import com.lumi.app.repository.search.TicketTagSearchRepository;
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
 * Integration tests for the {@link TicketTagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketTagResourceIT {

    private static final Long DEFAULT_TICKET_ID = 1L;
    private static final Long UPDATED_TICKET_ID = 2L;

    private static final Long DEFAULT_TAG_ID = 1L;
    private static final Long UPDATED_TAG_ID = 2L;

    private static final String ENTITY_API_URL = "/api/ticket-tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/ticket-tags/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketTagRepository ticketTagRepository;

    @Autowired
    private TicketTagSearchRepository ticketTagSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketTagMockMvc;

    private TicketTag ticketTag;

    private TicketTag insertedTicketTag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketTag createEntity() {
        return new TicketTag().ticketId(DEFAULT_TICKET_ID).tagId(DEFAULT_TAG_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketTag createUpdatedEntity() {
        return new TicketTag().ticketId(UPDATED_TICKET_ID).tagId(UPDATED_TAG_ID);
    }

    @BeforeEach
    void initTest() {
        ticketTag = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketTag != null) {
            ticketTagRepository.delete(insertedTicketTag);
            ticketTagSearchRepository.delete(insertedTicketTag);
            insertedTicketTag = null;
        }
    }

    @Test
    @Transactional
    void createTicketTag() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        // Create the TicketTag
        var returnedTicketTag = om.readValue(
            restTicketTagMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTag)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketTag.class
        );

        // Validate the TicketTag in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTicketTagUpdatableFieldsEquals(returnedTicketTag, getPersistedTicketTag(returnedTicketTag));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTicketTag = returnedTicketTag;
    }

    @Test
    @Transactional
    void createTicketTagWithExistingId() throws Exception {
        // Create the TicketTag with an existing ID
        ticketTag.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTag)))
            .andExpect(status().isBadRequest());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTicketIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        // set the field null
        ticketTag.setTicketId(null);

        // Create the TicketTag, which fails.

        restTicketTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTag)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTagIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        // set the field null
        ticketTag.setTagId(null);

        // Create the TicketTag, which fails.

        restTicketTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTag)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTicketTags() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);

        // Get all the ticketTagList
        restTicketTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].tagId").value(hasItem(DEFAULT_TAG_ID.intValue())));
    }

    @Test
    @Transactional
    void getTicketTag() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);

        // Get the ticketTag
        restTicketTagMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketTag.getId().intValue()))
            .andExpect(jsonPath("$.ticketId").value(DEFAULT_TICKET_ID.intValue()))
            .andExpect(jsonPath("$.tagId").value(DEFAULT_TAG_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingTicketTag() throws Exception {
        // Get the ticketTag
        restTicketTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketTag() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketTagSearchRepository.save(ticketTag);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());

        // Update the ticketTag
        TicketTag updatedTicketTag = ticketTagRepository.findById(ticketTag.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketTag are not directly saved in db
        em.detach(updatedTicketTag);
        updatedTicketTag.ticketId(UPDATED_TICKET_ID).tagId(UPDATED_TAG_ID);

        restTicketTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTicketTag.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTicketTag))
            )
            .andExpect(status().isOk());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketTagToMatchAllProperties(updatedTicketTag);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TicketTag> ticketTagSearchList = Streamable.of(ticketTagSearchRepository.findAll()).toList();
                TicketTag testTicketTagSearch = ticketTagSearchList.get(searchDatabaseSizeAfter - 1);

                assertTicketTagAllPropertiesEquals(testTicketTagSearch, updatedTicketTag);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicketTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        ticketTag.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketTag.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        ticketTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        ticketTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketTagWithPatch() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketTag using partial update
        TicketTag partialUpdatedTicketTag = new TicketTag();
        partialUpdatedTicketTag.setId(ticketTag.getId());

        partialUpdatedTicketTag.ticketId(UPDATED_TICKET_ID).tagId(UPDATED_TAG_ID);

        restTicketTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketTag))
            )
            .andExpect(status().isOk());

        // Validate the TicketTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketTagUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketTag, ticketTag),
            getPersistedTicketTag(ticketTag)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketTagWithPatch() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketTag using partial update
        TicketTag partialUpdatedTicketTag = new TicketTag();
        partialUpdatedTicketTag.setId(ticketTag.getId());

        partialUpdatedTicketTag.ticketId(UPDATED_TICKET_ID).tagId(UPDATED_TAG_ID);

        restTicketTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketTag))
            )
            .andExpect(status().isOk());

        // Validate the TicketTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketTagUpdatableFieldsEquals(partialUpdatedTicketTag, getPersistedTicketTag(partialUpdatedTicketTag));
    }

    @Test
    @Transactional
    void patchNonExistingTicketTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        ticketTag.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        ticketTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        ticketTag.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTagMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicketTag() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);
        ticketTagRepository.save(ticketTag);
        ticketTagSearchRepository.save(ticketTag);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticketTag
        restTicketTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketTag.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketTagSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicketTag() throws Exception {
        // Initialize the database
        insertedTicketTag = ticketTagRepository.saveAndFlush(ticketTag);
        ticketTagSearchRepository.save(ticketTag);

        // Search the ticketTag
        restTicketTagMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticketTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].tagId").value(hasItem(DEFAULT_TAG_ID.intValue())));
    }

    protected long getRepositoryCount() {
        return ticketTagRepository.count();
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

    protected TicketTag getPersistedTicketTag(TicketTag ticketTag) {
        return ticketTagRepository.findById(ticketTag.getId()).orElseThrow();
    }

    protected void assertPersistedTicketTagToMatchAllProperties(TicketTag expectedTicketTag) {
        assertTicketTagAllPropertiesEquals(expectedTicketTag, getPersistedTicketTag(expectedTicketTag));
    }

    protected void assertPersistedTicketTagToMatchUpdatableProperties(TicketTag expectedTicketTag) {
        assertTicketTagAllUpdatablePropertiesEquals(expectedTicketTag, getPersistedTicketTag(expectedTicketTag));
    }
}
