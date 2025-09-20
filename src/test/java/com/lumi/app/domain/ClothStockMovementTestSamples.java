package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ClothStockMovementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClothStockMovement getClothStockMovementSample1() {
        return new ClothStockMovement().id(1L).clothId(1L).warehouseId(1L).delta(1L).refOrderId(1L);
    }

    public static ClothStockMovement getClothStockMovementSample2() {
        return new ClothStockMovement().id(2L).clothId(2L).warehouseId(2L).delta(2L).refOrderId(2L);
    }

    public static ClothStockMovement getClothStockMovementRandomSampleGenerator() {
        return new ClothStockMovement()
            .id(longCount.incrementAndGet())
            .clothId(longCount.incrementAndGet())
            .warehouseId(longCount.incrementAndGet())
            .delta(longCount.incrementAndGet())
            .refOrderId(longCount.incrementAndGet());
    }
}
