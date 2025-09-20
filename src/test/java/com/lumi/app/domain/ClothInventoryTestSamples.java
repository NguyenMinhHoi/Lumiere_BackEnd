package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ClothInventoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClothInventory getClothInventorySample1() {
        return new ClothInventory().id(1L).clothId(1L).warehouseId(1L).quantity(1L);
    }

    public static ClothInventory getClothInventorySample2() {
        return new ClothInventory().id(2L).clothId(2L).warehouseId(2L).quantity(2L);
    }

    public static ClothInventory getClothInventoryRandomSampleGenerator() {
        return new ClothInventory()
            .id(longCount.incrementAndGet())
            .clothId(longCount.incrementAndGet())
            .warehouseId(longCount.incrementAndGet())
            .quantity(longCount.incrementAndGet());
    }
}
