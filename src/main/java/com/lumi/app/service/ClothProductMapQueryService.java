package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.ClothProductMap;
import com.lumi.app.repository.ClothProductMapRepository;
import com.lumi.app.repository.search.ClothProductMapSearchRepository;
import com.lumi.app.service.criteria.ClothProductMapCriteria;
import com.lumi.app.service.dto.ClothProductMapDTO;
import com.lumi.app.service.mapper.ClothProductMapMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ClothProductMap} entities in the database.
 * The main input is a {@link ClothProductMapCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClothProductMapDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClothProductMapQueryService extends QueryService<ClothProductMap> {

    private static final Logger LOG = LoggerFactory.getLogger(ClothProductMapQueryService.class);

    private final ClothProductMapRepository clothProductMapRepository;

    private final ClothProductMapMapper clothProductMapMapper;

    private final ClothProductMapSearchRepository clothProductMapSearchRepository;

    public ClothProductMapQueryService(
        ClothProductMapRepository clothProductMapRepository,
        ClothProductMapMapper clothProductMapMapper,
        ClothProductMapSearchRepository clothProductMapSearchRepository
    ) {
        this.clothProductMapRepository = clothProductMapRepository;
        this.clothProductMapMapper = clothProductMapMapper;
        this.clothProductMapSearchRepository = clothProductMapSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ClothProductMapDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothProductMapDTO> findByCriteria(ClothProductMapCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClothProductMap> specification = createSpecification(criteria);
        return clothProductMapRepository.findAll(specification, page).map(clothProductMapMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClothProductMapCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ClothProductMap> specification = createSpecification(criteria);
        return clothProductMapRepository.count(specification);
    }

    /**
     * Function to convert {@link ClothProductMapCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClothProductMap> createSpecification(ClothProductMapCriteria criteria) {
        Specification<ClothProductMap> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ClothProductMap_.id),
                buildRangeSpecification(criteria.getClothId(), ClothProductMap_.clothId),
                buildRangeSpecification(criteria.getProductId(), ClothProductMap_.productId),
                buildRangeSpecification(criteria.getQuantity(), ClothProductMap_.quantity),
                buildStringSpecification(criteria.getUnit(), ClothProductMap_.unit),
                buildStringSpecification(criteria.getNote(), ClothProductMap_.note),
                buildRangeSpecification(criteria.getCreatedAt(), ClothProductMap_.createdAt)
            );
        }
        return specification;
    }
}
