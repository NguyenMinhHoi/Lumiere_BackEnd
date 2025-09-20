package com.lumi.app.service.mapper;

import static com.lumi.app.domain.SurveyAsserts.*;
import static com.lumi.app.domain.SurveyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveyMapperTest {

    private SurveyMapper surveyMapper;

    @BeforeEach
    void setUp() {
        surveyMapper = new SurveyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSurveySample1();
        var actual = surveyMapper.toEntity(surveyMapper.toDto(expected));
        assertSurveyAllPropertiesEquals(expected, actual);
    }
}
