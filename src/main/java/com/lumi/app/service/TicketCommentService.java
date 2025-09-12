package com.lumi.app.service;

import com.lumi.app.service.dto.TicketCommentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.TicketComment}.
 */
public interface TicketCommentService {
    /**
     * Save a ticketComment.
     *
     * @param ticketCommentDTO the entity to save.
     * @return the persisted entity.
     */
    TicketCommentDTO save(TicketCommentDTO ticketCommentDTO);

    /**
     * Updates a ticketComment.
     *
     * @param ticketCommentDTO the entity to update.
     * @return the persisted entity.
     */
    TicketCommentDTO update(TicketCommentDTO ticketCommentDTO);

    /**
     * Partially updates a ticketComment.
     *
     * @param ticketCommentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TicketCommentDTO> partialUpdate(TicketCommentDTO ticketCommentDTO);

    /**
     * Get all the ticketComments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketCommentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" ticketComment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TicketCommentDTO> findOne(Long id);

    /**
     * Delete the "id" ticketComment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the ticketComment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketCommentDTO> search(String query, Pageable pageable);
}
