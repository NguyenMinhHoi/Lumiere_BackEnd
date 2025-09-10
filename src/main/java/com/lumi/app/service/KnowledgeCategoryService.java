package com.lumi.app.service;

import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.repository.KnowledgeCategoryRepository;
import com.lumi.app.repository.search.KnowledgeCategorySearchRepository;
import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import com.lumi.app.service.mapper.KnowledgeCategoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.KnowledgeCategory}.
 */
@Service
@Transactional
public class KnowledgeCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeCategoryService.class);

    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    private final KnowledgeCategoryMapper knowledgeCategoryMapper;

    private final KnowledgeCategorySearchRepository knowledgeCategorySearchRepository;

    public KnowledgeCategoryService(
        KnowledgeCategoryRepository knowledgeCategoryRepository,
        KnowledgeCategoryMapper knowledgeCategoryMapper,
        KnowledgeCategorySearchRepository knowledgeCategorySearchRepository
    ) {
        this.knowledgeCategoryRepository = knowledgeCategoryRepository;
        this.knowledgeCategoryMapper = knowledgeCategoryMapper;
        this.knowledgeCategorySearchRepository = knowledgeCategorySearchRepository;
    }

    /**
     * Save a knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public KnowledgeCategoryDTO save(KnowledgeCategoryDTO knowledgeCategoryDTO) {
        LOG.debug("Request to save KnowledgeCategory : {}", knowledgeCategoryDTO);
        KnowledgeCategory knowledgeCategory = knowledgeCategoryMapper.toEntity(knowledgeCategoryDTO);
        knowledgeCategory = knowledgeCategoryRepository.save(knowledgeCategory);
        knowledgeCategorySearchRepository.index(knowledgeCategory);
        return knowledgeCategoryMapper.toDto(knowledgeCategory);
    }

    /**
     * Update a knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public KnowledgeCategoryDTO update(KnowledgeCategoryDTO knowledgeCategoryDTO) {
        LOG.debug("Request to update KnowledgeCategory : {}", knowledgeCategoryDTO);
        KnowledgeCategory knowledgeCategory = knowledgeCategoryMapper.toEntity(knowledgeCategoryDTO);
        knowledgeCategory = knowledgeCategoryRepository.save(knowledgeCategory);
        knowledgeCategorySearchRepository.index(knowledgeCategory);
        return knowledgeCategoryMapper.toDto(knowledgeCategory);
    }

    /**
     * Partially update a knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<KnowledgeCategoryDTO> partialUpdate(KnowledgeCategoryDTO knowledgeCategoryDTO) {
        LOG.debug("Request to partially update KnowledgeCategory : {}", knowledgeCategoryDTO);

        return knowledgeCategoryRepository
            .findById(knowledgeCategoryDTO.getId())
            .map(existingKnowledgeCategory -> {
                knowledgeCategoryMapper.partialUpdate(existingKnowledgeCategory, knowledgeCategoryDTO);

                return existingKnowledgeCategory;
            })
            .map(knowledgeCategoryRepository::save)
            .map(savedKnowledgeCategory -> {
                knowledgeCategorySearchRepository.index(savedKnowledgeCategory);
                return savedKnowledgeCategory;
            })
            .map(knowledgeCategoryMapper::toDto);
    }

    /**
     * Get all the knowledgeCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<KnowledgeCategoryDTO> findAll() {
        LOG.debug("Request to get all KnowledgeCategories");
        return knowledgeCategoryRepository
            .findAll()
            .stream()
            .map(knowledgeCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one knowledgeCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<KnowledgeCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeCategory : {}", id);
        return knowledgeCategoryRepository.findById(id).map(knowledgeCategoryMapper::toDto);
    }

    /**
     * Delete the knowledgeCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeCategory : {}", id);
        knowledgeCategoryRepository.deleteById(id);
        knowledgeCategorySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the knowledgeCategory corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<KnowledgeCategoryDTO> search(String query) {
        LOG.debug("Request to search KnowledgeCategories for query {}", query);
        try {
            return StreamSupport.stream(knowledgeCategorySearchRepository.search(query).spliterator(), false)
                .map(knowledgeCategoryMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
