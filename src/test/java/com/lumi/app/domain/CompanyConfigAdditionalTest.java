package com.lumi.app.domain;

import static com.lumi.app.domain.CompanyConfigAdditionalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanyConfigAdditionalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanyConfigAdditional.class);
        CompanyConfigAdditional companyConfigAdditional1 = getCompanyConfigAdditionalSample1();
        CompanyConfigAdditional companyConfigAdditional2 = new CompanyConfigAdditional();
        assertThat(companyConfigAdditional1).isNotEqualTo(companyConfigAdditional2);

        companyConfigAdditional2.setId(companyConfigAdditional1.getId());
        assertThat(companyConfigAdditional1).isEqualTo(companyConfigAdditional2);

        companyConfigAdditional2 = getCompanyConfigAdditionalSample2();
        assertThat(companyConfigAdditional1).isNotEqualTo(companyConfigAdditional2);
    }
}
