package com.lumi.app.service.mapper;

import com.lumi.app.domain.Survey;
import com.lumi.app.service.dto.SurveyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Survey} and its DTO {@link SurveyDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveyMapper extends EntityMapper<SurveyDTO, Survey> {}
