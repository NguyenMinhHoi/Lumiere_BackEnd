package com.lumi.app.service.impl;

import com.lumi.app.domain.StockMovement;
import com.lumi.app.repository.StockMovementRepository;
import com.lumi.app.repository.search.StockMovementSearchRepository;
import com.lumi.app.service.StockMovementService;
import com.lumi.app.service.dto.StockMovementDTO;
import com.lumi.app.service.mapper.StockMovementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.StockMovement}.
 */
@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(StockMovementServiceImpl.class);

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    private final StockMovementSearchRepository stockMovementSearchRepository;

    public StockMovementServiceImpl(
        StockMovementRepository stockMovementRepository,
        StockMovementMapper stockMovementMapper,
        StockMovementSearchRepository stockMovementSearchRepository
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
        this.stockMovementSearchRepository = stockMovementSearchRepository;
    }

    @Override
    public StockMovementDTO save(StockMovementDTO stockMovementDTO) {
        LOG.debug("Request to save StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        stockMovementSearchRepository.index(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    @Override
    public StockMovementDTO update(StockMovementDTO stockMovementDTO) {
        LOG.debug("Request to update StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        stockMovementSearchRepository.index(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    @Override
    public Optional<StockMovementDTO> partialUpdate(StockMovementDTO stockMovementDTO) {
        LOG.debug("Request to partially update StockMovement : {}", stockMovementDTO);

        return stockMovementRepository
            .findById(stockMovementDTO.getId())
            .map(existingStockMovement -> {
                stockMovementMapper.partialUpdate(existingStockMovement, stockMovementDTO);

                return existingStockMovement;
            })
            .map(stockMovementRepository::save)
            .map(savedStockMovement -> {
                stockMovementSearchRepository.index(savedStockMovement);
                return savedStockMovement;
            })
            .map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all StockMovements");
        return stockMovementRepository.findAll(pageable).map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockMovementDTO> findOne(Long id) {
        LOG.debug("Request to get StockMovement : {}", id);
        return stockMovementRepository.findById(id).map(stockMovementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StockMovement : {}", id);
        stockMovementRepository.deleteById(id);
        stockMovementSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of StockMovements for query {}", query);
        return stockMovementSearchRepository.search(query, pageable).map(stockMovementMapper::toDto);
    }
}
