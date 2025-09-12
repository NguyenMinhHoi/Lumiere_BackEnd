package com.lumi.app.domain;

import static com.lumi.app.domain.CampaignTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CampaignTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Campaign.class);
        Campaign campaign1 = getCampaignSample1();
        Campaign campaign2 = new Campaign();
        assertThat(campaign1).isNotEqualTo(campaign2);

        campaign2.setId(campaign1.getId());
        assertThat(campaign1).isEqualTo(campaign2);

        campaign2 = getCampaignSample2();
        assertThat(campaign1).isNotEqualTo(campaign2);
    }
}
