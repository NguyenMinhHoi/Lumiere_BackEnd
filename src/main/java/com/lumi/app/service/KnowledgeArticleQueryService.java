package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.repository.KnowledgeArticleRepository;
import com.lumi.app.repository.search.KnowledgeArticleSearchRepository;
import com.lumi.app.service.criteria.KnowledgeArticleCriteria;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import com.lumi.app.service.mapper.KnowledgeArticleMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link KnowledgeArticle} entities in the database.
 * The main input is a {@link KnowledgeArticleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link KnowledgeArticleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class KnowledgeArticleQueryService extends QueryService<KnowledgeArticle> {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeArticleQueryService.class);

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    private final KnowledgeArticleMapper knowledgeArticleMapper;

    private final KnowledgeArticleSearchRepository knowledgeArticleSearchRepository;

    public KnowledgeArticleQueryService(
        KnowledgeArticleRepository knowledgeArticleRepository,
        KnowledgeArticleMapper knowledgeArticleMapper,
        KnowledgeArticleSearchRepository knowledgeArticleSearchRepository
    ) {
        this.knowledgeArticleRepository = knowledgeArticleRepository;
        this.knowledgeArticleMapper = knowledgeArticleMapper;
        this.knowledgeArticleSearchRepository = knowledgeArticleSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link KnowledgeArticleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleDTO> findByCriteria(KnowledgeArticleCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<KnowledgeArticle> specification = createSpecification(criteria);
        return knowledgeArticleRepository
            .fetchBagRelationships(knowledgeArticleRepository.findAll(specification, page))
            .map(knowledgeArticleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(KnowledgeArticleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<KnowledgeArticle> specification = createSpecification(criteria);
        return knowledgeArticleRepository.count(specification);
    }

    /**
     * Function to convert {@link KnowledgeArticleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<KnowledgeArticle> createSpecification(KnowledgeArticleCriteria criteria) {
        Specification<KnowledgeArticle> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), KnowledgeArticle_.id),
                buildStringSpecification(criteria.getTitle(), KnowledgeArticle_.title),
                buildSpecification(criteria.getPublished(), KnowledgeArticle_.published),
                buildRangeSpecification(criteria.getViews(), KnowledgeArticle_.views),
                buildRangeSpecification(criteria.getUpdatedAt(), KnowledgeArticle_.updatedAt),
                buildSpecification(criteria.getCategoryId(), root ->
                    root.join(KnowledgeArticle_.category, JoinType.LEFT).get(KnowledgeCategory_.id)
                ),
                buildSpecification(criteria.getTagsId(), root -> root.join(KnowledgeArticle_.tags, JoinType.LEFT).get(Tag_.id))
            );
        }
        return specification;
    }
}
