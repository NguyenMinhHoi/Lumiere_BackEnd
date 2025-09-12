package com.lumi.app.service;

import com.lumi.app.service.dto.ProductVariantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.ProductVariant}.
 */
public interface ProductVariantService {
    /**
     * Save a productVariant.
     *
     * @param productVariantDTO the entity to save.
     * @return the persisted entity.
     */
    ProductVariantDTO save(ProductVariantDTO productVariantDTO);

    /**
     * Updates a productVariant.
     *
     * @param productVariantDTO the entity to update.
     * @return the persisted entity.
     */
    ProductVariantDTO update(ProductVariantDTO productVariantDTO);

    /**
     * Partially updates a productVariant.
     *
     * @param productVariantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductVariantDTO> partialUpdate(ProductVariantDTO productVariantDTO);

    /**
     * Get the "id" productVariant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductVariantDTO> findOne(Long id);

    /**
     * Delete the "id" productVariant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the productVariant corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductVariantDTO> search(String query, Pageable pageable);
}
