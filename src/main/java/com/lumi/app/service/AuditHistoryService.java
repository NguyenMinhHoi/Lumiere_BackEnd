package com.lumi.app.service;

import com.lumi.app.service.dto.AuditHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.AuditHistory}.
 */
public interface AuditHistoryService {
    /**
     * Save a auditHistory.
     *
     * @param auditHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    AuditHistoryDTO save(AuditHistoryDTO auditHistoryDTO);

    /**
     * Updates a auditHistory.
     *
     * @param auditHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    AuditHistoryDTO update(AuditHistoryDTO auditHistoryDTO);

    /**
     * Partially updates a auditHistory.
     *
     * @param auditHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AuditHistoryDTO> partialUpdate(AuditHistoryDTO auditHistoryDTO);

    /**
     * Get the "id" auditHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AuditHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" auditHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the auditHistory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AuditHistoryDTO> search(String query, Pageable pageable);
}
