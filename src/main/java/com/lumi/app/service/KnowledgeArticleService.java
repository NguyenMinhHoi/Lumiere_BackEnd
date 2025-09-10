package com.lumi.app.service;

import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.repository.KnowledgeArticleRepository;
import com.lumi.app.repository.search.KnowledgeArticleSearchRepository;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import com.lumi.app.service.mapper.KnowledgeArticleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.KnowledgeArticle}.
 */
@Service
@Transactional
public class KnowledgeArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeArticleService.class);

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    private final KnowledgeArticleMapper knowledgeArticleMapper;

    private final KnowledgeArticleSearchRepository knowledgeArticleSearchRepository;

    public KnowledgeArticleService(
        KnowledgeArticleRepository knowledgeArticleRepository,
        KnowledgeArticleMapper knowledgeArticleMapper,
        KnowledgeArticleSearchRepository knowledgeArticleSearchRepository
    ) {
        this.knowledgeArticleRepository = knowledgeArticleRepository;
        this.knowledgeArticleMapper = knowledgeArticleMapper;
        this.knowledgeArticleSearchRepository = knowledgeArticleSearchRepository;
    }

    /**
     * Save a knowledgeArticle.
     *
     * @param knowledgeArticleDTO the entity to save.
     * @return the persisted entity.
     */
    public KnowledgeArticleDTO save(KnowledgeArticleDTO knowledgeArticleDTO) {
        LOG.debug("Request to save KnowledgeArticle : {}", knowledgeArticleDTO);
        KnowledgeArticle knowledgeArticle = knowledgeArticleMapper.toEntity(knowledgeArticleDTO);
        knowledgeArticle = knowledgeArticleRepository.save(knowledgeArticle);
        knowledgeArticleSearchRepository.index(knowledgeArticle);
        return knowledgeArticleMapper.toDto(knowledgeArticle);
    }

    /**
     * Update a knowledgeArticle.
     *
     * @param knowledgeArticleDTO the entity to save.
     * @return the persisted entity.
     */
    public KnowledgeArticleDTO update(KnowledgeArticleDTO knowledgeArticleDTO) {
        LOG.debug("Request to update KnowledgeArticle : {}", knowledgeArticleDTO);
        KnowledgeArticle knowledgeArticle = knowledgeArticleMapper.toEntity(knowledgeArticleDTO);
        knowledgeArticle = knowledgeArticleRepository.save(knowledgeArticle);
        knowledgeArticleSearchRepository.index(knowledgeArticle);
        return knowledgeArticleMapper.toDto(knowledgeArticle);
    }

    /**
     * Partially update a knowledgeArticle.
     *
     * @param knowledgeArticleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<KnowledgeArticleDTO> partialUpdate(KnowledgeArticleDTO knowledgeArticleDTO) {
        LOG.debug("Request to partially update KnowledgeArticle : {}", knowledgeArticleDTO);

        return knowledgeArticleRepository
            .findById(knowledgeArticleDTO.getId())
            .map(existingKnowledgeArticle -> {
                knowledgeArticleMapper.partialUpdate(existingKnowledgeArticle, knowledgeArticleDTO);

                return existingKnowledgeArticle;
            })
            .map(knowledgeArticleRepository::save)
            .map(savedKnowledgeArticle -> {
                knowledgeArticleSearchRepository.index(savedKnowledgeArticle);
                return savedKnowledgeArticle;
            })
            .map(knowledgeArticleMapper::toDto);
    }

    /**
     * Get all the knowledgeArticles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<KnowledgeArticleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return knowledgeArticleRepository.findAllWithEagerRelationships(pageable).map(knowledgeArticleMapper::toDto);
    }

    /**
     * Get one knowledgeArticle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<KnowledgeArticleDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeArticle : {}", id);
        return knowledgeArticleRepository.findOneWithEagerRelationships(id).map(knowledgeArticleMapper::toDto);
    }

    /**
     * Delete the knowledgeArticle by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeArticle : {}", id);
        knowledgeArticleRepository.deleteById(id);
        knowledgeArticleSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the knowledgeArticle corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of KnowledgeArticles for query {}", query);
        return knowledgeArticleSearchRepository.search(query, pageable).map(knowledgeArticleMapper::toDto);
    }
}
