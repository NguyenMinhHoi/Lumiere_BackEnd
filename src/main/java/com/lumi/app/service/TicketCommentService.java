package com.lumi.app.service;

import com.lumi.app.domain.TicketComment;
import com.lumi.app.repository.TicketCommentRepository;
import com.lumi.app.repository.search.TicketCommentSearchRepository;
import com.lumi.app.service.dto.TicketCommentDTO;
import com.lumi.app.service.mapper.TicketCommentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.TicketComment}.
 */
@Service
@Transactional
public class TicketCommentService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCommentService.class);

    private final TicketCommentRepository ticketCommentRepository;

    private final TicketCommentMapper ticketCommentMapper;

    private final TicketCommentSearchRepository ticketCommentSearchRepository;

    public TicketCommentService(
        TicketCommentRepository ticketCommentRepository,
        TicketCommentMapper ticketCommentMapper,
        TicketCommentSearchRepository ticketCommentSearchRepository
    ) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.ticketCommentMapper = ticketCommentMapper;
        this.ticketCommentSearchRepository = ticketCommentSearchRepository;
    }

    /**
     * Save a ticketComment.
     *
     * @param ticketCommentDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketCommentDTO save(TicketCommentDTO ticketCommentDTO) {
        LOG.debug("Request to save TicketComment : {}", ticketCommentDTO);
        TicketComment ticketComment = ticketCommentMapper.toEntity(ticketCommentDTO);
        ticketComment = ticketCommentRepository.save(ticketComment);
        ticketCommentSearchRepository.index(ticketComment);
        return ticketCommentMapper.toDto(ticketComment);
    }

    /**
     * Update a ticketComment.
     *
     * @param ticketCommentDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketCommentDTO update(TicketCommentDTO ticketCommentDTO) {
        LOG.debug("Request to update TicketComment : {}", ticketCommentDTO);
        TicketComment ticketComment = ticketCommentMapper.toEntity(ticketCommentDTO);
        ticketComment = ticketCommentRepository.save(ticketComment);
        ticketCommentSearchRepository.index(ticketComment);
        return ticketCommentMapper.toDto(ticketComment);
    }

    /**
     * Partially update a ticketComment.
     *
     * @param ticketCommentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TicketCommentDTO> partialUpdate(TicketCommentDTO ticketCommentDTO) {
        LOG.debug("Request to partially update TicketComment : {}", ticketCommentDTO);

        return ticketCommentRepository
            .findById(ticketCommentDTO.getId())
            .map(existingTicketComment -> {
                ticketCommentMapper.partialUpdate(existingTicketComment, ticketCommentDTO);

                return existingTicketComment;
            })
            .map(ticketCommentRepository::save)
            .map(savedTicketComment -> {
                ticketCommentSearchRepository.index(savedTicketComment);
                return savedTicketComment;
            })
            .map(ticketCommentMapper::toDto);
    }

    /**
     * Get all the ticketComments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketCommentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TicketComments");
        return ticketCommentRepository.findAll(pageable).map(ticketCommentMapper::toDto);
    }

    /**
     * Get all the ticketComments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TicketCommentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ticketCommentRepository.findAllWithEagerRelationships(pageable).map(ticketCommentMapper::toDto);
    }

    /**
     * Get one ticketComment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TicketCommentDTO> findOne(Long id) {
        LOG.debug("Request to get TicketComment : {}", id);
        return ticketCommentRepository.findOneWithEagerRelationships(id).map(ticketCommentMapper::toDto);
    }

    /**
     * Delete the ticketComment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TicketComment : {}", id);
        ticketCommentRepository.deleteById(id);
        ticketCommentSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the ticketComment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketCommentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of TicketComments for query {}", query);
        return ticketCommentSearchRepository.search(query, pageable).map(ticketCommentMapper::toDto);
    }
}
