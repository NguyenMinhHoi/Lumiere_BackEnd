package com.lumi.app.service.mapper;

import com.lumi.app.domain.SurveyResponse;
import com.lumi.app.service.dto.SurveyResponseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SurveyResponse} and its DTO {@link SurveyResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveyResponseMapper extends EntityMapper<SurveyResponseDTO, SurveyResponse> {}
