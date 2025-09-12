package com.lumi.app.service;

import com.lumi.app.service.dto.VoucherRedemptionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.VoucherRedemption}.
 */
public interface VoucherRedemptionService {
    /**
     * Save a voucherRedemption.
     *
     * @param voucherRedemptionDTO the entity to save.
     * @return the persisted entity.
     */
    VoucherRedemptionDTO save(VoucherRedemptionDTO voucherRedemptionDTO);

    /**
     * Updates a voucherRedemption.
     *
     * @param voucherRedemptionDTO the entity to update.
     * @return the persisted entity.
     */
    VoucherRedemptionDTO update(VoucherRedemptionDTO voucherRedemptionDTO);

    /**
     * Partially updates a voucherRedemption.
     *
     * @param voucherRedemptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VoucherRedemptionDTO> partialUpdate(VoucherRedemptionDTO voucherRedemptionDTO);

    /**
     * Get all the voucherRedemptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VoucherRedemptionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" voucherRedemption.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VoucherRedemptionDTO> findOne(Long id);

    /**
     * Delete the "id" voucherRedemption.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the voucherRedemption corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VoucherRedemptionDTO> search(String query, Pageable pageable);
}
