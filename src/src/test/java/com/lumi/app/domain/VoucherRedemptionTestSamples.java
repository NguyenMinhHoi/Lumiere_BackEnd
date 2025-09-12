package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class VoucherRedemptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static VoucherRedemption getVoucherRedemptionSample1() {
        return new VoucherRedemption().id(1L).voucherId(1L).orderId(1L).customerId(1L);
    }

    public static VoucherRedemption getVoucherRedemptionSample2() {
        return new VoucherRedemption().id(2L).voucherId(2L).orderId(2L).customerId(2L);
    }

    public static VoucherRedemption getVoucherRedemptionRandomSampleGenerator() {
        return new VoucherRedemption()
            .id(longCount.incrementAndGet())
            .voucherId(longCount.incrementAndGet())
            .orderId(longCount.incrementAndGet())
            .customerId(longCount.incrementAndGet());
    }
}
