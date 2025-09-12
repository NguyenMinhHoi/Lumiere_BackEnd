package com.lumi.app.service;

import com.lumi.app.service.dto.SupplementDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.Supplement}.
 */
public interface SupplementService {
    /**
     * Save a supplement.
     *
     * @param supplementDTO the entity to save.
     * @return the persisted entity.
     */
    SupplementDTO save(SupplementDTO supplementDTO);

    /**
     * Updates a supplement.
     *
     * @param supplementDTO the entity to update.
     * @return the persisted entity.
     */
    SupplementDTO update(SupplementDTO supplementDTO);

    /**
     * Partially updates a supplement.
     *
     * @param supplementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SupplementDTO> partialUpdate(SupplementDTO supplementDTO);

    /**
     * Get the "id" supplement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SupplementDTO> findOne(Long id);

    /**
     * Delete the "id" supplement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the supplement corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SupplementDTO> search(String query, Pageable pageable);
}
