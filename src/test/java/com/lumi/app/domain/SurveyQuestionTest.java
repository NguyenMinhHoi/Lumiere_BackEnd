package com.lumi.app.domain;

import static com.lumi.app.domain.SurveyQuestionTestSamples.*;
import static com.lumi.app.domain.SurveyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyQuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyQuestion.class);
        SurveyQuestion surveyQuestion1 = getSurveyQuestionSample1();
        SurveyQuestion surveyQuestion2 = new SurveyQuestion();
        assertThat(surveyQuestion1).isNotEqualTo(surveyQuestion2);

        surveyQuestion2.setId(surveyQuestion1.getId());
        assertThat(surveyQuestion1).isEqualTo(surveyQuestion2);

        surveyQuestion2 = getSurveyQuestionSample2();
        assertThat(surveyQuestion1).isNotEqualTo(surveyQuestion2);
    }

    @Test
    void surveyTest() {
        SurveyQuestion surveyQuestion = getSurveyQuestionRandomSampleGenerator();
        Survey surveyBack = getSurveyRandomSampleGenerator();

        surveyQuestion.setSurvey(surveyBack);
        assertThat(surveyQuestion.getSurvey()).isEqualTo(surveyBack);

        surveyQuestion.survey(null);
        assertThat(surveyQuestion.getSurvey()).isNull();
    }
}
