package com.lumi.app.service.impl;

import com.lumi.app.domain.TicketComment;
import com.lumi.app.repository.TicketCommentRepository;
import com.lumi.app.repository.search.TicketCommentSearchRepository;
import com.lumi.app.service.TicketCommentService;
import com.lumi.app.service.dto.TicketCommentDTO;
import com.lumi.app.service.mapper.TicketCommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link TicketComment}.
 */
@Service
@Transactional
public class TicketCommentServiceImpl implements TicketCommentService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCommentServiceImpl.class);

    private final TicketCommentRepository ticketCommentRepository;

    private final TicketCommentMapper ticketCommentMapper;

    private final TicketCommentSearchRepository ticketCommentSearchRepository;

    public TicketCommentServiceImpl(
        TicketCommentRepository ticketCommentRepository,
        TicketCommentMapper ticketCommentMapper,
        TicketCommentSearchRepository ticketCommentSearchRepository
    ) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.ticketCommentMapper = ticketCommentMapper;
        this.ticketCommentSearchRepository = ticketCommentSearchRepository;
    }

    @Override
    public TicketCommentDTO save(TicketCommentDTO ticketCommentDTO) {
        LOG.debug("Request to save TicketComment : {}", ticketCommentDTO);
        TicketComment ticketComment = ticketCommentMapper.toEntity(ticketCommentDTO);
        ticketComment = ticketCommentRepository.save(ticketComment);
        ticketCommentSearchRepository.index(ticketComment);
        return ticketCommentMapper.toDto(ticketComment);
    }

    @Override
    public TicketCommentDTO update(TicketCommentDTO ticketCommentDTO) {
        LOG.debug("Request to update TicketComment : {}", ticketCommentDTO);
        TicketComment ticketComment = ticketCommentMapper.toEntity(ticketCommentDTO);
        ticketComment = ticketCommentRepository.save(ticketComment);
        ticketCommentSearchRepository.index(ticketComment);
        return ticketCommentMapper.toDto(ticketComment);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Page<TicketCommentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TicketComments");
        return ticketCommentRepository.findAll(pageable).map(ticketCommentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketCommentDTO> findOne(Long id) {
        LOG.debug("Request to get TicketComment : {}", id);
        return ticketCommentRepository.findById(id).map(ticketCommentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TicketComment : {}", id);
        ticketCommentRepository.deleteById(id);
        ticketCommentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketCommentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of TicketComments for query {}", query);
        return ticketCommentSearchRepository.search(query, pageable).map(ticketCommentMapper::toDto);
    }
}
