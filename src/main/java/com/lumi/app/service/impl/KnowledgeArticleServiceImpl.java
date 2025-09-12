package com.lumi.app.service.impl;

import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.repository.KnowledgeArticleRepository;
import com.lumi.app.repository.search.KnowledgeArticleSearchRepository;
import com.lumi.app.service.KnowledgeArticleService;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import com.lumi.app.service.mapper.KnowledgeArticleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link KnowledgeArticle}.
 */
@Service
@Transactional
public class KnowledgeArticleServiceImpl implements KnowledgeArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeArticleServiceImpl.class);

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    private final KnowledgeArticleMapper knowledgeArticleMapper;

    private final KnowledgeArticleSearchRepository knowledgeArticleSearchRepository;

    public KnowledgeArticleServiceImpl(
        KnowledgeArticleRepository knowledgeArticleRepository,
        KnowledgeArticleMapper knowledgeArticleMapper,
        KnowledgeArticleSearchRepository knowledgeArticleSearchRepository
    ) {
        this.knowledgeArticleRepository = knowledgeArticleRepository;
        this.knowledgeArticleMapper = knowledgeArticleMapper;
        this.knowledgeArticleSearchRepository = knowledgeArticleSearchRepository;
    }

    @Override
    public KnowledgeArticleDTO save(KnowledgeArticleDTO knowledgeArticleDTO) {
        LOG.debug("Request to save KnowledgeArticle : {}", knowledgeArticleDTO);
        KnowledgeArticle knowledgeArticle = knowledgeArticleMapper.toEntity(knowledgeArticleDTO);
        knowledgeArticle = knowledgeArticleRepository.save(knowledgeArticle);
        knowledgeArticleSearchRepository.index(knowledgeArticle);
        return knowledgeArticleMapper.toDto(knowledgeArticle);
    }

    @Override
    public KnowledgeArticleDTO update(KnowledgeArticleDTO knowledgeArticleDTO) {
        LOG.debug("Request to update KnowledgeArticle : {}", knowledgeArticleDTO);
        KnowledgeArticle knowledgeArticle = knowledgeArticleMapper.toEntity(knowledgeArticleDTO);
        knowledgeArticle = knowledgeArticleRepository.save(knowledgeArticle);
        knowledgeArticleSearchRepository.index(knowledgeArticle);
        return knowledgeArticleMapper.toDto(knowledgeArticle);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Optional<KnowledgeArticleDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeArticle : {}", id);
        return knowledgeArticleRepository.findById(id).map(knowledgeArticleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeArticle : {}", id);
        knowledgeArticleRepository.deleteById(id);
        knowledgeArticleSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of KnowledgeArticles for query {}", query);
        return knowledgeArticleSearchRepository.search(query, pageable).map(knowledgeArticleMapper::toDto);
    }
}
