package com.lumi.app.service.impl;

import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.repository.KnowledgeCategoryRepository;
import com.lumi.app.repository.search.KnowledgeCategorySearchRepository;
import com.lumi.app.service.KnowledgeCategoryService;
import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import com.lumi.app.service.mapper.KnowledgeCategoryMapper;
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
 * Service Implementation for managing {@link KnowledgeCategory}.
 */
@Service
@Transactional
public class KnowledgeCategoryServiceImpl implements KnowledgeCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeCategoryServiceImpl.class);

    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    private final KnowledgeCategoryMapper knowledgeCategoryMapper;

    private final KnowledgeCategorySearchRepository knowledgeCategorySearchRepository;

    public KnowledgeCategoryServiceImpl(
        KnowledgeCategoryRepository knowledgeCategoryRepository,
        KnowledgeCategoryMapper knowledgeCategoryMapper,
        KnowledgeCategorySearchRepository knowledgeCategorySearchRepository
    ) {
        this.knowledgeCategoryRepository = knowledgeCategoryRepository;
        this.knowledgeCategoryMapper = knowledgeCategoryMapper;
        this.knowledgeCategorySearchRepository = knowledgeCategorySearchRepository;
    }

    @Override
    public KnowledgeCategoryDTO save(KnowledgeCategoryDTO knowledgeCategoryDTO) {
        LOG.debug("Request to save KnowledgeCategory : {}", knowledgeCategoryDTO);
        KnowledgeCategory knowledgeCategory = knowledgeCategoryMapper.toEntity(knowledgeCategoryDTO);
        knowledgeCategory = knowledgeCategoryRepository.save(knowledgeCategory);
        knowledgeCategorySearchRepository.index(knowledgeCategory);
        return knowledgeCategoryMapper.toDto(knowledgeCategory);
    }

    @Override
    public KnowledgeCategoryDTO update(KnowledgeCategoryDTO knowledgeCategoryDTO) {
        LOG.debug("Request to update KnowledgeCategory : {}", knowledgeCategoryDTO);
        KnowledgeCategory knowledgeCategory = knowledgeCategoryMapper.toEntity(knowledgeCategoryDTO);
        knowledgeCategory = knowledgeCategoryRepository.save(knowledgeCategory);
        knowledgeCategorySearchRepository.index(knowledgeCategory);
        return knowledgeCategoryMapper.toDto(knowledgeCategory);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeCategoryDTO> findAll() {
        LOG.debug("Request to get all KnowledgeCategories");
        return knowledgeCategoryRepository
            .findAll()
            .stream()
            .map(knowledgeCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KnowledgeCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeCategory : {}", id);
        return knowledgeCategoryRepository.findById(id).map(knowledgeCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeCategory : {}", id);
        knowledgeCategoryRepository.deleteById(id);
        knowledgeCategorySearchRepository.deleteFromIndexById(id);
    }

    @Override
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
