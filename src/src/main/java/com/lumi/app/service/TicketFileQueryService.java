package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.TicketFile;
import com.lumi.app.repository.TicketFileRepository;
import com.lumi.app.repository.search.TicketFileSearchRepository;
import com.lumi.app.service.criteria.TicketFileCriteria;
import com.lumi.app.service.dto.TicketFileDTO;
import com.lumi.app.service.mapper.TicketFileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TicketFile} entities in the database.
 * The main input is a {@link TicketFileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TicketFileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketFileQueryService extends QueryService<TicketFile> {

    private static final Logger LOG = LoggerFactory.getLogger(TicketFileQueryService.class);

    private final TicketFileRepository ticketFileRepository;

    private final TicketFileMapper ticketFileMapper;

    private final TicketFileSearchRepository ticketFileSearchRepository;

    public TicketFileQueryService(
        TicketFileRepository ticketFileRepository,
        TicketFileMapper ticketFileMapper,
        TicketFileSearchRepository ticketFileSearchRepository
    ) {
        this.ticketFileRepository = ticketFileRepository;
        this.ticketFileMapper = ticketFileMapper;
        this.ticketFileSearchRepository = ticketFileSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TicketFileDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketFileDTO> findByCriteria(TicketFileCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TicketFile> specification = createSpecification(criteria);
        return ticketFileRepository.findAll(specification, page).map(ticketFileMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketFileCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TicketFile> specification = createSpecification(criteria);
        return ticketFileRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketFileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TicketFile> createSpecification(TicketFileCriteria criteria) {
        Specification<TicketFile> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TicketFile_.id),
                buildRangeSpecification(criteria.getTicketId(), TicketFile_.ticketId),
                buildRangeSpecification(criteria.getUploaderId(), TicketFile_.uploaderId),
                buildStringSpecification(criteria.getFileName(), TicketFile_.fileName),
                buildStringSpecification(criteria.getOriginalName(), TicketFile_.originalName),
                buildStringSpecification(criteria.getContentType(), TicketFile_.contentType),
                buildRangeSpecification(criteria.getCapacity(), TicketFile_.capacity),
                buildSpecification(criteria.getStorageType(), TicketFile_.storageType),
                buildStringSpecification(criteria.getPath(), TicketFile_.path),
                buildStringSpecification(criteria.getUrl(), TicketFile_.url),
                buildStringSpecification(criteria.getChecksum(), TicketFile_.checksum),
                buildSpecification(criteria.getStatus(), TicketFile_.status),
                buildRangeSpecification(criteria.getUploadedAt(), TicketFile_.uploadedAt)
            );
        }
        return specification;
    }
}
