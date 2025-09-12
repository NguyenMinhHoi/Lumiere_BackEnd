package com.lumi.app.domain;

import static com.lumi.app.domain.AuditHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditHistory.class);
        AuditHistory auditHistory1 = getAuditHistorySample1();
        AuditHistory auditHistory2 = new AuditHistory();
        assertThat(auditHistory1).isNotEqualTo(auditHistory2);

        auditHistory2.setId(auditHistory1.getId());
        assertThat(auditHistory1).isEqualTo(auditHistory2);

        auditHistory2 = getAuditHistorySample2();
        assertThat(auditHistory1).isNotEqualTo(auditHistory2);
    }
}
