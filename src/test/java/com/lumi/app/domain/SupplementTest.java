package com.lumi.app.domain;

import static com.lumi.app.domain.SupplementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Supplement.class);
        Supplement supplement1 = getSupplementSample1();
        Supplement supplement2 = new Supplement();
        assertThat(supplement1).isNotEqualTo(supplement2);

        supplement2.setId(supplement1.getId());
        assertThat(supplement1).isEqualTo(supplement2);

        supplement2 = getSupplementSample2();
        assertThat(supplement1).isNotEqualTo(supplement2);
    }
}
