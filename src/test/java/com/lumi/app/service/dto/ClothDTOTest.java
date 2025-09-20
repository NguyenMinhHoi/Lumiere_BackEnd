package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothDTO.class);
        ClothDTO clothDTO1 = new ClothDTO();
        clothDTO1.setId(1L);
        ClothDTO clothDTO2 = new ClothDTO();
        assertThat(clothDTO1).isNotEqualTo(clothDTO2);
        clothDTO2.setId(clothDTO1.getId());
        assertThat(clothDTO1).isEqualTo(clothDTO2);
        clothDTO2.setId(2L);
        assertThat(clothDTO1).isNotEqualTo(clothDTO2);
        clothDTO1.setId(null);
        assertThat(clothDTO1).isNotEqualTo(clothDTO2);
    }
}
