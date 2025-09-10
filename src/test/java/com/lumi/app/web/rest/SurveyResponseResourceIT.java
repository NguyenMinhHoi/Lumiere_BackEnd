package com.lumi.app.web.rest;

import static com.lumi.app.domain.SurveyResponseAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.SurveyResponse;
import com.lumi.app.repository.SurveyResponseRepository;
import com.lumi.app.repository.search.SurveyResponseSearchRepository;
import com.lumi.app.service.SurveyResponseService;
import com.lumi.app.service.dto.SurveyResponseDTO;
import com.lumi.app.service.mapper.SurveyResponseMapper;
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
 * Integration tests for the {@link SurveyResponseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SurveyResponseResourceIT {

    private static final Instant DEFAULT_RESPONDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESPONDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/survey-responses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/survey-responses/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Mock
    private SurveyResponseRepository surveyResponseRepositoryMock;

    @Autowired
    private SurveyResponseMapper surveyResponseMapper;

    @Mock
    private SurveyResponseService surveyResponseServiceMock;

    @Autowired
    private SurveyResponseSearchRepository surveyResponseSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSurveyResponseMockMvc;

    private SurveyResponse surveyResponse;

    private SurveyResponse insertedSurveyResponse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyResponse createEntity() {
        return new SurveyResponse().respondedAt(DEFAULT_RESPONDED_AT).score(DEFAULT_SCORE).comment(DEFAULT_COMMENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyResponse createUpdatedEntity() {
        return new SurveyResponse().respondedAt(UPDATED_RESPONDED_AT).score(UPDATED_SCORE).comment(UPDATED_COMMENT);
    }

    @BeforeEach
    void initTest() {
        surveyResponse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSurveyResponse != null) {
            surveyResponseRepository.delete(insertedSurveyResponse);
            surveyResponseSearchRepository.delete(insertedSurveyResponse);
            insertedSurveyResponse = null;
        }
    }

    @Test
    @Transactional
    void createSurveyResponse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);
        var returnedSurveyResponseDTO = om.readValue(
            restSurveyResponseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyResponseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SurveyResponseDTO.class
        );

        // Validate the SurveyResponse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSurveyResponse = surveyResponseMapper.toEntity(returnedSurveyResponseDTO);
        assertSurveyResponseUpdatableFieldsEquals(returnedSurveyResponse, getPersistedSurveyResponse(returnedSurveyResponse));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSurveyResponse = returnedSurveyResponse;
    }

    @Test
    @Transactional
    void createSurveyResponseWithExistingId() throws Exception {
        // Create the SurveyResponse with an existing ID
        surveyResponse.setId(1L);
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveyResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyResponseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRespondedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        // set the field null
        surveyResponse.setRespondedAt(null);

        // Create the SurveyResponse, which fails.
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        restSurveyResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyResponseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSurveyResponses() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);

        // Get all the surveyResponseList
        restSurveyResponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveyResponse.getId().intValue())))
            .andExpect(jsonPath("$.[*].respondedAt").value(hasItem(DEFAULT_RESPONDED_AT.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSurveyResponsesWithEagerRelationshipsIsEnabled() throws Exception {
        when(surveyResponseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSurveyResponseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(surveyResponseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSurveyResponsesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(surveyResponseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSurveyResponseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(surveyResponseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSurveyResponse() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);

        // Get the surveyResponse
        restSurveyResponseMockMvc
            .perform(get(ENTITY_API_URL_ID, surveyResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(surveyResponse.getId().intValue()))
            .andExpect(jsonPath("$.respondedAt").value(DEFAULT_RESPONDED_AT.toString()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT));
    }

    @Test
    @Transactional
    void getNonExistingSurveyResponse() throws Exception {
        // Get the surveyResponse
        restSurveyResponseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSurveyResponse() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        surveyResponseSearchRepository.save(surveyResponse);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());

        // Update the surveyResponse
        SurveyResponse updatedSurveyResponse = surveyResponseRepository.findById(surveyResponse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSurveyResponse are not directly saved in db
        em.detach(updatedSurveyResponse);
        updatedSurveyResponse.respondedAt(UPDATED_RESPONDED_AT).score(UPDATED_SCORE).comment(UPDATED_COMMENT);
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(updatedSurveyResponse);

        restSurveyResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyResponseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyResponseDTO))
            )
            .andExpect(status().isOk());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSurveyResponseToMatchAllProperties(updatedSurveyResponse);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SurveyResponse> surveyResponseSearchList = Streamable.of(surveyResponseSearchRepository.findAll()).toList();
                SurveyResponse testSurveyResponseSearch = surveyResponseSearchList.get(searchDatabaseSizeAfter - 1);

                assertSurveyResponseAllPropertiesEquals(testSurveyResponseSearch, updatedSurveyResponse);
            });
    }

    @Test
    @Transactional
    void putNonExistingSurveyResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        surveyResponse.setId(longCount.incrementAndGet());

        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyResponseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSurveyResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        surveyResponse.setId(longCount.incrementAndGet());

        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSurveyResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        surveyResponse.setId(longCount.incrementAndGet());

        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyResponseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyResponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSurveyResponseWithPatch() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the surveyResponse using partial update
        SurveyResponse partialUpdatedSurveyResponse = new SurveyResponse();
        partialUpdatedSurveyResponse.setId(surveyResponse.getId());

        partialUpdatedSurveyResponse.respondedAt(UPDATED_RESPONDED_AT).comment(UPDATED_COMMENT);

        restSurveyResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSurveyResponse))
            )
            .andExpect(status().isOk());

        // Validate the SurveyResponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSurveyResponseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSurveyResponse, surveyResponse),
            getPersistedSurveyResponse(surveyResponse)
        );
    }

    @Test
    @Transactional
    void fullUpdateSurveyResponseWithPatch() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the surveyResponse using partial update
        SurveyResponse partialUpdatedSurveyResponse = new SurveyResponse();
        partialUpdatedSurveyResponse.setId(surveyResponse.getId());

        partialUpdatedSurveyResponse.respondedAt(UPDATED_RESPONDED_AT).score(UPDATED_SCORE).comment(UPDATED_COMMENT);

        restSurveyResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSurveyResponse))
            )
            .andExpect(status().isOk());

        // Validate the SurveyResponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSurveyResponseUpdatableFieldsEquals(partialUpdatedSurveyResponse, getPersistedSurveyResponse(partialUpdatedSurveyResponse));
    }

    @Test
    @Transactional
    void patchNonExistingSurveyResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        surveyResponse.setId(longCount.incrementAndGet());

        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, surveyResponseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(surveyResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSurveyResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        surveyResponse.setId(longCount.incrementAndGet());

        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(surveyResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSurveyResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        surveyResponse.setId(longCount.incrementAndGet());

        // Create the SurveyResponse
        SurveyResponseDTO surveyResponseDTO = surveyResponseMapper.toDto(surveyResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyResponseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(surveyResponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveyResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSurveyResponse() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);
        surveyResponseRepository.save(surveyResponse);
        surveyResponseSearchRepository.save(surveyResponse);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the surveyResponse
        restSurveyResponseMockMvc
            .perform(delete(ENTITY_API_URL_ID, surveyResponse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSurveyResponse() throws Exception {
        // Initialize the database
        insertedSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);
        surveyResponseSearchRepository.save(surveyResponse);

        // Search the surveyResponse
        restSurveyResponseMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + surveyResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveyResponse.getId().intValue())))
            .andExpect(jsonPath("$.[*].respondedAt").value(hasItem(DEFAULT_RESPONDED_AT.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    protected long getRepositoryCount() {
        return surveyResponseRepository.count();
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

    protected SurveyResponse getPersistedSurveyResponse(SurveyResponse surveyResponse) {
        return surveyResponseRepository.findById(surveyResponse.getId()).orElseThrow();
    }

    protected void assertPersistedSurveyResponseToMatchAllProperties(SurveyResponse expectedSurveyResponse) {
        assertSurveyResponseAllPropertiesEquals(expectedSurveyResponse, getPersistedSurveyResponse(expectedSurveyResponse));
    }

    protected void assertPersistedSurveyResponseToMatchUpdatableProperties(SurveyResponse expectedSurveyResponse) {
        assertSurveyResponseAllUpdatablePropertiesEquals(expectedSurveyResponse, getPersistedSurveyResponse(expectedSurveyResponse));
    }
}
