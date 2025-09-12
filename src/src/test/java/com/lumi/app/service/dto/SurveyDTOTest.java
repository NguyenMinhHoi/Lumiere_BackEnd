package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyDTO.class);
        SurveyDTO surveyDTO1 = new SurveyDTO();
        surveyDTO1.setId(1L);
        SurveyDTO surveyDTO2 = new SurveyDTO();
        assertThat(surveyDTO1).isNotEqualTo(surveyDTO2);
        surveyDTO2.setId(surveyDTO1.getId());
        assertThat(surveyDTO1).isEqualTo(surveyDTO2);
        surveyDTO2.setId(2L);
        assertThat(surveyDTO1).isNotEqualTo(surveyDTO2);
        surveyDTO1.setId(null);
        assertThat(surveyDTO1).isNotEqualTo(surveyDTO2);
    }
}
