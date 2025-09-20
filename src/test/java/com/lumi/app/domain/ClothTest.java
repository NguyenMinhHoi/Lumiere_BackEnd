package com.lumi.app.domain;

import static com.lumi.app.domain.ClothTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClothTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cloth.class);
        Cloth cloth1 = getClothSample1();
        Cloth cloth2 = new Cloth();
        assertThat(cloth1).isNotEqualTo(cloth2);

        cloth2.setId(cloth1.getId());
        assertThat(cloth1).isEqualTo(cloth2);

        cloth2 = getClothSample2();
        assertThat(cloth1).isNotEqualTo(cloth2);
    }
}
