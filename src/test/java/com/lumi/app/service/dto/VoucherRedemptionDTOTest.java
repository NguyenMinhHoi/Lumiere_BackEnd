package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VoucherRedemptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VoucherRedemptionDTO.class);
        VoucherRedemptionDTO voucherRedemptionDTO1 = new VoucherRedemptionDTO();
        voucherRedemptionDTO1.setId(1L);
        VoucherRedemptionDTO voucherRedemptionDTO2 = new VoucherRedemptionDTO();
        assertThat(voucherRedemptionDTO1).isNotEqualTo(voucherRedemptionDTO2);
        voucherRedemptionDTO2.setId(voucherRedemptionDTO1.getId());
        assertThat(voucherRedemptionDTO1).isEqualTo(voucherRedemptionDTO2);
        voucherRedemptionDTO2.setId(2L);
        assertThat(voucherRedemptionDTO1).isNotEqualTo(voucherRedemptionDTO2);
        voucherRedemptionDTO1.setId(null);
        assertThat(voucherRedemptionDTO1).isNotEqualTo(voucherRedemptionDTO2);
    }
}
