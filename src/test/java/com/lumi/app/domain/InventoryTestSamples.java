package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Inventory getInventorySample1() {
        return new Inventory().id(1L).productVariantId(1L).warehouseId(1L).quantity(1L);
    }

    public static Inventory getInventorySample2() {
        return new Inventory().id(2L).productVariantId(2L).warehouseId(2L).quantity(2L);
    }

    public static Inventory getInventoryRandomSampleGenerator() {
        return new Inventory()
            .id(longCount.incrementAndGet())
            .productVariantId(longCount.incrementAndGet())
            .warehouseId(longCount.incrementAndGet())
            .quantity(longCount.incrementAndGet());
    }
}
