package com.lumi.app.service.impl;

import com.lumi.app.domain.VoucherRedemption;
import com.lumi.app.repository.VoucherRedemptionRepository;
import com.lumi.app.repository.search.VoucherRedemptionSearchRepository;
import com.lumi.app.service.VoucherRedemptionService;
import com.lumi.app.service.dto.VoucherRedemptionDTO;
import com.lumi.app.service.mapper.VoucherRedemptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.VoucherRedemption}.
 */
@Service
@Transactional
public class VoucherRedemptionServiceImpl implements VoucherRedemptionService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherRedemptionServiceImpl.class);

    private final VoucherRedemptionRepository voucherRedemptionRepository;

    private final VoucherRedemptionMapper voucherRedemptionMapper;

    private final VoucherRedemptionSearchRepository voucherRedemptionSearchRepository;

    public VoucherRedemptionServiceImpl(
        VoucherRedemptionRepository voucherRedemptionRepository,
        VoucherRedemptionMapper voucherRedemptionMapper,
        VoucherRedemptionSearchRepository voucherRedemptionSearchRepository
    ) {
        this.voucherRedemptionRepository = voucherRedemptionRepository;
        this.voucherRedemptionMapper = voucherRedemptionMapper;
        this.voucherRedemptionSearchRepository = voucherRedemptionSearchRepository;
    }

    @Override
    public VoucherRedemptionDTO save(VoucherRedemptionDTO voucherRedemptionDTO) {
        LOG.debug("Request to save VoucherRedemption : {}", voucherRedemptionDTO);
        VoucherRedemption voucherRedemption = voucherRedemptionMapper.toEntity(voucherRedemptionDTO);
        voucherRedemption = voucherRedemptionRepository.save(voucherRedemption);
        voucherRedemptionSearchRepository.index(voucherRedemption);
        return voucherRedemptionMapper.toDto(voucherRedemption);
    }

    @Override
    public VoucherRedemptionDTO update(VoucherRedemptionDTO voucherRedemptionDTO) {
        LOG.debug("Request to update VoucherRedemption : {}", voucherRedemptionDTO);
        VoucherRedemption voucherRedemption = voucherRedemptionMapper.toEntity(voucherRedemptionDTO);
        voucherRedemption = voucherRedemptionRepository.save(voucherRedemption);
        voucherRedemptionSearchRepository.index(voucherRedemption);
        return voucherRedemptionMapper.toDto(voucherRedemption);
    }

    @Override
    public Optional<VoucherRedemptionDTO> partialUpdate(VoucherRedemptionDTO voucherRedemptionDTO) {
        LOG.debug("Request to partially update VoucherRedemption : {}", voucherRedemptionDTO);

        return voucherRedemptionRepository
            .findById(voucherRedemptionDTO.getId())
            .map(existingVoucherRedemption -> {
                voucherRedemptionMapper.partialUpdate(existingVoucherRedemption, voucherRedemptionDTO);

                return existingVoucherRedemption;
            })
            .map(voucherRedemptionRepository::save)
            .map(savedVoucherRedemption -> {
                voucherRedemptionSearchRepository.index(savedVoucherRedemption);
                return savedVoucherRedemption;
            })
            .map(voucherRedemptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherRedemptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all VoucherRedemptions");
        return voucherRedemptionRepository.findAll(pageable).map(voucherRedemptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherRedemptionDTO> findOne(Long id) {
        LOG.debug("Request to get VoucherRedemption : {}", id);
        return voucherRedemptionRepository.findById(id).map(voucherRedemptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete VoucherRedemption : {}", id);
        voucherRedemptionRepository.deleteById(id);
        voucherRedemptionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherRedemptionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of VoucherRedemptions for query {}", query);
        return voucherRedemptionSearchRepository.search(query, pageable).map(voucherRedemptionMapper::toDto);
    }
}
