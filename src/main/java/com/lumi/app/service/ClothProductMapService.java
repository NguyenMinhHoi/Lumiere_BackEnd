package com.lumi.app.service;

import com.lumi.app.domain.ClothProductMap;
import com.lumi.app.repository.ClothProductMapRepository;
import com.lumi.app.repository.search.ClothProductMapSearchRepository;
import com.lumi.app.service.dto.ClothProductMapDTO;
import com.lumi.app.service.mapper.ClothProductMapMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ClothProductMap}.
 */
@Service
@Transactional
public class ClothProductMapService {

    private static final Logger LOG = LoggerFactory.getLogger(ClothProductMapService.class);

    private final ClothProductMapRepository clothProductMapRepository;

    private final ClothProductMapMapper clothProductMapMapper;

    private final ClothProductMapSearchRepository clothProductMapSearchRepository;

    public ClothProductMapService(
        ClothProductMapRepository clothProductMapRepository,
        ClothProductMapMapper clothProductMapMapper,
        ClothProductMapSearchRepository clothProductMapSearchRepository
    ) {
        this.clothProductMapRepository = clothProductMapRepository;
        this.clothProductMapMapper = clothProductMapMapper;
        this.clothProductMapSearchRepository = clothProductMapSearchRepository;
    }

    /**
     * Save a clothProductMap.
     *
     * @param clothProductMapDTO the entity to save.
     * @return the persisted entity.
     */
    public ClothProductMapDTO save(ClothProductMapDTO clothProductMapDTO) {
        LOG.debug("Request to save ClothProductMap : {}", clothProductMapDTO);
        ClothProductMap clothProductMap = clothProductMapMapper.toEntity(clothProductMapDTO);
        clothProductMap = clothProductMapRepository.save(clothProductMap);
        clothProductMapSearchRepository.index(clothProductMap);
        return clothProductMapMapper.toDto(clothProductMap);
    }

    /**
     * Update a clothProductMap.
     *
     * @param clothProductMapDTO the entity to save.
     * @return the persisted entity.
     */
    public ClothProductMapDTO update(ClothProductMapDTO clothProductMapDTO) {
        LOG.debug("Request to update ClothProductMap : {}", clothProductMapDTO);
        ClothProductMap clothProductMap = clothProductMapMapper.toEntity(clothProductMapDTO);
        clothProductMap = clothProductMapRepository.save(clothProductMap);
        clothProductMapSearchRepository.index(clothProductMap);
        return clothProductMapMapper.toDto(clothProductMap);
    }

    /**
     * Partially update a clothProductMap.
     *
     * @param clothProductMapDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClothProductMapDTO> partialUpdate(ClothProductMapDTO clothProductMapDTO) {
        LOG.debug("Request to partially update ClothProductMap : {}", clothProductMapDTO);

        return clothProductMapRepository
            .findById(clothProductMapDTO.getId())
            .map(existingClothProductMap -> {
                clothProductMapMapper.partialUpdate(existingClothProductMap, clothProductMapDTO);

                return existingClothProductMap;
            })
            .map(clothProductMapRepository::save)
            .map(savedClothProductMap -> {
                clothProductMapSearchRepository.index(savedClothProductMap);
                return savedClothProductMap;
            })
            .map(clothProductMapMapper::toDto);
    }

    /**
     * Get one clothProductMap by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClothProductMapDTO> findOne(Long id) {
        LOG.debug("Request to get ClothProductMap : {}", id);
        return clothProductMapRepository.findById(id).map(clothProductMapMapper::toDto);
    }

    /**
     * Delete the clothProductMap by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ClothProductMap : {}", id);
        clothProductMapRepository.deleteById(id);
        clothProductMapSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the clothProductMap corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothProductMapDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ClothProductMaps for query {}", query);
        return clothProductMapSearchRepository.search(query, pageable).map(clothProductMapMapper::toDto);
    }
}
