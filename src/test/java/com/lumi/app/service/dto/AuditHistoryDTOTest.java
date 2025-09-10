package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditHistoryDTO.class);
        AuditHistoryDTO auditHistoryDTO1 = new AuditHistoryDTO();
        auditHistoryDTO1.setId(1L);
        AuditHistoryDTO auditHistoryDTO2 = new AuditHistoryDTO();
        assertThat(auditHistoryDTO1).isNotEqualTo(auditHistoryDTO2);
        auditHistoryDTO2.setId(auditHistoryDTO1.getId());
        assertThat(auditHistoryDTO1).isEqualTo(auditHistoryDTO2);
        auditHistoryDTO2.setId(2L);
        assertThat(auditHistoryDTO1).isNotEqualTo(auditHistoryDTO2);
        auditHistoryDTO1.setId(null);
        assertThat(auditHistoryDTO1).isNotEqualTo(auditHistoryDTO2);
    }
}
