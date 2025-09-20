package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothStockMovementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothStockMovementDTO.class);
        ClothStockMovementDTO clothStockMovementDTO1 = new ClothStockMovementDTO();
        clothStockMovementDTO1.setId(1L);
        ClothStockMovementDTO clothStockMovementDTO2 = new ClothStockMovementDTO();
        assertThat(clothStockMovementDTO1).isNotEqualTo(clothStockMovementDTO2);
        clothStockMovementDTO2.setId(clothStockMovementDTO1.getId());
        assertThat(clothStockMovementDTO1).isEqualTo(clothStockMovementDTO2);
        clothStockMovementDTO2.setId(2L);
        assertThat(clothStockMovementDTO1).isNotEqualTo(clothStockMovementDTO2);
        clothStockMovementDTO1.setId(null);
        assertThat(clothStockMovementDTO1).isNotEqualTo(clothStockMovementDTO2);
    }
}
