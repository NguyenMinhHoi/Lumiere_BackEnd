package com.lumi.app.service.impl;

import com.lumi.app.domain.AppConfig;
import com.lumi.app.repository.AppConfigRepository;
import com.lumi.app.repository.search.AppConfigSearchRepository;
import com.lumi.app.service.AppConfigService;
import com.lumi.app.service.dto.AppConfigDTO;
import com.lumi.app.service.mapper.AppConfigMapper;
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
 * Service Implementation for managing {@link com.lumi.app.domain.AppConfig}.
 */
@Service
@Transactional
public class AppConfigServiceImpl implements AppConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfigServiceImpl.class);

    private final AppConfigRepository appConfigRepository;

    private final AppConfigMapper appConfigMapper;

    private final AppConfigSearchRepository appConfigSearchRepository;

    public AppConfigServiceImpl(
        AppConfigRepository appConfigRepository,
        AppConfigMapper appConfigMapper,
        AppConfigSearchRepository appConfigSearchRepository
    ) {
        this.appConfigRepository = appConfigRepository;
        this.appConfigMapper = appConfigMapper;
        this.appConfigSearchRepository = appConfigSearchRepository;
    }

    @Override
    public AppConfigDTO save(AppConfigDTO appConfigDTO) {
        LOG.debug("Request to save AppConfig : {}", appConfigDTO);
        AppConfig appConfig = appConfigMapper.toEntity(appConfigDTO);
        appConfig = appConfigRepository.save(appConfig);
        appConfigSearchRepository.index(appConfig);
        return appConfigMapper.toDto(appConfig);
    }

    @Override
    public AppConfigDTO update(AppConfigDTO appConfigDTO) {
        LOG.debug("Request to update AppConfig : {}", appConfigDTO);
        AppConfig appConfig = appConfigMapper.toEntity(appConfigDTO);
        appConfig = appConfigRepository.save(appConfig);
        appConfigSearchRepository.index(appConfig);
        return appConfigMapper.toDto(appConfig);
    }

    @Override
    public Optional<AppConfigDTO> partialUpdate(AppConfigDTO appConfigDTO) {
        LOG.debug("Request to partially update AppConfig : {}", appConfigDTO);

        return appConfigRepository
            .findById(appConfigDTO.getId())
            .map(existingAppConfig -> {
                appConfigMapper.partialUpdate(existingAppConfig, appConfigDTO);

                return existingAppConfig;
            })
            .map(appConfigRepository::save)
            .map(savedAppConfig -> {
                appConfigSearchRepository.index(savedAppConfig);
                return savedAppConfig;
            })
            .map(appConfigMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppConfigDTO> findAll() {
        LOG.debug("Request to get all AppConfigs");
        return appConfigRepository.findAll().stream().map(appConfigMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppConfigDTO> findOne(Long id) {
        LOG.debug("Request to get AppConfig : {}", id);
        return appConfigRepository.findById(id).map(appConfigMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AppConfig : {}", id);
        appConfigRepository.deleteById(id);
        appConfigSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppConfigDTO> search(String query) {
        LOG.debug("Request to search AppConfigs for query {}", query);
        try {
            return StreamSupport.stream(appConfigSearchRepository.search(query).spliterator(), false).map(appConfigMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
