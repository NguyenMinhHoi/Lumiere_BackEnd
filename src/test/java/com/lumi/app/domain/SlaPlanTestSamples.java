package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SlaPlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SlaPlan getSlaPlanSample1() {
        return new SlaPlan().id(1L).name("name1").firstResponseMins(1).resolutionMins(1);
    }

    public static SlaPlan getSlaPlanSample2() {
        return new SlaPlan().id(2L).name("name2").firstResponseMins(2).resolutionMins(2);
    }

    public static SlaPlan getSlaPlanRandomSampleGenerator() {
        return new SlaPlan()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .firstResponseMins(intCount.incrementAndGet())
            .resolutionMins(intCount.incrementAndGet());
    }
}
