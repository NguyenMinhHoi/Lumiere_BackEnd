package com.lumi.app.service;

import com.lumi.app.domain.Orders;
import com.lumi.app.repository.OrdersRepository;
import com.lumi.app.repository.search.OrdersSearchRepository;
import com.lumi.app.service.criteria.OrdersCriteria;
import com.lumi.app.service.dto.OrdersDTO;
import com.lumi.app.service.mapper.OrdersMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Orders} entities in the database.
 * The main input is a {@link OrdersCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OrdersDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrdersQueryService extends QueryService<Orders> {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersQueryService.class);

    private final OrdersRepository ordersRepository;

    private final OrdersMapper ordersMapper;

    private final OrdersSearchRepository ordersSearchRepository;

    public OrdersQueryService(OrdersRepository ordersRepository, OrdersMapper ordersMapper, OrdersSearchRepository ordersSearchRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.ordersSearchRepository = ordersSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link OrdersDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrdersDTO> findByCriteria(OrdersCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.findAll(specification, page).map(ordersMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrdersCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.count(specification);
    }

    /**
     * Function to convert {@link OrdersCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Orders> createSpecification(OrdersCriteria criteria) {
        Specification<Orders> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Orders_.id),
                buildRangeSpecification(criteria.getCustomerId(), Orders_.customerId),
                buildStringSpecification(criteria.getCode(), Orders_.code),
                buildSpecification(criteria.getStatus(), Orders_.status),
                buildSpecification(criteria.getPaymentStatus(), Orders_.paymentStatus),
                buildSpecification(criteria.getFulfillmentStatus(), Orders_.fulfillmentStatus),
                buildRangeSpecification(criteria.getTotalAmount(), Orders_.totalAmount),
                buildStringSpecification(criteria.getCurrency(), Orders_.currency),
                buildStringSpecification(criteria.getNote(), Orders_.note),
                buildRangeSpecification(criteria.getPlacedAt(), Orders_.placedAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Orders_.updatedAt)
            );
        }
        return specification;
    }
}
