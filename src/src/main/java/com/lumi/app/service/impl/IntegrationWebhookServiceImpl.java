package com.lumi.app.service.impl;

import com.lumi.app.domain.IntegrationWebhook;
import com.lumi.app.repository.IntegrationWebhookRepository;
import com.lumi.app.repository.search.IntegrationWebhookSearchRepository;
import com.lumi.app.service.IntegrationWebhookService;
import com.lumi.app.service.dto.IntegrationWebhookDTO;
import com.lumi.app.service.mapper.IntegrationWebhookMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.IntegrationWebhook}.
 */
@Service
@Transactional
public class IntegrationWebhookServiceImpl implements IntegrationWebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationWebhookServiceImpl.class);

    private final IntegrationWebhookRepository integrationWebhookRepository;

    private final IntegrationWebhookMapper integrationWebhookMapper;

    private final IntegrationWebhookSearchRepository integrationWebhookSearchRepository;

    public IntegrationWebhookServiceImpl(
        IntegrationWebhookRepository integrationWebhookRepository,
        IntegrationWebhookMapper integrationWebhookMapper,
        IntegrationWebhookSearchRepository integrationWebhookSearchRepository
    ) {
        this.integrationWebhookRepository = integrationWebhookRepository;
        this.integrationWebhookMapper = integrationWebhookMapper;
        this.integrationWebhookSearchRepository = integrationWebhookSearchRepository;
    }

    @Override
    public IntegrationWebhookDTO save(IntegrationWebhookDTO integrationWebhookDTO) {
        LOG.debug("Request to save IntegrationWebhook : {}", integrationWebhookDTO);
        IntegrationWebhook integrationWebhook = integrationWebhookMapper.toEntity(integrationWebhookDTO);
        integrationWebhook = integrationWebhookRepository.save(integrationWebhook);
        integrationWebhookSearchRepository.index(integrationWebhook);
        return integrationWebhookMapper.toDto(integrationWebhook);
    }

    @Override
    public IntegrationWebhookDTO update(IntegrationWebhookDTO integrationWebhookDTO) {
        LOG.debug("Request to update IntegrationWebhook : {}", integrationWebhookDTO);
        IntegrationWebhook integrationWebhook = integrationWebhookMapper.toEntity(integrationWebhookDTO);
        integrationWebhook = integrationWebhookRepository.save(integrationWebhook);
        integrationWebhookSearchRepository.index(integrationWebhook);
        return integrationWebhookMapper.toDto(integrationWebhook);
    }

    @Override
    public Optional<IntegrationWebhookDTO> partialUpdate(IntegrationWebhookDTO integrationWebhookDTO) {
        LOG.debug("Request to partially update IntegrationWebhook : {}", integrationWebhookDTO);

        return integrationWebhookRepository
            .findById(integrationWebhookDTO.getId())
            .map(existingIntegrationWebhook -> {
                integrationWebhookMapper.partialUpdate(existingIntegrationWebhook, integrationWebhookDTO);

                return existingIntegrationWebhook;
            })
            .map(integrationWebhookRepository::save)
            .map(savedIntegrationWebhook -> {
                integrationWebhookSearchRepository.index(savedIntegrationWebhook);
                return savedIntegrationWebhook;
            })
            .map(integrationWebhookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationWebhookDTO> findAll() {
        LOG.debug("Request to get all IntegrationWebhooks");
        return integrationWebhookRepository
            .findAll()
            .stream()
            .map(integrationWebhookMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IntegrationWebhookDTO> findOne(Long id) {
        LOG.debug("Request to get IntegrationWebhook : {}", id);
        return integrationWebhookRepository.findById(id).map(integrationWebhookMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete IntegrationWebhook : {}", id);
        integrationWebhookRepository.deleteById(id);
        integrationWebhookSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationWebhookDTO> search(String query) {
        LOG.debug("Request to search IntegrationWebhooks for query {}", query);
        try {
            return StreamSupport.stream(integrationWebhookSearchRepository.search(query).spliterator(), false)
                .map(integrationWebhookMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
