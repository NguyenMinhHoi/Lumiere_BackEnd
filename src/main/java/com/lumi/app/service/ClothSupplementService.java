package com.lumi.app.service;

import com.lumi.app.service.dto.ClothSupplementDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.ClothSupplement}.
 */
public interface ClothSupplementService {
    /**
     * Save a clothSupplement.
     *
     * @param clothSupplementDTO the entity to save.
     * @return the persisted entity.
     */
    ClothSupplementDTO save(ClothSupplementDTO clothSupplementDTO);

    /**
     * Updates a clothSupplement.
     *
     * @param clothSupplementDTO the entity to update.
     * @return the persisted entity.
     */
    ClothSupplementDTO update(ClothSupplementDTO clothSupplementDTO);

    /**
     * Partially updates a clothSupplement.
     *
     * @param clothSupplementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClothSupplementDTO> partialUpdate(ClothSupplementDTO clothSupplementDTO);

    /**
     * Get the "id" clothSupplement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClothSupplementDTO> findOne(Long id);

    /**
     * Delete the "id" clothSupplement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the clothSupplement corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClothSupplementDTO> search(String query, Pageable pageable);
}
