package com.lumi.app.domain;

import static com.lumi.app.domain.SlaPlanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SlaPlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SlaPlan.class);
        SlaPlan slaPlan1 = getSlaPlanSample1();
        SlaPlan slaPlan2 = new SlaPlan();
        assertThat(slaPlan1).isNotEqualTo(slaPlan2);

        slaPlan2.setId(slaPlan1.getId());
        assertThat(slaPlan1).isEqualTo(slaPlan2);

        slaPlan2 = getSlaPlanSample2();
        assertThat(slaPlan1).isNotEqualTo(slaPlan2);
    }
}
