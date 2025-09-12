package com.lumi.app.service.impl;

import com.lumi.app.domain.SurveyResponse;
import com.lumi.app.repository.SurveyResponseRepository;
import com.lumi.app.repository.search.SurveyResponseSearchRepository;
import com.lumi.app.service.SurveyResponseService;
import com.lumi.app.service.dto.SurveyResponseDTO;
import com.lumi.app.service.mapper.SurveyResponseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.SurveyResponse}.
 */
@Service
@Transactional
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyResponseServiceImpl.class);

    private final SurveyResponseRepository surveyResponseRepository;

    private final SurveyResponseMapper surveyResponseMapper;

    private final SurveyResponseSearchRepository surveyResponseSearchRepository;

    public SurveyResponseServiceImpl(
        SurveyResponseRepository surveyResponseRepository,
        SurveyResponseMapper surveyResponseMapper,
        SurveyResponseSearchRepository surveyResponseSearchRepository
    ) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyResponseMapper = surveyResponseMapper;
        this.surveyResponseSearchRepository = surveyResponseSearchRepository;
    }

    @Override
    public SurveyResponseDTO save(SurveyResponseDTO surveyResponseDTO) {
        LOG.debug("Request to save SurveyResponse : {}", surveyResponseDTO);
        SurveyResponse surveyResponse = surveyResponseMapper.toEntity(surveyResponseDTO);
        surveyResponse = surveyResponseRepository.save(surveyResponse);
        surveyResponseSearchRepository.index(surveyResponse);
        return surveyResponseMapper.toDto(surveyResponse);
    }

    @Override
    public SurveyResponseDTO update(SurveyResponseDTO surveyResponseDTO) {
        LOG.debug("Request to update SurveyResponse : {}", surveyResponseDTO);
        SurveyResponse surveyResponse = surveyResponseMapper.toEntity(surveyResponseDTO);
        surveyResponse = surveyResponseRepository.save(surveyResponse);
        surveyResponseSearchRepository.index(surveyResponse);
        return surveyResponseMapper.toDto(surveyResponse);
    }

    @Override
    public Optional<SurveyResponseDTO> partialUpdate(SurveyResponseDTO surveyResponseDTO) {
        LOG.debug("Request to partially update SurveyResponse : {}", surveyResponseDTO);

        return surveyResponseRepository
            .findById(surveyResponseDTO.getId())
            .map(existingSurveyResponse -> {
                surveyResponseMapper.partialUpdate(existingSurveyResponse, surveyResponseDTO);

                return existingSurveyResponse;
            })
            .map(surveyResponseRepository::save)
            .map(savedSurveyResponse -> {
                surveyResponseSearchRepository.index(savedSurveyResponse);
                return savedSurveyResponse;
            })
            .map(surveyResponseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyResponseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SurveyResponses");
        return surveyResponseRepository.findAll(pageable).map(surveyResponseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurveyResponseDTO> findOne(Long id) {
        LOG.debug("Request to get SurveyResponse : {}", id);
        return surveyResponseRepository.findById(id).map(surveyResponseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SurveyResponse : {}", id);
        surveyResponseRepository.deleteById(id);
        surveyResponseSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyResponseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of SurveyResponses for query {}", query);
        return surveyResponseSearchRepository.search(query, pageable).map(surveyResponseMapper::toDto);
    }
}
