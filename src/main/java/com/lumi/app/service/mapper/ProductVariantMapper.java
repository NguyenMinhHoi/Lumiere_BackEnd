package com.lumi.app.service.mapper;

import com.lumi.app.domain.ProductVariant;
import com.lumi.app.service.dto.ProductVariantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVariant} and its DTO {@link ProductVariantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductVariantMapper extends EntityMapper<ProductVariantDTO, ProductVariant> {}
