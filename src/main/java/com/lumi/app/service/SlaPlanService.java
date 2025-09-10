package com.lumi.app.service;

import com.lumi.app.domain.SlaPlan;
import com.lumi.app.repository.SlaPlanRepository;
import com.lumi.app.repository.search.SlaPlanSearchRepository;
import com.lumi.app.service.dto.SlaPlanDTO;
import com.lumi.app.service.mapper.SlaPlanMapper;
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
 * Service Implementation for managing {@link com.lumi.app.domain.SlaPlan}.
 */
@Service
@Transactional
public class SlaPlanService {

    private static final Logger LOG = LoggerFactory.getLogger(SlaPlanService.class);

    private final SlaPlanRepository slaPlanRepository;

    private final SlaPlanMapper slaPlanMapper;

    private final SlaPlanSearchRepository slaPlanSearchRepository;

    public SlaPlanService(
        SlaPlanRepository slaPlanRepository,
        SlaPlanMapper slaPlanMapper,
        SlaPlanSearchRepository slaPlanSearchRepository
    ) {
        this.slaPlanRepository = slaPlanRepository;
        this.slaPlanMapper = slaPlanMapper;
        this.slaPlanSearchRepository = slaPlanSearchRepository;
    }

    /**
     * Save a slaPlan.
     *
     * @param slaPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public SlaPlanDTO save(SlaPlanDTO slaPlanDTO) {
        LOG.debug("Request to save SlaPlan : {}", slaPlanDTO);
        SlaPlan slaPlan = slaPlanMapper.toEntity(slaPlanDTO);
        slaPlan = slaPlanRepository.save(slaPlan);
        slaPlanSearchRepository.index(slaPlan);
        return slaPlanMapper.toDto(slaPlan);
    }

    /**
     * Update a slaPlan.
     *
     * @param slaPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public SlaPlanDTO update(SlaPlanDTO slaPlanDTO) {
        LOG.debug("Request to update SlaPlan : {}", slaPlanDTO);
        SlaPlan slaPlan = slaPlanMapper.toEntity(slaPlanDTO);
        slaPlan = slaPlanRepository.save(slaPlan);
        slaPlanSearchRepository.index(slaPlan);
        return slaPlanMapper.toDto(slaPlan);
    }

    /**
     * Partially update a slaPlan.
     *
     * @param slaPlanDTO the entity to update partially.
     * @return the persisted entity.
     */
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

    /**
     * Get all the slaPlans.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SlaPlanDTO> findAll() {
        LOG.debug("Request to get all SlaPlans");
        return slaPlanRepository.findAll().stream().map(slaPlanMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one slaPlan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SlaPlanDTO> findOne(Long id) {
        LOG.debug("Request to get SlaPlan : {}", id);
        return slaPlanRepository.findById(id).map(slaPlanMapper::toDto);
    }

    /**
     * Delete the slaPlan by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SlaPlan : {}", id);
        slaPlanRepository.deleteById(id);
        slaPlanSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the slaPlan corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
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
