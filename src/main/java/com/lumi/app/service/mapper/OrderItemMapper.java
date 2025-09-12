package com.lumi.app.service.mapper;

import com.lumi.app.domain.OrderItem;
import com.lumi.app.service.dto.OrderItemDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {}
