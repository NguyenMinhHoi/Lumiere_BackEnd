package com.lumi.app.service.mapper;

import com.lumi.app.domain.Customer;
import com.lumi.app.domain.Survey;
import com.lumi.app.domain.SurveyResponse;
import com.lumi.app.domain.Ticket;
import com.lumi.app.service.dto.CustomerDTO;
import com.lumi.app.service.dto.SurveyDTO;
import com.lumi.app.service.dto.SurveyResponseDTO;
import com.lumi.app.service.dto.TicketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SurveyResponse} and its DTO {@link SurveyResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveyResponseMapper extends EntityMapper<SurveyResponseDTO, SurveyResponse> {
    @Mapping(target = "survey", source = "survey", qualifiedByName = "surveyTitle")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerCode")
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketCode")
    SurveyResponseDTO toDto(SurveyResponse s);

    @Named("surveyTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    SurveyDTO toDtoSurveyTitle(Survey survey);

    @Named("customerCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CustomerDTO toDtoCustomerCode(Customer customer);

    @Named("ticketCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    TicketDTO toDtoTicketCode(Ticket ticket);
}
