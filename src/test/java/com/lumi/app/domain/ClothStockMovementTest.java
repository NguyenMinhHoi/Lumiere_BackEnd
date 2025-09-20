package com.lumi.app.domain;

import static com.lumi.app.domain.ClothStockMovementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothStockMovementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothStockMovement.class);
        ClothStockMovement clothStockMovement1 = getClothStockMovementSample1();
        ClothStockMovement clothStockMovement2 = new ClothStockMovement();
        assertThat(clothStockMovement1).isNotEqualTo(clothStockMovement2);

        clothStockMovement2.setId(clothStockMovement1.getId());
        assertThat(clothStockMovement1).isEqualTo(clothStockMovement2);

        clothStockMovement2 = getClothStockMovementSample2();
        assertThat(clothStockMovement1).isNotEqualTo(clothStockMovement2);
    }
}
