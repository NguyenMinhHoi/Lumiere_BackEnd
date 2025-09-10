package com.lumi.app.service;

import com.lumi.app.domain.AuditHistory;
import com.lumi.app.repository.AuditHistoryRepository;
import com.lumi.app.repository.search.AuditHistorySearchRepository;
import com.lumi.app.service.dto.AuditHistoryDTO;
import com.lumi.app.service.mapper.AuditHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.AuditHistory}.
 */
@Service
@Transactional
public class AuditHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditHistoryService.class);

    private final AuditHistoryRepository auditHistoryRepository;

    private final AuditHistoryMapper auditHistoryMapper;

    private final AuditHistorySearchRepository auditHistorySearchRepository;

    public AuditHistoryService(
        AuditHistoryRepository auditHistoryRepository,
        AuditHistoryMapper auditHistoryMapper,
        AuditHistorySearchRepository auditHistorySearchRepository
    ) {
        this.auditHistoryRepository = auditHistoryRepository;
        this.auditHistoryMapper = auditHistoryMapper;
        this.auditHistorySearchRepository = auditHistorySearchRepository;
    }

    /**
     * Save a auditHistory.
     *
     * @param auditHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public AuditHistoryDTO save(AuditHistoryDTO auditHistoryDTO) {
        LOG.debug("Request to save AuditHistory : {}", auditHistoryDTO);
        AuditHistory auditHistory = auditHistoryMapper.toEntity(auditHistoryDTO);
        auditHistory = auditHistoryRepository.save(auditHistory);
        auditHistorySearchRepository.index(auditHistory);
        return auditHistoryMapper.toDto(auditHistory);
    }

    /**
     * Update a auditHistory.
     *
     * @param auditHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public AuditHistoryDTO update(AuditHistoryDTO auditHistoryDTO) {
        LOG.debug("Request to update AuditHistory : {}", auditHistoryDTO);
        AuditHistory auditHistory = auditHistoryMapper.toEntity(auditHistoryDTO);
        auditHistory = auditHistoryRepository.save(auditHistory);
        auditHistorySearchRepository.index(auditHistory);
        return auditHistoryMapper.toDto(auditHistory);
    }

    /**
     * Partially update a auditHistory.
     *
     * @param auditHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AuditHistoryDTO> partialUpdate(AuditHistoryDTO auditHistoryDTO) {
        LOG.debug("Request to partially update AuditHistory : {}", auditHistoryDTO);

        return auditHistoryRepository
            .findById(auditHistoryDTO.getId())
            .map(existingAuditHistory -> {
                auditHistoryMapper.partialUpdate(existingAuditHistory, auditHistoryDTO);

                return existingAuditHistory;
            })
            .map(auditHistoryRepository::save)
            .map(savedAuditHistory -> {
                auditHistorySearchRepository.index(savedAuditHistory);
                return savedAuditHistory;
            })
            .map(auditHistoryMapper::toDto);
    }

    /**
     * Get one auditHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AuditHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get AuditHistory : {}", id);
        return auditHistoryRepository.findById(id).map(auditHistoryMapper::toDto);
    }

    /**
     * Delete the auditHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AuditHistory : {}", id);
        auditHistoryRepository.deleteById(id);
        auditHistorySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the auditHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AuditHistoryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of AuditHistories for query {}", query);
        return auditHistorySearchRepository.search(query, pageable).map(auditHistoryMapper::toDto);
    }
}
