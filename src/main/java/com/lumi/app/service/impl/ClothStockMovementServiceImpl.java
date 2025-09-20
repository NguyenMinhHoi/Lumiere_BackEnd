package com.lumi.app.service.impl;

import com.lumi.app.domain.ClothStockMovement;
import com.lumi.app.repository.ClothStockMovementRepository;
import com.lumi.app.repository.search.ClothStockMovementSearchRepository;
import com.lumi.app.service.ClothStockMovementService;
import com.lumi.app.service.dto.ClothStockMovementDTO;
import com.lumi.app.service.mapper.ClothStockMovementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ClothStockMovement}.
 */
@Service
@Transactional
public class ClothStockMovementServiceImpl implements ClothStockMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(ClothStockMovementServiceImpl.class);

    private final ClothStockMovementRepository clothStockMovementRepository;

    private final ClothStockMovementMapper clothStockMovementMapper;

    private final ClothStockMovementSearchRepository clothStockMovementSearchRepository;

    public ClothStockMovementServiceImpl(
        ClothStockMovementRepository clothStockMovementRepository,
        ClothStockMovementMapper clothStockMovementMapper,
        ClothStockMovementSearchRepository clothStockMovementSearchRepository
    ) {
        this.clothStockMovementRepository = clothStockMovementRepository;
        this.clothStockMovementMapper = clothStockMovementMapper;
        this.clothStockMovementSearchRepository = clothStockMovementSearchRepository;
    }

    @Override
    public ClothStockMovementDTO save(ClothStockMovementDTO clothStockMovementDTO) {
        LOG.debug("Request to save ClothStockMovement : {}", clothStockMovementDTO);
        ClothStockMovement clothStockMovement = clothStockMovementMapper.toEntity(clothStockMovementDTO);
        clothStockMovement = clothStockMovementRepository.save(clothStockMovement);
        clothStockMovementSearchRepository.index(clothStockMovement);
        return clothStockMovementMapper.toDto(clothStockMovement);
    }

    @Override
    public ClothStockMovementDTO update(ClothStockMovementDTO clothStockMovementDTO) {
        LOG.debug("Request to update ClothStockMovement : {}", clothStockMovementDTO);
        ClothStockMovement clothStockMovement = clothStockMovementMapper.toEntity(clothStockMovementDTO);
        clothStockMovement = clothStockMovementRepository.save(clothStockMovement);
        clothStockMovementSearchRepository.index(clothStockMovement);
        return clothStockMovementMapper.toDto(clothStockMovement);
    }

    @Override
    public Optional<ClothStockMovementDTO> partialUpdate(ClothStockMovementDTO clothStockMovementDTO) {
        LOG.debug("Request to partially update ClothStockMovement : {}", clothStockMovementDTO);

        return clothStockMovementRepository
            .findById(clothStockMovementDTO.getId())
            .map(existingClothStockMovement -> {
                clothStockMovementMapper.partialUpdate(existingClothStockMovement, clothStockMovementDTO);

                return existingClothStockMovement;
            })
            .map(clothStockMovementRepository::save)
            .map(savedClothStockMovement -> {
                clothStockMovementSearchRepository.index(savedClothStockMovement);
                return savedClothStockMovement;
            })
            .map(clothStockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClothStockMovementDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ClothStockMovements");
        return clothStockMovementRepository.findAll(pageable).map(clothStockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClothStockMovementDTO> findOne(Long id) {
        LOG.debug("Request to get ClothStockMovement : {}", id);
        return clothStockMovementRepository.findById(id).map(clothStockMovementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ClothStockMovement : {}", id);
        clothStockMovementRepository.deleteById(id);
        clothStockMovementSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClothStockMovementDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ClothStockMovements for query {}", query);
        return clothStockMovementSearchRepository.search(query, pageable).map(clothStockMovementMapper::toDto);
    }
}
