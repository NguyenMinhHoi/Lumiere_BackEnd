package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SurveyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Survey getSurveySample1() {
        return new Survey().id(1L).customerId(1L).title("title1");
    }

    public static Survey getSurveySample2() {
        return new Survey().id(2L).customerId(2L).title("title2");
    }

    public static Survey getSurveyRandomSampleGenerator() {
        return new Survey().id(longCount.incrementAndGet()).customerId(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
