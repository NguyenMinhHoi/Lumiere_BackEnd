package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.ClothSupplement;
import com.lumi.app.repository.ClothSupplementRepository;
import com.lumi.app.repository.search.ClothSupplementSearchRepository;
import com.lumi.app.service.criteria.ClothSupplementCriteria;
import com.lumi.app.service.dto.ClothSupplementDTO;
import com.lumi.app.service.mapper.ClothSupplementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ClothSupplement} entities in the database.
 * The main input is a {@link ClothSupplementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClothSupplementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClothSupplementQueryService extends QueryService<ClothSupplement> {

    private static final Logger LOG = LoggerFactory.getLogger(ClothSupplementQueryService.class);

    private final ClothSupplementRepository clothSupplementRepository;

    private final ClothSupplementMapper clothSupplementMapper;

    private final ClothSupplementSearchRepository clothSupplementSearchRepository;

    public ClothSupplementQueryService(
        ClothSupplementRepository clothSupplementRepository,
        ClothSupplementMapper clothSupplementMapper,
        ClothSupplementSearchRepository clothSupplementSearchRepository
    ) {
        this.clothSupplementRepository = clothSupplementRepository;
        this.clothSupplementMapper = clothSupplementMapper;
        this.clothSupplementSearchRepository = clothSupplementSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ClothSupplementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothSupplementDTO> findByCriteria(ClothSupplementCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClothSupplement> specification = createSpecification(criteria);
        return clothSupplementRepository.findAll(specification, page).map(clothSupplementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClothSupplementCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ClothSupplement> specification = createSpecification(criteria);
        return clothSupplementRepository.count(specification);
    }

    /**
     * Function to convert {@link ClothSupplementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClothSupplement> createSpecification(ClothSupplementCriteria criteria) {
        Specification<ClothSupplement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ClothSupplement_.id),
                buildRangeSpecification(criteria.getClothId(), ClothSupplement_.clothId),
                buildRangeSpecification(criteria.getSupplierId(), ClothSupplement_.supplierId),
                buildRangeSpecification(criteria.getSupplyPrice(), ClothSupplement_.supplyPrice),
                buildStringSpecification(criteria.getCurrency(), ClothSupplement_.currency),
                buildRangeSpecification(criteria.getLeadTimeDays(), ClothSupplement_.leadTimeDays),
                buildRangeSpecification(criteria.getMinOrderQty(), ClothSupplement_.minOrderQty),
                buildSpecification(criteria.getIsPreferred(), ClothSupplement_.isPreferred),
                buildRangeSpecification(criteria.getCreatedAt(), ClothSupplement_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), ClothSupplement_.updatedAt)
            );
        }
        return specification;
    }
}
