package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SlaPlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SlaPlanDTO.class);
        SlaPlanDTO slaPlanDTO1 = new SlaPlanDTO();
        slaPlanDTO1.setId(1L);
        SlaPlanDTO slaPlanDTO2 = new SlaPlanDTO();
        assertThat(slaPlanDTO1).isNotEqualTo(slaPlanDTO2);
        slaPlanDTO2.setId(slaPlanDTO1.getId());
        assertThat(slaPlanDTO1).isEqualTo(slaPlanDTO2);
        slaPlanDTO2.setId(2L);
        assertThat(slaPlanDTO1).isNotEqualTo(slaPlanDTO2);
        slaPlanDTO1.setId(null);
        assertThat(slaPlanDTO1).isNotEqualTo(slaPlanDTO2);
    }
}
