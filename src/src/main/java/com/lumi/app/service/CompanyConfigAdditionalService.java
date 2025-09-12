package com.lumi.app.service;

import com.lumi.app.service.dto.CompanyConfigAdditionalDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.CompanyConfigAdditional}.
 */
public interface CompanyConfigAdditionalService {
    /**
     * Save a companyConfigAdditional.
     *
     * @param companyConfigAdditionalDTO the entity to save.
     * @return the persisted entity.
     */
    CompanyConfigAdditionalDTO save(CompanyConfigAdditionalDTO companyConfigAdditionalDTO);

    /**
     * Updates a companyConfigAdditional.
     *
     * @param companyConfigAdditionalDTO the entity to update.
     * @return the persisted entity.
     */
    CompanyConfigAdditionalDTO update(CompanyConfigAdditionalDTO companyConfigAdditionalDTO);

    /**
     * Partially updates a companyConfigAdditional.
     *
     * @param companyConfigAdditionalDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CompanyConfigAdditionalDTO> partialUpdate(CompanyConfigAdditionalDTO companyConfigAdditionalDTO);

    /**
     * Get all the companyConfigAdditionals.
     *
     * @return the list of entities.
     */
    List<CompanyConfigAdditionalDTO> findAll();

    /**
     * Get the "id" companyConfigAdditional.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CompanyConfigAdditionalDTO> findOne(Long id);

    /**
     * Delete the "id" companyConfigAdditional.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the companyConfigAdditional corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<CompanyConfigAdditionalDTO> search(String query);
}
