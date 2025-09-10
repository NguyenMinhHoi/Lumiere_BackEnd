package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrdersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Orders getOrdersSample1() {
        return new Orders().id(1L).code("code1").currency("currency1").note("note1");
    }

    public static Orders getOrdersSample2() {
        return new Orders().id(2L).code("code2").currency("currency2").note("note2");
    }

    public static Orders getOrdersRandomSampleGenerator() {
        return new Orders()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
