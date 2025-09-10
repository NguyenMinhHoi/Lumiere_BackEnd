package com.lumi.app.service;

import com.lumi.app.domain.SurveyResponse;
import com.lumi.app.repository.SurveyResponseRepository;
import com.lumi.app.repository.search.SurveyResponseSearchRepository;
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
public class SurveyResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyResponseService.class);

    private final SurveyResponseRepository surveyResponseRepository;

    private final SurveyResponseMapper surveyResponseMapper;

    private final SurveyResponseSearchRepository surveyResponseSearchRepository;

    public SurveyResponseService(
        SurveyResponseRepository surveyResponseRepository,
        SurveyResponseMapper surveyResponseMapper,
        SurveyResponseSearchRepository surveyResponseSearchRepository
    ) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyResponseMapper = surveyResponseMapper;
        this.surveyResponseSearchRepository = surveyResponseSearchRepository;
    }

    /**
     * Save a surveyResponse.
     *
     * @param surveyResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyResponseDTO save(SurveyResponseDTO surveyResponseDTO) {
        LOG.debug("Request to save SurveyResponse : {}", surveyResponseDTO);
        SurveyResponse surveyResponse = surveyResponseMapper.toEntity(surveyResponseDTO);
        surveyResponse = surveyResponseRepository.save(surveyResponse);
        surveyResponseSearchRepository.index(surveyResponse);
        return surveyResponseMapper.toDto(surveyResponse);
    }

    /**
     * Update a surveyResponse.
     *
     * @param surveyResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyResponseDTO update(SurveyResponseDTO surveyResponseDTO) {
        LOG.debug("Request to update SurveyResponse : {}", surveyResponseDTO);
        SurveyResponse surveyResponse = surveyResponseMapper.toEntity(surveyResponseDTO);
        surveyResponse = surveyResponseRepository.save(surveyResponse);
        surveyResponseSearchRepository.index(surveyResponse);
        return surveyResponseMapper.toDto(surveyResponse);
    }

    /**
     * Partially update a surveyResponse.
     *
     * @param surveyResponseDTO the entity to update partially.
     * @return the persisted entity.
     */
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

    /**
     * Get all the surveyResponses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SurveyResponseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SurveyResponses");
        return surveyResponseRepository.findAll(pageable).map(surveyResponseMapper::toDto);
    }

    /**
     * Get all the surveyResponses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SurveyResponseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return surveyResponseRepository.findAllWithEagerRelationships(pageable).map(surveyResponseMapper::toDto);
    }

    /**
     * Get one surveyResponse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SurveyResponseDTO> findOne(Long id) {
        LOG.debug("Request to get SurveyResponse : {}", id);
        return surveyResponseRepository.findOneWithEagerRelationships(id).map(surveyResponseMapper::toDto);
    }

    /**
     * Delete the surveyResponse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SurveyResponse : {}", id);
        surveyResponseRepository.deleteById(id);
        surveyResponseSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the surveyResponse corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SurveyResponseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of SurveyResponses for query {}", query);
        return surveyResponseSearchRepository.search(query, pageable).map(surveyResponseMapper::toDto);
    }
}
