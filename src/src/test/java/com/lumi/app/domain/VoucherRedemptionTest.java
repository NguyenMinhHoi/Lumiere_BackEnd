package com.lumi.app.domain;

import static com.lumi.app.domain.VoucherRedemptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VoucherRedemptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VoucherRedemption.class);
        VoucherRedemption voucherRedemption1 = getVoucherRedemptionSample1();
        VoucherRedemption voucherRedemption2 = new VoucherRedemption();
        assertThat(voucherRedemption1).isNotEqualTo(voucherRedemption2);

        voucherRedemption2.setId(voucherRedemption1.getId());
        assertThat(voucherRedemption1).isEqualTo(voucherRedemption2);

        voucherRedemption2 = getVoucherRedemptionSample2();
        assertThat(voucherRedemption1).isNotEqualTo(voucherRedemption2);
    }
}
