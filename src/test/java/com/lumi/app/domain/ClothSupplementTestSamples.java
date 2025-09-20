package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClothSupplementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ClothSupplement getClothSupplementSample1() {
        return new ClothSupplement().id(1L).clothId(1L).supplierId(1L).currency("currency1").leadTimeDays(1).minOrderQty(1);
    }

    public static ClothSupplement getClothSupplementSample2() {
        return new ClothSupplement().id(2L).clothId(2L).supplierId(2L).currency("currency2").leadTimeDays(2).minOrderQty(2);
    }

    public static ClothSupplement getClothSupplementRandomSampleGenerator() {
        return new ClothSupplement()
            .id(longCount.incrementAndGet())
            .clothId(longCount.incrementAndGet())
            .supplierId(longCount.incrementAndGet())
            .currency(UUID.randomUUID().toString())
            .leadTimeDays(intCount.incrementAndGet())
            .minOrderQty(intCount.incrementAndGet());
    }
}
