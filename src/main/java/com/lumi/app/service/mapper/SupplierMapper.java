package com.lumi.app.service.mapper;

import com.lumi.app.domain.Supplier;
import com.lumi.app.service.dto.SupplierDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Supplier} and its DTO {@link SupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier> {}
