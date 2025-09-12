package com.lumi.app.service;

import com.lumi.app.domain.AuditHistory;
import com.lumi.app.repository.AuditHistoryRepository;
import com.lumi.app.repository.search.AuditHistorySearchRepository;
import com.lumi.app.service.criteria.AuditHistoryCriteria;
import com.lumi.app.service.dto.AuditHistoryDTO;
import com.lumi.app.service.mapper.AuditHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AuditHistory} entities in the database.
 * The main input is a {@link AuditHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuditHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuditHistoryQueryService extends QueryService<AuditHistory> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditHistoryQueryService.class);

    private final AuditHistoryRepository auditHistoryRepository;

    private final AuditHistoryMapper auditHistoryMapper;

    private final AuditHistorySearchRepository auditHistorySearchRepository;

    public AuditHistoryQueryService(
        AuditHistoryRepository auditHistoryRepository,
        AuditHistoryMapper auditHistoryMapper,
        AuditHistorySearchRepository auditHistorySearchRepository
    ) {
        this.auditHistoryRepository = auditHistoryRepository;
        this.auditHistoryMapper = auditHistoryMapper;
        this.auditHistorySearchRepository = auditHistorySearchRepository;
    }

    /**
     * Return a {@link Page} of {@link AuditHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuditHistoryDTO> findByCriteria(AuditHistoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AuditHistory> specification = createSpecification(criteria);
        return auditHistoryRepository.findAll(specification, page).map(auditHistoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuditHistoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AuditHistory> specification = createSpecification(criteria);
        return auditHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link AuditHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AuditHistory> createSpecification(AuditHistoryCriteria criteria) {
        Specification<AuditHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), AuditHistory_.id),
                buildStringSpecification(criteria.getEntityName(), AuditHistory_.entityName),
                buildStringSpecification(criteria.getEntityId(), AuditHistory_.entityId),
                buildSpecification(criteria.getAction(), AuditHistory_.action),
                buildStringSpecification(criteria.getPerformedBy(), AuditHistory_.performedBy),
                buildRangeSpecification(criteria.getPerformedAt(), AuditHistory_.performedAt),
                buildStringSpecification(criteria.getIpAddress(), AuditHistory_.ipAddress)
            );
        }
        return specification;
    }
}
