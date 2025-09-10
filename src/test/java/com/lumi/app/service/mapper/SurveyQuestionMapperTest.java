package com.lumi.app.service.mapper;

import static com.lumi.app.domain.SurveyQuestionAsserts.*;
import static com.lumi.app.domain.SurveyQuestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveyQuestionMapperTest {

    private SurveyQuestionMapper surveyQuestionMapper;

    @BeforeEach
    void setUp() {
        surveyQuestionMapper = new SurveyQuestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSurveyQuestionSample1();
        var actual = surveyQuestionMapper.toEntity(surveyQuestionMapper.toDto(expected));
        assertSurveyQuestionAllPropertiesEquals(expected, actual);
    }
}
