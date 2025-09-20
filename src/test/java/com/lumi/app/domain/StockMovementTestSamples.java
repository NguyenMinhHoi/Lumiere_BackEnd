package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StockMovementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockMovement getStockMovementSample1() {
        return new StockMovement().id(1L).productVariantId(1L).warehouseId(1L).delta(1L).refOrderId(1L);
    }

    public static StockMovement getStockMovementSample2() {
        return new StockMovement().id(2L).productVariantId(2L).warehouseId(2L).delta(2L).refOrderId(2L);
    }

    public static StockMovement getStockMovementRandomSampleGenerator() {
        return new StockMovement()
            .id(longCount.incrementAndGet())
            .productVariantId(longCount.incrementAndGet())
            .warehouseId(longCount.incrementAndGet())
            .delta(longCount.incrementAndGet())
            .refOrderId(longCount.incrementAndGet());
    }
}
