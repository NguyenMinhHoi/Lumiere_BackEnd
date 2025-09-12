package com.lumi.app.domain;

import static com.lumi.app.domain.IntegrationWebhookTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntegrationWebhookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntegrationWebhook.class);
        IntegrationWebhook integrationWebhook1 = getIntegrationWebhookSample1();
        IntegrationWebhook integrationWebhook2 = new IntegrationWebhook();
        assertThat(integrationWebhook1).isNotEqualTo(integrationWebhook2);

        integrationWebhook2.setId(integrationWebhook1.getId());
        assertThat(integrationWebhook1).isEqualTo(integrationWebhook2);

        integrationWebhook2 = getIntegrationWebhookSample2();
        assertThat(integrationWebhook1).isNotEqualTo(integrationWebhook2);
    }
}
