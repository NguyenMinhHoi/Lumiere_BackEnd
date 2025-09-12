package com.lumi.app.service.impl;

import com.lumi.app.domain.Survey;
import com.lumi.app.repository.SurveyRepository;
import com.lumi.app.repository.search.SurveySearchRepository;
import com.lumi.app.service.SurveyService;
import com.lumi.app.service.dto.SurveyDTO;
import com.lumi.app.service.mapper.SurveyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Survey}.
 */
@Service
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyServiceImpl.class);

    private final SurveyRepository surveyRepository;

    private final SurveyMapper surveyMapper;

    private final SurveySearchRepository surveySearchRepository;

    public SurveyServiceImpl(SurveyRepository surveyRepository, SurveyMapper surveyMapper, SurveySearchRepository surveySearchRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.surveySearchRepository = surveySearchRepository;
    }

    @Override
    public SurveyDTO save(SurveyDTO surveyDTO) {
        LOG.debug("Request to save Survey : {}", surveyDTO);
        Survey survey = surveyMapper.toEntity(surveyDTO);
        survey = surveyRepository.save(survey);
        surveySearchRepository.index(survey);
        return surveyMapper.toDto(survey);
    }

    @Override
    public SurveyDTO update(SurveyDTO surveyDTO) {
        LOG.debug("Request to update Survey : {}", surveyDTO);
        Survey survey = surveyMapper.toEntity(surveyDTO);
        survey = surveyRepository.save(survey);
        surveySearchRepository.index(survey);
        return surveyMapper.toDto(survey);
    }

    @Override
    public Optional<SurveyDTO> partialUpdate(SurveyDTO surveyDTO) {
        LOG.debug("Request to partially update Survey : {}", surveyDTO);

        return surveyRepository
            .findById(surveyDTO.getId())
            .map(existingSurvey -> {
                surveyMapper.partialUpdate(existingSurvey, surveyDTO);

                return existingSurvey;
            })
            .map(surveyRepository::save)
            .map(savedSurvey -> {
                surveySearchRepository.index(savedSurvey);
                return savedSurvey;
            })
            .map(surveyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Surveys");
        return surveyRepository.findAll(pageable).map(surveyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurveyDTO> findOne(Long id) {
        LOG.debug("Request to get Survey : {}", id);
        return surveyRepository.findById(id).map(surveyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Survey : {}", id);
        surveyRepository.deleteById(id);
        surveySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Surveys for query {}", query);
        return surveySearchRepository.search(query, pageable).map(surveyMapper::toDto);
    }
}
