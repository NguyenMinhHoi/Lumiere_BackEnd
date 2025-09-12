package com.lumi.app.service.impl;

import com.lumi.app.domain.ChannelMessage;
import com.lumi.app.repository.ChannelMessageRepository;
import com.lumi.app.repository.search.ChannelMessageSearchRepository;
import com.lumi.app.service.ChannelMessageService;
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
public class ChannelMessageServiceImpl implements ChannelMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelMessageServiceImpl.class);

    private final ChannelMessageRepository channelMessageRepository;

    private final ChannelMessageMapper channelMessageMapper;

    private final ChannelMessageSearchRepository channelMessageSearchRepository;

    public ChannelMessageServiceImpl(
        ChannelMessageRepository channelMessageRepository,
        ChannelMessageMapper channelMessageMapper,
        ChannelMessageSearchRepository channelMessageSearchRepository
    ) {
        this.channelMessageRepository = channelMessageRepository;
        this.channelMessageMapper = channelMessageMapper;
        this.channelMessageSearchRepository = channelMessageSearchRepository;
    }

    @Override
    public ChannelMessageDTO save(ChannelMessageDTO channelMessageDTO) {
        LOG.debug("Request to save ChannelMessage : {}", channelMessageDTO);
        ChannelMessage channelMessage = channelMessageMapper.toEntity(channelMessageDTO);
        channelMessage = channelMessageRepository.save(channelMessage);
        channelMessageSearchRepository.index(channelMessage);
        return channelMessageMapper.toDto(channelMessage);
    }

    @Override
    public ChannelMessageDTO update(ChannelMessageDTO channelMessageDTO) {
        LOG.debug("Request to update ChannelMessage : {}", channelMessageDTO);
        ChannelMessage channelMessage = channelMessageMapper.toEntity(channelMessageDTO);
        channelMessage = channelMessageRepository.save(channelMessage);
        channelMessageSearchRepository.index(channelMessage);
        return channelMessageMapper.toDto(channelMessage);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChannelMessages");
        return channelMessageRepository.findAll(pageable).map(channelMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChannelMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ChannelMessage : {}", id);
        return channelMessageRepository.findById(id).map(channelMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ChannelMessage : {}", id);
        channelMessageRepository.deleteById(id);
        channelMessageSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelMessageDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ChannelMessages for query {}", query);
        return channelMessageSearchRepository.search(query, pageable).map(channelMessageMapper::toDto);
    }
}
