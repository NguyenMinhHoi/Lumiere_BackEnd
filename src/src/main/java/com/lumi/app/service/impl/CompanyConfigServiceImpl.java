package com.lumi.app.service.impl;

import com.lumi.app.domain.CompanyConfig;
import com.lumi.app.repository.CompanyConfigRepository;
import com.lumi.app.repository.search.CompanyConfigSearchRepository;
import com.lumi.app.service.CompanyConfigService;
import com.lumi.app.service.dto.CompanyConfigDTO;
import com.lumi.app.service.mapper.CompanyConfigMapper;
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
 * Service Implementation for managing {@link com.lumi.app.domain.CompanyConfig}.
 */
@Service
@Transactional
public class CompanyConfigServiceImpl implements CompanyConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyConfigServiceImpl.class);

    private final CompanyConfigRepository companyConfigRepository;

    private final CompanyConfigMapper companyConfigMapper;

    private final CompanyConfigSearchRepository companyConfigSearchRepository;

    public CompanyConfigServiceImpl(
        CompanyConfigRepository companyConfigRepository,
        CompanyConfigMapper companyConfigMapper,
        CompanyConfigSearchRepository companyConfigSearchRepository
    ) {
        this.companyConfigRepository = companyConfigRepository;
        this.companyConfigMapper = companyConfigMapper;
        this.companyConfigSearchRepository = companyConfigSearchRepository;
    }

    @Override
    public CompanyConfigDTO save(CompanyConfigDTO companyConfigDTO) {
        LOG.debug("Request to save CompanyConfig : {}", companyConfigDTO);
        CompanyConfig companyConfig = companyConfigMapper.toEntity(companyConfigDTO);
        companyConfig = companyConfigRepository.save(companyConfig);
        companyConfigSearchRepository.index(companyConfig);
        return companyConfigMapper.toDto(companyConfig);
    }

    @Override
    public CompanyConfigDTO update(CompanyConfigDTO companyConfigDTO) {
        LOG.debug("Request to update CompanyConfig : {}", companyConfigDTO);
        CompanyConfig companyConfig = companyConfigMapper.toEntity(companyConfigDTO);
        companyConfig = companyConfigRepository.save(companyConfig);
        companyConfigSearchRepository.index(companyConfig);
        return companyConfigMapper.toDto(companyConfig);
    }

    @Override
    public Optional<CompanyConfigDTO> partialUpdate(CompanyConfigDTO companyConfigDTO) {
        LOG.debug("Request to partially update CompanyConfig : {}", companyConfigDTO);

        return companyConfigRepository
            .findById(companyConfigDTO.getId())
            .map(existingCompanyConfig -> {
                companyConfigMapper.partialUpdate(existingCompanyConfig, companyConfigDTO);

                return existingCompanyConfig;
            })
            .map(companyConfigRepository::save)
            .map(savedCompanyConfig -> {
                companyConfigSearchRepository.index(savedCompanyConfig);
                return savedCompanyConfig;
            })
            .map(companyConfigMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyConfigDTO> findAll() {
        LOG.debug("Request to get all CompanyConfigs");
        return companyConfigRepository.findAll().stream().map(companyConfigMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyConfigDTO> findOne(Long id) {
        LOG.debug("Request to get CompanyConfig : {}", id);
        return companyConfigRepository.findById(id).map(companyConfigMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CompanyConfig : {}", id);
        companyConfigRepository.deleteById(id);
        companyConfigSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyConfigDTO> search(String query) {
        LOG.debug("Request to search CompanyConfigs for query {}", query);
        try {
            return StreamSupport.stream(companyConfigSearchRepository.search(query).spliterator(), false)
                .map(companyConfigMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
