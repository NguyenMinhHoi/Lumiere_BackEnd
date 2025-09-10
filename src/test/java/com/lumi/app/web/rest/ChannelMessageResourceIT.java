package com.lumi.app.web.rest;

import static com.lumi.app.domain.ChannelMessageAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.ChannelMessage;
import com.lumi.app.domain.enumeration.MessageDirection;
import com.lumi.app.repository.ChannelMessageRepository;
import com.lumi.app.repository.UserRepository;
import com.lumi.app.repository.search.ChannelMessageSearchRepository;
import com.lumi.app.service.ChannelMessageService;
import com.lumi.app.service.dto.ChannelMessageDTO;
import com.lumi.app.service.mapper.ChannelMessageMapper;
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
 * Integration tests for the {@link ChannelMessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChannelMessageResourceIT {

    private static final MessageDirection DEFAULT_DIRECTION = MessageDirection.INBOUND;
    private static final MessageDirection UPDATED_DIRECTION = MessageDirection.OUTBOUND;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_EXTERNAL_MESSAGE_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_MESSAGE_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/channel-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/channel-messages/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChannelMessageRepository channelMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ChannelMessageRepository channelMessageRepositoryMock;

    @Autowired
    private ChannelMessageMapper channelMessageMapper;

    @Mock
    private ChannelMessageService channelMessageServiceMock;

    @Autowired
    private ChannelMessageSearchRepository channelMessageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChannelMessageMockMvc;

    private ChannelMessage channelMessage;

    private ChannelMessage insertedChannelMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChannelMessage createEntity() {
        return new ChannelMessage()
            .direction(DEFAULT_DIRECTION)
            .content(DEFAULT_CONTENT)
            .sentAt(DEFAULT_SENT_AT)
            .externalMessageId(DEFAULT_EXTERNAL_MESSAGE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChannelMessage createUpdatedEntity() {
        return new ChannelMessage()
            .direction(UPDATED_DIRECTION)
            .content(UPDATED_CONTENT)
            .sentAt(UPDATED_SENT_AT)
            .externalMessageId(UPDATED_EXTERNAL_MESSAGE_ID);
    }

    @BeforeEach
    void initTest() {
        channelMessage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChannelMessage != null) {
            channelMessageRepository.delete(insertedChannelMessage);
            channelMessageSearchRepository.delete(insertedChannelMessage);
            insertedChannelMessage = null;
        }
    }

    @Test
    @Transactional
    void createChannelMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);
        var returnedChannelMessageDTO = om.readValue(
            restChannelMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channelMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChannelMessageDTO.class
        );

        // Validate the ChannelMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChannelMessage = channelMessageMapper.toEntity(returnedChannelMessageDTO);
        assertChannelMessageUpdatableFieldsEquals(returnedChannelMessage, getPersistedChannelMessage(returnedChannelMessage));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedChannelMessage = returnedChannelMessage;
    }

    @Test
    @Transactional
    void createChannelMessageWithExistingId() throws Exception {
        // Create the ChannelMessage with an existing ID
        channelMessage.setId(1L);
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restChannelMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channelMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDirectionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        // set the field null
        channelMessage.setDirection(null);

        // Create the ChannelMessage, which fails.
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        restChannelMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channelMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSentAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        // set the field null
        channelMessage.setSentAt(null);

        // Create the ChannelMessage, which fails.
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        restChannelMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channelMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllChannelMessages() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);

        // Get all the channelMessageList
        restChannelMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(channelMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].direction").value(hasItem(DEFAULT_DIRECTION.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].externalMessageId").value(hasItem(DEFAULT_EXTERNAL_MESSAGE_ID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChannelMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(channelMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChannelMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(channelMessageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChannelMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(channelMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChannelMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(channelMessageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChannelMessage() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);

        // Get the channelMessage
        restChannelMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, channelMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(channelMessage.getId().intValue()))
            .andExpect(jsonPath("$.direction").value(DEFAULT_DIRECTION.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.externalMessageId").value(DEFAULT_EXTERNAL_MESSAGE_ID));
    }

    @Test
    @Transactional
    void getNonExistingChannelMessage() throws Exception {
        // Get the channelMessage
        restChannelMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChannelMessage() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        channelMessageSearchRepository.save(channelMessage);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());

        // Update the channelMessage
        ChannelMessage updatedChannelMessage = channelMessageRepository.findById(channelMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChannelMessage are not directly saved in db
        em.detach(updatedChannelMessage);
        updatedChannelMessage
            .direction(UPDATED_DIRECTION)
            .content(UPDATED_CONTENT)
            .sentAt(UPDATED_SENT_AT)
            .externalMessageId(UPDATED_EXTERNAL_MESSAGE_ID);
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(updatedChannelMessage);

        restChannelMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, channelMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(channelMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChannelMessageToMatchAllProperties(updatedChannelMessage);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ChannelMessage> channelMessageSearchList = Streamable.of(channelMessageSearchRepository.findAll()).toList();
                ChannelMessage testChannelMessageSearch = channelMessageSearchList.get(searchDatabaseSizeAfter - 1);

                assertChannelMessageAllPropertiesEquals(testChannelMessageSearch, updatedChannelMessage);
            });
    }

    @Test
    @Transactional
    void putNonExistingChannelMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        channelMessage.setId(longCount.incrementAndGet());

        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChannelMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, channelMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(channelMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchChannelMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        channelMessage.setId(longCount.incrementAndGet());

        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(channelMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChannelMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        channelMessage.setId(longCount.incrementAndGet());

        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channelMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateChannelMessageWithPatch() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the channelMessage using partial update
        ChannelMessage partialUpdatedChannelMessage = new ChannelMessage();
        partialUpdatedChannelMessage.setId(channelMessage.getId());

        partialUpdatedChannelMessage.content(UPDATED_CONTENT);

        restChannelMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChannelMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChannelMessage))
            )
            .andExpect(status().isOk());

        // Validate the ChannelMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChannelMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChannelMessage, channelMessage),
            getPersistedChannelMessage(channelMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateChannelMessageWithPatch() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the channelMessage using partial update
        ChannelMessage partialUpdatedChannelMessage = new ChannelMessage();
        partialUpdatedChannelMessage.setId(channelMessage.getId());

        partialUpdatedChannelMessage
            .direction(UPDATED_DIRECTION)
            .content(UPDATED_CONTENT)
            .sentAt(UPDATED_SENT_AT)
            .externalMessageId(UPDATED_EXTERNAL_MESSAGE_ID);

        restChannelMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChannelMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChannelMessage))
            )
            .andExpect(status().isOk());

        // Validate the ChannelMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChannelMessageUpdatableFieldsEquals(partialUpdatedChannelMessage, getPersistedChannelMessage(partialUpdatedChannelMessage));
    }

    @Test
    @Transactional
    void patchNonExistingChannelMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        channelMessage.setId(longCount.incrementAndGet());

        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChannelMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, channelMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(channelMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChannelMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        channelMessage.setId(longCount.incrementAndGet());

        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(channelMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChannelMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        channelMessage.setId(longCount.incrementAndGet());

        // Create the ChannelMessage
        ChannelMessageDTO channelMessageDTO = channelMessageMapper.toDto(channelMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(channelMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChannelMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteChannelMessage() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);
        channelMessageRepository.save(channelMessage);
        channelMessageSearchRepository.save(channelMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the channelMessage
        restChannelMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, channelMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(channelMessageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchChannelMessage() throws Exception {
        // Initialize the database
        insertedChannelMessage = channelMessageRepository.saveAndFlush(channelMessage);
        channelMessageSearchRepository.save(channelMessage);

        // Search the channelMessage
        restChannelMessageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + channelMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(channelMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].direction").value(hasItem(DEFAULT_DIRECTION.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].externalMessageId").value(hasItem(DEFAULT_EXTERNAL_MESSAGE_ID)));
    }

    protected long getRepositoryCount() {
        return channelMessageRepository.count();
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

    protected ChannelMessage getPersistedChannelMessage(ChannelMessage channelMessage) {
        return channelMessageRepository.findById(channelMessage.getId()).orElseThrow();
    }

    protected void assertPersistedChannelMessageToMatchAllProperties(ChannelMessage expectedChannelMessage) {
        assertChannelMessageAllPropertiesEquals(expectedChannelMessage, getPersistedChannelMessage(expectedChannelMessage));
    }

    protected void assertPersistedChannelMessageToMatchUpdatableProperties(ChannelMessage expectedChannelMessage) {
        assertChannelMessageAllUpdatablePropertiesEquals(expectedChannelMessage, getPersistedChannelMessage(expectedChannelMessage));
    }
}
