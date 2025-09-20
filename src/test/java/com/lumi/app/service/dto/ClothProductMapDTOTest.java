package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothProductMapDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothProductMapDTO.class);
        ClothProductMapDTO clothProductMapDTO1 = new ClothProductMapDTO();
        clothProductMapDTO1.setId(1L);
        ClothProductMapDTO clothProductMapDTO2 = new ClothProductMapDTO();
        assertThat(clothProductMapDTO1).isNotEqualTo(clothProductMapDTO2);
        clothProductMapDTO2.setId(clothProductMapDTO1.getId());
        assertThat(clothProductMapDTO1).isEqualTo(clothProductMapDTO2);
        clothProductMapDTO2.setId(2L);
        assertThat(clothProductMapDTO1).isNotEqualTo(clothProductMapDTO2);
        clothProductMapDTO1.setId(null);
        assertThat(clothProductMapDTO1).isNotEqualTo(clothProductMapDTO2);
    }
}
