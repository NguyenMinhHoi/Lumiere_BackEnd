package com.lumi.app.service.impl;

import com.lumi.app.domain.Supplier;
import com.lumi.app.repository.SupplierRepository;
import com.lumi.app.repository.search.SupplierSearchRepository;
import com.lumi.app.service.SupplierService;
import com.lumi.app.service.dto.SupplierDTO;
import com.lumi.app.service.mapper.SupplierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Supplier}.
 */
@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierServiceImpl.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    private final SupplierSearchRepository supplierSearchRepository;

    public SupplierServiceImpl(
        SupplierRepository supplierRepository,
        SupplierMapper supplierMapper,
        SupplierSearchRepository supplierSearchRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.supplierSearchRepository = supplierSearchRepository;
    }

    @Override
    public SupplierDTO save(SupplierDTO supplierDTO) {
        LOG.debug("Request to save Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        supplierSearchRepository.index(supplier);
        return supplierMapper.toDto(supplier);
    }

    @Override
    public SupplierDTO update(SupplierDTO supplierDTO) {
        LOG.debug("Request to update Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        supplierSearchRepository.index(supplier);
        return supplierMapper.toDto(supplier);
    }

    @Override
    public Optional<SupplierDTO> partialUpdate(SupplierDTO supplierDTO) {
        LOG.debug("Request to partially update Supplier : {}", supplierDTO);

        return supplierRepository
            .findById(supplierDTO.getId())
            .map(existingSupplier -> {
                supplierMapper.partialUpdate(existingSupplier, supplierDTO);

                return existingSupplier;
            })
            .map(supplierRepository::save)
            .map(savedSupplier -> {
                supplierSearchRepository.index(savedSupplier);
                return savedSupplier;
            })
            .map(supplierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> findOne(Long id) {
        LOG.debug("Request to get Supplier : {}", id);
        return supplierRepository.findById(id).map(supplierMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Supplier : {}", id);
        supplierRepository.deleteById(id);
        supplierSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Suppliers for query {}", query);
        return supplierSearchRepository.search(query, pageable).map(supplierMapper::toDto);
    }
}
