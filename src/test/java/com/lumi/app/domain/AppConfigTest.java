package com.lumi.app.domain;

import static com.lumi.app.domain.AppConfigTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppConfig.class);
        AppConfig appConfig1 = getAppConfigSample1();
        AppConfig appConfig2 = new AppConfig();
        assertThat(appConfig1).isNotEqualTo(appConfig2);

        appConfig2.setId(appConfig1.getId());
        assertThat(appConfig1).isEqualTo(appConfig2);

        appConfig2 = getAppConfigSample2();
        assertThat(appConfig1).isNotEqualTo(appConfig2);
    }
}
