package com.lumi.app.service;

import com.lumi.app.service.dto.CampaignDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.Campaign}.
 */
public interface CampaignService {
    /**
     * Save a campaign.
     *
     * @param campaignDTO the entity to save.
     * @return the persisted entity.
     */
    CampaignDTO save(CampaignDTO campaignDTO);

    /**
     * Updates a campaign.
     *
     * @param campaignDTO the entity to update.
     * @return the persisted entity.
     */
    CampaignDTO update(CampaignDTO campaignDTO);

    /**
     * Partially updates a campaign.
     *
     * @param campaignDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CampaignDTO> partialUpdate(CampaignDTO campaignDTO);

    /**
     * Get all the campaigns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CampaignDTO> findAll(Pageable pageable);

    /**
     * Get the "id" campaign.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CampaignDTO> findOne(Long id);

    /**
     * Delete the "id" campaign.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the campaign corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CampaignDTO> search(String query, Pageable pageable);
}
