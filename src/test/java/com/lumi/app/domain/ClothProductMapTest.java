package com.lumi.app.domain;

import static com.lumi.app.domain.ClothProductMapTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothProductMapTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClothProductMap.class);
        ClothProductMap clothProductMap1 = getClothProductMapSample1();
        ClothProductMap clothProductMap2 = new ClothProductMap();
        assertThat(clothProductMap1).isNotEqualTo(clothProductMap2);

        clothProductMap2.setId(clothProductMap1.getId());
        assertThat(clothProductMap1).isEqualTo(clothProductMap2);

        clothProductMap2 = getClothProductMapSample2();
        assertThat(clothProductMap1).isNotEqualTo(clothProductMap2);
    }
}
