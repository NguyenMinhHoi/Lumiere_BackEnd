package com.lumi.app.service;

import com.lumi.app.domain.Ticket;
import com.lumi.app.repository.TicketRepository;
import com.lumi.app.repository.search.TicketSearchRepository;
import com.lumi.app.service.criteria.TicketCriteria;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.mapper.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Ticket} entities in the database.
 * The main input is a {@link TicketCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TicketDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketQueryService extends QueryService<Ticket> {

    private static final Logger LOG = LoggerFactory.getLogger(TicketQueryService.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    private final TicketSearchRepository ticketSearchRepository;

    public TicketQueryService(TicketRepository ticketRepository, TicketMapper ticketMapper, TicketSearchRepository ticketSearchRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.ticketSearchRepository = ticketSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TicketDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketDTO> findByCriteria(TicketCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.findAll(specification, page).map(ticketMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ticket> createSpecification(TicketCriteria criteria) {
        Specification<Ticket> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Ticket_.id),
                buildRangeSpecification(criteria.getCustomerId(), Ticket_.customerId),
                buildRangeSpecification(criteria.getSlaPlanId(), Ticket_.slaPlanId),
                buildRangeSpecification(criteria.getOrderId(), Ticket_.orderId),
                buildRangeSpecification(criteria.getAssigneeEmployeeId(), Ticket_.assigneeEmployeeId),
                buildStringSpecification(criteria.getCode(), Ticket_.code),
                buildStringSpecification(criteria.getSubject(), Ticket_.subject),
                buildSpecification(criteria.getStatus(), Ticket_.status),
                buildSpecification(criteria.getPriority(), Ticket_.priority),
                buildSpecification(criteria.getChannel(), Ticket_.channel),
                buildRangeSpecification(criteria.getOpenedAt(), Ticket_.openedAt),
                buildRangeSpecification(criteria.getFirstResponseAt(), Ticket_.firstResponseAt),
                buildRangeSpecification(criteria.getResolvedAt(), Ticket_.resolvedAt),
                buildRangeSpecification(criteria.getSlaDueAt(), Ticket_.slaDueAt)
            );
        }
        return specification;
    }
}
