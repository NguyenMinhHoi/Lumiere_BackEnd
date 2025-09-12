package com.lumi.app.service.impl;

import com.lumi.app.domain.Warehouse;
import com.lumi.app.repository.WarehouseRepository;
import com.lumi.app.repository.search.WarehouseSearchRepository;
import com.lumi.app.service.WarehouseService;
import com.lumi.app.service.dto.WarehouseDTO;
import com.lumi.app.service.mapper.WarehouseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Warehouse}.
 */
@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    private final WarehouseSearchRepository warehouseSearchRepository;

    public WarehouseServiceImpl(
        WarehouseRepository warehouseRepository,
        WarehouseMapper warehouseMapper,
        WarehouseSearchRepository warehouseSearchRepository
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.warehouseSearchRepository = warehouseSearchRepository;
    }

    @Override
    public WarehouseDTO save(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to save Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        warehouseSearchRepository.index(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public WarehouseDTO update(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to update Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        warehouseSearchRepository.index(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public Optional<WarehouseDTO> partialUpdate(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to partially update Warehouse : {}", warehouseDTO);

        return warehouseRepository
            .findById(warehouseDTO.getId())
            .map(existingWarehouse -> {
                warehouseMapper.partialUpdate(existingWarehouse, warehouseDTO);

                return existingWarehouse;
            })
            .map(warehouseRepository::save)
            .map(savedWarehouse -> {
                warehouseSearchRepository.index(savedWarehouse);
                return savedWarehouse;
            })
            .map(warehouseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Warehouses");
        return warehouseRepository.findAll(pageable).map(warehouseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDTO> findOne(Long id) {
        LOG.debug("Request to get Warehouse : {}", id);
        return warehouseRepository.findById(id).map(warehouseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Warehouse : {}", id);
        warehouseRepository.deleteById(id);
        warehouseSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Warehouses for query {}", query);
        return warehouseSearchRepository.search(query, pageable).map(warehouseMapper::toDto);
    }
}
