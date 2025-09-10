package com.lumi.app.service.mapper;

import com.lumi.app.domain.Survey;
import com.lumi.app.domain.SurveyQuestion;
import com.lumi.app.service.dto.SurveyDTO;
import com.lumi.app.service.dto.SurveyQuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SurveyQuestion} and its DTO {@link SurveyQuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveyQuestionMapper extends EntityMapper<SurveyQuestionDTO, SurveyQuestion> {
    @Mapping(target = "survey", source = "survey", qualifiedByName = "surveyTitle")
    SurveyQuestionDTO toDto(SurveyQuestion s);

    @Named("surveyTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    SurveyDTO toDtoSurveyTitle(Survey survey);
}
