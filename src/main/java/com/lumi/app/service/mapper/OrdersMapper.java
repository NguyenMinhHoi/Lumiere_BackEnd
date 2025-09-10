package com.lumi.app.service.mapper;

import com.lumi.app.domain.Customer;
import com.lumi.app.domain.Orders;
import com.lumi.app.service.dto.CustomerDTO;
import com.lumi.app.service.dto.OrdersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Orders} and its DTO {@link OrdersDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrdersMapper extends EntityMapper<OrdersDTO, Orders> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerCode")
    OrdersDTO toDto(Orders s);

    @Named("customerCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CustomerDTO toDtoCustomerCode(Customer customer);
}
