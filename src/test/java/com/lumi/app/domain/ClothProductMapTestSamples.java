package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClothProductMapTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClothProductMap getClothProductMapSample1() {
        return new ClothProductMap().id(1L).clothId(1L).productId(1L).unit("unit1").note("note1");
    }

    public static ClothProductMap getClothProductMapSample2() {
        return new ClothProductMap().id(2L).clothId(2L).productId(2L).unit("unit2").note("note2");
    }

    public static ClothProductMap getClothProductMapRandomSampleGenerator() {
        return new ClothProductMap()
            .id(longCount.incrementAndGet())
            .clothId(longCount.incrementAndGet())
            .productId(longCount.incrementAndGet())
            .unit(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
