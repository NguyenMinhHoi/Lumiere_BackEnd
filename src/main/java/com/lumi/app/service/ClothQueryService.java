package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.Cloth;
import com.lumi.app.repository.ClothRepository;
import com.lumi.app.repository.search.ClothSearchRepository;
import com.lumi.app.service.criteria.ClothCriteria;
import com.lumi.app.service.dto.ClothDTO;
import com.lumi.app.service.mapper.ClothMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Cloth} entities in the database.
 * The main input is a {@link ClothCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClothDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClothQueryService extends QueryService<Cloth> {

    private static final Logger LOG = LoggerFactory.getLogger(ClothQueryService.class);

    private final ClothRepository clothRepository;

    private final ClothMapper clothMapper;

    private final ClothSearchRepository clothSearchRepository;

    public ClothQueryService(ClothRepository clothRepository, ClothMapper clothMapper, ClothSearchRepository clothSearchRepository) {
        this.clothRepository = clothRepository;
        this.clothMapper = clothMapper;
        this.clothSearchRepository = clothSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ClothDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothDTO> findByCriteria(ClothCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cloth> specification = createSpecification(criteria);
        return clothRepository.findAll(specification, page).map(clothMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClothCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Cloth> specification = createSpecification(criteria);
        return clothRepository.count(specification);
    }

    /**
     * Function to convert {@link ClothCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Cloth> createSpecification(ClothCriteria criteria) {
        Specification<Cloth> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Cloth_.id),
                buildStringSpecification(criteria.getCode(), Cloth_.code),
                buildStringSpecification(criteria.getName(), Cloth_.name),
                buildStringSpecification(criteria.getMaterial(), Cloth_.material),
                buildStringSpecification(criteria.getColor(), Cloth_.color),
                buildRangeSpecification(criteria.getWidth(), Cloth_.width),
                buildRangeSpecification(criteria.getLength(), Cloth_.length),
                buildStringSpecification(criteria.getUnit(), Cloth_.unit),
                buildSpecification(criteria.getStatus(), Cloth_.status),
                buildRangeSpecification(criteria.getCreatedAt(), Cloth_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Cloth_.updatedAt)
            );
        }
        return specification;
    }
}
