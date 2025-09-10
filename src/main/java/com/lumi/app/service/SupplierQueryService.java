package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.Supplier;
import com.lumi.app.repository.SupplierRepository;
import com.lumi.app.repository.search.SupplierSearchRepository;
import com.lumi.app.service.criteria.SupplierCriteria;
import com.lumi.app.service.dto.SupplierDTO;
import com.lumi.app.service.mapper.SupplierMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Supplier} entities in the database.
 * The main input is a {@link SupplierCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SupplierDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SupplierQueryService extends QueryService<Supplier> {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierQueryService.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    private final SupplierSearchRepository supplierSearchRepository;

    public SupplierQueryService(
        SupplierRepository supplierRepository,
        SupplierMapper supplierMapper,
        SupplierSearchRepository supplierSearchRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.supplierSearchRepository = supplierSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SupplierDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplierDTO> findByCriteria(SupplierCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Supplier> specification = createSpecification(criteria);
        return supplierRepository.findAll(specification, page).map(supplierMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SupplierCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Supplier> specification = createSpecification(criteria);
        return supplierRepository.count(specification);
    }

    /**
     * Function to convert {@link SupplierCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Supplier> createSpecification(SupplierCriteria criteria) {
        Specification<Supplier> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Supplier_.id),
                buildStringSpecification(criteria.getCode(), Supplier_.code),
                buildStringSpecification(criteria.getName(), Supplier_.name),
                buildStringSpecification(criteria.getEmail(), Supplier_.email),
                buildStringSpecification(criteria.getPhone(), Supplier_.phone),
                buildStringSpecification(criteria.getAddress(), Supplier_.address),
                buildSpecification(criteria.getStatus(), Supplier_.status),
                buildRangeSpecification(criteria.getCreatedAt(), Supplier_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Supplier_.updatedAt)
            );
        }
        return specification;
    }
}
