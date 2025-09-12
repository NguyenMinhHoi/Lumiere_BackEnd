package com.lumi.app.service;

import com.lumi.app.service.dto.SlaPlanDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.SlaPlan}.
 */
public interface SlaPlanService {
    /**
     * Save a slaPlan.
     *
     * @param slaPlanDTO the entity to save.
     * @return the persisted entity.
     */
    SlaPlanDTO save(SlaPlanDTO slaPlanDTO);

    /**
     * Updates a slaPlan.
     *
     * @param slaPlanDTO the entity to update.
     * @return the persisted entity.
     */
    SlaPlanDTO update(SlaPlanDTO slaPlanDTO);

    /**
     * Partially updates a slaPlan.
     *
     * @param slaPlanDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SlaPlanDTO> partialUpdate(SlaPlanDTO slaPlanDTO);

    /**
     * Get all the slaPlans.
     *
     * @return the list of entities.
     */
    List<SlaPlanDTO> findAll();

    /**
     * Get the "id" slaPlan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SlaPlanDTO> findOne(Long id);

    /**
     * Delete the "id" slaPlan.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the slaPlan corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<SlaPlanDTO> search(String query);
}
