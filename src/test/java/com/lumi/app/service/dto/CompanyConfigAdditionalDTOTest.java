package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanyConfigAdditionalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanyConfigAdditionalDTO.class);
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO1 = new CompanyConfigAdditionalDTO();
        companyConfigAdditionalDTO1.setId(1L);
        CompanyConfigAdditionalDTO companyConfigAdditionalDTO2 = new CompanyConfigAdditionalDTO();
        assertThat(companyConfigAdditionalDTO1).isNotEqualTo(companyConfigAdditionalDTO2);
        companyConfigAdditionalDTO2.setId(companyConfigAdditionalDTO1.getId());
        assertThat(companyConfigAdditionalDTO1).isEqualTo(companyConfigAdditionalDTO2);
        companyConfigAdditionalDTO2.setId(2L);
        assertThat(companyConfigAdditionalDTO1).isNotEqualTo(companyConfigAdditionalDTO2);
        companyConfigAdditionalDTO1.setId(null);
        assertThat(companyConfigAdditionalDTO1).isNotEqualTo(companyConfigAdditionalDTO2);
    }
}
