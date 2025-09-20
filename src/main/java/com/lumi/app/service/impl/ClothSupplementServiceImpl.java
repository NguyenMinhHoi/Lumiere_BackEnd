package com.lumi.app.service.impl;

import com.lumi.app.domain.ClothSupplement;
import com.lumi.app.repository.ClothSupplementRepository;
import com.lumi.app.repository.search.ClothSupplementSearchRepository;
import com.lumi.app.service.ClothSupplementService;
import com.lumi.app.service.dto.ClothSupplementDTO;
import com.lumi.app.service.mapper.ClothSupplementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ClothSupplement}.
 */
@Service
@Transactional
public class ClothSupplementServiceImpl implements ClothSupplementService {

    private static final Logger LOG = LoggerFactory.getLogger(ClothSupplementServiceImpl.class);

    private final ClothSupplementRepository clothSupplementRepository;

    private final ClothSupplementMapper clothSupplementMapper;

    private final ClothSupplementSearchRepository clothSupplementSearchRepository;

    public ClothSupplementServiceImpl(
        ClothSupplementRepository clothSupplementRepository,
        ClothSupplementMapper clothSupplementMapper,
        ClothSupplementSearchRepository clothSupplementSearchRepository
    ) {
        this.clothSupplementRepository = clothSupplementRepository;
        this.clothSupplementMapper = clothSupplementMapper;
        this.clothSupplementSearchRepository = clothSupplementSearchRepository;
    }

    @Override
    public ClothSupplementDTO save(ClothSupplementDTO clothSupplementDTO) {
        LOG.debug("Request to save ClothSupplement : {}", clothSupplementDTO);
        ClothSupplement clothSupplement = clothSupplementMapper.toEntity(clothSupplementDTO);
        clothSupplement = clothSupplementRepository.save(clothSupplement);
        clothSupplementSearchRepository.index(clothSupplement);
        return clothSupplementMapper.toDto(clothSupplement);
    }

    @Override
    public ClothSupplementDTO update(ClothSupplementDTO clothSupplementDTO) {
        LOG.debug("Request to update ClothSupplement : {}", clothSupplementDTO);
        ClothSupplement clothSupplement = clothSupplementMapper.toEntity(clothSupplementDTO);
        clothSupplement = clothSupplementRepository.save(clothSupplement);
        clothSupplementSearchRepository.index(clothSupplement);
        return clothSupplementMapper.toDto(clothSupplement);
    }

    @Override
    public Optional<ClothSupplementDTO> partialUpdate(ClothSupplementDTO clothSupplementDTO) {
        LOG.debug("Request to partially update ClothSupplement : {}", clothSupplementDTO);

        return clothSupplementRepository
            .findById(clothSupplementDTO.getId())
            .map(existingClothSupplement -> {
                clothSupplementMapper.partialUpdate(existingClothSupplement, clothSupplementDTO);

                return existingClothSupplement;
            })
            .map(clothSupplementRepository::save)
            .map(savedClothSupplement -> {
                clothSupplementSearchRepository.index(savedClothSupplement);
                return savedClothSupplement;
            })
            .map(clothSupplementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClothSupplementDTO> findOne(Long id) {
        LOG.debug("Request to get ClothSupplement : {}", id);
        return clothSupplementRepository.findById(id).map(clothSupplementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ClothSupplement : {}", id);
        clothSupplementRepository.deleteById(id);
        clothSupplementSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClothSupplementDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ClothSupplements for query {}", query);
        return clothSupplementSearchRepository.search(query, pageable).map(clothSupplementMapper::toDto);
    }
}
