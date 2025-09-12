package com.lumi.app.domain;

import static com.lumi.app.domain.IntegrationLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntegrationLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntegrationLog.class);
        IntegrationLog integrationLog1 = getIntegrationLogSample1();
        IntegrationLog integrationLog2 = new IntegrationLog();
        assertThat(integrationLog1).isNotEqualTo(integrationLog2);

        integrationLog2.setId(integrationLog1.getId());
        assertThat(integrationLog1).isEqualTo(integrationLog2);

        integrationLog2 = getIntegrationLogSample2();
        assertThat(integrationLog1).isNotEqualTo(integrationLog2);
    }
}
