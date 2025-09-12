package com.lumi.app.service.impl;

import com.lumi.app.domain.TicketFile;
import com.lumi.app.repository.TicketFileRepository;
import com.lumi.app.repository.search.TicketFileSearchRepository;
import com.lumi.app.service.TicketFileService;
import com.lumi.app.service.dto.TicketFileDTO;
import com.lumi.app.service.mapper.TicketFileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link TicketFile}.
 */
@Service
@Transactional
public class TicketFileServiceImpl implements TicketFileService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketFileServiceImpl.class);

    private final TicketFileRepository ticketFileRepository;

    private final TicketFileMapper ticketFileMapper;

    private final TicketFileSearchRepository ticketFileSearchRepository;

    public TicketFileServiceImpl(
        TicketFileRepository ticketFileRepository,
        TicketFileMapper ticketFileMapper,
        TicketFileSearchRepository ticketFileSearchRepository
    ) {
        this.ticketFileRepository = ticketFileRepository;
        this.ticketFileMapper = ticketFileMapper;
        this.ticketFileSearchRepository = ticketFileSearchRepository;
    }

    @Override
    public TicketFileDTO save(TicketFileDTO ticketFileDTO) {
        LOG.debug("Request to save TicketFile : {}", ticketFileDTO);
        TicketFile ticketFile = ticketFileMapper.toEntity(ticketFileDTO);
        ticketFile = ticketFileRepository.save(ticketFile);
        ticketFileSearchRepository.index(ticketFile);
        return ticketFileMapper.toDto(ticketFile);
    }

    @Override
    public TicketFileDTO update(TicketFileDTO ticketFileDTO) {
        LOG.debug("Request to update TicketFile : {}", ticketFileDTO);
        TicketFile ticketFile = ticketFileMapper.toEntity(ticketFileDTO);
        ticketFile = ticketFileRepository.save(ticketFile);
        ticketFileSearchRepository.index(ticketFile);
        return ticketFileMapper.toDto(ticketFile);
    }

    @Override
    public Optional<TicketFileDTO> partialUpdate(TicketFileDTO ticketFileDTO) {
        LOG.debug("Request to partially update TicketFile : {}", ticketFileDTO);

        return ticketFileRepository
            .findById(ticketFileDTO.getId())
            .map(existingTicketFile -> {
                ticketFileMapper.partialUpdate(existingTicketFile, ticketFileDTO);

                return existingTicketFile;
            })
            .map(ticketFileRepository::save)
            .map(savedTicketFile -> {
                ticketFileSearchRepository.index(savedTicketFile);
                return savedTicketFile;
            })
            .map(ticketFileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketFileDTO> findOne(Long id) {
        LOG.debug("Request to get TicketFile : {}", id);
        return ticketFileRepository.findById(id).map(ticketFileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TicketFile : {}", id);
        ticketFileRepository.deleteById(id);
        ticketFileSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketFileDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of TicketFiles for query {}", query);
        return ticketFileSearchRepository.search(query, pageable).map(ticketFileMapper::toDto);
    }
}
