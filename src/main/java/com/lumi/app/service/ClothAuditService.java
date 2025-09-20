package com.lumi.app.service;

import com.lumi.app.domain.ClothAudit;
import com.lumi.app.repository.ClothAuditRepository;
import com.lumi.app.repository.search.ClothAuditSearchRepository;
import com.lumi.app.service.dto.ClothAuditDTO;
import com.lumi.app.service.mapper.ClothAuditMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ClothAudit}.
 */
@Service
@Transactional
public class ClothAuditService {

    private static final Logger LOG = LoggerFactory.getLogger(ClothAuditService.class);

    private final ClothAuditRepository clothAuditRepository;

    private final ClothAuditMapper clothAuditMapper;

    private final ClothAuditSearchRepository clothAuditSearchRepository;

    public ClothAuditService(
        ClothAuditRepository clothAuditRepository,
        ClothAuditMapper clothAuditMapper,
        ClothAuditSearchRepository clothAuditSearchRepository
    ) {
        this.clothAuditRepository = clothAuditRepository;
        this.clothAuditMapper = clothAuditMapper;
        this.clothAuditSearchRepository = clothAuditSearchRepository;
    }

    /**
     * Save a clothAudit.
     *
     * @param clothAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public ClothAuditDTO save(ClothAuditDTO clothAuditDTO) {
        LOG.debug("Request to save ClothAudit : {}", clothAuditDTO);
        ClothAudit clothAudit = clothAuditMapper.toEntity(clothAuditDTO);
        clothAudit = clothAuditRepository.save(clothAudit);
        clothAuditSearchRepository.index(clothAudit);
        return clothAuditMapper.toDto(clothAudit);
    }

    /**
     * Update a clothAudit.
     *
     * @param clothAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public ClothAuditDTO update(ClothAuditDTO clothAuditDTO) {
        LOG.debug("Request to update ClothAudit : {}", clothAuditDTO);
        ClothAudit clothAudit = clothAuditMapper.toEntity(clothAuditDTO);
        clothAudit = clothAuditRepository.save(clothAudit);
        clothAuditSearchRepository.index(clothAudit);
        return clothAuditMapper.toDto(clothAudit);
    }

    /**
     * Partially update a clothAudit.
     *
     * @param clothAuditDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClothAuditDTO> partialUpdate(ClothAuditDTO clothAuditDTO) {
        LOG.debug("Request to partially update ClothAudit : {}", clothAuditDTO);

        return clothAuditRepository
            .findById(clothAuditDTO.getId())
            .map(existingClothAudit -> {
                clothAuditMapper.partialUpdate(existingClothAudit, clothAuditDTO);

                return existingClothAudit;
            })
            .map(clothAuditRepository::save)
            .map(savedClothAudit -> {
                clothAuditSearchRepository.index(savedClothAudit);
                return savedClothAudit;
            })
            .map(clothAuditMapper::toDto);
    }

    /**
     * Get one clothAudit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClothAuditDTO> findOne(Long id) {
        LOG.debug("Request to get ClothAudit : {}", id);
        return clothAuditRepository.findById(id).map(clothAuditMapper::toDto);
    }

    /**
     * Delete the clothAudit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ClothAudit : {}", id);
        clothAuditRepository.deleteById(id);
        clothAuditSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the clothAudit corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothAuditDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ClothAudits for query {}", query);
        return clothAuditSearchRepository.search(query, pageable).map(clothAuditMapper::toDto);
    }
}
