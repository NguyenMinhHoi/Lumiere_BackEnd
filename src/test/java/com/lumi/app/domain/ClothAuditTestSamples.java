package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClothAuditTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClothAudit getClothAuditSample1() {
        return new ClothAudit().id(1L).clothId(1L).supplierId(1L).productId(1L).unit("unit1").note("note1");
    }

    public static ClothAudit getClothAuditSample2() {
        return new ClothAudit().id(2L).clothId(2L).supplierId(2L).productId(2L).unit("unit2").note("note2");
    }

    public static ClothAudit getClothAuditRandomSampleGenerator() {
        return new ClothAudit()
            .id(longCount.incrementAndGet())
            .clothId(longCount.incrementAndGet())
            .supplierId(longCount.incrementAndGet())
            .productId(longCount.incrementAndGet())
            .unit(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
