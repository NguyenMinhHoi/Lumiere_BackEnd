package com.lumi.app.service.mapper;

import com.lumi.app.domain.Voucher;
import com.lumi.app.service.dto.VoucherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Voucher} and its DTO {@link VoucherDTO}.
 */
@Mapper(componentModel = "spring")
public interface VoucherMapper extends EntityMapper<VoucherDTO, Voucher> {}
