package com.lumi.app.service.mapper;

import com.lumi.app.domain.Customer;
import com.lumi.app.domain.Survey;
import com.lumi.app.service.dto.CustomerDTO;
import com.lumi.app.service.dto.SurveyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Survey} and its DTO {@link SurveyDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveyMapper extends EntityMapper<SurveyDTO, Survey> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerCode")
    SurveyDTO toDto(Survey s);

    @Named("customerCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CustomerDTO toDtoCustomerCode(Customer customer);
}
