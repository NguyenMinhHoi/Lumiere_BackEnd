package com.lumi.app.service;

import com.lumi.app.service.dto.SurveyQuestionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.SurveyQuestion}.
 */
public interface SurveyQuestionService {
    /**
     * Save a surveyQuestion.
     *
     * @param surveyQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    SurveyQuestionDTO save(SurveyQuestionDTO surveyQuestionDTO);

    /**
     * Updates a surveyQuestion.
     *
     * @param surveyQuestionDTO the entity to update.
     * @return the persisted entity.
     */
    SurveyQuestionDTO update(SurveyQuestionDTO surveyQuestionDTO);

    /**
     * Partially updates a surveyQuestion.
     *
     * @param surveyQuestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SurveyQuestionDTO> partialUpdate(SurveyQuestionDTO surveyQuestionDTO);

    /**
     * Get all the surveyQuestions.
     *
     * @return the list of entities.
     */
    List<SurveyQuestionDTO> findAll();

    /**
     * Get the "id" surveyQuestion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SurveyQuestionDTO> findOne(Long id);

    /**
     * Delete the "id" surveyQuestion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the surveyQuestion corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<SurveyQuestionDTO> search(String query);
}
