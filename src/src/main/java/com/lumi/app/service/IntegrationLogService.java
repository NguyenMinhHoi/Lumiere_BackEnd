package com.lumi.app.service;

import com.lumi.app.service.dto.IntegrationLogDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.IntegrationLog}.
 */
public interface IntegrationLogService {
    /**
     * Save a integrationLog.
     *
     * @param integrationLogDTO the entity to save.
     * @return the persisted entity.
     */
    IntegrationLogDTO save(IntegrationLogDTO integrationLogDTO);

    /**
     * Updates a integrationLog.
     *
     * @param integrationLogDTO the entity to update.
     * @return the persisted entity.
     */
    IntegrationLogDTO update(IntegrationLogDTO integrationLogDTO);

    /**
     * Partially updates a integrationLog.
     *
     * @param integrationLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<IntegrationLogDTO> partialUpdate(IntegrationLogDTO integrationLogDTO);

    /**
     * Get the "id" integrationLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<IntegrationLogDTO> findOne(Long id);

    /**
     * Delete the "id" integrationLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the integrationLog corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IntegrationLogDTO> search(String query, Pageable pageable);
}
