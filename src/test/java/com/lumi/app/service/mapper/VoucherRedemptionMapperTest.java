package com.lumi.app.service.mapper;

import static com.lumi.app.domain.VoucherRedemptionAsserts.*;
import static com.lumi.app.domain.VoucherRedemptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VoucherRedemptionMapperTest {

    private VoucherRedemptionMapper voucherRedemptionMapper;

    @BeforeEach
    void setUp() {
        voucherRedemptionMapper = new VoucherRedemptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVoucherRedemptionSample1();
        var actual = voucherRedemptionMapper.toEntity(voucherRedemptionMapper.toDto(expected));
        assertVoucherRedemptionAllPropertiesEquals(expected, actual);
    }
}
