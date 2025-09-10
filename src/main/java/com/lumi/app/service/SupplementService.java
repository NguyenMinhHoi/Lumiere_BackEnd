package com.lumi.app.service;

import com.lumi.app.domain.Supplement;
import com.lumi.app.repository.SupplementRepository;
import com.lumi.app.repository.search.SupplementSearchRepository;
import com.lumi.app.service.dto.SupplementDTO;
import com.lumi.app.service.mapper.SupplementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Supplement}.
 */
@Service
@Transactional
public class SupplementService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplementService.class);

    private final SupplementRepository supplementRepository;

    private final SupplementMapper supplementMapper;

    private final SupplementSearchRepository supplementSearchRepository;

    public SupplementService(
        SupplementRepository supplementRepository,
        SupplementMapper supplementMapper,
        SupplementSearchRepository supplementSearchRepository
    ) {
        this.supplementRepository = supplementRepository;
        this.supplementMapper = supplementMapper;
        this.supplementSearchRepository = supplementSearchRepository;
    }

    /**
     * Save a supplement.
     *
     * @param supplementDTO the entity to save.
     * @return the persisted entity.
     */
    public SupplementDTO save(SupplementDTO supplementDTO) {
        LOG.debug("Request to save Supplement : {}", supplementDTO);
        Supplement supplement = supplementMapper.toEntity(supplementDTO);
        supplement = supplementRepository.save(supplement);
        supplementSearchRepository.index(supplement);
        return supplementMapper.toDto(supplement);
    }

    /**
     * Update a supplement.
     *
     * @param supplementDTO the entity to save.
     * @return the persisted entity.
     */
    public SupplementDTO update(SupplementDTO supplementDTO) {
        LOG.debug("Request to update Supplement : {}", supplementDTO);
        Supplement supplement = supplementMapper.toEntity(supplementDTO);
        supplement = supplementRepository.save(supplement);
        supplementSearchRepository.index(supplement);
        return supplementMapper.toDto(supplement);
    }

    /**
     * Partially update a supplement.
     *
     * @param supplementDTO the entity to update partially.
     * @return the persisted entity.
     */
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

    /**
     * Get all the supplements with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SupplementDTO> findAllWithEagerRelationships(Pageable pageable) {
        return supplementRepository.findAllWithEagerRelationships(pageable).map(supplementMapper::toDto);
    }

    /**
     * Get one supplement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SupplementDTO> findOne(Long id) {
        LOG.debug("Request to get Supplement : {}", id);
        return supplementRepository.findOneWithEagerRelationships(id).map(supplementMapper::toDto);
    }

    /**
     * Delete the supplement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Supplement : {}", id);
        supplementRepository.deleteById(id);
        supplementSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the supplement corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplementDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Supplements for query {}", query);
        return supplementSearchRepository.search(query, pageable).map(supplementMapper::toDto);
    }
}
