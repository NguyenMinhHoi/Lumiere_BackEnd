package com.lumi.app.service.impl;

import com.lumi.app.domain.Supplement;
import com.lumi.app.repository.SupplementRepository;
import com.lumi.app.repository.search.SupplementSearchRepository;
import com.lumi.app.service.SupplementService;
import com.lumi.app.service.dto.SupplementDTO;
import com.lumi.app.service.mapper.SupplementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Supplement}.
 */
@Service
@Transactional
public class SupplementServiceImpl implements SupplementService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplementServiceImpl.class);

    private final SupplementRepository supplementRepository;

    private final SupplementMapper supplementMapper;

    private final SupplementSearchRepository supplementSearchRepository;

    public SupplementServiceImpl(
        SupplementRepository supplementRepository,
        SupplementMapper supplementMapper,
        SupplementSearchRepository supplementSearchRepository
    ) {
        this.supplementRepository = supplementRepository;
        this.supplementMapper = supplementMapper;
        this.supplementSearchRepository = supplementSearchRepository;
    }

    @Override
    public SupplementDTO save(SupplementDTO supplementDTO) {
        LOG.debug("Request to save Supplement : {}", supplementDTO);
        Supplement supplement = supplementMapper.toEntity(supplementDTO);
        supplement = supplementRepository.save(supplement);
        supplementSearchRepository.index(supplement);
        return supplementMapper.toDto(supplement);
    }

    @Override
    public SupplementDTO update(SupplementDTO supplementDTO) {
        LOG.debug("Request to update Supplement : {}", supplementDTO);
        Supplement supplement = supplementMapper.toEntity(supplementDTO);
        supplement = supplementRepository.save(supplement);
        supplementSearchRepository.index(supplement);
        return supplementMapper.toDto(supplement);
    }

    @Override
    public Optional<SupplementDTO> partialUpdate(SupplementDTO supplementDTO) {
        LOG.debug("Request to partially update Supplement : {}", supplementDTO);

        return supplementRepository
            .findById(supplementDTO.getId())
            .map(existingSupplement -> {
                supplementMapper.partialUpdate(existingSupplement, supplementDTO);

                return existingSupplement;
            })
            .map(supplementRepository::save)
            .map(savedSupplement -> {
                supplementSearchRepository.index(savedSupplement);
                return savedSupplement;
            })
            .map(supplementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplementDTO> findOne(Long id) {
        LOG.debug("Request to get Supplement : {}", id);
        return supplementRepository.findById(id).map(supplementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Supplement : {}", id);
        supplementRepository.deleteById(id);
        supplementSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplementDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Supplements for query {}", query);
        return supplementSearchRepository.search(query, pageable).map(supplementMapper::toDto);
    }
}
