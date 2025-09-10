package com.lumi.app.web.rest;

import static com.lumi.app.domain.SurveyQuestionAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.SurveyQuestion;
import com.lumi.app.domain.enumeration.QuestionType;
import com.lumi.app.repository.SurveyQuestionRepository;
import com.lumi.app.repository.search.SurveyQuestionSearchRepository;
import com.lumi.app.service.SurveyQuestionService;
import com.lumi.app.service.dto.SurveyQuestionDTO;
import com.lumi.app.service.mapper.SurveyQuestionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link SurveyQuestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SurveyQuestionResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final QuestionType DEFAULT_QUESTION_TYPE = QuestionType.SCALE;
    private static final QuestionType UPDATED_QUESTION_TYPE = QuestionType.SINGLE_CHOICE;

    private static final Integer DEFAULT_SCALE_MIN = 1;
    private static final Integer UPDATED_SCALE_MIN = 2;

    private static final Integer DEFAULT_SCALE_MAX = 1;
    private static final Integer UPDATED_SCALE_MAX = 2;

    private static final Boolean DEFAULT_IS_NEED = false;
    private static final Boolean UPDATED_IS_NEED = true;

    private static final Integer DEFAULT_ORDER_NO = 1;
    private static final Integer UPDATED_ORDER_NO = 2;

    private static final String ENTITY_API_URL = "/api/survey-questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/survey-questions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SurveyQuestionRepository surveyQuestionRepository;

    @Mock
    private SurveyQuestionRepository surveyQuestionRepositoryMock;

    @Autowired
    private SurveyQuestionMapper surveyQuestionMapper;

    @Mock
    private SurveyQuestionService surveyQuestionServiceMock;

    @Autowired
    private SurveyQuestionSearchRepository surveyQuestionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSurveyQuestionMockMvc;

    private SurveyQuestion surveyQuestion;

    private SurveyQuestion insertedSurveyQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyQuestion createEntity() {
        return new SurveyQuestion()
            .text(DEFAULT_TEXT)
            .questionType(DEFAULT_QUESTION_TYPE)
            .scaleMin(DEFAULT_SCALE_MIN)
            .scaleMax(DEFAULT_SCALE_MAX)
            .isNeed(DEFAULT_IS_NEED)
            .orderNo(DEFAULT_ORDER_NO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyQuestion createUpdatedEntity() {
        return new SurveyQuestion()
            .text(UPDATED_TEXT)
            .questionType(UPDATED_QUESTION_TYPE)
            .scaleMin(UPDATED_SCALE_MIN)
            .scaleMax(UPDATED_SCALE_MAX)
            .isNeed(UPDATED_IS_NEED)
            .orderNo(UPDATED_ORDER_NO);
    }

    @BeforeEach
    void initTest() {
        surveyQuestion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSurveyQuestion != null) {
            surveyQuestionRepository.delete(insertedSurveyQuestion);
            surveyQuestionSearchRepository.delete(insertedSurveyQuestion);
            insertedSurveyQuestion = null;
        }
    }

    @Test
    @Transactional
    void createSurveyQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);
        var returnedSurveyQuestionDTO = om.readValue(
            restSurveyQuestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SurveyQuestionDTO.class
        );

        // Validate the SurveyQuestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSurveyQuestion = surveyQuestionMapper.toEntity(returnedSurveyQuestionDTO);
        assertSurveyQuestionUpdatableFieldsEquals(returnedSurveyQuestion, getPersistedSurveyQuestion(returnedSurveyQuestion));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSurveyQuestion = returnedSurveyQuestion;
    }

    @Test
    @Transactional
    void createSurveyQuestionWithExistingId() throws Exception {
        // Create the SurveyQuestion with an existing ID
        surveyQuestion.setId(1L);
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveyQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        // set the field null
        surveyQuestion.setText(null);

        // Create the SurveyQuestion, which fails.
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        restSurveyQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuestionTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        // set the field null
        surveyQuestion.setQuestionType(null);

        // Create the SurveyQuestion, which fails.
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        restSurveyQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsNeedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        // set the field null
        surveyQuestion.setIsNeed(null);

        // Create the SurveyQuestion, which fails.
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        restSurveyQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOrderNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        // set the field null
        surveyQuestion.setOrderNo(null);

        // Create the SurveyQuestion, which fails.
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        restSurveyQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSurveyQuestions() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);

        // Get all the surveyQuestionList
        restSurveyQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveyQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].questionType").value(hasItem(DEFAULT_QUESTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].scaleMin").value(hasItem(DEFAULT_SCALE_MIN)))
            .andExpect(jsonPath("$.[*].scaleMax").value(hasItem(DEFAULT_SCALE_MAX)))
            .andExpect(jsonPath("$.[*].isNeed").value(hasItem(DEFAULT_IS_NEED)))
            .andExpect(jsonPath("$.[*].orderNo").value(hasItem(DEFAULT_ORDER_NO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSurveyQuestionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(surveyQuestionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSurveyQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(surveyQuestionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSurveyQuestionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(surveyQuestionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSurveyQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(surveyQuestionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSurveyQuestion() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);

        // Get the surveyQuestion
        restSurveyQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, surveyQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(surveyQuestion.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.questionType").value(DEFAULT_QUESTION_TYPE.toString()))
            .andExpect(jsonPath("$.scaleMin").value(DEFAULT_SCALE_MIN))
            .andExpect(jsonPath("$.scaleMax").value(DEFAULT_SCALE_MAX))
            .andExpect(jsonPath("$.isNeed").value(DEFAULT_IS_NEED))
            .andExpect(jsonPath("$.orderNo").value(DEFAULT_ORDER_NO));
    }

    @Test
    @Transactional
    void getNonExistingSurveyQuestion() throws Exception {
        // Get the surveyQuestion
        restSurveyQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSurveyQuestion() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        surveyQuestionSearchRepository.save(surveyQuestion);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());

        // Update the surveyQuestion
        SurveyQuestion updatedSurveyQuestion = surveyQuestionRepository.findById(surveyQuestion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSurveyQuestion are not directly saved in db
        em.detach(updatedSurveyQuestion);
        updatedSurveyQuestion
            .text(UPDATED_TEXT)
            .questionType(UPDATED_QUESTION_TYPE)
            .scaleMin(UPDATED_SCALE_MIN)
            .scaleMax(UPDATED_SCALE_MAX)
            .isNeed(UPDATED_IS_NEED)
            .orderNo(UPDATED_ORDER_NO);
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(updatedSurveyQuestion);

        restSurveyQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyQuestionDTO))
            )
            .andExpect(status().isOk());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSurveyQuestionToMatchAllProperties(updatedSurveyQuestion);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SurveyQuestion> surveyQuestionSearchList = Streamable.of(surveyQuestionSearchRepository.findAll()).toList();
                SurveyQuestion testSurveyQuestionSearch = surveyQuestionSearchList.get(searchDatabaseSizeAfter - 1);

                assertSurveyQuestionAllPropertiesEquals(testSurveyQuestionSearch, updatedSurveyQuestion);
            });
    }

    @Test
    @Transactional
    void putNonExistingSurveyQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        surveyQuestion.setId(longCount.incrementAndGet());

        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSurveyQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        surveyQuestion.setId(longCount.incrementAndGet());

        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(surveyQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSurveyQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        surveyQuestion.setId(longCount.incrementAndGet());

        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSurveyQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the surveyQuestion using partial update
        SurveyQuestion partialUpdatedSurveyQuestion = new SurveyQuestion();
        partialUpdatedSurveyQuestion.setId(surveyQuestion.getId());

        partialUpdatedSurveyQuestion.questionType(UPDATED_QUESTION_TYPE).scaleMin(UPDATED_SCALE_MIN);

        restSurveyQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSurveyQuestion))
            )
            .andExpect(status().isOk());

        // Validate the SurveyQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSurveyQuestionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSurveyQuestion, surveyQuestion),
            getPersistedSurveyQuestion(surveyQuestion)
        );
    }

    @Test
    @Transactional
    void fullUpdateSurveyQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the surveyQuestion using partial update
        SurveyQuestion partialUpdatedSurveyQuestion = new SurveyQuestion();
        partialUpdatedSurveyQuestion.setId(surveyQuestion.getId());

        partialUpdatedSurveyQuestion
            .text(UPDATED_TEXT)
            .questionType(UPDATED_QUESTION_TYPE)
            .scaleMin(UPDATED_SCALE_MIN)
            .scaleMax(UPDATED_SCALE_MAX)
            .isNeed(UPDATED_IS_NEED)
            .orderNo(UPDATED_ORDER_NO);

        restSurveyQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSurveyQuestion))
            )
            .andExpect(status().isOk());

        // Validate the SurveyQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSurveyQuestionUpdatableFieldsEquals(partialUpdatedSurveyQuestion, getPersistedSurveyQuestion(partialUpdatedSurveyQuestion));
    }

    @Test
    @Transactional
    void patchNonExistingSurveyQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        surveyQuestion.setId(longCount.incrementAndGet());

        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, surveyQuestionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(surveyQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSurveyQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        surveyQuestion.setId(longCount.incrementAndGet());

        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(surveyQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSurveyQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        surveyQuestion.setId(longCount.incrementAndGet());

        // Create the SurveyQuestion
        SurveyQuestionDTO surveyQuestionDTO = surveyQuestionMapper.toDto(surveyQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(surveyQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveyQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSurveyQuestion() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);
        surveyQuestionRepository.save(surveyQuestion);
        surveyQuestionSearchRepository.save(surveyQuestion);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the surveyQuestion
        restSurveyQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, surveyQuestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(surveyQuestionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSurveyQuestion() throws Exception {
        // Initialize the database
        insertedSurveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);
        surveyQuestionSearchRepository.save(surveyQuestion);

        // Search the surveyQuestion
        restSurveyQuestionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + surveyQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveyQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].questionType").value(hasItem(DEFAULT_QUESTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].scaleMin").value(hasItem(DEFAULT_SCALE_MIN)))
            .andExpect(jsonPath("$.[*].scaleMax").value(hasItem(DEFAULT_SCALE_MAX)))
            .andExpect(jsonPath("$.[*].isNeed").value(hasItem(DEFAULT_IS_NEED)))
            .andExpect(jsonPath("$.[*].orderNo").value(hasItem(DEFAULT_ORDER_NO)));
    }

    protected long getRepositoryCount() {
        return surveyQuestionRepository.count();
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

    protected SurveyQuestion getPersistedSurveyQuestion(SurveyQuestion surveyQuestion) {
        return surveyQuestionRepository.findById(surveyQuestion.getId()).orElseThrow();
    }

    protected void assertPersistedSurveyQuestionToMatchAllProperties(SurveyQuestion expectedSurveyQuestion) {
        assertSurveyQuestionAllPropertiesEquals(expectedSurveyQuestion, getPersistedSurveyQuestion(expectedSurveyQuestion));
    }

    protected void assertPersistedSurveyQuestionToMatchUpdatableProperties(SurveyQuestion expectedSurveyQuestion) {
        assertSurveyQuestionAllUpdatablePropertiesEquals(expectedSurveyQuestion, getPersistedSurveyQuestion(expectedSurveyQuestion));
    }
}
