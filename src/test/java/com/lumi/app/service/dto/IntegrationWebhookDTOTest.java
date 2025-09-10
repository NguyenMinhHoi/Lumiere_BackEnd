package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntegrationWebhookDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntegrationWebhookDTO.class);
        IntegrationWebhookDTO integrationWebhookDTO1 = new IntegrationWebhookDTO();
        integrationWebhookDTO1.setId(1L);
        IntegrationWebhookDTO integrationWebhookDTO2 = new IntegrationWebhookDTO();
        assertThat(integrationWebhookDTO1).isNotEqualTo(integrationWebhookDTO2);
        integrationWebhookDTO2.setId(integrationWebhookDTO1.getId());
        assertThat(integrationWebhookDTO1).isEqualTo(integrationWebhookDTO2);
        integrationWebhookDTO2.setId(2L);
        assertThat(integrationWebhookDTO1).isNotEqualTo(integrationWebhookDTO2);
        integrationWebhookDTO1.setId(null);
        assertThat(integrationWebhookDTO1).isNotEqualTo(integrationWebhookDTO2);
    }
}
