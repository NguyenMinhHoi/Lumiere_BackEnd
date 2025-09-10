package com.lumi.app.service;

import com.lumi.app.domain.ChannelMessage;
import com.lumi.app.repository.ChannelMessageRepository;
import com.lumi.app.repository.search.ChannelMessageSearchRepository;
import com.lumi.app.service.dto.ChannelMessageDTO;
import com.lumi.app.service.mapper.ChannelMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ChannelMessage}.
 */
@Service
@Transactional
public class ChannelMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelMessageService.class);

    private final ChannelMessageRepository channelMessageRepository;

    private final ChannelMessageMapper channelMessageMapper;

    private final ChannelMessageSearchRepository channelMessageSearchRepository;

    public ChannelMessageService(
        ChannelMessageRepository channelMessageRepository,
        ChannelMessageMapper channelMessageMapper,
        ChannelMessageSearchRepository channelMessageSearchRepository
    ) {
        this.channelMessageRepository = channelMessageRepository;
        this.channelMessageMapper = channelMessageMapper;
        this.channelMessageSearchRepository = channelMessageSearchRepository;
    }

    /**
     * Save a channelMessage.
     *
     * @param channelMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public ChannelMessageDTO save(ChannelMessageDTO channelMessageDTO) {
        LOG.debug("Request to save ChannelMessage : {}", channelMessageDTO);
        ChannelMessage channelMessage = channelMessageMapper.toEntity(channelMessageDTO);
        channelMessage = channelMessageRepository.save(channelMessage);
        channelMessageSearchRepository.index(channelMessage);
        return channelMessageMapper.toDto(channelMessage);
    }

    /**
     * Update a channelMessage.
     *
     * @param channelMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public ChannelMessageDTO update(ChannelMessageDTO channelMessageDTO) {
        LOG.debug("Request to update ChannelMessage : {}", channelMessageDTO);
        ChannelMessage channelMessage = channelMessageMapper.toEntity(channelMessageDTO);
        channelMessage = channelMessageRepository.save(channelMessage);
        channelMessageSearchRepository.index(channelMessage);
        return channelMessageMapper.toDto(channelMessage);
    }

    /**
     * Partially update a channelMessage.
     *
     * @param channelMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChannelMessageDTO> partialUpdate(ChannelMessageDTO channelMessageDTO) {
        LOG.debug("Request to partially update ChannelMessage : {}", channelMessageDTO);

        return channelMessageRepository
            .findById(channelMessageDTO.getId())
            .map(existingChannelMessage -> {
                channelMessageMapper.partialUpdate(existingChannelMessage, channelMessageDTO);

                return existingChannelMessage;
            })
            .map(channelMessageRepository::save)
            .map(savedChannelMessage -> {
                channelMessageSearchRepository.index(savedChannelMessage);
                return savedChannelMessage;
            })
            .map(channelMessageMapper::toDto);
    }

    /**
     * Get all the channelMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChannelMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChannelMessages");
        return channelMessageRepository.findAll(pageable).map(channelMessageMapper::toDto);
    }

    /**
     * Get all the channelMessages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ChannelMessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return channelMessageRepository.findAllWithEagerRelationships(pageable).map(channelMessageMapper::toDto);
    }

    /**
     * Get one channelMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChannelMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ChannelMessage : {}", id);
        return channelMessageRepository.findOneWithEagerRelationships(id).map(channelMessageMapper::toDto);
    }

    /**
     * Delete the channelMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ChannelMessage : {}", id);
        channelMessageRepository.deleteById(id);
        channelMessageSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the channelMessage corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChannelMessageDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ChannelMessages for query {}", query);
        return channelMessageSearchRepository.search(query, pageable).map(channelMessageMapper::toDto);
    }
}
