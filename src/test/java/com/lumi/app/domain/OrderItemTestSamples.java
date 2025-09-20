package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OrderItem getOrderItemSample1() {
        return new OrderItem().id(1L).orderId(1L).variantId(1L).quantity(1L).nameSnapshot("nameSnapshot1").skuSnapshot("skuSnapshot1");
    }

    public static OrderItem getOrderItemSample2() {
        return new OrderItem().id(2L).orderId(2L).variantId(2L).quantity(2L).nameSnapshot("nameSnapshot2").skuSnapshot("skuSnapshot2");
    }

    public static OrderItem getOrderItemRandomSampleGenerator() {
        return new OrderItem()
            .id(longCount.incrementAndGet())
            .orderId(longCount.incrementAndGet())
            .variantId(longCount.incrementAndGet())
            .quantity(longCount.incrementAndGet())
            .nameSnapshot(UUID.randomUUID().toString())
            .skuSnapshot(UUID.randomUUID().toString());
    }
}
