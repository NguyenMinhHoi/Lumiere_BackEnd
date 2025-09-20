package com.lumi.app.domain;

import static com.lumi.app.domain.CompanyConfigTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanyConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanyConfig.class);
        CompanyConfig companyConfig1 = getCompanyConfigSample1();
        CompanyConfig companyConfig2 = new CompanyConfig();
        assertThat(companyConfig1).isNotEqualTo(companyConfig2);

        companyConfig2.setId(companyConfig1.getId());
        assertThat(companyConfig1).isEqualTo(companyConfig2);

        companyConfig2 = getCompanyConfigSample2();
        assertThat(companyConfig1).isNotEqualTo(companyConfig2);
    }
}
