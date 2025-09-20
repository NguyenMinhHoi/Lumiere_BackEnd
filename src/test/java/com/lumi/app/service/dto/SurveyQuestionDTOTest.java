package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyQuestionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyQuestionDTO.class);
        SurveyQuestionDTO surveyQuestionDTO1 = new SurveyQuestionDTO();
        surveyQuestionDTO1.setId(1L);
        SurveyQuestionDTO surveyQuestionDTO2 = new SurveyQuestionDTO();
        assertThat(surveyQuestionDTO1).isNotEqualTo(surveyQuestionDTO2);
        surveyQuestionDTO2.setId(surveyQuestionDTO1.getId());
        assertThat(surveyQuestionDTO1).isEqualTo(surveyQuestionDTO2);
        surveyQuestionDTO2.setId(2L);
        assertThat(surveyQuestionDTO1).isNotEqualTo(surveyQuestionDTO2);
        surveyQuestionDTO1.setId(null);
        assertThat(surveyQuestionDTO1).isNotEqualTo(surveyQuestionDTO2);
    }
}
