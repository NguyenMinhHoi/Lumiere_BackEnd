package com.lumi.app.web.rest;

import static com.lumi.app.domain.SurveyAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Survey;
import com.lumi.app.domain.enumeration.SurveyType;
import com.lumi.app.repository.SurveyRepository;
import com.lumi.app.repository.search.SurveySearchRepository;
import com.lumi.app.service.dto.SurveyDTO;
import com.lumi.app.service.mapper.SurveyMapper;
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
 * Integration tests for the {@link SurveyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SurveyResourceIT {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;

    private static final SurveyType DEFAULT_SURVEY_TYPE = SurveyType.CSAT;
    private static final SurveyType UPDATED_SURVEY_TYPE = SurveyType.NPS;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DUE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/surveys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/surveys/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private SurveySearchRepository surveySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSurveyMockMvc;

    private Survey survey;

    private Survey insertedSurvey;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Survey createEntity() {
        return new Survey()
            .customerId(DEFAULT_CUSTOMER_ID)
            .surveyType(DEFAULT_SURVEY_TYPE)
            .title(DEFAULT_TITLE)
            .sentAt(DEFAULT_SENT_AT)
            .dueAt(DEFAULT_DUE_AT)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Survey createUpdatedEntity() {
        return new Survey()
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyType(UPDATED_SURVEY_TYPE)
            .title(UPDATED_TITLE)
            .sentAt(UPDATED_SENT_AT)
            .dueAt(UPDATED_DUE_AT)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        survey = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSurvey != null) {
            surveyRepository.delete(insertedSurvey);
            surveySearchRepository.delete(insertedSurvey);
            insertedSurvey = null;
        }
    }

    @Test
    @Transactional
    void createSurvey() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);
        var returnedSurveyDTO = om.readValue(
            restSurveyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SurveyDTO.class
        );

        // Validate the Survey in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSurvey = surveyMapper.toEntity(returnedSurveyDTO);
        assertSurveyUpdatableFieldsEquals(returnedSurvey, getPersistedSurvey(returnedSurvey));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSurvey = returnedSurvey;
    }

    @Test
    @Transactional
    void createSurveyWithExistingId() throws Exception {
        // Create the Survey with an existing ID
        survey.setId(1L);
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSurveyTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        // set the field null
        survey.setSurveyType(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        restSurveyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        // set the field null
        survey.setTitle(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        restSurveyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        // set the field null
        survey.setIsActive(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        restSurveyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSurveys() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);

        // Get all the surveyList
        restSurveyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(survey.getId().intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].surveyType").value(hasItem(DEFAULT_SURVEY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].dueAt").value(hasItem(DEFAULT_DUE_AT.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getSurvey() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);

        // Get the survey
        restSurveyMockMvc
            .perform(get(ENTITY_API_URL_ID, survey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(survey.getId().intValue()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID.intValue()))
            .andExpect(jsonPath("$.surveyType").value(DEFAULT_SURVEY_TYPE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.dueAt").value(DEFAULT_DUE_AT.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingSurvey() throws Exception {
        // Get the survey
        restSurveyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSurvey() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        surveySearchRepository.save(survey);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());

        // Update the survey
        Survey updatedSurvey = surveyRepository.findById(survey.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSurvey are not directly saved in db
        em.detach(updatedSurvey);
        updatedSurvey
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyType(UPDATED_SURVEY_TYPE)
            .title(UPDATED_TITLE)
            .sentAt(UPDATED_SENT_AT)
            .dueAt(UPDATED_DUE_AT)
            .isActive(UPDATED_IS_ACTIVE);
        SurveyDTO surveyDTO = surveyMapper.toDto(updatedSurvey);

        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSurveyToMatchAllProperties(updatedSurvey);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Survey> surveySearchList = Streamable.of(surveySearchRepository.findAll()).toList();
                Survey testSurveySearch = surveySearchList.get(searchDatabaseSizeAfter - 1);

                assertSurveyAllPropertiesEquals(testSurveySearch, updatedSurvey);
            });
    }

    @Test
    @Transactional
    void putNonExistingSurvey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        survey.setId(longCount.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSurvey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        survey.setId(longCount.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSurvey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        survey.setId(longCount.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSurveyWithPatch() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the survey using partial update
        Survey partialUpdatedSurvey = new Survey();
        partialUpdatedSurvey.setId(survey.getId());

        partialUpdatedSurvey.surveyType(UPDATED_SURVEY_TYPE).sentAt(UPDATED_SENT_AT).isActive(UPDATED_IS_ACTIVE);

        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurvey.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSurvey))
            )
            .andExpect(status().isOk());

        // Validate the Survey in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSurveyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSurvey, survey), getPersistedSurvey(survey));
    }

    @Test
    @Transactional
    void fullUpdateSurveyWithPatch() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the survey using partial update
        Survey partialUpdatedSurvey = new Survey();
        partialUpdatedSurvey.setId(survey.getId());

        partialUpdatedSurvey
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyType(UPDATED_SURVEY_TYPE)
            .title(UPDATED_TITLE)
            .sentAt(UPDATED_SENT_AT)
            .dueAt(UPDATED_DUE_AT)
            .isActive(UPDATED_IS_ACTIVE);

        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurvey.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSurvey))
            )
            .andExpect(status().isOk());

        // Validate the Survey in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSurveyUpdatableFieldsEquals(partialUpdatedSurvey, getPersistedSurvey(partialUpdatedSurvey));
    }

    @Test
    @Transactional
    void patchNonExistingSurvey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        survey.setId(longCount.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, surveyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSurvey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        survey.setId(longCount.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSurvey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        survey.setId(longCount.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(surveyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Survey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSurvey() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);
        surveyRepository.save(survey);
        surveySearchRepository.save(survey);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the survey
        restSurveyMockMvc
            .perform(delete(ENTITY_API_URL_ID, survey.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSurvey() throws Exception {
        // Initialize the database
        insertedSurvey = surveyRepository.saveAndFlush(survey);
        surveySearchRepository.save(survey);

        // Search the survey
        restSurveyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + survey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(survey.getId().intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].surveyType").value(hasItem(DEFAULT_SURVEY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].dueAt").value(hasItem(DEFAULT_DUE_AT.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    protected long getRepositoryCount() {
        return surveyRepository.count();
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

    protected Survey getPersistedSurvey(Survey survey) {
        return surveyRepository.findById(survey.getId()).orElseThrow();
    }

    protected void assertPersistedSurveyToMatchAllProperties(Survey expectedSurvey) {
        assertSurveyAllPropertiesEquals(expectedSurvey, getPersistedSurvey(expectedSurvey));
    }

    protected void assertPersistedSurveyToMatchUpdatableProperties(Survey expectedSurvey) {
        assertSurveyAllUpdatablePropertiesEquals(expectedSurvey, getPersistedSurvey(expectedSurvey));
    }
}
