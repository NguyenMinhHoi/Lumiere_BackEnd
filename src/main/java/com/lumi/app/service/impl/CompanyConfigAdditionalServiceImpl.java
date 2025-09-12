package com.lumi.app.service.impl;

import com.lumi.app.domain.CompanyConfigAdditional;
import com.lumi.app.repository.CompanyConfigAdditionalRepository;
import com.lumi.app.repository.search.CompanyConfigAdditionalSearchRepository;
import com.lumi.app.service.CompanyConfigAdditionalService;
import com.lumi.app.service.dto.CompanyConfigAdditionalDTO;
import com.lumi.app.service.mapper.CompanyConfigAdditionalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.CompanyConfigAdditional}.
 */
@Service
@Transactional
public class CompanyConfigAdditionalServiceImpl implements CompanyConfigAdditionalService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyConfigAdditionalServiceImpl.class);

    private final CompanyConfigAdditionalRepository companyConfigAdditionalRepository;

    private final CompanyConfigAdditionalMapper companyConfigAdditionalMapper;

    private final CompanyConfigAdditionalSearchRepository companyConfigAdditionalSearchRepository;

    public CompanyConfigAdditionalServiceImpl(
        CompanyConfigAdditionalRepository companyConfigAdditionalRepository,
        CompanyConfigAdditionalMapper companyConfigAdditionalMapper,
        CompanyConfigAdditionalSearchRepository companyConfigAdditionalSearchRepository
    ) {
        this.companyConfigAdditionalRepository = companyConfigAdditionalRepository;
        this.companyConfigAdditionalMapper = companyConfigAdditionalMapper;
        this.companyConfigAdditionalSearchRepository = companyConfigAdditionalSearchRepository;
    }

    @Override
    public CompanyConfigAdditionalDTO save(CompanyConfigAdditionalDTO companyConfigAdditionalDTO) {
        LOG.debug("Request to save CompanyConfigAdditional : {}", companyConfigAdditionalDTO);
        CompanyConfigAdditional companyConfigAdditional = companyConfigAdditionalMapper.toEntity(companyConfigAdditionalDTO);
        companyConfigAdditional = companyConfigAdditionalRepository.save(companyConfigAdditional);
        companyConfigAdditionalSearchRepository.index(companyConfigAdditional);
        return companyConfigAdditionalMapper.toDto(companyConfigAdditional);
    }

    @Override
    public CompanyConfigAdditionalDTO update(CompanyConfigAdditionalDTO companyConfigAdditionalDTO) {
        LOG.debug("Request to update CompanyConfigAdditional : {}", companyConfigAdditionalDTO);
        CompanyConfigAdditional companyConfigAdditional = companyConfigAdditionalMapper.toEntity(companyConfigAdditionalDTO);
        companyConfigAdditional = companyConfigAdditionalRepository.save(companyConfigAdditional);
        companyConfigAdditionalSearchRepository.index(companyConfigAdditional);
        return companyConfigAdditionalMapper.toDto(companyConfigAdditional);
    }

    @Override
    public Optional<CompanyConfigAdditionalDTO> partialUpdate(CompanyConfigAdditionalDTO companyConfigAdditionalDTO) {
        LOG.debug("Request to partially update CompanyConfigAdditional : {}", companyConfigAdditionalDTO);

        return companyConfigAdditionalRepository
            .findById(companyConfigAdditionalDTO.getId())
            .map(existingCompanyConfigAdditional -> {
                companyConfigAdditionalMapper.partialUpdate(existingCompanyConfigAdditional, companyConfigAdditionalDTO);

                return existingCompanyConfigAdditional;
            })
            .map(companyConfigAdditionalRepository::save)
            .map(savedCompanyConfigAdditional -> {
                companyConfigAdditionalSearchRepository.index(savedCompanyConfigAdditional);
                return savedCompanyConfigAdditional;
            })
            .map(companyConfigAdditionalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyConfigAdditionalDTO> findAll() {
        LOG.debug("Request to get all CompanyConfigAdditionals");
        return companyConfigAdditionalRepository
            .findAll()
            .stream()
            .map(companyConfigAdditionalMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyConfigAdditionalDTO> findOne(Long id) {
        LOG.debug("Request to get CompanyConfigAdditional : {}", id);
        return companyConfigAdditionalRepository.findById(id).map(companyConfigAdditionalMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CompanyConfigAdditional : {}", id);
        companyConfigAdditionalRepository.deleteById(id);
        companyConfigAdditionalSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyConfigAdditionalDTO> search(String query) {
        LOG.debug("Request to search CompanyConfigAdditionals for query {}", query);
        try {
            return StreamSupport.stream(companyConfigAdditionalSearchRepository.search(query).spliterator(), false)
                .map(companyConfigAdditionalMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
