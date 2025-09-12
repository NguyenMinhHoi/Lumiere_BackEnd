package com.lumi.app.domain;

import static com.lumi.app.domain.SurveyResponseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyResponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyResponse.class);
        SurveyResponse surveyResponse1 = getSurveyResponseSample1();
        SurveyResponse surveyResponse2 = new SurveyResponse();
        assertThat(surveyResponse1).isNotEqualTo(surveyResponse2);

        surveyResponse2.setId(surveyResponse1.getId());
        assertThat(surveyResponse1).isEqualTo(surveyResponse2);

        surveyResponse2 = getSurveyResponseSample2();
        assertThat(surveyResponse1).isNotEqualTo(surveyResponse2);
    }
}
