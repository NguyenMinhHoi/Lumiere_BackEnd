package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.Voucher;
import com.lumi.app.repository.VoucherRepository;
import com.lumi.app.repository.search.VoucherSearchRepository;
import com.lumi.app.service.criteria.VoucherCriteria;
import com.lumi.app.service.dto.VoucherDTO;
import com.lumi.app.service.mapper.VoucherMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Voucher} entities in the database.
 * The main input is a {@link VoucherCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VoucherDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VoucherQueryService extends QueryService<Voucher> {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherQueryService.class);

    private final VoucherRepository voucherRepository;

    private final VoucherMapper voucherMapper;

    private final VoucherSearchRepository voucherSearchRepository;

    public VoucherQueryService(
        VoucherRepository voucherRepository,
        VoucherMapper voucherMapper,
        VoucherSearchRepository voucherSearchRepository
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
        this.voucherSearchRepository = voucherSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link VoucherDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VoucherDTO> findByCriteria(VoucherCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Voucher> specification = createSpecification(criteria);
        return voucherRepository.findAll(specification, page).map(voucherMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VoucherCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Voucher> specification = createSpecification(criteria);
        return voucherRepository.count(specification);
    }

    /**
     * Function to convert {@link VoucherCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Voucher> createSpecification(VoucherCriteria criteria) {
        Specification<Voucher> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Voucher_.id),
                buildStringSpecification(criteria.getCode(), Voucher_.code),
                buildSpecification(criteria.getDiscountType(), Voucher_.discountType),
                buildRangeSpecification(criteria.getDiscountValue(), Voucher_.discountValue),
                buildRangeSpecification(criteria.getMinOrderValue(), Voucher_.minOrderValue),
                buildRangeSpecification(criteria.getMaxDiscountValue(), Voucher_.maxDiscountValue),
                buildRangeSpecification(criteria.getUsageLimit(), Voucher_.usageLimit),
                buildRangeSpecification(criteria.getUsedCount(), Voucher_.usedCount),
                buildRangeSpecification(criteria.getValidFrom(), Voucher_.validFrom),
                buildRangeSpecification(criteria.getValidTo(), Voucher_.validTo),
                buildSpecification(criteria.getStatus(), Voucher_.status),
                buildRangeSpecification(criteria.getCreatedAt(), Voucher_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Voucher_.updatedAt)
            );
        }
        return specification;
    }
}
