package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.OrderItem;
import com.lumi.app.repository.OrderItemRepository;
import com.lumi.app.repository.search.OrderItemSearchRepository;
import com.lumi.app.service.criteria.OrderItemCriteria;
import com.lumi.app.service.dto.OrderItemDTO;
import com.lumi.app.service.mapper.OrderItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OrderItem} entities in the database.
 * The main input is a {@link OrderItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OrderItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderItemQueryService extends QueryService<OrderItem> {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemQueryService.class);

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    private final OrderItemSearchRepository orderItemSearchRepository;

    public OrderItemQueryService(
        OrderItemRepository orderItemRepository,
        OrderItemMapper orderItemMapper,
        OrderItemSearchRepository orderItemSearchRepository
    ) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.orderItemSearchRepository = orderItemSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link OrderItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderItemDTO> findByCriteria(OrderItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderItem> specification = createSpecification(criteria);
        return orderItemRepository.findAll(specification, page).map(orderItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OrderItem> specification = createSpecification(criteria);
        return orderItemRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderItem> createSpecification(OrderItemCriteria criteria) {
        Specification<OrderItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), OrderItem_.id),
                buildRangeSpecification(criteria.getOrderId(), OrderItem_.orderId),
                buildRangeSpecification(criteria.getVariantId(), OrderItem_.variantId),
                buildRangeSpecification(criteria.getQuantity(), OrderItem_.quantity),
                buildRangeSpecification(criteria.getUnitPrice(), OrderItem_.unitPrice),
                buildRangeSpecification(criteria.getTotalPrice(), OrderItem_.totalPrice),
                buildStringSpecification(criteria.getNameSnapshot(), OrderItem_.nameSnapshot),
                buildStringSpecification(criteria.getSkuSnapshot(), OrderItem_.skuSnapshot)
            );
        }
        return specification;
    }
}
