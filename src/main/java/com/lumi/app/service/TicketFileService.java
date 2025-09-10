package com.lumi.app.service;

import com.lumi.app.domain.TicketFile;
import com.lumi.app.repository.TicketFileRepository;
import com.lumi.app.repository.search.TicketFileSearchRepository;
import com.lumi.app.service.dto.TicketFileDTO;
import com.lumi.app.service.mapper.TicketFileMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.TicketFile}.
 */
@Service
@Transactional
public class TicketFileService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketFileService.class);

    private final TicketFileRepository ticketFileRepository;

    private final TicketFileMapper ticketFileMapper;

    private final TicketFileSearchRepository ticketFileSearchRepository;

    public TicketFileService(
        TicketFileRepository ticketFileRepository,
        TicketFileMapper ticketFileMapper,
        TicketFileSearchRepository ticketFileSearchRepository
    ) {
        this.ticketFileRepository = ticketFileRepository;
        this.ticketFileMapper = ticketFileMapper;
        this.ticketFileSearchRepository = ticketFileSearchRepository;
    }

    /**
     * Save a ticketFile.
     *
     * @param ticketFileDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketFileDTO save(TicketFileDTO ticketFileDTO) {
        LOG.debug("Request to save TicketFile : {}", ticketFileDTO);
        TicketFile ticketFile = ticketFileMapper.toEntity(ticketFileDTO);
        ticketFile = ticketFileRepository.save(ticketFile);
        ticketFileSearchRepository.index(ticketFile);
        return ticketFileMapper.toDto(ticketFile);
    }

    /**
     * Update a ticketFile.
     *
     * @param ticketFileDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketFileDTO update(TicketFileDTO ticketFileDTO) {
        LOG.debug("Request to update TicketFile : {}", ticketFileDTO);
        TicketFile ticketFile = ticketFileMapper.toEntity(ticketFileDTO);
        ticketFile = ticketFileRepository.save(ticketFile);
        ticketFileSearchRepository.index(ticketFile);
        return ticketFileMapper.toDto(ticketFile);
    }

    /**
     * Partially update a ticketFile.
     *
     * @param ticketFileDTO the entity to update partially.
     * @return the persisted entity.
     */
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

    /**
     * Get all the ticketFiles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TicketFileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ticketFileRepository.findAllWithEagerRelationships(pageable).map(ticketFileMapper::toDto);
    }

    /**
     * Get one ticketFile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TicketFileDTO> findOne(Long id) {
        LOG.debug("Request to get TicketFile : {}", id);
        return ticketFileRepository.findOneWithEagerRelationships(id).map(ticketFileMapper::toDto);
    }

    /**
     * Delete the ticketFile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TicketFile : {}", id);
        ticketFileRepository.deleteById(id);
        ticketFileSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the ticketFile corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketFileDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of TicketFiles for query {}", query);
        return ticketFileSearchRepository.search(query, pageable).map(ticketFileMapper::toDto);
    }
}
