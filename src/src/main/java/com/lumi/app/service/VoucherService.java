package com.lumi.app.service;

import com.lumi.app.service.dto.VoucherDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.Voucher}.
 */
public interface VoucherService {
    /**
     * Save a voucher.
     *
     * @param voucherDTO the entity to save.
     * @return the persisted entity.
     */
    VoucherDTO save(VoucherDTO voucherDTO);

    /**
     * Updates a voucher.
     *
     * @param voucherDTO the entity to update.
     * @return the persisted entity.
     */
    VoucherDTO update(VoucherDTO voucherDTO);

    /**
     * Partially updates a voucher.
     *
     * @param voucherDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VoucherDTO> partialUpdate(VoucherDTO voucherDTO);

    /**
     * Get the "id" voucher.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VoucherDTO> findOne(Long id);

    /**
     * Delete the "id" voucher.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the voucher corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VoucherDTO> search(String query, Pageable pageable);
}
