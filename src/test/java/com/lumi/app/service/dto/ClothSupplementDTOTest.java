package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothSupplementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothSupplementDTO.class);
        ClothSupplementDTO clothSupplementDTO1 = new ClothSupplementDTO();
        clothSupplementDTO1.setId(1L);
        ClothSupplementDTO clothSupplementDTO2 = new ClothSupplementDTO();
        assertThat(clothSupplementDTO1).isNotEqualTo(clothSupplementDTO2);
        clothSupplementDTO2.setId(clothSupplementDTO1.getId());
        assertThat(clothSupplementDTO1).isEqualTo(clothSupplementDTO2);
        clothSupplementDTO2.setId(2L);
        assertThat(clothSupplementDTO1).isNotEqualTo(clothSupplementDTO2);
        clothSupplementDTO1.setId(null);
        assertThat(clothSupplementDTO1).isNotEqualTo(clothSupplementDTO2);
    }
}
