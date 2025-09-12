package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanyConfigDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanyConfigDTO.class);
        CompanyConfigDTO companyConfigDTO1 = new CompanyConfigDTO();
        companyConfigDTO1.setId(1L);
        CompanyConfigDTO companyConfigDTO2 = new CompanyConfigDTO();
        assertThat(companyConfigDTO1).isNotEqualTo(companyConfigDTO2);
        companyConfigDTO2.setId(companyConfigDTO1.getId());
        assertThat(companyConfigDTO1).isEqualTo(companyConfigDTO2);
        companyConfigDTO2.setId(2L);
        assertThat(companyConfigDTO1).isNotEqualTo(companyConfigDTO2);
        companyConfigDTO1.setId(null);
        assertThat(companyConfigDTO1).isNotEqualTo(companyConfigDTO2);
    }
}
