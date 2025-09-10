package com.lumi.app.service.mapper;

import com.lumi.app.domain.Customer;
import com.lumi.app.domain.Orders;
import com.lumi.app.domain.SlaPlan;
import com.lumi.app.domain.Tag;
import com.lumi.app.domain.Ticket;
import com.lumi.app.domain.User;
import com.lumi.app.service.dto.CustomerDTO;
import com.lumi.app.service.dto.OrdersDTO;
import com.lumi.app.service.dto.SlaPlanDTO;
import com.lumi.app.service.dto.TagDTO;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerCode")
    @Mapping(target = "assignee", source = "assignee", qualifiedByName = "userLogin")
    @Mapping(target = "slaPlan", source = "slaPlan", qualifiedByName = "slaPlanName")
    @Mapping(target = "order", source = "order", qualifiedByName = "ordersCode")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNameSet")
    TicketDTO toDto(Ticket s);

    @Mapping(target = "removeTags", ignore = true)
    Ticket toEntity(TicketDTO ticketDTO);

    @Named("customerCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CustomerDTO toDtoCustomerCode(Customer customer);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("slaPlanName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SlaPlanDTO toDtoSlaPlanName(SlaPlan slaPlan);

    @Named("ordersCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    OrdersDTO toDtoOrdersCode(Orders orders);

    @Named("tagName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDTO toDtoTagName(Tag tag);

    @Named("tagNameSet")
    default Set<TagDTO> toDtoTagNameSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagName).collect(Collectors.toSet());
    }
}
