package com.lumi.app.service.mapper;

import com.lumi.app.domain.Product;
import com.lumi.app.domain.ProductVariant;
import com.lumi.app.service.dto.ProductDTO;
import com.lumi.app.service.dto.ProductVariantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVariant} and its DTO {@link ProductVariantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductVariantMapper extends EntityMapper<ProductVariantDTO, ProductVariant> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productCode")
    ProductVariantDTO toDto(ProductVariant s);

    @Named("productCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ProductDTO toDtoProductCode(Product product);
}
