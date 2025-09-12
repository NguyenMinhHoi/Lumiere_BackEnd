package com.lumi.app.service;

import com.lumi.app.service.dto.TicketFileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.TicketFile}.
 */
public interface TicketFileService {
    /**
     * Save a ticketFile.
     *
     * @param ticketFileDTO the entity to save.
     * @return the persisted entity.
     */
    TicketFileDTO save(TicketFileDTO ticketFileDTO);

    /**
     * Updates a ticketFile.
     *
     * @param ticketFileDTO the entity to update.
     * @return the persisted entity.
     */
    TicketFileDTO update(TicketFileDTO ticketFileDTO);

    /**
     * Partially updates a ticketFile.
     *
     * @param ticketFileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TicketFileDTO> partialUpdate(TicketFileDTO ticketFileDTO);

    /**
     * Get the "id" ticketFile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TicketFileDTO> findOne(Long id);

    /**
     * Delete the "id" ticketFile.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the ticketFile corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketFileDTO> search(String query, Pageable pageable);
}
