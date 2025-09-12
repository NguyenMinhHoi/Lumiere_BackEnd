package com.lumi.app.service;

import com.lumi.app.service.dto.IntegrationWebhookDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.IntegrationWebhook}.
 */
public interface IntegrationWebhookService {
    /**
     * Save a integrationWebhook.
     *
     * @param integrationWebhookDTO the entity to save.
     * @return the persisted entity.
     */
    IntegrationWebhookDTO save(IntegrationWebhookDTO integrationWebhookDTO);

    /**
     * Updates a integrationWebhook.
     *
     * @param integrationWebhookDTO the entity to update.
     * @return the persisted entity.
     */
    IntegrationWebhookDTO update(IntegrationWebhookDTO integrationWebhookDTO);

    /**
     * Partially updates a integrationWebhook.
     *
     * @param integrationWebhookDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<IntegrationWebhookDTO> partialUpdate(IntegrationWebhookDTO integrationWebhookDTO);

    /**
     * Get all the integrationWebhooks.
     *
     * @return the list of entities.
     */
    List<IntegrationWebhookDTO> findAll();

    /**
     * Get the "id" integrationWebhook.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<IntegrationWebhookDTO> findOne(Long id);

    /**
     * Delete the "id" integrationWebhook.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the integrationWebhook corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<IntegrationWebhookDTO> search(String query);
}
