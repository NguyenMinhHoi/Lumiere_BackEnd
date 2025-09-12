package com.lumi.app.service.impl;

import com.lumi.app.domain.IntegrationLog;
import com.lumi.app.repository.IntegrationLogRepository;
import com.lumi.app.repository.search.IntegrationLogSearchRepository;
import com.lumi.app.service.IntegrationLogService;
import com.lumi.app.service.dto.IntegrationLogDTO;
import com.lumi.app.service.mapper.IntegrationLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.IntegrationLog}.
 */
@Service
@Transactional
public class IntegrationLogServiceImpl implements IntegrationLogService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationLogServiceImpl.class);

    private final IntegrationLogRepository integrationLogRepository;

    private final IntegrationLogMapper integrationLogMapper;

    private final IntegrationLogSearchRepository integrationLogSearchRepository;

    public IntegrationLogServiceImpl(
        IntegrationLogRepository integrationLogRepository,
        IntegrationLogMapper integrationLogMapper,
        IntegrationLogSearchRepository integrationLogSearchRepository
    ) {
        this.integrationLogRepository = integrationLogRepository;
        this.integrationLogMapper = integrationLogMapper;
        this.integrationLogSearchRepository = integrationLogSearchRepository;
    }

    @Override
    public IntegrationLogDTO save(IntegrationLogDTO integrationLogDTO) {
        LOG.debug("Request to save IntegrationLog : {}", integrationLogDTO);
        IntegrationLog integrationLog = integrationLogMapper.toEntity(integrationLogDTO);
        integrationLog = integrationLogRepository.save(integrationLog);
        integrationLogSearchRepository.index(integrationLog);
        return integrationLogMapper.toDto(integrationLog);
    }

    @Override
    public IntegrationLogDTO update(IntegrationLogDTO integrationLogDTO) {
        LOG.debug("Request to update IntegrationLog : {}", integrationLogDTO);
        IntegrationLog integrationLog = integrationLogMapper.toEntity(integrationLogDTO);
        integrationLog = integrationLogRepository.save(integrationLog);
        integrationLogSearchRepository.index(integrationLog);
        return integrationLogMapper.toDto(integrationLog);
    }

    @Override
    public Optional<IntegrationLogDTO> partialUpdate(IntegrationLogDTO integrationLogDTO) {
        LOG.debug("Request to partially update IntegrationLog : {}", integrationLogDTO);

        return integrationLogRepository
            .findById(integrationLogDTO.getId())
            .map(existingIntegrationLog -> {
                integrationLogMapper.partialUpdate(existingIntegrationLog, integrationLogDTO);

                return existingIntegrationLog;
            })
            .map(integrationLogRepository::save)
            .map(savedIntegrationLog -> {
                integrationLogSearchRepository.index(savedIntegrationLog);
                return savedIntegrationLog;
            })
            .map(integrationLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IntegrationLogDTO> findOne(Long id) {
        LOG.debug("Request to get IntegrationLog : {}", id);
        return integrationLogRepository.findById(id).map(integrationLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete IntegrationLog : {}", id);
        integrationLogRepository.deleteById(id);
        integrationLogSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IntegrationLogDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of IntegrationLogs for query {}", query);
        return integrationLogSearchRepository.search(query, pageable).map(integrationLogMapper::toDto);
    }
}
