package com.lumi.app.service.mapper;

import com.lumi.app.domain.Customer;
import com.lumi.app.domain.Notification;
import com.lumi.app.domain.Survey;
import com.lumi.app.domain.Ticket;
import com.lumi.app.service.dto.CustomerDTO;
import com.lumi.app.service.dto.NotificationDTO;
import com.lumi.app.service.dto.SurveyDTO;
import com.lumi.app.service.dto.TicketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketCode")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerCode")
    @Mapping(target = "survey", source = "survey", qualifiedByName = "surveyTitle")
    NotificationDTO toDto(Notification s);

    @Named("ticketCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    TicketDTO toDtoTicketCode(Ticket ticket);

    @Named("customerCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CustomerDTO toDtoCustomerCode(Customer customer);

    @Named("surveyTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    SurveyDTO toDtoSurveyTitle(Survey survey);
}
