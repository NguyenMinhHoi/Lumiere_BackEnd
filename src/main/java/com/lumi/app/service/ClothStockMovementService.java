package com.lumi.app.service;

import com.lumi.app.service.dto.ClothStockMovementDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.ClothStockMovement}.
 */
public interface ClothStockMovementService {
    /**
     * Save a clothStockMovement.
     *
     * @param clothStockMovementDTO the entity to save.
     * @return the persisted entity.
     */
    ClothStockMovementDTO save(ClothStockMovementDTO clothStockMovementDTO);

    /**
     * Updates a clothStockMovement.
     *
     * @param clothStockMovementDTO the entity to update.
     * @return the persisted entity.
     */
    ClothStockMovementDTO update(ClothStockMovementDTO clothStockMovementDTO);

    /**
     * Partially updates a clothStockMovement.
     *
     * @param clothStockMovementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClothStockMovementDTO> partialUpdate(ClothStockMovementDTO clothStockMovementDTO);

    /**
     * Get all the clothStockMovements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClothStockMovementDTO> findAll(Pageable pageable);

    /**
     * Get the "id" clothStockMovement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClothStockMovementDTO> findOne(Long id);

    /**
     * Delete the "id" clothStockMovement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the clothStockMovement corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClothStockMovementDTO> search(String query, Pageable pageable);
}
