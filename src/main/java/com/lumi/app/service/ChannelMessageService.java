package com.lumi.app.service;

import com.lumi.app.service.dto.ChannelMessageDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.ChannelMessage}.
 */
public interface ChannelMessageService {
    /**
     * Save a channelMessage.
     *
     * @param channelMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ChannelMessageDTO save(ChannelMessageDTO channelMessageDTO);

    /**
     * Updates a channelMessage.
     *
     * @param channelMessageDTO the entity to update.
     * @return the persisted entity.
     */
    ChannelMessageDTO update(ChannelMessageDTO channelMessageDTO);

    /**
     * Partially updates a channelMessage.
     *
     * @param channelMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ChannelMessageDTO> partialUpdate(ChannelMessageDTO channelMessageDTO);

    /**
     * Get all the channelMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ChannelMessageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" channelMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChannelMessageDTO> findOne(Long id);

    /**
     * Delete the "id" channelMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the channelMessage corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ChannelMessageDTO> search(String query, Pageable pageable);
}
