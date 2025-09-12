package com.lumi.app.service;

import com.lumi.app.service.dto.OrderItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.OrderItem}.
 */
public interface OrderItemService {
    /**
     * Save a orderItem.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    OrderItemDTO save(OrderItemDTO orderItemDTO);

    /**
     * Updates a orderItem.
     *
     * @param orderItemDTO the entity to update.
     * @return the persisted entity.
     */
    OrderItemDTO update(OrderItemDTO orderItemDTO);

    /**
     * Partially updates a orderItem.
     *
     * @param orderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrderItemDTO> partialUpdate(OrderItemDTO orderItemDTO);

    /**
     * Get the "id" orderItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderItemDTO> findOne(Long id);

    /**
     * Delete the "id" orderItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the orderItem corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderItemDTO> search(String query, Pageable pageable);
}
