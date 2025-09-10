package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SupplementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Supplement getSupplementSample1() {
        return new Supplement().id(1L).currency("currency1").leadTimeDays(1).minOrderQty(1);
    }

    public static Supplement getSupplementSample2() {
        return new Supplement().id(2L).currency("currency2").leadTimeDays(2).minOrderQty(2);
    }

    public static Supplement getSupplementRandomSampleGenerator() {
        return new Supplement()
            .id(longCount.incrementAndGet())
            .currency(UUID.randomUUID().toString())
            .leadTimeDays(intCount.incrementAndGet())
            .minOrderQty(intCount.incrementAndGet());
    }
}
