package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClothTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Cloth getClothSample1() {
        return new Cloth().id(1L).code("code1").name("name1").material("material1").color("color1").unit("unit1");
    }

    public static Cloth getClothSample2() {
        return new Cloth().id(2L).code("code2").name("name2").material("material2").color("color2").unit("unit2");
    }

    public static Cloth getClothRandomSampleGenerator() {
        return new Cloth()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .material(UUID.randomUUID().toString())
            .color(UUID.randomUUID().toString())
            .unit(UUID.randomUUID().toString());
    }
}
