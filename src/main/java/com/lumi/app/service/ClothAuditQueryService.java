package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.ClothAudit;
import com.lumi.app.repository.ClothAuditRepository;
import com.lumi.app.repository.search.ClothAuditSearchRepository;
import com.lumi.app.service.criteria.ClothAuditCriteria;
import com.lumi.app.service.dto.ClothAuditDTO;
import com.lumi.app.service.mapper.ClothAuditMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ClothAudit} entities in the database.
 * The main input is a {@link ClothAuditCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClothAuditDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClothAuditQueryService extends QueryService<ClothAudit> {

    private static final Logger LOG = LoggerFactory.getLogger(ClothAuditQueryService.class);

    private final ClothAuditRepository clothAuditRepository;

    private final ClothAuditMapper clothAuditMapper;

    private final ClothAuditSearchRepository clothAuditSearchRepository;

    public ClothAuditQueryService(
        ClothAuditRepository clothAuditRepository,
        ClothAuditMapper clothAuditMapper,
        ClothAuditSearchRepository clothAuditSearchRepository
    ) {
        this.clothAuditRepository = clothAuditRepository;
        this.clothAuditMapper = clothAuditMapper;
        this.clothAuditSearchRepository = clothAuditSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ClothAuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothAuditDTO> findByCriteria(ClothAuditCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClothAudit> specification = createSpecification(criteria);
        return clothAuditRepository.findAll(specification, page).map(clothAuditMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClothAuditCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ClothAudit> specification = createSpecification(criteria);
        return clothAuditRepository.count(specification);
    }

    /**
     * Function to convert {@link ClothAuditCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClothAudit> createSpecification(ClothAuditCriteria criteria) {
        Specification<ClothAudit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ClothAudit_.id),
                buildRangeSpecification(criteria.getClothId(), ClothAudit_.clothId),
                buildRangeSpecification(criteria.getSupplierId(), ClothAudit_.supplierId),
                buildRangeSpecification(criteria.getProductId(), ClothAudit_.productId),
                buildSpecification(criteria.getAction(), ClothAudit_.action),
                buildRangeSpecification(criteria.getQuantity(), ClothAudit_.quantity),
                buildStringSpecification(criteria.getUnit(), ClothAudit_.unit),
                buildRangeSpecification(criteria.getSentAt(), ClothAudit_.sentAt),
                buildStringSpecification(criteria.getNote(), ClothAudit_.note),
                buildRangeSpecification(criteria.getCreatedAt(), ClothAudit_.createdAt)
            );
        }
        return specification;
    }
}
