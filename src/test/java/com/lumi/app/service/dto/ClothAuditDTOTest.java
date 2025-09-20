package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothAuditDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothAuditDTO.class);
        ClothAuditDTO clothAuditDTO1 = new ClothAuditDTO();
        clothAuditDTO1.setId(1L);
        ClothAuditDTO clothAuditDTO2 = new ClothAuditDTO();
        assertThat(clothAuditDTO1).isNotEqualTo(clothAuditDTO2);
        clothAuditDTO2.setId(clothAuditDTO1.getId());
        assertThat(clothAuditDTO1).isEqualTo(clothAuditDTO2);
        clothAuditDTO2.setId(2L);
        assertThat(clothAuditDTO1).isNotEqualTo(clothAuditDTO2);
        clothAuditDTO1.setId(null);
        assertThat(clothAuditDTO1).isNotEqualTo(clothAuditDTO2);
    }
}
