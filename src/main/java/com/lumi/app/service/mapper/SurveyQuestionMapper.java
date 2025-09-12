package com.lumi.app.service.mapper;

import com.lumi.app.domain.SurveyQuestion;
import com.lumi.app.service.dto.SurveyQuestionDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link SurveyQuestion} and its DTO {@link SurveyQuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveyQuestionMapper extends EntityMapper<SurveyQuestionDTO, SurveyQuestion> {}
