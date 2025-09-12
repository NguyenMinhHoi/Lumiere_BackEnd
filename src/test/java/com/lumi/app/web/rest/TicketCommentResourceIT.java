package com.lumi.app.web.rest;

import static com.lumi.app.domain.TicketCommentAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.TicketComment;
import com.lumi.app.domain.enumeration.Visibility;
import com.lumi.app.repository.TicketCommentRepository;
import com.lumi.app.repository.search.TicketCommentSearchRepository;
import com.lumi.app.service.dto.TicketCommentDTO;
import com.lumi.app.service.mapper.TicketCommentMapper;
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
 * Integration tests for the {@link TicketCommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketCommentResourceIT {

    private static final Long DEFAULT_TICKET_ID = 1L;
    private static final Long UPDATED_TICKET_ID = 2L;

    private static final Long DEFAULT_AUTHOR_ID = 1L;
    private static final Long UPDATED_AUTHOR_ID = 2L;

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Visibility DEFAULT_VISIBILITY = Visibility.PUBLIC;
    private static final Visibility UPDATED_VISIBILITY = Visibility.INTERNAL;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/ticket-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/ticket-comments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Autowired
    private TicketCommentMapper ticketCommentMapper;

    @Autowired
    private TicketCommentSearchRepository ticketCommentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketCommentMockMvc;

    private TicketComment ticketComment;

    private TicketComment insertedTicketComment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketComment createEntity() {
        return new TicketComment()
            .ticketId(DEFAULT_TICKET_ID)
            .authorId(DEFAULT_AUTHOR_ID)
            .body(DEFAULT_BODY)
            .visibility(DEFAULT_VISIBILITY)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketComment createUpdatedEntity() {
        return new TicketComment()
            .ticketId(UPDATED_TICKET_ID)
            .authorId(UPDATED_AUTHOR_ID)
            .body(UPDATED_BODY)
            .visibility(UPDATED_VISIBILITY)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        ticketComment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketComment != null) {
            ticketCommentRepository.delete(insertedTicketComment);
            ticketCommentSearchRepository.delete(insertedTicketComment);
            insertedTicketComment = null;
        }
    }

    @Test
    @Transactional
    void createTicketComment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);
        var returnedTicketCommentDTO = om.readValue(
            restTicketCommentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketCommentDTO.class
        );

        // Validate the TicketComment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTicketComment = ticketCommentMapper.toEntity(returnedTicketCommentDTO);
        assertTicketCommentUpdatableFieldsEquals(returnedTicketComment, getPersistedTicketComment(returnedTicketComment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTicketComment = returnedTicketComment;
    }

    @Test
    @Transactional
    void createTicketCommentWithExistingId() throws Exception {
        // Create the TicketComment with an existing ID
        ticketComment.setId(1L);
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTicketIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        // set the field null
        ticketComment.setTicketId(null);

        // Create the TicketComment, which fails.
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        restTicketCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAuthorIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        // set the field null
        ticketComment.setAuthorId(null);

        // Create the TicketComment, which fails.
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        restTicketCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkVisibilityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        // set the field null
        ticketComment.setVisibility(null);

        // Create the TicketComment, which fails.
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        restTicketCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        // set the field null
        ticketComment.setCreatedAt(null);

        // Create the TicketComment, which fails.
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        restTicketCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTicketComments() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);

        // Get all the ticketCommentList
        restTicketCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getTicketComment() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);

        // Get the ticketComment
        restTicketCommentMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketComment.getId().intValue()))
            .andExpect(jsonPath("$.ticketId").value(DEFAULT_TICKET_ID.intValue()))
            .andExpect(jsonPath("$.authorId").value(DEFAULT_AUTHOR_ID.intValue()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTicketComment() throws Exception {
        // Get the ticketComment
        restTicketCommentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketComment() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCommentSearchRepository.save(ticketComment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());

        // Update the ticketComment
        TicketComment updatedTicketComment = ticketCommentRepository.findById(ticketComment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketComment are not directly saved in db
        em.detach(updatedTicketComment);
        updatedTicketComment
            .ticketId(UPDATED_TICKET_ID)
            .authorId(UPDATED_AUTHOR_ID)
            .body(UPDATED_BODY)
            .visibility(UPDATED_VISIBILITY)
            .createdAt(UPDATED_CREATED_AT);
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(updatedTicketComment);

        restTicketCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketCommentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCommentDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketCommentToMatchAllProperties(updatedTicketComment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TicketComment> ticketCommentSearchList = Streamable.of(ticketCommentSearchRepository.findAll()).toList();
                TicketComment testTicketCommentSearch = ticketCommentSearchList.get(searchDatabaseSizeAfter - 1);

                assertTicketCommentAllPropertiesEquals(testTicketCommentSearch, updatedTicketComment);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicketComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        ticketComment.setId(longCount.incrementAndGet());

        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketCommentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        ticketComment.setId(longCount.incrementAndGet());

        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        ticketComment.setId(longCount.incrementAndGet());

        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCommentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketCommentWithPatch() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketComment using partial update
        TicketComment partialUpdatedTicketComment = new TicketComment();
        partialUpdatedTicketComment.setId(ticketComment.getId());

        partialUpdatedTicketComment.body(UPDATED_BODY).visibility(UPDATED_VISIBILITY).createdAt(UPDATED_CREATED_AT);

        restTicketCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketComment))
            )
            .andExpect(status().isOk());

        // Validate the TicketComment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketCommentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketComment, ticketComment),
            getPersistedTicketComment(ticketComment)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketCommentWithPatch() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketComment using partial update
        TicketComment partialUpdatedTicketComment = new TicketComment();
        partialUpdatedTicketComment.setId(ticketComment.getId());

        partialUpdatedTicketComment
            .ticketId(UPDATED_TICKET_ID)
            .authorId(UPDATED_AUTHOR_ID)
            .body(UPDATED_BODY)
            .visibility(UPDATED_VISIBILITY)
            .createdAt(UPDATED_CREATED_AT);

        restTicketCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketComment))
            )
            .andExpect(status().isOk());

        // Validate the TicketComment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketCommentUpdatableFieldsEquals(partialUpdatedTicketComment, getPersistedTicketComment(partialUpdatedTicketComment));
    }

    @Test
    @Transactional
    void patchNonExistingTicketComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        ticketComment.setId(longCount.incrementAndGet());

        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketCommentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        ticketComment.setId(longCount.incrementAndGet());

        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        ticketComment.setId(longCount.incrementAndGet());

        // Create the TicketComment
        TicketCommentDTO ticketCommentDTO = ticketCommentMapper.toDto(ticketComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCommentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketCommentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicketComment() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);
        ticketCommentRepository.save(ticketComment);
        ticketCommentSearchRepository.save(ticketComment);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticketComment
        restTicketCommentMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketComment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketCommentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicketComment() throws Exception {
        // Initialize the database
        insertedTicketComment = ticketCommentRepository.saveAndFlush(ticketComment);
        ticketCommentSearchRepository.save(ticketComment);

        // Search the ticketComment
        restTicketCommentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticketComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return ticketCommentRepository.count();
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

    protected TicketComment getPersistedTicketComment(TicketComment ticketComment) {
        return ticketCommentRepository.findById(ticketComment.getId()).orElseThrow();
    }

    protected void assertPersistedTicketCommentToMatchAllProperties(TicketComment expectedTicketComment) {
        assertTicketCommentAllPropertiesEquals(expectedTicketComment, getPersistedTicketComment(expectedTicketComment));
    }

    protected void assertPersistedTicketCommentToMatchUpdatableProperties(TicketComment expectedTicketComment) {
        assertTicketCommentAllUpdatablePropertiesEquals(expectedTicketComment, getPersistedTicketComment(expectedTicketComment));
    }
}
