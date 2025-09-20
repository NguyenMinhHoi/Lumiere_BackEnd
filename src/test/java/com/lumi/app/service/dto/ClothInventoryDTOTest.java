package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothInventoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothInventoryDTO.class);
        ClothInventoryDTO clothInventoryDTO1 = new ClothInventoryDTO();
        clothInventoryDTO1.setId(1L);
        ClothInventoryDTO clothInventoryDTO2 = new ClothInventoryDTO();
        assertThat(clothInventoryDTO1).isNotEqualTo(clothInventoryDTO2);
        clothInventoryDTO2.setId(clothInventoryDTO1.getId());
        assertThat(clothInventoryDTO1).isEqualTo(clothInventoryDTO2);
        clothInventoryDTO2.setId(2L);
        assertThat(clothInventoryDTO1).isNotEqualTo(clothInventoryDTO2);
        clothInventoryDTO1.setId(null);
        assertThat(clothInventoryDTO1).isNotEqualTo(clothInventoryDTO2);
    }
}
