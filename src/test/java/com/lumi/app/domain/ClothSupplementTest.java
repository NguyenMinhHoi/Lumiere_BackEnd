package com.lumi.app.domain;

import static com.lumi.app.domain.ClothSupplementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothSupplementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothSupplement.class);
        ClothSupplement clothSupplement1 = getClothSupplementSample1();
        ClothSupplement clothSupplement2 = new ClothSupplement();
        assertThat(clothSupplement1).isNotEqualTo(clothSupplement2);

        clothSupplement2.setId(clothSupplement1.getId());
        assertThat(clothSupplement1).isEqualTo(clothSupplement2);

        clothSupplement2 = getClothSupplementSample2();
        assertThat(clothSupplement1).isNotEqualTo(clothSupplement2);
    }
}
