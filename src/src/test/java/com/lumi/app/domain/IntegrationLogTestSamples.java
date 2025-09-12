package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IntegrationLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static IntegrationLog getIntegrationLogSample1() {
        return new IntegrationLog().id(1L).retries(1);
    }

    public static IntegrationLog getIntegrationLogSample2() {
        return new IntegrationLog().id(2L).retries(2);
    }

    public static IntegrationLog getIntegrationLogRandomSampleGenerator() {
        return new IntegrationLog().id(longCount.incrementAndGet()).retries(intCount.incrementAndGet());
    }
}
