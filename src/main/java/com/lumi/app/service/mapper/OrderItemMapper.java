package com.lumi.app.service.mapper;

import com.lumi.app.domain.OrderItem;
import com.lumi.app.domain.Orders;
import com.lumi.app.domain.ProductVariant;
import com.lumi.app.service.dto.OrderItemDTO;
import com.lumi.app.service.dto.OrdersDTO;
import com.lumi.app.service.dto.ProductVariantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "order", source = "order", qualifiedByName = "ordersCode")
    @Mapping(target = "variant", source = "variant", qualifiedByName = "productVariantSku")
    OrderItemDTO toDto(OrderItem s);

    @Named("ordersCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    OrdersDTO toDtoOrdersCode(Orders orders);

    @Named("productVariantSku")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    ProductVariantDTO toDtoProductVariantSku(ProductVariant productVariant);
}
