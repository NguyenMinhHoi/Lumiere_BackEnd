package com.lumi.app.service;

import com.lumi.app.service.dto.ClothInventoryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.ClothInventory}.
 */
public interface ClothInventoryService {
    /**
     * Save a clothInventory.
     *
     * @param clothInventoryDTO the entity to save.
     * @return the persisted entity.
     */
    ClothInventoryDTO save(ClothInventoryDTO clothInventoryDTO);

    /**
     * Updates a clothInventory.
     *
     * @param clothInventoryDTO the entity to update.
     * @return the persisted entity.
     */
    ClothInventoryDTO update(ClothInventoryDTO clothInventoryDTO);

    /**
     * Partially updates a clothInventory.
     *
     * @param clothInventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClothInventoryDTO> partialUpdate(ClothInventoryDTO clothInventoryDTO);

    /**
     * Get all the clothInventories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClothInventoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" clothInventory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClothInventoryDTO> findOne(Long id);

    /**
     * Delete the "id" clothInventory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the clothInventory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClothInventoryDTO> search(String query, Pageable pageable);
}
