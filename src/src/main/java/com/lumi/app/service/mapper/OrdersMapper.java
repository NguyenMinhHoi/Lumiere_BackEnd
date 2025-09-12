package com.lumi.app.service.mapper;

import com.lumi.app.domain.Orders;
import com.lumi.app.service.dto.OrdersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Orders} and its DTO {@link OrdersDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrdersMapper extends EntityMapper<OrdersDTO, Orders> {}
