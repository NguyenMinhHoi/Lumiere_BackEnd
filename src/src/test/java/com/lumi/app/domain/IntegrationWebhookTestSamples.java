package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IntegrationWebhookTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static IntegrationWebhook getIntegrationWebhookSample1() {
        return new IntegrationWebhook()
            .id(1L)
            .name("name1")
            .targetUrl("targetUrl1")
            .secret("secret1")
            .subscribedEvents("subscribedEvents1");
    }

    public static IntegrationWebhook getIntegrationWebhookSample2() {
        return new IntegrationWebhook()
            .id(2L)
            .name("name2")
            .targetUrl("targetUrl2")
            .secret("secret2")
            .subscribedEvents("subscribedEvents2");
    }

    public static IntegrationWebhook getIntegrationWebhookRandomSampleGenerator() {
        return new IntegrationWebhook()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .targetUrl(UUID.randomUUID().toString())
            .secret(UUID.randomUUID().toString())
            .subscribedEvents(UUID.randomUUID().toString());
    }
}
