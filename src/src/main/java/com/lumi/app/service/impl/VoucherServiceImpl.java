package com.lumi.app.service.impl;

import com.lumi.app.domain.Voucher;
import com.lumi.app.repository.VoucherRepository;
import com.lumi.app.repository.search.VoucherSearchRepository;
import com.lumi.app.service.VoucherService;
import com.lumi.app.service.dto.VoucherDTO;
import com.lumi.app.service.mapper.VoucherMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Voucher}.
 */
@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherServiceImpl.class);

    private final VoucherRepository voucherRepository;

    private final VoucherMapper voucherMapper;

    private final VoucherSearchRepository voucherSearchRepository;

    public VoucherServiceImpl(
        VoucherRepository voucherRepository,
        VoucherMapper voucherMapper,
        VoucherSearchRepository voucherSearchRepository
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
        this.voucherSearchRepository = voucherSearchRepository;
    }

    @Override
    public VoucherDTO save(VoucherDTO voucherDTO) {
        LOG.debug("Request to save Voucher : {}", voucherDTO);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher = voucherRepository.save(voucher);
        voucherSearchRepository.index(voucher);
        return voucherMapper.toDto(voucher);
    }

    @Override
    public VoucherDTO update(VoucherDTO voucherDTO) {
        LOG.debug("Request to update Voucher : {}", voucherDTO);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher = voucherRepository.save(voucher);
        voucherSearchRepository.index(voucher);
        return voucherMapper.toDto(voucher);
    }

    @Override
    public Optional<VoucherDTO> partialUpdate(VoucherDTO voucherDTO) {
        LOG.debug("Request to partially update Voucher : {}", voucherDTO);

        return voucherRepository
            .findById(voucherDTO.getId())
            .map(existingVoucher -> {
                voucherMapper.partialUpdate(existingVoucher, voucherDTO);

                return existingVoucher;
            })
            .map(voucherRepository::save)
            .map(savedVoucher -> {
                voucherSearchRepository.index(savedVoucher);
                return savedVoucher;
            })
            .map(voucherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherDTO> findOne(Long id) {
        LOG.debug("Request to get Voucher : {}", id);
        return voucherRepository.findById(id).map(voucherMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Voucher : {}", id);
        voucherRepository.deleteById(id);
        voucherSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Vouchers for query {}", query);
        return voucherSearchRepository.search(query, pageable).map(voucherMapper::toDto);
    }
}
