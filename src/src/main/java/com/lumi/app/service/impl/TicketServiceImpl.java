package com.lumi.app.service.impl;

import com.lumi.app.domain.Ticket;
import com.lumi.app.repository.TicketRepository;
import com.lumi.app.repository.search.TicketSearchRepository;
import com.lumi.app.service.TicketService;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.mapper.TicketMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Ticket}.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    private final TicketSearchRepository ticketSearchRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, TicketSearchRepository ticketSearchRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.ticketSearchRepository = ticketSearchRepository;
    }

    @Override
    public TicketDTO save(TicketDTO ticketDTO) {
        LOG.debug("Request to save Ticket : {}", ticketDTO);
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket = ticketRepository.save(ticket);
        ticketSearchRepository.index(ticket);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public TicketDTO update(TicketDTO ticketDTO) {
        LOG.debug("Request to update Ticket : {}", ticketDTO);
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket = ticketRepository.save(ticket);
        ticketSearchRepository.index(ticket);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public Optional<TicketDTO> partialUpdate(TicketDTO ticketDTO) {
        LOG.debug("Request to partially update Ticket : {}", ticketDTO);

        return ticketRepository
            .findById(ticketDTO.getId())
            .map(existingTicket -> {
                ticketMapper.partialUpdate(existingTicket, ticketDTO);

                return existingTicket;
            })
            .map(ticketRepository::save)
            .map(savedTicket -> {
                ticketSearchRepository.index(savedTicket);
                return savedTicket;
            })
            .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDTO> findOne(Long id) {
        LOG.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id).map(ticketMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Ticket : {}", id);
        ticketRepository.deleteById(id);
        ticketSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Tickets for query {}", query);
        return ticketSearchRepository.search(query, pageable).map(ticketMapper::toDto);
    }
}
