package com.lumi.app.service;

import com.lumi.app.service.dto.CompanyConfigDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.CompanyConfig}.
 */
public interface CompanyConfigService {
    /**
     * Save a companyConfig.
     *
     * @param companyConfigDTO the entity to save.
     * @return the persisted entity.
     */
    CompanyConfigDTO save(CompanyConfigDTO companyConfigDTO);

    /**
     * Updates a companyConfig.
     *
     * @param companyConfigDTO the entity to update.
     * @return the persisted entity.
     */
    CompanyConfigDTO update(CompanyConfigDTO companyConfigDTO);

    /**
     * Partially updates a companyConfig.
     *
     * @param companyConfigDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CompanyConfigDTO> partialUpdate(CompanyConfigDTO companyConfigDTO);

    /**
     * Get all the companyConfigs.
     *
     * @return the list of entities.
     */
    List<CompanyConfigDTO> findAll();

    /**
     * Get the "id" companyConfig.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CompanyConfigDTO> findOne(Long id);

    /**
     * Delete the "id" companyConfig.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the companyConfig corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<CompanyConfigDTO> search(String query);
}
