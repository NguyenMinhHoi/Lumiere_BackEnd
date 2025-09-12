package com.lumi.app.service;

import com.lumi.app.service.dto.CustomerDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.Customer}.
 */
public interface CustomerService {
    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    CustomerDTO save(CustomerDTO customerDTO);

    /**
     * Updates a customer.
     *
     * @param customerDTO the entity to update.
     * @return the persisted entity.
     */
    CustomerDTO update(CustomerDTO customerDTO);

    /**
     * Partially updates a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CustomerDTO> partialUpdate(CustomerDTO customerDTO);

    /**
     * Get the "id" customer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CustomerDTO> findOne(Long id);

    /**
     * Delete the "id" customer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the customer corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CustomerDTO> search(String query, Pageable pageable);
}
