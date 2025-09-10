package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyResponseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyResponseDTO.class);
        SurveyResponseDTO surveyResponseDTO1 = new SurveyResponseDTO();
        surveyResponseDTO1.setId(1L);
        SurveyResponseDTO surveyResponseDTO2 = new SurveyResponseDTO();
        assertThat(surveyResponseDTO1).isNotEqualTo(surveyResponseDTO2);
        surveyResponseDTO2.setId(surveyResponseDTO1.getId());
        assertThat(surveyResponseDTO1).isEqualTo(surveyResponseDTO2);
        surveyResponseDTO2.setId(2L);
        assertThat(surveyResponseDTO1).isNotEqualTo(surveyResponseDTO2);
        surveyResponseDTO1.setId(null);
        assertThat(surveyResponseDTO1).isNotEqualTo(surveyResponseDTO2);
    }
}
