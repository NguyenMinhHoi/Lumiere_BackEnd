package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CampaignTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Campaign getCampaignSample1() {
        return new Campaign().id(1L).name("name1");
    }

    public static Campaign getCampaignSample2() {
        return new Campaign().id(2L).name("name2");
    }

    public static Campaign getCampaignRandomSampleGenerator() {
        return new Campaign().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
