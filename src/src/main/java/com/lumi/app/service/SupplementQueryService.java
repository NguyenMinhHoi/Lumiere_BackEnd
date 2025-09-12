package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.Supplement;
import com.lumi.app.repository.SupplementRepository;
import com.lumi.app.repository.search.SupplementSearchRepository;
import com.lumi.app.service.criteria.SupplementCriteria;
import com.lumi.app.service.dto.SupplementDTO;
import com.lumi.app.service.mapper.SupplementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Supplement} entities in the database.
 * The main input is a {@link SupplementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SupplementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SupplementQueryService extends QueryService<Supplement> {

    private static final Logger LOG = LoggerFactory.getLogger(SupplementQueryService.class);

    private final SupplementRepository supplementRepository;

    private final SupplementMapper supplementMapper;

    private final SupplementSearchRepository supplementSearchRepository;

    public SupplementQueryService(
        SupplementRepository supplementRepository,
        SupplementMapper supplementMapper,
        SupplementSearchRepository supplementSearchRepository
    ) {
        this.supplementRepository = supplementRepository;
        this.supplementMapper = supplementMapper;
        this.supplementSearchRepository = supplementSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SupplementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplementDTO> findByCriteria(SupplementCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Supplement> specification = createSpecification(criteria);
        return supplementRepository.findAll(specification, page).map(supplementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SupplementCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Supplement> specification = createSpecification(criteria);
        return supplementRepository.count(specification);
    }

    /**
     * Function to convert {@link SupplementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Supplement> createSpecification(SupplementCriteria criteria) {
        Specification<Supplement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Supplement_.id),
                buildRangeSpecification(criteria.getProductId(), Supplement_.productId),
                buildRangeSpecification(criteria.getSupplierId(), Supplement_.supplierId),
                buildRangeSpecification(criteria.getSupplyPrice(), Supplement_.supplyPrice),
                buildStringSpecification(criteria.getCurrency(), Supplement_.currency),
                buildRangeSpecification(criteria.getLeadTimeDays(), Supplement_.leadTimeDays),
                buildRangeSpecification(criteria.getMinOrderQty(), Supplement_.minOrderQty),
                buildSpecification(criteria.getIsPreferred(), Supplement_.isPreferred),
                buildRangeSpecification(criteria.getCreatedAt(), Supplement_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Supplement_.updatedAt)
            );
        }
        return specification;
    }
}
