package com.lumi.app.service.mapper;

import com.lumi.app.domain.VoucherRedemption;
import com.lumi.app.service.dto.VoucherRedemptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VoucherRedemption} and its DTO {@link VoucherRedemptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface VoucherRedemptionMapper extends EntityMapper<VoucherRedemptionDTO, VoucherRedemption> {}
