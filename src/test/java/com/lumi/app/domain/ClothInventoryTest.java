package com.lumi.app.domain;

import static com.lumi.app.domain.ClothInventoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothInventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothInventory.class);
        ClothInventory clothInventory1 = getClothInventorySample1();
        ClothInventory clothInventory2 = new ClothInventory();
        assertThat(clothInventory1).isNotEqualTo(clothInventory2);

        clothInventory2.setId(clothInventory1.getId());
        assertThat(clothInventory1).isEqualTo(clothInventory2);

        clothInventory2 = getClothInventorySample2();
        assertThat(clothInventory1).isNotEqualTo(clothInventory2);
    }
}
