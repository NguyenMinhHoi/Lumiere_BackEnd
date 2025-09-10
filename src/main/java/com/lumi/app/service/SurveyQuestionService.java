package com.lumi.app.service;

import com.lumi.app.domain.SurveyQuestion;
import com.lumi.app.repository.SurveyQuestionRepository;
import com.lumi.app.repository.search.SurveyQuestionSearchRepository;
import com.lumi.app.service.dto.SurveyQuestionDTO;
import com.lumi.app.service.mapper.SurveyQuestionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.SurveyQuestion}.
 */
@Service
@Transactional
public class SurveyQuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyQuestionService.class);

    private final SurveyQuestionRepository surveyQuestionRepository;

    private final SurveyQuestionMapper surveyQuestionMapper;

    private final SurveyQuestionSearchRepository surveyQuestionSearchRepository;

    public SurveyQuestionService(
        SurveyQuestionRepository surveyQuestionRepository,
        SurveyQuestionMapper surveyQuestionMapper,
        SurveyQuestionSearchRepository surveyQuestionSearchRepository
    ) {
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyQuestionMapper = surveyQuestionMapper;
        this.surveyQuestionSearchRepository = surveyQuestionSearchRepository;
    }

    /**
     * Save a surveyQuestion.
     *
     * @param surveyQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyQuestionDTO save(SurveyQuestionDTO surveyQuestionDTO) {
        LOG.debug("Request to save SurveyQuestion : {}", surveyQuestionDTO);
        SurveyQuestion surveyQuestion = surveyQuestionMapper.toEntity(surveyQuestionDTO);
        surveyQuestion = surveyQuestionRepository.save(surveyQuestion);
        surveyQuestionSearchRepository.index(surveyQuestion);
        return surveyQuestionMapper.toDto(surveyQuestion);
    }

    /**
     * Update a surveyQuestion.
     *
     * @param surveyQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyQuestionDTO update(SurveyQuestionDTO surveyQuestionDTO) {
        LOG.debug("Request to update SurveyQuestion : {}", surveyQuestionDTO);
        SurveyQuestion surveyQuestion = surveyQuestionMapper.toEntity(surveyQuestionDTO);
        surveyQuestion = surveyQuestionRepository.save(surveyQuestion);
        surveyQuestionSearchRepository.index(surveyQuestion);
        return surveyQuestionMapper.toDto(surveyQuestion);
    }

    /**
     * Partially update a surveyQuestion.
     *
     * @param surveyQuestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SurveyQuestionDTO> partialUpdate(SurveyQuestionDTO surveyQuestionDTO) {
        LOG.debug("Request to partially update SurveyQuestion : {}", surveyQuestionDTO);

        return surveyQuestionRepository
            .findById(surveyQuestionDTO.getId())
            .map(existingSurveyQuestion -> {
                surveyQuestionMapper.partialUpdate(existingSurveyQuestion, surveyQuestionDTO);

                return existingSurveyQuestion;
            })
            .map(surveyQuestionRepository::save)
            .map(savedSurveyQuestion -> {
                surveyQuestionSearchRepository.index(savedSurveyQuestion);
                return savedSurveyQuestion;
            })
            .map(surveyQuestionMapper::toDto);
    }

    /**
     * Get all the surveyQuestions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SurveyQuestionDTO> findAll() {
        LOG.debug("Request to get all SurveyQuestions");
        return surveyQuestionRepository
            .findAll()
            .stream()
            .map(surveyQuestionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the surveyQuestions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SurveyQuestionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return surveyQuestionRepository.findAllWithEagerRelationships(pageable).map(surveyQuestionMapper::toDto);
    }

    /**
     * Get one surveyQuestion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SurveyQuestionDTO> findOne(Long id) {
        LOG.debug("Request to get SurveyQuestion : {}", id);
        return surveyQuestionRepository.findOneWithEagerRelationships(id).map(surveyQuestionMapper::toDto);
    }

    /**
     * Delete the surveyQuestion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SurveyQuestion : {}", id);
        surveyQuestionRepository.deleteById(id);
        surveyQuestionSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the surveyQuestion corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SurveyQuestionDTO> search(String query) {
        LOG.debug("Request to search SurveyQuestions for query {}", query);
        try {
            return StreamSupport.stream(surveyQuestionSearchRepository.search(query).spliterator(), false)
                .map(surveyQuestionMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
