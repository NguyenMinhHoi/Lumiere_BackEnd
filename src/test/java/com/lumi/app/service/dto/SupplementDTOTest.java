package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplementDTO.class);
        SupplementDTO supplementDTO1 = new SupplementDTO();
        supplementDTO1.setId(1L);
        SupplementDTO supplementDTO2 = new SupplementDTO();
        assertThat(supplementDTO1).isNotEqualTo(supplementDTO2);
        supplementDTO2.setId(supplementDTO1.getId());
        assertThat(supplementDTO1).isEqualTo(supplementDTO2);
        supplementDTO2.setId(2L);
        assertThat(supplementDTO1).isNotEqualTo(supplementDTO2);
        supplementDTO1.setId(null);
        assertThat(supplementDTO1).isNotEqualTo(supplementDTO2);
    }
}
