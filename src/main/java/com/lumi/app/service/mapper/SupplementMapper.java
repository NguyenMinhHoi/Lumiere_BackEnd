package com.lumi.app.service.mapper;

import com.lumi.app.domain.Product;
import com.lumi.app.domain.Supplement;
import com.lumi.app.domain.Supplier;
import com.lumi.app.service.dto.ProductDTO;
import com.lumi.app.service.dto.SupplementDTO;
import com.lumi.app.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplement} and its DTO {@link SupplementDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplementMapper extends EntityMapper<SupplementDTO, Supplement> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productCode")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierCode")
    SupplementDTO toDto(Supplement s);

    @Named("productCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ProductDTO toDtoProductCode(Product product);

    @Named("supplierCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    SupplierDTO toDtoSupplierCode(Supplier supplier);
}
