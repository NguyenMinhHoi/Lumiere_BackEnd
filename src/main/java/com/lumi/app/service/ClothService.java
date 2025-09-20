package com.lumi.app.service;

import com.lumi.app.domain.Cloth;
import com.lumi.app.repository.ClothRepository;
import com.lumi.app.repository.search.ClothSearchRepository;
import com.lumi.app.service.dto.ClothDTO;
import com.lumi.app.service.mapper.ClothMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Cloth}.
 */
@Service
@Transactional
public class ClothService {

    private static final Logger LOG = LoggerFactory.getLogger(ClothService.class);

    private final ClothRepository clothRepository;

    private final ClothMapper clothMapper;

    private final ClothSearchRepository clothSearchRepository;

    public ClothService(ClothRepository clothRepository, ClothMapper clothMapper, ClothSearchRepository clothSearchRepository) {
        this.clothRepository = clothRepository;
        this.clothMapper = clothMapper;
        this.clothSearchRepository = clothSearchRepository;
    }

    /**
     * Save a cloth.
     *
     * @param clothDTO the entity to save.
     * @return the persisted entity.
     */
    public ClothDTO save(ClothDTO clothDTO) {
        LOG.debug("Request to save Cloth : {}", clothDTO);
        Cloth cloth = clothMapper.toEntity(clothDTO);
        cloth = clothRepository.save(cloth);
        clothSearchRepository.index(cloth);
        return clothMapper.toDto(cloth);
    }

    /**
     * Update a cloth.
     *
     * @param clothDTO the entity to save.
     * @return the persisted entity.
     */
    public ClothDTO update(ClothDTO clothDTO) {
        LOG.debug("Request to update Cloth : {}", clothDTO);
        Cloth cloth = clothMapper.toEntity(clothDTO);
        cloth = clothRepository.save(cloth);
        clothSearchRepository.index(cloth);
        return clothMapper.toDto(cloth);
    }

    /**
     * Partially update a cloth.
     *
     * @param clothDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClothDTO> partialUpdate(ClothDTO clothDTO) {
        LOG.debug("Request to partially update Cloth : {}", clothDTO);

        return clothRepository
            .findById(clothDTO.getId())
            .map(existingCloth -> {
                clothMapper.partialUpdate(existingCloth, clothDTO);

                return existingCloth;
            })
            .map(clothRepository::save)
            .map(savedCloth -> {
                clothSearchRepository.index(savedCloth);
                return savedCloth;
            })
            .map(clothMapper::toDto);
    }

    /**
     * Get one cloth by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClothDTO> findOne(Long id) {
        LOG.debug("Request to get Cloth : {}", id);
        return clothRepository.findById(id).map(clothMapper::toDto);
    }

    /**
     * Delete the cloth by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Cloth : {}", id);
        clothRepository.deleteById(id);
        clothSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the cloth corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClothDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Cloths for query {}", query);
        return clothSearchRepository.search(query, pageable).map(clothMapper::toDto);
    }
}
