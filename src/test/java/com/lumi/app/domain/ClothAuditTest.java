package com.lumi.app.domain;

import static com.lumi.app.domain.ClothAuditTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothAudit.class);
        ClothAudit clothAudit1 = getClothAuditSample1();
        ClothAudit clothAudit2 = new ClothAudit();
        assertThat(clothAudit1).isNotEqualTo(clothAudit2);

        clothAudit2.setId(clothAudit1.getId());
        assertThat(clothAudit1).isEqualTo(clothAudit2);

        clothAudit2 = getClothAuditSample2();
        assertThat(clothAudit1).isNotEqualTo(clothAudit2);
    }
}
