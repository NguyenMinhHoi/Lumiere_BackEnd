package com.lumi.app.service.impl;

import com.lumi.app.domain.SlaPlan;
import com.lumi.app.repository.SlaPlanRepository;
import com.lumi.app.repository.search.SlaPlanSearchRepository;
import com.lumi.app.service.SlaPlanService;
import com.lumi.app.service.dto.SlaPlanDTO;
import com.lumi.app.service.mapper.SlaPlanMapper;
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
 * Service Implementation for managing {@link SlaPlan}.
 */
@Service
@Transactional
public class SlaPlanServiceImpl implements SlaPlanService {

    private static final Logger LOG = LoggerFactory.getLogger(SlaPlanServiceImpl.class);

    private final SlaPlanRepository slaPlanRepository;

    private final SlaPlanMapper slaPlanMapper;

    private final SlaPlanSearchRepository slaPlanSearchRepository;

    public SlaPlanServiceImpl(
        SlaPlanRepository slaPlanRepository,
        SlaPlanMapper slaPlanMapper,
        SlaPlanSearchRepository slaPlanSearchRepository
    ) {
        this.slaPlanRepository = slaPlanRepository;
        this.slaPlanMapper = slaPlanMapper;
        this.slaPlanSearchRepository = slaPlanSearchRepository;
    }

    @Override
    public SlaPlanDTO save(SlaPlanDTO slaPlanDTO) {
        LOG.debug("Request to save SlaPlan : {}", slaPlanDTO);
        SlaPlan slaPlan = slaPlanMapper.toEntity(slaPlanDTO);
        slaPlan = slaPlanRepository.save(slaPlan);
        slaPlanSearchRepository.index(slaPlan);
        return slaPlanMapper.toDto(slaPlan);
    }

    @Override
    public SlaPlanDTO update(SlaPlanDTO slaPlanDTO) {
        LOG.debug("Request to update SlaPlan : {}", slaPlanDTO);
        SlaPlan slaPlan = slaPlanMapper.toEntity(slaPlanDTO);
        slaPlan = slaPlanRepository.save(slaPlan);
        slaPlanSearchRepository.index(slaPlan);
        return slaPlanMapper.toDto(slaPlan);
    }

    @Override
    public Optional<SlaPlanDTO> partialUpdate(SlaPlanDTO slaPlanDTO) {
        LOG.debug("Request to partially update SlaPlan : {}", slaPlanDTO);

        return slaPlanRepository
            .findById(slaPlanDTO.getId())
            .map(existingSlaPlan -> {
                slaPlanMapper.partialUpdate(existingSlaPlan, slaPlanDTO);

                return existingSlaPlan;
            })
            .map(slaPlanRepository::save)
            .map(savedSlaPlan -> {
                slaPlanSearchRepository.index(savedSlaPlan);
                return savedSlaPlan;
            })
            .map(slaPlanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlaPlanDTO> findAll() {
        LOG.debug("Request to get all SlaPlans");
        return slaPlanRepository.findAll().stream().map(slaPlanMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SlaPlanDTO> findOne(Long id) {
        LOG.debug("Request to get SlaPlan : {}", id);
        return slaPlanRepository.findById(id).map(slaPlanMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SlaPlan : {}", id);
        slaPlanRepository.deleteById(id);
        slaPlanSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlaPlanDTO> search(String query) {
        LOG.debug("Request to search SlaPlans for query {}", query);
        try {
            return StreamSupport.stream(slaPlanSearchRepository.search(query).spliterator(), false).map(slaPlanMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
