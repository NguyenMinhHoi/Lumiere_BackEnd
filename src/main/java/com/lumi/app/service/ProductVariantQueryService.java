package com.lumi.app.service;

import com.lumi.app.domain.ProductVariant;
import com.lumi.app.repository.ProductVariantRepository;
import com.lumi.app.repository.search.ProductVariantSearchRepository;
import com.lumi.app.service.criteria.ProductVariantCriteria;
import com.lumi.app.service.dto.ProductVariantDTO;
import com.lumi.app.service.mapper.ProductVariantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProductVariant} entities in the database.
 * The main input is a {@link ProductVariantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProductVariantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductVariantQueryService extends QueryService<ProductVariant> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductVariantQueryService.class);

    private final ProductVariantRepository productVariantRepository;

    private final ProductVariantMapper productVariantMapper;

    private final ProductVariantSearchRepository productVariantSearchRepository;

    public ProductVariantQueryService(
        ProductVariantRepository productVariantRepository,
        ProductVariantMapper productVariantMapper,
        ProductVariantSearchRepository productVariantSearchRepository
    ) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
        this.productVariantSearchRepository = productVariantSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ProductVariantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductVariantDTO> findByCriteria(ProductVariantCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductVariant> specification = createSpecification(criteria);
        return productVariantRepository.findAll(specification, page).map(productVariantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductVariantCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProductVariant> specification = createSpecification(criteria);
        return productVariantRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductVariantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductVariant> createSpecification(ProductVariantCriteria criteria) {
        Specification<ProductVariant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ProductVariant_.id),
                buildRangeSpecification(criteria.getProductId(), ProductVariant_.productId),
                buildStringSpecification(criteria.getSku(), ProductVariant_.sku),
                buildStringSpecification(criteria.getName(), ProductVariant_.name),
                buildRangeSpecification(criteria.getPrice(), ProductVariant_.price),
                buildRangeSpecification(criteria.getCompareAtPrice(), ProductVariant_.compareAtPrice),
                buildStringSpecification(criteria.getCurrency(), ProductVariant_.currency),
                buildRangeSpecification(criteria.getStockQuantity(), ProductVariant_.stockQuantity),
                buildRangeSpecification(criteria.getWeight(), ProductVariant_.weight),
                buildRangeSpecification(criteria.getLength(), ProductVariant_.length),
                buildRangeSpecification(criteria.getWidth(), ProductVariant_.width),
                buildRangeSpecification(criteria.getHeight(), ProductVariant_.height),
                buildSpecification(criteria.getIsDefault(), ProductVariant_.isDefault),
                buildRangeSpecification(criteria.getCreatedAt(), ProductVariant_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), ProductVariant_.updatedAt)
            );
        }
        return specification;
    }
}
