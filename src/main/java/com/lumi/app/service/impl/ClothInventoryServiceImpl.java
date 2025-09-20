package com.lumi.app.service.impl;

import com.lumi.app.domain.ClothInventory;
import com.lumi.app.repository.ClothInventoryRepository;
import com.lumi.app.repository.search.ClothInventorySearchRepository;
import com.lumi.app.service.ClothInventoryService;
import com.lumi.app.service.dto.ClothInventoryDTO;
import com.lumi.app.service.mapper.ClothInventoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ClothInventory}.
 */
@Service
@Transactional
public class ClothInventoryServiceImpl implements ClothInventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ClothInventoryServiceImpl.class);

    private final ClothInventoryRepository clothInventoryRepository;

    private final ClothInventoryMapper clothInventoryMapper;

    private final ClothInventorySearchRepository clothInventorySearchRepository;

    public ClothInventoryServiceImpl(
        ClothInventoryRepository clothInventoryRepository,
        ClothInventoryMapper clothInventoryMapper,
        ClothInventorySearchRepository clothInventorySearchRepository
    ) {
        this.clothInventoryRepository = clothInventoryRepository;
        this.clothInventoryMapper = clothInventoryMapper;
        this.clothInventorySearchRepository = clothInventorySearchRepository;
    }

    @Override
    public ClothInventoryDTO save(ClothInventoryDTO clothInventoryDTO) {
        LOG.debug("Request to save ClothInventory : {}", clothInventoryDTO);
        ClothInventory clothInventory = clothInventoryMapper.toEntity(clothInventoryDTO);
        clothInventory = clothInventoryRepository.save(clothInventory);
        clothInventorySearchRepository.index(clothInventory);
        return clothInventoryMapper.toDto(clothInventory);
    }

    @Override
    public ClothInventoryDTO update(ClothInventoryDTO clothInventoryDTO) {
        LOG.debug("Request to update ClothInventory : {}", clothInventoryDTO);
        ClothInventory clothInventory = clothInventoryMapper.toEntity(clothInventoryDTO);
        clothInventory = clothInventoryRepository.save(clothInventory);
        clothInventorySearchRepository.index(clothInventory);
        return clothInventoryMapper.toDto(clothInventory);
    }

    @Override
    public Optional<ClothInventoryDTO> partialUpdate(ClothInventoryDTO clothInventoryDTO) {
        LOG.debug("Request to partially update ClothInventory : {}", clothInventoryDTO);

        return clothInventoryRepository
            .findById(clothInventoryDTO.getId())
            .map(existingClothInventory -> {
                clothInventoryMapper.partialUpdate(existingClothInventory, clothInventoryDTO);

                return existingClothInventory;
            })
            .map(clothInventoryRepository::save)
            .map(savedClothInventory -> {
                clothInventorySearchRepository.index(savedClothInventory);
                return savedClothInventory;
            })
            .map(clothInventoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClothInventoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ClothInventories");
        return clothInventoryRepository.findAll(pageable).map(clothInventoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClothInventoryDTO> findOne(Long id) {
        LOG.debug("Request to get ClothInventory : {}", id);
        return clothInventoryRepository.findById(id).map(clothInventoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ClothInventory : {}", id);
        clothInventoryRepository.deleteById(id);
        clothInventorySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClothInventoryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ClothInventories for query {}", query);
        return clothInventorySearchRepository.search(query, pageable).map(clothInventoryMapper::toDto);
    }
}
