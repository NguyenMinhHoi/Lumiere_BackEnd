package com.lumi.app.service.impl;

import com.lumi.app.domain.SurveyQuestion;
import com.lumi.app.repository.SurveyQuestionRepository;
import com.lumi.app.repository.search.SurveyQuestionSearchRepository;
import com.lumi.app.service.SurveyQuestionService;
import com.lumi.app.service.dto.SurveyQuestionDTO;
import com.lumi.app.service.mapper.SurveyQuestionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link SurveyQuestion}.
 */
@Service
@Transactional
public class SurveyQuestionServiceImpl implements SurveyQuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyQuestionServiceImpl.class);

    private final SurveyQuestionRepository surveyQuestionRepository;

    private final SurveyQuestionMapper surveyQuestionMapper;

    private final SurveyQuestionSearchRepository surveyQuestionSearchRepository;

    public SurveyQuestionServiceImpl(
        SurveyQuestionRepository surveyQuestionRepository,
        SurveyQuestionMapper surveyQuestionMapper,
        SurveyQuestionSearchRepository surveyQuestionSearchRepository
    ) {
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyQuestionMapper = surveyQuestionMapper;
        this.surveyQuestionSearchRepository = surveyQuestionSearchRepository;
    }

    @Override
    public SurveyQuestionDTO save(SurveyQuestionDTO surveyQuestionDTO) {
        LOG.debug("Request to save SurveyQuestion : {}", surveyQuestionDTO);
        SurveyQuestion surveyQuestion = surveyQuestionMapper.toEntity(surveyQuestionDTO);
        surveyQuestion = surveyQuestionRepository.save(surveyQuestion);
        surveyQuestionSearchRepository.index(surveyQuestion);
        return surveyQuestionMapper.toDto(surveyQuestion);
    }

    @Override
    public SurveyQuestionDTO update(SurveyQuestionDTO surveyQuestionDTO) {
        LOG.debug("Request to update SurveyQuestion : {}", surveyQuestionDTO);
        SurveyQuestion surveyQuestion = surveyQuestionMapper.toEntity(surveyQuestionDTO);
        surveyQuestion = surveyQuestionRepository.save(surveyQuestion);
        surveyQuestionSearchRepository.index(surveyQuestion);
        return surveyQuestionMapper.toDto(surveyQuestion);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<SurveyQuestionDTO> findAll() {
        LOG.debug("Request to get all SurveyQuestions");
        return surveyQuestionRepository
            .findAll()
            .stream()
            .map(surveyQuestionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurveyQuestionDTO> findOne(Long id) {
        LOG.debug("Request to get SurveyQuestion : {}", id);
        return surveyQuestionRepository.findById(id).map(surveyQuestionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SurveyQuestion : {}", id);
        surveyQuestionRepository.deleteById(id);
        surveyQuestionSearchRepository.deleteFromIndexById(id);
    }

    @Override
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
