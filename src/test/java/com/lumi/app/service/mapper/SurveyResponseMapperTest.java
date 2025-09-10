package com.lumi.app.service.mapper;

import static com.lumi.app.domain.SurveyResponseAsserts.*;
import static com.lumi.app.domain.SurveyResponseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveyResponseMapperTest {

    private SurveyResponseMapper surveyResponseMapper;

    @BeforeEach
    void setUp() {
        surveyResponseMapper = new SurveyResponseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSurveyResponseSample1();
        var actual = surveyResponseMapper.toEntity(surveyResponseMapper.toDto(expected));
        assertSurveyResponseAllPropertiesEquals(expected, actual);
    }
}
